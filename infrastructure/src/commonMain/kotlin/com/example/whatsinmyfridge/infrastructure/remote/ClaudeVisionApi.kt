package com.example.whatsinmyfridge.infrastructure.remote

import com.example.whatsinmyfridge.domain.model.IngredientRecognitionResult
import com.example.whatsinmyfridge.infrastructure.remote.dto.ClaudeErrorResponseDto
import com.example.whatsinmyfridge.infrastructure.remote.dto.ClaudeIngredientsPayloadDto
import com.example.whatsinmyfridge.infrastructure.remote.dto.ClaudeMessageResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

private const val CLAUDE_MODEL = "claude-opus-5"
private const val ANTHROPIC_VERSION = "2023-06-01"

private const val RECOGNITION_PROMPT = """
Du siehst ein Foto aus einem Kühlschrank, einer Speisekammer oder von Lebensmitteln.
Identifiziere alle einzelnen, klar erkennbaren Lebensmittel/Zutaten auf dem Bild und teile
sie in zwei Listen auf:
- confidentIngredients: Zutaten, bei denen du dir SICHER bist.
- uncertainIngredients: Zutaten, die du für möglich hältst, bei denen du dir aber NICHT
  sicher bist (z.B. teilweise verdeckt, unscharf, mehrdeutig) - diese lieber mit aufnehmen
  statt komplett wegzulassen, sie werden dem Nutzer separat zur Bestätigung angezeigt.
Regeln:
- Antworte ausschließlich auf Deutsch, kleingeschrieben, im Singular (z.B. "tomate" statt "tomaten").
- Keine Markennamen, keine Verpackungsbeschreibungen, keine Mengenangaben.
- Keine Duplikate, auch nicht zwischen den beiden Listen.
- Wenn keine Lebensmittel erkennbar sind, gib zwei leere Listen zurück.
"""

private val lenientJson = Json { ignoreUnknownKeys = true }

private val ingredientsSchema = buildJsonObject {
    put("type", "object")
    putJsonObject("properties") {
        putJsonObject("confidentIngredients") {
            put("type", "array")
            putJsonObject("items") { put("type", "string") }
        }
        putJsonObject("uncertainIngredients") {
            put("type", "array")
            putJsonObject("items") { put("type", "string") }
        }
    }
    put("additionalProperties", false)
    putJsonArray("required") { add("confidentIngredients"); add("uncertainIngredients") }
}

/**
 * Ruft die Claude Vision API (Anthropic Messages API) auf, um Zutaten aus einem Foto
 * zu erkennen. Erzwingt strukturierte JSON-Ausgabe via output_config.format, damit das
 * Parsen zuverlässig ist statt Freitext zu parsen.
 */
class ClaudeVisionApi(
    private val httpClient: HttpClient,
    private val apiKey: String,
) {
    @OptIn(ExperimentalEncodingApi::class)
    suspend fun recognizeIngredients(imageBytes: ByteArray): Result<IngredientRecognitionResult> {
        if (apiKey.isBlank()) {
            return Result.failure(IllegalStateException("Kein Anthropic API-Key konfiguriert"))
        }

        val base64Image = Base64.Default.encode(imageBytes)

        val requestBody = buildJsonObject {
            put("model", CLAUDE_MODEL)
            put("max_tokens", 1024)
            putJsonArray("messages") {
                add(
                    buildJsonObject {
                        put("role", "user")
                        putJsonArray("content") {
                            add(
                                buildJsonObject {
                                    put("type", "image")
                                    putJsonObject("source") {
                                        put("type", "base64")
                                        put("media_type", "image/jpeg")
                                        put("data", base64Image)
                                    }
                                },
                            )
                            add(
                                buildJsonObject {
                                    put("type", "text")
                                    put("text", RECOGNITION_PROMPT.trim())
                                },
                            )
                        }
                    },
                )
            }
            putJsonObject("output_config") {
                putJsonObject("format") {
                    put("type", "json_schema")
                    put("schema", ingredientsSchema)
                }
            }
        }

        val response = httpClient.post("$ANTHROPIC_BASE_URL/v1/messages") {
            header("x-api-key", apiKey)
            header("anthropic-version", ANTHROPIC_VERSION)
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }

        if (!response.status.isSuccess()) {
            return Result.failure(IllegalStateException(extractErrorMessage(response)))
        }

        val body = response.body<ClaudeMessageResponseDto>()
        val text = body.content.firstOrNull { it.type == "text" }?.text
            ?: return Result.failure(IllegalStateException("Keine Antwort von der Bilderkennung erhalten"))

        val payload = runCatching {
            lenientJson.decodeFromString(ClaudeIngredientsPayloadDto.serializer(), text)
        }.getOrElse {
            return Result.failure(IllegalStateException("Antwort der Bilderkennung konnte nicht gelesen werden"))
        }

        val confident = payload.confidentIngredients.map { it.trim() }.filter { it.isNotBlank() }.distinct()
        val uncertain = payload.uncertainIngredients.map { it.trim() }.filter { it.isNotBlank() }.distinct().filterNot { it in confident }

        return Result.success(IngredientRecognitionResult(confident = confident, uncertain = uncertain))
    }

    private suspend fun extractErrorMessage(response: HttpResponse): String =
        runCatching { response.body<ClaudeErrorResponseDto>().error?.message }
            .getOrNull()
            ?: "Bilderkennung fehlgeschlagen (${response.status.value})"
}

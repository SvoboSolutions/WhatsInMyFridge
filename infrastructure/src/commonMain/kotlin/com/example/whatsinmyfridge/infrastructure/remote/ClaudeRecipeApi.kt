package com.example.whatsinmyfridge.infrastructure.remote

import com.example.whatsinmyfridge.infrastructure.remote.dto.ClaudeErrorResponseDto
import com.example.whatsinmyfridge.infrastructure.remote.dto.ClaudeMessageResponseDto
import com.example.whatsinmyfridge.infrastructure.remote.dto.ClaudeRecipeSuggestionPayloadDto
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

// Bewusst Sonnet statt Opus: eine reine Text-Aufgabe (kein Bild), soll möglichst wenig
// Tokens/Kosten verursachen statt das teuerste Modell zu nutzen.
private const val CLAUDE_MODEL = "claude-sonnet-5"
private const val ANTHROPIC_VERSION = "2023-06-01"

private val lenientJson = Json { ignoreUnknownKeys = true }

private val recipeSchema = buildJsonObject {
    put("type", "object")
    putJsonObject("properties") {
        putJsonObject("title") { put("type", "string") }
        putJsonObject("readyInMinutes") { put("type", "integer") }
        putJsonObject("servings") { put("type", "integer") }
        putJsonObject("summary") { put("type", "string") }
        putJsonObject("ingredients") {
            put("type", "array")
            putJsonObject("items") {
                put("type", "object")
                putJsonObject("properties") {
                    putJsonObject("name") { put("type", "string") }
                    putJsonObject("amount") { put("type", "number") }
                    putJsonObject("unit") { put("type", "string") }
                    putJsonObject("original") { put("type", "string") }
                }
                put("additionalProperties", false)
                putJsonArray("required") { add("name"); add("original") }
            }
        }
        putJsonObject("instructions") {
            put("type", "array")
            putJsonObject("items") { put("type", "string") }
        }
    }
    put("additionalProperties", false)
    putJsonArray("required") { add("title"); add("ingredients"); add("instructions") }
}

/**
 * Ruft Claude (Anthropic Messages API, reiner Text) auf, um GENAU EIN Rezept aus
 * vorhandenen Zutaten vorzuschlagen - kein Bild, kein Spoonacular. Erzwingt strukturierte
 * JSON-Ausgabe via output_config.format, gleiches Muster wie ClaudeVisionApi.
 */
class ClaudeRecipeApi(
    private val httpClient: HttpClient,
    private val apiKey: String,
) {
    suspend fun suggestRecipe(
        ingredientNames: List<String>,
        dietType: String,
        allergyNames: List<String>,
        allowExtraIngredients: Boolean,
        excludeTitles: List<String>,
    ): Result<ClaudeRecipeSuggestionPayloadDto> {
        if (apiKey.isBlank()) {
            return Result.failure(IllegalStateException("Kein Anthropic API-Key konfiguriert"))
        }

        val prompt = buildPrompt(ingredientNames, dietType, allergyNames, allowExtraIngredients, excludeTitles)

        val requestBody = buildJsonObject {
            put("model", CLAUDE_MODEL)
            put("max_tokens", 1200)
            putJsonArray("messages") {
                add(
                    buildJsonObject {
                        put("role", "user")
                        putJsonArray("content") {
                            add(
                                buildJsonObject {
                                    put("type", "text")
                                    put("text", prompt)
                                },
                            )
                        }
                    },
                )
            }
            putJsonObject("output_config") {
                putJsonObject("format") {
                    put("type", "json_schema")
                    put("schema", recipeSchema)
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
            ?: return Result.failure(IllegalStateException("Keine Antwort vom Rezeptvorschlag erhalten"))

        val payload = runCatching {
            lenientJson.decodeFromString(ClaudeRecipeSuggestionPayloadDto.serializer(), text)
        }.getOrElse {
            return Result.failure(IllegalStateException("Antwort des Rezeptvorschlags konnte nicht gelesen werden"))
        }

        return Result.success(payload)
    }

    private fun buildPrompt(
        ingredientNames: List<String>,
        dietType: String,
        allergyNames: List<String>,
        allowExtraIngredients: Boolean,
        excludeTitles: List<String>,
    ): String = buildString {
        appendLine("Du bist ein Kochassistent. Erstelle GENAU EIN Rezept.")
        appendLine("Vorhandene Zutaten: ${ingredientNames.joinToString(", ")}")
        appendLine("Ernährungsweise: $dietType")
        appendLine("Unverträglichkeiten (unbedingt vermeiden): ${allergyNames.ifEmpty { listOf("keine") }.joinToString(", ")}")
        if (allowExtraIngredients) {
            appendLine("Du darfst bis zu 3 zusätzliche Zutaten einplanen, die noch eingekauft werden müssten.")
        } else {
            appendLine("Verwende AUSSCHLIESSLICH die aufgeführten vorhandenen Zutaten, keine weiteren.")
        }
        if (excludeTitles.isNotEmpty()) {
            appendLine("Schlage KEIN Rezept vor, das einem dieser bereits gezeigten Titel entspricht oder sehr ähnlich ist: ${excludeTitles.joinToString(", ")}")
        }
        append("Antworte ausschließlich mit dem strukturierten JSON gemäß Schema, auf Deutsch.")
    }

    private suspend fun extractErrorMessage(response: HttpResponse): String =
        runCatching { response.body<ClaudeErrorResponseDto>().error?.message }
            .getOrNull()
            ?: "Rezeptvorschlag fehlgeschlagen (${response.status.value})"
}

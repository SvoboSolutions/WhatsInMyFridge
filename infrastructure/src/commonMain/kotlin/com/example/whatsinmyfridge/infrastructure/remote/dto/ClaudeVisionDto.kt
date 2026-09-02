package com.example.whatsinmyfridge.infrastructure.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClaudeMessageResponseDto(
    val content: List<ClaudeContentBlockDto> = emptyList(),
    @SerialName("stop_reason") val stopReason: String? = null,
)

@Serializable
data class ClaudeContentBlockDto(
    val type: String,
    val text: String? = null,
)

@Serializable
data class ClaudeIngredientsPayloadDto(
    val confidentIngredients: List<String> = emptyList(),
    val uncertainIngredients: List<String> = emptyList(),
)

@Serializable
data class ClaudeErrorResponseDto(
    val error: ClaudeErrorDetailDto? = null,
)

@Serializable
data class ClaudeErrorDetailDto(
    val message: String? = null,
)

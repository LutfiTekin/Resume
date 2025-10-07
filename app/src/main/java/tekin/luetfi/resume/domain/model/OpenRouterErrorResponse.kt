package tekin.luetfi.resume.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class OpenRouterErrorResponse(
    val error: Error,
    val user_id: String
)

@Serializable
data class Error(
    val message: String,
    val code: Int,
    val metadata: ErrorMetadata
)

@Serializable
data class ErrorMetadata(
    val raw: String,
    val provider_name: String
)
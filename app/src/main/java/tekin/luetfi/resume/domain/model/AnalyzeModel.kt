package tekin.luetfi.resume.domain.model

import kotlinx.serialization.Serializable


@Serializable
data class AnalyzeModel(
    val id: String,
    val displayName: String,
    val category: Category
) {
    @Serializable
    enum class Category {
        FAST, RELIABLE, FASTEST
    }

    companion object{
        val default = AnalyzeModel("nvidia/nemotron-nano-9b-v2:free","NVIDIA", Category.FASTEST)

    }

}
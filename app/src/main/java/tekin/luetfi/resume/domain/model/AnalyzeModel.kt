package tekin.luetfi.resume.domain.model

enum class AnalyzeModel(
    val id: String,
    val displayName: String,
    val category: Category
) {
    GROK_4_FAST(
        id = "x-ai/grok-4-fast:free",
        displayName = "Grok",
        category = Category.FAST
    ),
    NEMOTRON_NANO(
        id = "nvidia/nemotron-nano-9b-v2:free",
        displayName = "NVIDIA",
        category = Category.RELIABLE
    ),
    DEEPSEEK_R1(
        id = "deepseek/deepseek-r1-0528:free",
        displayName = "DeepSeek",
        category = Category.RELIABLE
    ),
    META_LLAMA_3_3_8B(
        id = "meta-llama/llama-3.3-8b-instruct:free",
        displayName = "Meta",
        category = Category.FASTEST
    ),
    GEMINI_2_0_FLASH(
        id = "google/gemini-2.0-flash-exp:free",
        displayName = "Gemini",
        category = Category.FAST
    ),
    GPT_OSS_120B(
        id = "openai/gpt-oss-120b:free",
        displayName = "OpenAI",
        category = Category.FASTEST
    );

    enum class Category { FAST, RELIABLE, FASTEST }
}
package tekin.luetfi.resume.domain.model

enum class AnalyzeModel(
    val id: String,
    val displayName: String,
    val category: Category
) {
    GROK_4_FAST(
        id = "x-ai/grok-4-fast:free",
        displayName = "xAI: Grok 4 Fast",
        category = Category.FAST
    ),
    NEMOTRON_NANO(
        id = "nvidia/nemotron-nano-9b-v2:free",
        displayName = "NVIDIA: Nemotron Nano 9B V2",
        category = Category.RELIABLE
    ),
    DEEPSEEK_V31(
        id = "deepseek/deepseek-chat-v3.1:free",
        displayName = "DeepSeek: DeepSeek V3.1",
        category = Category.RELIABLE
    ),
    GPT_OSS_120B(
        id = "openai/gpt-oss-120b:free",
        displayName = "OpenAI: gpt-oss-120b",
        category = Category.FASTEST
    ),
    GPT_OSS_20B(
        id = "openai/gpt-oss-20b:free",
        displayName = "OpenAI: gpt-oss-20b",
        category = Category.FAST
    );

    enum class Category { FAST, RELIABLE, FASTEST }
}
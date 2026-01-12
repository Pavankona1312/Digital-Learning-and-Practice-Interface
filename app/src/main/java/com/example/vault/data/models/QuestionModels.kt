data class Question(
    val questionId: String,
    val status: QuestionStatus,
    val difficulty: QuestionLevel
)

enum class QuestionLevel{
    Basic,
    Mains,
    Advanced
}
enum class QuestionStatus {
    NOT_ATTEMPTED,
    ATTEMPTED,
    DONE
}

data class AnswerSheet(
    val questionId: String,
    val userId: String,
    val inkDataUrl: String,
    val lastUpdated: Long
)

data class ImportantNote(
    val questionId: String,
    val userId: String,
    val notes: String
)

data class Progress(
    val done: Int,
    val total: Int
)
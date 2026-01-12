data class Subject(
    val id: String,
    val name: String
)

data class Chapter(
    val id: String,
    val subjectId: String,
    val name: String,
    val order: Int
)

data class Subtopic(
    val id: String,
    val chapterId: String,
    val name: String,
    val order: Int
)

data class ChapterGroup(
    val id: String,
    val subjectId: String,
    val name: String,
    val order: Int
)

data class ChapterGroupUiModel(
    val group: ChapterGroup,
    val chapters: List<ChapterUiModel>
)

data class ChapterUiModel(
    val chapter: Chapter,
    val subtopics: List<Subtopic>,
    val progress: Progress?
)


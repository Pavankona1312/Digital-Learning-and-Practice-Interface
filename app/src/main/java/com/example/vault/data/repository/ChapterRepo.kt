import kotlinx.coroutines.delay

interface ChapterRepository {

    suspend fun getChapterInfo(chapterId: String): Chapter

    suspend fun getSubtopics(chapterId: String): List<Subtopic>

    suspend fun getQuestions(subtopicId: String): List<Question>
}

class MockChapterRepository : ChapterRepository {

    override suspend fun getChapterInfo(chapterId: String): Chapter {
        delay(200) // simulate network

        return Chapter(
            id = chapterId,
            subjectId = "P",
            name = "Kinematics",
            order = 1
        )
    }

    override suspend fun getSubtopics(chapterId: String): List<Subtopic> {
        delay(300)

        return listOf(
            Subtopic("s1", chapterId, "1D Horizontal Motion", 1),
            Subtopic("s2", chapterId, "1D Vertical Motion", 2),
            Subtopic("s3", chapterId, "Motion on Inclined Plane", 3),
            Subtopic("s4", chapterId, "Relative Motion", 4),
            Subtopic("s5", chapterId, "Projectile Motion", 5),
            Subtopic("s6", chapterId, "Horizontal Projectile", 6),
            Subtopic("s7", chapterId, "Projectile on Inclined Plane", 7),
            Subtopic("s8", chapterId, "Misc Problems", 8)
        )
    }

    override suspend fun getQuestions(subtopicId: String): List<Question> {
        delay(400)

        return (1..60).map { index ->
            Question(
                questionId = "$subtopicId-q$index",
                status = when {
                    index % 3 == 0 -> QuestionStatus.DONE
                    index % 3 == 1 -> QuestionStatus.ATTEMPTED
                    else -> QuestionStatus.NOT_ATTEMPTED
                },
                difficulty = when {
                    index % 7 == 0 -> QuestionLevel.Basic
                    index % 7 == 1 -> QuestionLevel.Advanced
                    else -> QuestionLevel.Mains
                }
            )
        }
    }
}

class MockSubjectRepository : SubjectRepository {

    override suspend fun getChapterGroups(subjectId: String): List<ChapterGroup> {
        return listOf(
            ChapterGroup("mech", subjectId, "Mechanics", 1),
            ChapterGroup("elec", subjectId, "Electrodynamics", 2)
        )
    }

    override suspend fun getChapters(groupId: String): List<Chapter> {
        return when (groupId) {
            "mech" -> listOf(
                Chapter("kin", groupId, "Kinematics", 1),
                Chapter("nlm", groupId, "Newton’s Laws", 2)
            )
            "elec" -> listOf(
                Chapter("ec", groupId, "Electrostatics", 1)
            )
            else -> emptyList()
        }
    }

    override suspend fun getSubtopics(chapterId: String): List<Subtopic> {
        return listOf(
            Subtopic("s1", chapterId, "Basic Maths", 1),
            Subtopic("s2", chapterId, "Graphs", 2)
        )
    }

    override suspend fun getChapterProgress(
        subjectId: String,
        userId: String
    ): Map<String, Progress> {
        return mapOf(
            "kin" to Progress(3, 10),
            "nlm" to Progress(0, 8)
        )
    }
}

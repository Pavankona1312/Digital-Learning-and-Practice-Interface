interface SubjectRepository {
    suspend fun getChapterGroups(subjectId: String): List<ChapterGroup>
    suspend fun getChapters(groupId: String): List<Chapter>
    suspend fun getSubtopics(chapterId: String): List<Subtopic>
    suspend fun getChapterProgress(
        subjectId: String,
        userId: String
    ): Map<String, Progress>
}



package com.example.vault.ui.subject

import ChapterGroupUiModel
import ChapterUiModel
import MockSubjectRepository
import SubjectRepository
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class SubjectViewModel(
    private val repository: SubjectRepository = MockSubjectRepository()
) : ViewModel() {

    var groups by mutableStateOf<List<ChapterGroupUiModel>>(emptyList())
        private set

    fun loadSubject(subjectId: String, userId: String) {
        viewModelScope.launch {
            val progressMap = repository.getChapterProgress(subjectId, userId)

            groups = repository.getChapterGroups(subjectId).map { group ->
                val chapters = repository.getChapters(group.id).map { chapter ->
                    ChapterUiModel(
                        chapter = chapter,
                        subtopics = repository.getSubtopics(chapter.id),
                        progress = progressMap[chapter.id]
                    )
                }
                ChapterGroupUiModel(group, chapters)
            }
        }
    }
}



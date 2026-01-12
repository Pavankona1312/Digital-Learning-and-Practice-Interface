import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ChapterViewModel(
    private val repository: ChapterRepository = MockChapterRepository()
) : ViewModel() {

    var chapterInfo by mutableStateOf<Chapter?>(null)
        private set

    var subtopics by mutableStateOf<List<Subtopic>>(emptyList())
        private set

    var questions by mutableStateOf<List<Question>>(emptyList())
        private set

    fun loadChapter(chapterId: String) {
        viewModelScope.launch {
            chapterInfo = repository.getChapterInfo(chapterId)
            subtopics = repository.getSubtopics(chapterId)
        }
    }

    fun loadQuestions(subtopicId: String) {
        viewModelScope.launch {
            questions = repository.getQuestions(subtopicId)
        }
    }
}

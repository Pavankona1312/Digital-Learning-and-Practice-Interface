import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vault.R

fun loadMockQuestionBitmap(context: Context): Bitmap {
    return BitmapFactory.decodeResource(
        context.resources,
        R.drawable.mock_question
    )
}

@Composable
fun QuestionScreen(
    questionBitmap: Bitmap,
    viewModel: WritingViewModel = viewModel()
) {
    QuestionWritingArea(
        viewModel = viewModel,
        questionBitmap = questionBitmap
    )
}



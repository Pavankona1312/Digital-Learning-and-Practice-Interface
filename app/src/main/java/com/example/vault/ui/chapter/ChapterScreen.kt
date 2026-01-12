import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vault.ui.subject.EmptyState
import com.example.vault.ui.subject.SidebarButton
import com.example.vault.ui.theme.VaultTheme

@Composable
fun ChapterScreen(
    chapterId: String,
    onBack: () -> Unit,
    viewModel: ChapterViewModel = viewModel()
) {
    LaunchedEffect(chapterId) {
        viewModel.loadChapter(chapterId)
    }

    val chapter = viewModel.chapterInfo
    val subtopics = viewModel.subtopics

    var selectedSubtopicId by remember { mutableStateOf<String?>(null) }

    Row(modifier = Modifier.fillMaxSize()) {

        SubtopicSidebar(
            chapterName = chapter?.name ?: "",
            subtopics = subtopics,
            selectedSubtopicId = selectedSubtopicId,
            onSubtopicClick = {
                selectedSubtopicId = it
                viewModel.loadQuestions(it)
            },
            onBack = onBack
        )

        QuestionArea(
            selectedSubtopic = subtopics.find { it.id == selectedSubtopicId },
            questions = viewModel.questions
        )
    }
}


@Composable
fun QuestionArea(
    selectedSubtopic: Subtopic?,
    questions: List<Question>
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        if (selectedSubtopic == null) {
            EmptyState("Select a subtopic from the sidebar")
        } else {
            QuestionContent(
                subtopic = selectedSubtopic,
                questions = questions
            )
        }
    }
}
@Composable
fun QuestionContent(
    subtopic: Subtopic,
    questions: List<Question>
) {
    Column {

        // Breadcrumb
        Text(
            text = "Physics • Mechanics • Kinematics",
            color = Color.Gray,
            fontSize = 12.sp
        )

        Spacer(Modifier.height(4.dp))

        // Subtopic title
        Text(
            text = subtopic.name,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold
        )

        // Count
        Text(
            text = "${questions.size} problems total",
            color = Color.Gray,
            fontSize = 13.sp
        )

        Spacer(Modifier.height(20.dp))

        // ---- BASIC SECTION (as in screenshot) ----
        QuestionSection(
            title = "Basic",
            questions = questions.take(50)
        )

        Spacer(Modifier.height(32.dp))

        // ---- INTERMEDIATE SECTION ----
        QuestionSection(
            title = "Intermediate",
            questions = questions.drop(50)
        )
    }
}

@Composable
fun QuestionSection(
    title: String,
    questions: List<Question>
) {
    Column {

        // Section header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (title == "Basic") Color(0xFFD1FAE5)
                    else Color(0xFFFEF9C3)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color.White)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = questions.size.toString(),
                    fontSize = 12.sp
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Compact grid
        QuestionCompactGrid(questions)
    }
}
@Composable
fun QuestionCompactGrid(
    questions: List<Question>
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 44.dp), // 👈 AUTO columns
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        items(questions) { question ->
            val color = when (question.status) {
                QuestionStatus.DONE -> Color(0xFF22C55E)
                QuestionStatus.ATTEMPTED -> Color(0xFFFACC15)
                QuestionStatus.NOT_ATTEMPTED -> Color(0xFFE5E7EB)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()        // fill grid cell width
                    .aspectRatio(1f)      // 👈 FORCE SQUARE
                    .clip(RoundedCornerShape(8.dp))
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = question.questionId.takeLastWhile { it.isDigit() },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}



@Composable
fun SubtopicSidebar(
    chapterName: String,
    subtopics: List<Subtopic>,
    selectedSubtopicId: String?,
    onSubtopicClick: (String) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(320.dp)
            .fillMaxHeight()
            .background(Color(0xFFF8FAFC))
            .border(1.dp, Color(0xFFE5E7EB))
            .padding(16.dp)
    ) {

        // Back
        SidebarButton(
            text = "← Back to Mechanics",
            onClick = onBack
        )


        // Chapter header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFEAF2FF)),
                contentAlignment = Alignment.Center
            ) {
                Text("📘")
            }

            Spacer(Modifier.width(12.dp))

            Column {
                Text(
                    text = chapterName,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
                Text(
                    text = "${subtopics.size} subtopics",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(Modifier.height(24.dp))
        HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
        Spacer(Modifier.height(16.dp))

        // Subtopic list
        subtopics.forEachIndexed { index, subtopic ->
            SidebarButton(
                text = "${index + 1}. ${subtopic.name}",
                selected = subtopic.id == selectedSubtopicId,
                onClick = { onSubtopicClick(subtopic.id) }
            )
            Spacer(Modifier.height(6.dp))
        }

    }
}

private val previewSubtopics = listOf(
    Subtopic("s1", "kin", "1D Horizontal Motion", 1),
    Subtopic("s2", "kin", "1D Vertical Motion", 2),
    Subtopic("s3", "kin", "Projectile Motion", 3),
    Subtopic("s4", "kin", "Relative Motion", 4)
)

private val previewQuestions = (1..75).map { index ->
    Question(
        questionId = "q$index",
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

@Preview(showBackground = true, widthDp = 1400, heightDp = 900)
@Composable
fun ChapterScreenPreview() {
    VaultTheme {
        Row(modifier = Modifier.fillMaxSize()) {

            // LEFT: Subtopic sidebar
            SubtopicSidebar(
                chapterName = "Kinematics",
                subtopics = previewSubtopics,
                selectedSubtopicId = "s1",
                onSubtopicClick = {},
                onBack = {}
            )

            // RIGHT: Question area with questions visible
            QuestionArea(
                selectedSubtopic = previewSubtopics.first(),
                questions = previewQuestions
            )
        }
    }
}



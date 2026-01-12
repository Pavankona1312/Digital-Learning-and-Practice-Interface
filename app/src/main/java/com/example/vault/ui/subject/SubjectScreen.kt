package com.example.vault.ui.subject

import Chapter
import ChapterGroup
import ChapterGroupUiModel
import ChapterUiModel
import Progress
import Subtopic
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vault.ui.theme.VaultTheme

@Composable
fun SidebarButton(
    text: String,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (selected) Color(0xFFEAF2FF)
                else Color.Transparent
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Text(
            text = text,
            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal
        )
    }
}


@Composable
fun SubjectScreen(
    subjectId: String,
    userId: String,
    onBack: () -> Unit,
    onChapterClick: (String) -> Unit,
    viewModel: SubjectViewModel = viewModel()
) {
    LaunchedEffect(subjectId) {
        viewModel.loadSubject(subjectId, userId)
    }

    val groups = viewModel.groups

    var selectedGroupId by remember { mutableStateOf<String?>(null) }
    var selectedChapterId by remember { mutableStateOf<String?>(null) }

    Row(modifier = Modifier.fillMaxSize()) {
        TopicSidebar(
            groups = groups,
            selectedGroupId = selectedGroupId,
            onGroupClick = {
                selectedGroupId = it
                selectedChapterId = null
            },
            onBack
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            val selectedGroup =
                groups.find { it.group.id == selectedGroupId }

            when {
                selectedGroup == null -> {
                    EmptyState("Select a topic from the sidebar")
                }
                selectedChapterId == null -> {
                    ChapterList(
                        group = selectedGroup,
                        onChapterClick = onChapterClick
                    )
                }
                else -> {
                    Text("Chapter selected (next screen)")
                }
            }
        }
    }
}

@Composable
fun TopicSidebar(
    groups: List<ChapterGroupUiModel>,
    selectedGroupId: String?,
    onGroupClick: (String) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(300.dp)
            .fillMaxHeight()
            .background(Color(0xFFF8FAFC))
            .border(
                width = 1.dp,
                color = Color(0xFFE5E7EB)
            )
            .padding(16.dp)
    ) {
        SidebarButton(
            text = "← Back to Home",
            onClick = onBack
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFEAF2FF)),
                contentAlignment = Alignment.Center
            ) {
                Text("📘")
            }

            Spacer(Modifier.width(12.dp))

            Column {
                Text(
                    text = "Physics",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
                Text(
                    text = "${groups.size} topics",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
        Spacer(Modifier.height(24.dp))
        HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
        Spacer(Modifier.height(16.dp))

        groups.forEach { groupUi ->
            SidebarButton(
                text = groupUi.group.name,
                selected = groupUi.group.id == selectedGroupId,
                onClick = { onGroupClick(groupUi.group.id) }
            )
            Spacer(Modifier.height(6.dp))
        }

    }
}
@Composable
fun OverallProgressCard(
    solved: Int,
    attempted: Int,
    unattempted: Int,
    total: Int
) {
    val progress = if (total == 0) 0f else solved.toFloat() / total

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFF1F7FF))
            .padding(24.dp)
    ) {

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Overall Progress", fontWeight = FontWeight.Medium)
            Text("${(progress * 100).toInt()}%", fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(12.dp))

        LinearProgressIndicator(
            progress = {progress},
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = Color(0xFF2563EB),
            trackColor = Color(0xFFD6E4FF)
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "$solved solved • $attempted attempted • $unattempted unattempted • $total total",
            color = Color.Gray,
            fontSize = 12.sp
        )
    }
}

@Composable
fun ChapterList(
    group: ChapterGroupUiModel,
    onChapterClick: (String) -> Unit
) {
    Column(modifier = Modifier.padding(24.dp)) {

        Text(
            text = "Physics • ${group.group.name}",
            color = Color.Gray,
            fontSize = 12.sp
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = group.group.name,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(20.dp))

        OverallProgressCard(
            solved = 105,
            attempted = 115,
            unattempted = 260,
            total = 480
        )

        Spacer(Modifier.height(32.dp))

        Text("Chapters", fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(16.dp))

        group.chapters.forEach { chapterUi ->
            ChapterCard(chapterUi, onChapterClick)
            Spacer(Modifier.height(16.dp))
        }
    }
}


@Composable
fun ChapterCard(
    chapterUi: ChapterUiModel,
    onClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable { onClick(chapterUi.chapter.id) }
            .padding(20.dp)
    ) {
        Column {
            Text(
                text = chapterUi.chapter.name,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatChip("Solved", chapterUi.progress?.done ?: 0, Color(0xFFE8F5E9))
                StatChip("Attempted", 10, Color(0xFFFFF8E1))
                StatChip("Unattempted", 5, Color(0xFFF5F5F5))
                StatChip("Total", chapterUi.progress?.total ?: 0, Color(0xFFE3F2FD))
            }
        }
    }
}

@Composable
fun EmptyState(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.Gray,
            fontSize = 16.sp
        )
    }

}

@Composable
fun StatChip(
    label: String,
    value: Int,
    backgroundColor: Color
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .widthIn(min = 100.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.DarkGray
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = value.toString(),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private val previewChapterUi = ChapterUiModel(
    chapter = Chapter(
        id = "kin",
        subjectId = "phy",
        name = "Kinematics",
        order = 1
    ),
    subtopics = emptyList(),
    progress = Progress(
        done = 20,
        total = 60
    )
)

private val previewGroupUi = ChapterGroupUiModel(
    group = ChapterGroup(
        id = "mech",
        subjectId = "phy",
        name = "Mechanics",
        order = 1
    ),
    chapters = listOf(
        previewChapterUi,
        previewChapterUi.copy(
            chapter = previewChapterUi.chapter.copy(
                id = "bm",
                name = "Basic Mathematics"
            ),
            progress = Progress(35, 50)
        )
    )
)




@Preview(showBackground = true, widthDp = 1400, heightDp = 900)
@Composable
fun VaultPreview() {
    VaultTheme {
        Row(modifier = Modifier.fillMaxSize()) {
            TopicSidebar(
                groups = listOf(previewGroupUi),
                selectedGroupId = "mech",
                onGroupClick = {},
                onBack = {}
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                ChapterList(
                    group = previewGroupUi,
                    onChapterClick = {}
                )
            }
        }
    }
}




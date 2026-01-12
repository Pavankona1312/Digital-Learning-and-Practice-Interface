import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import kotlin.math.roundToInt

@Composable
fun QuestionWritingArea(
    viewModel: WritingViewModel,
    questionBitmap: Bitmap
) {
    val scrollState = rememberScrollState()
    var showScrollbar by remember { mutableStateOf(false) }
    val inkViewRef = remember { mutableStateOf<InkSurfaceView?>(null) }

    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.value }
            .collect {
                inkViewRef.value?.onScrollChanged()
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5000.dp),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 2.dp
            ) {
                AndroidView(
                    factory = { context ->
                        InkSurfaceView.create(
                            context = context,
                            viewModel = viewModel,
                            questionBitmap = questionBitmap,
                            getScrollOffset = { scrollState.value.toFloat() }
                        )
                    }
                )
            }

        }

        AnimatedVisibility(
            visible = showScrollbar,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            VerticalScrollbar(
                scrollState = scrollState,
                modifier = Modifier.padding(end = 4.dp)
            )
        }

        FloatingWritingToolbox(
            viewModel = viewModel,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .zIndex(10f)
        )
    }
}

@Composable
fun VerticalScrollbar(
    scrollState: ScrollState,
    modifier: Modifier = Modifier
) {
    val proportion by remember(scrollState) {
        derivedStateOf {
            scrollState.value.toFloat() /
                    scrollState.maxValue.coerceAtLeast(1)
        }
    }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .offset {
                    IntOffset(
                        0,
                        (proportion * scrollState.maxValue).toInt()
                    )
                }
                .background(
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    RoundedCornerShape(2.dp)
                )
        )
    }
}

@Composable
fun FloatingWritingToolbox(
    viewModel: WritingViewModel,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(true) }
    var offsetX by remember { mutableFloatStateOf(16f) }
    var offsetY by remember { mutableFloatStateOf(200f) }

    Surface(
        modifier = modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, drag ->
                    change.consume()
                    offsetX += drag.x
                    offsetY += drag.y
                }
            },
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 6.dp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {

            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    if (expanded) Icons.Default.ChevronLeft
                    else Icons.Default.ChevronRight,
                    contentDescription = null
                )
            }

            if (!expanded) return@Column

            Spacer(Modifier.height(8.dp))

            ToolIcon(Icons.AutoMirrored.Filled.Undo) { viewModel.undo() }
            ToolIcon(Icons.AutoMirrored.Filled.Redo) { viewModel.redo() }

            Spacer(Modifier.height(12.dp))

            ToolIcon(
                icon = Icons.Default.AutoFixHigh,
                selected = viewModel.eraserMode
            ) {
                viewModel.setEraser(!viewModel.eraserMode)
            }

            Spacer(Modifier.height(12.dp))

            ColorPalette(
                selectedColor = viewModel.selectedColor,
                onColorSelected = {
                    viewModel.setColor(it)
                    viewModel.setEraser(false)
                }
            )
        }
    }
}

@Composable
private fun ToolIcon(
    icon: ImageVector,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.1f else 1f,
        label = "tool-scale"
    )

    Surface(
        shape = CircleShape,
        tonalElevation = if (selected) 6.dp else 0.dp,
        shadowElevation = if (selected) 8.dp else 0.dp,
        color = if (selected)
            MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surface
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(48.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (selected)
                    MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}



@Composable
fun ColorPalette(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit
) {
    val colors = listOf(
        Color.Black,
        Color.Blue,
        Color.Red,
        Color.Green,
        Color(0xFF8E24AA),
        Color(0xFFFF9800)
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        colors.forEach { color ->
            val isSelected = color == selectedColor

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .padding(4.dp)
                    .background(color, CircleShape)
                    .border(
                        width = if (isSelected) 3.dp else 1.dp,
                        color = if (isSelected)
                            MaterialTheme.colorScheme.primary
                        else Color.LightGray,
                        shape = CircleShape
                    )
                    .clickable { onColorSelected(color) }
            )
        }
    }
}


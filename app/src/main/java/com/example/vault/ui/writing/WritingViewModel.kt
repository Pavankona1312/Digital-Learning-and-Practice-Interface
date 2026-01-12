import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import kotlin.math.hypot

data class Stroke(
    val normalizedPoints: List<Offset>, // 0f–1f
    val color: Color,
    val widthRatio: Float
)

class WritingViewModel : ViewModel() {

    // --- UI-observable state ---
    var selectedColor by mutableStateOf(Color.Black)
        private set

    var eraserMode by mutableStateOf(false)
        private set

    // --- non-UI drawing state (unchanged) ---
    private val strokes = mutableListOf<Stroke>()
    private val redoStack = mutableListOf<Stroke>()
    private val currentPoints = mutableListOf<Offset>()

    var invalidateCallback: (() -> Unit)? = null

    var selectedWidth: Float = 6f
    var canvasWidth: Float = 1f
    var canvasHeight: Float = 1f

    fun setEraser(enabled: Boolean) {
        eraserMode = enabled
    }

    fun setColor(color: Color) {
        selectedColor = color
        eraserMode = false
    }

    /* ---------- Drawing lifecycle ---------- */

    fun startStroke(point: Offset) {
        currentPoints.clear()
        currentPoints.add(point)
    }

    fun addPoint(point: Offset) {
        currentPoints.add(point)
    }

    fun endStroke() {
        if (currentPoints.isEmpty()) return

        val normalized = currentPoints.map {
            Offset(it.x / canvasWidth, it.y / canvasHeight)
        }

        strokes.add(
            Stroke(
                normalizedPoints = normalized,
                color = selectedColor,
                widthRatio = selectedWidth / canvasWidth
            )
        )

        currentPoints.clear()
        redoStack.clear()
        invalidateCallback?.invoke()
    }

    /* ---------- Access for drawing ---------- */

    fun getStrokes(): List<Stroke> = strokes

    /* ---------- Undo / Redo ---------- */

    fun undo() {
        if (strokes.isNotEmpty()) {
            redoStack.add(strokes.removeAt(strokes.lastIndex))
            invalidateCallback?.invoke()
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            strokes.add(redoStack.removeAt(redoStack.lastIndex))
            invalidateCallback?.invoke()
        }
    }

    /* ---------- Eraser ---------- */

    fun eraseAt(point: Offset) {

        val erasePoint = Offset(
            point.x / canvasWidth,
            point.y / canvasHeight
        )

        val iterator = strokes.listIterator()
        val newStrokes = mutableListOf<Stroke>()

        while (iterator.hasNext()) {
            val stroke = iterator.next()
            val radius = stroke.widthRatio * 2.5f

            val segments = splitStroke(
                stroke.normalizedPoints,
                erasePoint,
                radius
            )

            if (segments.size == 1 &&
                segments[0].size == stroke.normalizedPoints.size
            ) continue

            iterator.remove()

            segments.forEach { segment ->
                if (segment.size > 1) {
                    newStrokes.add(
                        Stroke(
                            normalizedPoints = segment,
                            color = stroke.color,
                            widthRatio = stroke.widthRatio
                        )
                    )
                }
            }

            redoStack.clear()
            invalidateCallback?.invoke()
            break
        }

        strokes.addAll(newStrokes)
    }

    private fun splitStroke(
        points: List<Offset>,
        erasePoint: Offset,
        radius: Float
    ): List<List<Offset>> {

        val result = mutableListOf<MutableList<Offset>>()
        var current = mutableListOf<Offset>()

        for (p in points) {
            if (distance(p, erasePoint) <= radius) {
                if (current.isNotEmpty()) {
                    result.add(current)
                    current = mutableListOf()
                }
            } else {
                current.add(p)
            }
        }

        if (current.isNotEmpty()) result.add(current)
        return result
    }

    private fun distance(a: Offset, b: Offset): Float =
        hypot(a.x - b.x, a.y - b.y)
}

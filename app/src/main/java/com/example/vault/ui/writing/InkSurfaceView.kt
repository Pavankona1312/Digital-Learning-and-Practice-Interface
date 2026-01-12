import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.toArgb
import kotlin.math.abs

class InkSurfaceView private constructor(
    context: Context
) : SurfaceView(context), SurfaceHolder.Callback {

    /* ---------------- Dependencies ---------------- */

    private lateinit var viewModel: WritingViewModel
    private lateinit var questionBitmap: Bitmap
    private lateinit var getScrollOffset: () -> Float

    /* ---------------- Layout ---------------- */

    private val questionPaddingPx = 44f
    private var questionHeightPx = 0
    private var questionDrawLeft = 0f

    /* ---------------- Factory ---------------- */

    companion object {
        fun create(
            context: Context,
            viewModel: WritingViewModel,
            questionBitmap: Bitmap,
            getScrollOffset: () -> Float
        ): InkSurfaceView {
            return InkSurfaceView(context).apply {
                this.viewModel = viewModel
                this.questionBitmap = questionBitmap
                this.questionHeightPx = questionBitmap.height
                this.getScrollOffset = getScrollOffset
                initView()
            }
        }
    }

    private fun initView() {
        holder.addCallback(this)
        holder.setFormat(PixelFormat.TRANSLUCENT)
        setZOrderOnTop(true)
        setBackgroundColor(android.graphics.Color.WHITE)
        isFocusable = false
        isFocusableInTouchMode = false
        // ViewModel-triggered redraws (undo / redo / erase)
        viewModel.invalidateCallback = { drawNow() }
    }

    /* ---------------- Touch State ---------------- */

    private var currentPath: Path? = null
    private var lastX = 0f
    private var lastDocY = 0f

    /* ---------------- Surface callbacks ---------------- */

    override fun surfaceCreated(holder: SurfaceHolder) = drawNow()
    override fun surfaceChanged(holder: SurfaceHolder, f: Int, w: Int, h: Int) = drawNow()
    override fun surfaceDestroyed(holder: SurfaceHolder) {}

    /* ---------------- Public redraw hook ---------------- */

    /** MUST be called when scroll position changes */
    fun onScrollChanged() {
        drawNow()
    }

    /* ---------------- Touch handling ---------------- */

    override fun onTouchEvent(event: MotionEvent): Boolean {

        val tool = event.getToolType(0)
        if (
            tool == MotionEvent.TOOL_TYPE_FINGER ||
            tool == MotionEvent.TOOL_TYPE_MOUSE ||
            tool == MotionEvent.TOOL_TYPE_UNKNOWN
        ) {
            parent?.requestDisallowInterceptTouchEvent(false)
            return false
        }

        parent?.requestDisallowInterceptTouchEvent(true)

        val viewX = event.x
        val viewY = event.y
        val scroll = getScrollOffset()

        // document-space Y
        val docY = viewY + scroll

        // 🚫 block writing on question image
        if (docY < questionHeightPx + questionPaddingPx) return false

        when (event.actionMasked) {

            MotionEvent.ACTION_DOWN -> {
                currentPath = Path().apply { moveTo(viewX, viewY) }
                lastX = viewX
                lastDocY = docY
                viewModel.startStroke(Offset(viewX, docY))
                return true
            }

            MotionEvent.ACTION_MOVE -> {

                if (viewModel.eraserMode) {
                    viewModel.eraseAt(Offset(viewX, docY))
                    drawNow()
                    return true
                }

                val dx = abs(viewX - lastX)
                val dy = abs(docY - lastDocY)

                if (dx >= 2f || dy >= 2f) {
                    currentPath?.quadTo(
                        lastX,
                        lastDocY - scroll,
                        (viewX + lastX) / 2f,
                        ((docY + lastDocY) / 2f) - scroll
                    )

                    lastX = viewX
                    lastDocY = docY
                    viewModel.addPoint(Offset(viewX, docY))
                    drawNow()
                }
                return true
            }

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                parent?.requestDisallowInterceptTouchEvent(false)
                viewModel.endStroke()
                currentPath = null
                drawNow()
                performClick()
                return true
            }
        }
        return false
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    /* ---------------- Drawing ---------------- */

    private fun drawNow() {
        val canvas = holder.lockCanvas() ?: return
        val scroll = getScrollOffset()

        val width = canvas.width.toFloat()
        val height = canvas.height.toFloat()

        viewModel.canvasWidth = width
        viewModel.canvasHeight = height

        // Clear frame
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR)

        // 🖼 Question image (draw ONLY if visible)
        questionDrawLeft = (width - questionBitmap.width) / 2f
        val top = questionPaddingPx - scroll
        val bottom = top + questionBitmap.height

        if (bottom > 0 && top < height) {
            canvas.drawBitmap(
                questionBitmap,
                questionDrawLeft,
                top,
                null
            )
        }
        val dividerPaint = Paint().apply {
            color = android.graphics.Color.LTGRAY
            strokeWidth = 2f
        }

        canvas.drawLine(
            questionDrawLeft,
            bottom + 16f,
            questionDrawLeft + questionBitmap.width,
            bottom + 16f,
            dividerPaint
        )

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }

        // 🖊 Saved strokes
        for (stroke in viewModel.getStrokes()) {
            paint.color = stroke.color.toArgb()
            paint.strokeWidth = stroke.widthRatio * width

            val path = Path()
            stroke.normalizedPoints.forEachIndexed { i, p ->
                val x = p.x * width
                val y = p.y * height - scroll
                if (i == 0) path.moveTo(x, y)
                else path.lineTo(x, y)
            }
            canvas.drawPath(path, paint)
        }

        // 🖊 Current stroke
        currentPath?.let {
            paint.color = viewModel.selectedColor.toArgb()
            paint.strokeWidth = viewModel.selectedWidth
            canvas.drawPath(it, paint)
        }

        holder.unlockCanvasAndPost(canvas)
    }
}

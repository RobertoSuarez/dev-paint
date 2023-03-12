package com.example.dev_paint

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class PaintView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var paint = Paint()
    private val path = Path()

    private val paths = mutableListOf<Path>()
    private val strokeWidths = mutableListOf<Float>()
    private var currentPath: Path? = null
    private var currentColor = Color.BLACK


    init {
        paint.isAntiAlias = true
        paint.color = currentColor
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = 10f

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for ((index, path) in paths.withIndex()) {
            paint.strokeWidth = strokeWidths[index]
            canvas.drawPath(path, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                currentPath = Path()
                currentPath?.moveTo(event.x, event.y)
                paths.add(currentPath!!)
                strokeWidths.add(paint.strokeWidth)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                currentPath?.lineTo(event.x, event.y)
                //path.lineTo(x, y)
                invalidate()
            }

            else -> return false
        }

        return true
    }

    fun setStrokeWidth(strokeWidth: Float) {
        this.paint.strokeWidth = strokeWidth
    }

    fun clear() {
        for (path in paths) {
            path.reset()
        }
        invalidate()
    }

}
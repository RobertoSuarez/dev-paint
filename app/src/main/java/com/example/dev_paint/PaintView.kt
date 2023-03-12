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
    private var currentPath: Path? = null
    private var currentColor = Color.BLACK
    private var currentStrokeWidth = 10f // ancho del trazo por defecto


    init {
        paint.isAntiAlias = true
        paint.color = currentColor
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = currentStrokeWidth


    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //canvas.drawPath(path, paint)
        for ((index, path) in paths.withIndex()) {

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

                //path.moveTo(x, y)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                currentPath?.lineTo(event.x, event.y)
                //path.lineTo(x, y)
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                paths.add(path)
                currentPath = Path()
            }

            else -> return false
        }

        return true
    }

    fun setStrokeWidth(strokeWidth: Float) {
        currentStrokeWidth = strokeWidth
        val newPaint = Paint(paint) // crea un nuevo objeto Paint a partir del anterior
        newPaint.strokeWidth = currentStrokeWidth
        paint = newPaint // reemplaza el objeto Paint anterior con el nuevo
    }

    fun clear() {
        for (path in paths) {
            path.reset()
        }
        invalidate()
    }

}
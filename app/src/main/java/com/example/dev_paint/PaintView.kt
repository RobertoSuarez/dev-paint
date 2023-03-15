package com.example.dev_paint

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.provider.MediaStore
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast

class PaintView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    var paint = Paint()
    private val path = Path()

    private val paths = mutableListOf<Path>()
    private val strokeWidths = mutableListOf<Float>()
    private val colores = mutableListOf<Int>()

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
            // establecemos el ancho y color de cada trazo o path
            paint.strokeWidth = strokeWidths[index]
            paint.color = colores[index]
            canvas.drawPath(path, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                currentPath = Path() // creamos un nuevo path, cada vez que el usuario toque la pantall
                currentPath?.moveTo(event.x, event.y)
                paths.add(currentPath!!)
                strokeWidths.add(paint.strokeWidth)
                colores.add(paint.color)
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

    fun setColor(color: Int) {
        this.paint.color = color
    }

    fun clear() {
        for (path in paths) {
            path.reset()
        }
        invalidate()
    }

    fun viewToBitmap(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    fun saveToGallery() {
        // Convierte el PaintView en un Bitmap
        val bitmap = viewToBitmap(this)

        // Comprueba si el Bitmap contiene algún pixel que no sea negro
        var hasNonBlackPixels = false
        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {
                if (bitmap.getPixel(x, y) != Color.BLACK) {
                    hasNonBlackPixels = true
                    break
                }
            }
            if (hasNonBlackPixels) break
        }

        // Si el Bitmap contiene algún pixel que no sea negro, guarda la imagen en la galería
        if (hasNonBlackPixels) {
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "MyImage")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            }

            val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

            if (uri != null) {
                val outputStream = context.contentResolver.openOutputStream(uri)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                outputStream?.close()
            }
        }
    }

    fun captureScreenAndSaveToGallery() {
        // Crea un objeto Bitmap del tamaño de la pantalla
        val width = Resources.getSystem().displayMetrics.widthPixels
        val height = Resources.getSystem().displayMetrics.heightPixels
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // Crea un objeto Canvas para dibujar en el Bitmap
        val canvas = Canvas(bitmap)

        // Dibuja la pantalla en el Bitmap
        val rootView = (context as Activity).window.decorView
        rootView.draw(canvas)
        //this.draw(canvas)
        val croppedBitmap = Bitmap.createBitmap(bitmap, 0, 400, width, height -500 )
        // Guarda el Bitmap en la galería
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "MyScreenshot")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }

        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        if (uri != null) {
            val outputStream = context.contentResolver.openOutputStream(uri)
            croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream?.close()
        }

        Toast.makeText(context, "Se guardo la imagen en galeria", Toast.LENGTH_SHORT).show()
    }

    fun takeScreenshot(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        draw(canvas)
        return bitmap
    }

}
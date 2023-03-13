package com.example.dev_paint

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar

class Lienzo : AppCompatActivity() {

    private lateinit var paintView: PaintView
    private lateinit var clearBtn : Button
    private lateinit var seekBar : SeekBar
    private lateinit var imageView : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lienzo)

        paintView = findViewById(R.id.paintView)
        clearBtn = findViewById<Button>(R.id.btnClear)
        seekBar = findViewById<SeekBar>(R.id.brushSize)
        imageView = findViewById<ImageView>(R.id.btn_save)
        seekBar.setProgress(10)

        

        clearBtn.setOnClickListener {
            paintView.clear()
        }

        imageView.setOnClickListener {
            println("Guarde imagen")
        }

        // Establecer un OnSeekBarChangeListener en el SeekBar
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            var progress = 0 // Variable global para almacenar el valor de progreso del SeekBar

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Actualizar la variable global con el nuevo valor de progreso
                this.progress = progress
                println(progress)
                //paintView.paint.strokeWidth = progress.toFloat()
                paintView.setStrokeWidth(progress.toFloat())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // No es necesario implementar este método para obtener el valor del SeekBar cada vez que cambie
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // No es necesario implementar este método para obtener el valor del SeekBar cada vez que cambie
            }
        })

    }

}
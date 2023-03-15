package com.example.dev_paint

import android.app.Dialog
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Toast
import com.google.android.material.appbar.MaterialToolbar
import com.maxkeppeler.sheets.color.ColorSheet

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DibujarFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DibujarFragment : Fragment()  {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var paintView: PaintView
    private lateinit var btnColor : Button
    private lateinit var seekBar : SeekBar
    private lateinit var imageView : ImageView
    private lateinit var toolbar : MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dibujar, container, false)

        paintView = view.findViewById(R.id.paintView)
        btnColor = view.findViewById<Button>(R.id.btn_select_color)
        seekBar = view.findViewById<SeekBar>(R.id.brushSize)
        seekBar.setProgress(10)

        btnColor.setOnClickListener {
            println("Seleccionar un color")
            ColorSheet().show(view.context) {
                title("Selecciona un color")
                onPositive { color ->
                    println(color)
                    paintView.paint.color = color
                }
            }
        }

        toolbar = view.findViewById(R.id.appbarDibujar)

        toolbar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.action_clean -> {
                    paintView.clear()
                    true
                }

                R.id.action_guardar -> {
                    println("Guardar")
                    paintView.captureScreenAndSaveToGallery()
                    true
                }
                else -> false
            }
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

        // Inflate the layout for this fragment
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DibujarFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DibujarFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}
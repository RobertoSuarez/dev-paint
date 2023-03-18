package com.example.dev_paint

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.dev_paint.models.User
import com.google.android.material.textview.MaterialTextView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ConfiguracionesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ConfiguracionesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_configuraciones, container, false)

        val nombre = view.findViewById<MaterialTextView>(R.id.nombre)
        val emailText = view.findViewById<MaterialTextView>(R.id.correo)
        val rol = view.findViewById<MaterialTextView>(R.id.rol)

        User().getCurrentUser()
            .addOnSuccessListener {user ->
                nombre.text = user.getString("nombre")
                emailText.text = user.getString("correo")
                rol.text = user.getString("rol")
            }

        val button = view.findViewById<Button>(R.id.boton_cerrar_sesion)

        button.setOnClickListener {
            startActivity(Intent(view.context, Login::class.java))
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ConfiguracionesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ConfiguracionesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
package com.example.dev_paint

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.example.dev_paint.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Registrar : AppCompatActivity() {

    private lateinit var Auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar)

        val db = FirebaseFirestore.getInstance()
        val refUsuario = db.collection("usuarios")
        val btn_registrar = findViewById<Button>(R.id.signupbtn)
        val input_nombre = findViewById<EditText>(R.id.nombre)
        val input_correo = findViewById<EditText>(R.id.username)
        val input_password = findViewById<EditText>(R.id.password)

        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.GONE

        Auth = FirebaseAuth.getInstance()
        btn_registrar.setOnClickListener {
            progressBar.visibility = View.VISIBLE // el spinner se puedo ver
            Auth.createUserWithEmailAndPassword(input_correo.text.toString(),input_password.text.toString())
                .addOnCompleteListener{task ->
                    if(task.isSuccessful){
                        Toast.makeText(
                            this,
                            "Usuario registrado correctamente",
                            Toast.LENGTH_LONG
                        ).show()
                        val user = task.result?.user
                        val uid= user?.uid
                        val usuario = User(
                            nombre = input_nombre.text.toString(),
                            apellido = input_correo.text.toString(),
                            rol = "Estudiante")

                        refUsuario
                            .document(uid.toString())
                            .set(usuario)
                            .addOnSuccessListener { documentReference ->
                                Log.d("Registrar", "El usuario fue insertado con ID: ${uid.toString()}")

                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                            }
                            .addOnFailureListener { e ->
                                Log.w("Registrar", "Error al insertar documento", e)
                            }
                            .addOnCompleteListener{ task ->
                                progressBar.visibility = View.GONE
                            }
                    }
                    else(
                            Toast.makeText(
                                this,
                                "Error al registrar usuario: ${task.exception?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                            )
                }
        }
    }
}
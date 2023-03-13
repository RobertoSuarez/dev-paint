package com.example.dev_paint

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val email = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)

        email.setText("tas.apo@gmail.com")
        password.setText("facil2020")

        val btnIniciar: Button = findViewById(R.id.login)
        val btnRegistrar: Button = findViewById(R.id.btnregistrar)

        btnRegistrar.setOnClickListener {
            val intent = Intent(this, Registrar::class.java)
            startActivity(intent)
        }

        btnIniciar.setOnClickListener {
            if (email.text.toString().isEmpty() || password.text.toString().isEmpty()) {
                Toast.makeText(this, "Ingresa el correo y contraseña", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(email.text.toString(), password.text.toString())
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // El inicio de sesión fue exitoso, puedes redirigir al usuario a la actividad principal
                    Toast.makeText(this, "El inicio de sesión fue exitoso", Toast.LENGTH_LONG).show()
                    Log.d("LOGIN", "El inicio de sesión fue exitoso")
                    //startActivity(Intent(this, Lienzo::class.java))
                    // abrimos el menu principal de la app
                    startActivity(Intent(this, LayoutPaint::class.java))

                } else {
                    // Si el inicio de sesión falló, muestra un mensaje al usuario.
                    Toast.makeText(this, "Inicio de sesión falló", Toast.LENGTH_LONG).show()
                    Log.d("LOGIN", "Inicio de sesión falló")
                }
            }
    }
}
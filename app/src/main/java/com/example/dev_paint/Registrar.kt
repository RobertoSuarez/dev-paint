package com.example.dev_paint

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.example.dev_paint.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Registrar : AppCompatActivity() {

    private lateinit var Auth: FirebaseAuth

    // Controles de la activity
    lateinit var btn_registrar: Button
    lateinit var input_nombre : EditText
    lateinit var input_correo : EditText
    lateinit var input_password : EditText
    lateinit var roleGroup : RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar)

        val db = FirebaseFirestore.getInstance()
        val refUsuario = db.collection("usuarios")
        btn_registrar = findViewById<Button>(R.id.signupbtn)
        input_nombre = findViewById<EditText>(R.id.nombre)
        input_correo = findViewById<EditText>(R.id.username)
        input_password = findViewById<EditText>(R.id.password)
        roleGroup = findViewById<RadioGroup>(R.id.roles)

        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.GONE

        Auth = FirebaseAuth.getInstance()

        btn_registrar.setOnClickListener {

            if (roleGroup.checkedRadioButtonId == null) {
                Toast.makeText(this, "Se requiere de un rol", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val radioRole = findViewById<RadioButton>(roleGroup.checkedRadioButtonId)


            progressBar.visibility = View.VISIBLE // el spinner se puedo ver
            // crea la cuenta en el servicio auth de firebase
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
                            rol = radioRole.text.toString())

                        refUsuario
                            .document(uid!!)
                            .set(usuario)
                            .addOnSuccessListener { documentReference ->
                                Log.d("Registrar", "El usuario fue insertado con ID: ${uid.toString()}")
                                val intent = Intent(this, Login::class.java)
                                startActivity(intent)
                            }
                            .addOnFailureListener { e ->
                                Log.w("Registrar", "Error al insertar documento", e)
                            }
                            .addOnCompleteListener{ task ->
                                progressBar.visibility = View.GONE
                                clearFormRegistro()
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


    fun clearFormRegistro() {
        println("Clear form")
        input_nombre.setText("")
        input_correo.setText("")
        input_password.setText("")
        val inputrol = findViewById<RadioButton>(roleGroup.checkedRadioButtonId)
        inputrol.isChecked = false
    }
}
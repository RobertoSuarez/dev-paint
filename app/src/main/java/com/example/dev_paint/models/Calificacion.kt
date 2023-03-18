package com.example.dev_paint.models

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class Calificacion(
    val uid: String = "",
    val name: String = "",
    val img1: String = "",
    val img2: String = "",
    val rating: Double = 0.0,
    val fecha: Date? = null // fecha de creación de documento
) {
    private val db = FirebaseFirestore.getInstance()

    fun obtenerCalificacionesPorUid(uid: String, onComplete: (List<Calificacion>) -> Unit) {
        db.collection("calificaciones")
            .whereEqualTo("uid", uid)
            .get()
            .addOnSuccessListener { documents ->
                val calificaciones = mutableListOf<Calificacion>()
                for (document in documents) {
                    val calificacion = document.toObject(Calificacion::class.java)
                    calificaciones.add(calificacion)
                }
                onComplete(calificaciones)
            }
            .addOnFailureListener { exception ->
                println("Error: " + exception.toString())
                onComplete(emptyList())
            }
    }

    fun calificaciones(onComplete: (List<Calificacion>) -> Unit) {
        db.collection("calificaciones")
            .get()
            .addOnSuccessListener { documents ->
                val calificaciones = mutableListOf<Calificacion>()
                for (document in documents) {
                    val calificacion = document.toObject(Calificacion::class.java)
                    calificaciones.add(calificacion)
                }
                onComplete(calificaciones)
            }
            .addOnFailureListener { exception ->
                println("Error: " + exception.toString())
                onComplete(emptyList())
            }
    }


}

package com.example.dev_paint.models

import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class Tarea(
    var id: String? = "",
    var urlImgen: String = "",
    var urlImgenEstudiante: String = "",
    var uidEstudiante: String = "",
    var nombreEstudiante: String = "",
    var uidProfesor: String? = "",
    var nombreProfesor: String = "",
    var estado: String = "",
    var fecha: Date = Date(),
    var calificacion: Int = 0
) {
    private val db = FirebaseFirestore.getInstance()
    private val coleccion = db.collection("tareas")

    fun guardarTarea() {
        coleccion.add(this)
            .addOnSuccessListener {
                println("Tarea guardada con éxito. ID: ${it.id}")
            }
            .addOnFailureListener {
                println("Error al guardar tarea: $it")
            }
    }

    fun obtenerTareas(callback: (List<Tarea>) -> Unit) {
        coleccion.get()
            .addOnSuccessListener { querySnapshot ->
                val tareas = mutableListOf<Tarea>()
                for (document in querySnapshot.documents) {
                    val tarea = document.toObject(Tarea::class.java)
                    tarea?.id = document.id
                    tarea?.let { tareas.add(it) }
                }
                callback(tareas)
            }
            .addOnFailureListener {
                println("Error al obtener tareas: $it")
            }
    }

    fun obtenerTareasPendientesDeEstudiante(uid: String, callback: (List<Tarea>) -> Unit) {
        coleccion.whereEqualTo("uidEstudiante", uid)
            .whereEqualTo("estado", "Pentiente")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val tareas = mutableListOf<Tarea>()
                for (document in querySnapshot.documents) {
                    val tarea = document.toObject(Tarea::class.java)
                    tarea?.id = document.id
                    tarea?.let { tareas.add(it) }
                }
                callback(tareas)
            }
            .addOnFailureListener {
                println("Error al obtener tareas pendientes del estudiante: $it")
            }
    }


    fun actualizarTarea(id: String, campos: Map<String, Any>) {
        coleccion.document(id).update(campos)
            .addOnSuccessListener {
                println("Tarea actualizada con éxito. ID: $id")
            }
            .addOnFailureListener {
                println("Error al actualizar tarea: $it")
            }
    }

    fun eliminarTarea(id: String) {
        coleccion.document(id).delete()
            .addOnSuccessListener {
                println("Tarea eliminada con éxito. ID: $id")
            }
            .addOnFailureListener {
                println("Error al eliminar tarea: $it")
            }
    }
}

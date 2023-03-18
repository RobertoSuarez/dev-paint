package com.example.dev_paint.models
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore


class User (
    @DocumentId val id: String? = null,
    val nombre: String? = null,
    val apellido: String? = null,
    val rol: String? = null
) {
    // Agrega la referencia a Firestore
    private val db = FirebaseFirestore.getInstance()
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    private val userRef = db.collection("usuarios").document(uid!!)

    // Agrega el método para obtener el documento de Firestore
    fun getCurrentUser(): Task<DocumentSnapshot> {
        return userRef.get()
    }
}
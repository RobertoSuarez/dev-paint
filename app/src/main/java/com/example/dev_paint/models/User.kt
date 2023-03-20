package com.example.dev_paint.models
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore


class User (
    var id: String? = null,
    var nombre: String? = null,
    var correo: String? = null,
    var rol: String? = null,
    var isSelected: Boolean = false
) {
    companion object {
        private const val COLLECTION_NAME = "usuarios"
        private val firestore by lazy { FirebaseFirestore.getInstance()
        }

        const val TAG = "User"

        fun create(user: User, onComplete: (DocumentReference) -> Unit) {
            firestore.collection(COLLECTION_NAME)
                .add(user)
                .addOnSuccessListener { documentReference ->
                    onComplete(documentReference)
                }
        }

        fun getCurrentUser(onComplete: (User?) -> Unit) {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val userId = user.uid
                read(userId, onComplete)
            } else {
                onComplete(null)
            }
        }

        fun read(id: String, onComplete: (User?) -> Unit) {
            firestore.collection(COLLECTION_NAME)
                .document(id)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val user = documentSnapshot.toObject(User::class.java)
                    user?.id = documentSnapshot.id
                    onComplete(user)
                }
        }

        fun update(user: User, onComplete: () -> Unit) {
            user.id?.let {
                firestore.collection(COLLECTION_NAME)
                    .document(it)
                    .set(user)
                    .addOnSuccessListener {
                        onComplete()
                    }
            }
        }

        fun delete(user: User, onComplete: () -> Unit) {
            user.id?.let {
                firestore.collection(COLLECTION_NAME)
                    .document(it)
                    .delete()
                    .addOnSuccessListener {
                        onComplete()
                    }
            }
        }

        fun getUsersByRole(role: String, onComplete: (List<User>?) -> Unit) {
            firestore.collection(COLLECTION_NAME)
                .whereEqualTo("rol", role)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val userList = mutableListOf<User>()
                    for (document in querySnapshot.documents) {
                        val user = document.toObject(User::class.java)
                        user?.id = document.id
                        if (user != null) {
                            userList.add(user)
                        }
                    }
                    onComplete(userList)
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error getting documents: ", exception)
                    onComplete(null)
                }
        }

    }
}
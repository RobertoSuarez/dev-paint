package com.example.dev_paint.models
import com.google.firebase.firestore.DocumentId


class User (
    @DocumentId val id: String? = null,
    val nombre: String? = null,
    val apellido: String? = null,
    val rol: String? = null
)
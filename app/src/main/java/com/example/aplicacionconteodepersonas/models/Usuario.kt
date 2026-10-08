package com.example.aplicacionconteodepersonas.models

/**
 * Modelo de datos del usuario almacenado en la colección "usuarios" de Firestore.
 */
data class Usuario(
    val uid: String = "",
    val email: String = "",
    val nombre: String = "",
    val fechaRegistro: String = ""
)

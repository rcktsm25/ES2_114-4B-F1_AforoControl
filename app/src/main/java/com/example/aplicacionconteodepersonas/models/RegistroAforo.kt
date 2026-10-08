package com.example.aplicacionconteodepersonas.models

import com.google.firebase.firestore.DocumentId

/**
 * Modelo de datos para representar el conteo de aforo de una zona o sala.
 * Cumple con los requerimientos de la evaluación:
 * - Al menos 3 campos (zona, cantidadPersonas, nivelAforo, fechaHora, uid).
 * - Campo numérico representativo de la medición del sensor IoT: [cantidadPersonas].
 * - ID del documento Firestore anotado con [@DocumentId].
 */
data class RegistroAforo(
    @DocumentId
    val id: String = "",
    val zona: String = "",
    val cantidadPersonas: Int = 0,
    val nivelAforo: String = "",
    val fechaHora: String = "",
    val uid: String = ""
)

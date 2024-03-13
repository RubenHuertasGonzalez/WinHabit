package com.institutvidreres.winhabit.tareas

import com.google.firebase.firestore.DocumentId


data class Tarea(
    val nombre: String? = null,
    val descripcion: String? = null,
    val dificultad: String? = null,
    val duracion: String? = null,
    val userId: String? = null
) {
}

package com.institutvidreres.winhabit.tareas

data class Tarea(
    val nombre: String,
    val descripcion: String,
    val dificultad: String?,
    val duracion: String?,
    val userId: String
)
package com.institutvidreres.winhabit.ui.logros

import com.institutvidreres.winhabit.R

data class LogrosItem(
    var tipo: String,
    var titulo: String,
    var descripcion: String,
    var imagenResource: Int,
    var cantidad: Int,
    var completado: Boolean = false,
    var reclamado: Boolean = false
) {
    private var isModified = true

    fun toggleImagen() {
        isModified = !isModified
        imagenResource = if (isModified) R.drawable.insignia_modified else R.drawable.insignia
    }
}

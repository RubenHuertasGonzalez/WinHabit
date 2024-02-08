package com.institutvidreres.winhabit.ui.logros

import com.institutvidreres.winhabit.R

data class LogrosItem(
    var titulo: String,
    var descripcion: String,
    var imagenResource: Int
) {
    private var isModified = true

    fun toggleImagen() {
        isModified = !isModified
        imagenResource = if (isModified) R.drawable.insignia_modified else R.drawable.insignia
    }
}

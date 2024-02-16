package com.institutvidreres.winhabit.ui.recompensas

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "RecompensaData")
data class Recompensa(

    @ColumnInfo(name = "nombre")
    var nombre: String,
    @ColumnInfo(name = "firebaseId")
    var firebaseId: Int,
    @ColumnInfo(name = "imagenResId")
    var imagenResId: Int,
    @ColumnInfo(name = "descripcion")
    var descripcion: String,
    @ColumnInfo(name = "precio")
    var precio: Int
) {
    @PrimaryKey(autoGenerate = true)
    var Id: Int? = null
}
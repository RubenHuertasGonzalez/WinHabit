package com.institutvidreres.winhabit.ui.recompensas

import android.content.Context
import androidx.lifecycle.ViewModel
import com.institutvidreres.winhabit.R
import com.institutvidreres.winhabit.repositori.RoomRepository

class RecompenasViewModel : ViewModel() {

    fun newRecompensa(context: Context, nombre: String, firebaseId: Int, imagenResId: Int, descripcion: String, precio: Int) {

        var recompensa = Recompensa(nombre, firebaseId, imagenResId, descripcion, precio)
        RoomRepository.insertRecompensa(context,recompensa)
    }

    val personajesList = listOf(
        Recompensa("Personaje", 6, R.drawable.payaso, "Payaso", 100),
        Recompensa("Personaje", 7, R.drawable.dracula, "Dracula", 100),
        Recompensa("Personaje", 8, R.drawable.genio, "Genio", 150),
        Recompensa("Personaje", 9, R.drawable.momia, "Momia", 200),
        Recompensa("Personaje", 10, R.drawable.orco, "Orco", 200),
        Recompensa("Personaje", 11, R.drawable.zombi, "Zombie", 250)
    )

    val personajesPremiumList = listOf(
        Recompensa("Personaje_Premium", 12, R.drawable.caballero, "Caballero", 300),
        Recompensa("Personaje_Premium", 13, R.drawable.rey, "Rey", 500),
        Recompensa("Personaje_Premium", 14, R.drawable.princesa, "Princesa", 500)
    )

    val bannersPerfil = listOf(
        Recompensa("Banner", 15, R.drawable.gradiante_azul, "Azulado", 120),
        Recompensa("Banner", 16, R.drawable.gradiante_purpura, "Gradiante Púrpura", 200),
        Recompensa("Banner", 17, R.drawable.gradiante_rojo, "Gradiante Carmesí", 300)
    )

    val bannersMulticolorPerfil = listOf(
        Recompensa("Banner_Multicolor", 18, R.drawable.gradiante_verde_blanco, "Pureza Verde", 300),
        Recompensa("Banner_Multicolor", 19, R.drawable.gradiante_azul_purpura, "Noche Neón", 300),
        Recompensa("Banner_Multicolor", 20, R.drawable.gradiante_azul_rojo, "Carmesí Azulado", 400),
        Recompensa("Banner_Multicolor", 21, R.drawable.gradiante_azul_purpura_rojo, "Fuego Violeta ¡TRICOLOR!", 500),
        Recompensa("Banner_Multicolor", 22, R.drawable.gradiante_dorado_negro, "Sombra Dorada", 1000)
    )
}

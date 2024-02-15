package com.institutvidreres.winhabit.ui.recompensas

import android.content.Context
import androidx.lifecycle.ViewModel
import com.institutvidreres.winhabit.R
import com.institutvidreres.winhabit.repositori.RoomRepository

class RecompenasViewModel : ViewModel() {

    fun newRecompensa(context: Context, nombre: String, imagenResId: Int, descripcion: String, precio: Int) {

        var recompensa = Recompensa(nombre, imagenResId, descripcion, precio)
        RoomRepository.insertRecompensa(context,recompensa)
    }

    val personajesList = listOf(
        Recompensa("Personaje", R.drawable.payaso, "Payaso", 100),
        Recompensa("Personaje", R.drawable.dracula, "Dracula", 100),
        Recompensa("Personaje", R.drawable.genio, "Genio", 150),
        Recompensa("Personaje", R.drawable.momia, "Momia", 200),
        Recompensa("Personaje", R.drawable.orco, "Orco", 200),
        Recompensa("Personaje", R.drawable.zombi, "Zombie", 250)
    )

    val personajesPremiumList = listOf(
        Recompensa("Personaje_Premium", R.drawable.caballero, "Caballero", 300),
        Recompensa("Personaje_Premium", R.drawable.rey, "Rey", 500),
        Recompensa("Personaje_Premium", R.drawable.princesa, "Princesa", 500)
    )

    val bannersPerfil = listOf(
        Recompensa("Banner", R.drawable.gradiante_azul, "Azulado", 120),
        Recompensa("Banner", R.drawable.gradiante_purpura, "Gradiante Púrpura", 200),
        Recompensa("Banner", R.drawable.gradiante_rojo, "Gradiante Carmesí", 300)
    )

    val bannersMulticolorPerfil = listOf(
        Recompensa("Banner_Multicolor", R.drawable.gradiante_verde_blanco, "Pureza Verde", 300),
        Recompensa("Banner_Multicolor", R.drawable.gradiante_azul_purpura, "Noche Neón", 300),
        Recompensa("Banner_Multicolor", R.drawable.gradiante_azul_rojo, "Carmesí Azulado", 400),
        Recompensa("Banner_Multicolor", R.drawable.gradiante_azul_purpura_rojo, "Fuego Violeta ¡TRICOLOR!", 500),
        Recompensa("Banner_Multicolor", R.drawable.gradiante_dorado_negro, "Sombra Dorada", 1000)
    )
}

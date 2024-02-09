package com.institutvidreres.winhabit.ui.recompensas

import androidx.lifecycle.ViewModel
import com.institutvidreres.winhabit.R

class RecompenasViewModel : ViewModel() {
    val personajesList = listOf(
        Recompensa("Dracula", R.drawable.dracula, "Dracula", 100),
        Recompensa("Genio", R.drawable.genio, "Genio", 150),
        Recompensa("Momia", R.drawable.momia, "Momia", 120),
        Recompensa("Orco", R.drawable.orco, "Orco", 200),
        Recompensa("Zombie", R.drawable.zombi, "Zombie", 230),
        Recompensa("Payaso", R.drawable.payaso, "Payaso", 180)
    )

    val personajesPremiumList = listOf(
        Recompensa("Rey", R.drawable.rey, "Rey", 350),
        Recompensa("Princesa", R.drawable.princesa, "Princesa", 180),
        Recompensa("Caballero", R.drawable.caballero, "Caballero", 250)
    )

    val bannersPerfil = listOf(
        Recompensa("Azulado", R.drawable.gradiante_azul, "Azulado", 120),
        Recompensa("Gradiante Púrpura", R.drawable.gradiante_purpura, "Gradiante Púrpura", 200),
        Recompensa("Gradiante Carmesí", R.drawable.gradiante_rojo, "Gradiante Carmesí", 300)
    )

    val bannersMulticolorPerfil = listOf(
        Recompensa("Pureza Verde", R.drawable.gradiante_verde_blanco, "Pureza Verde", 300),
        Recompensa("Noche Neón", R.drawable.gradiante_azul_purpura, "Noche Neón", 300),
        Recompensa("Carmesí Azulado", R.drawable.gradiante_azul_rojo, "Carmesí Azulado", 400),
        Recompensa("Fuego Violeta", R.drawable.gradiante_azul_purpura_rojo, "Fuego Violeta ¡TRICOLOR!", 500),
        Recompensa("Sombra Dorada", R.drawable.gradiante_dorado_negro, "Sombra Dorada", 1000)
    )
}

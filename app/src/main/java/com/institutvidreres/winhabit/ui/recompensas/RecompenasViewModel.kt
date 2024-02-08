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
}

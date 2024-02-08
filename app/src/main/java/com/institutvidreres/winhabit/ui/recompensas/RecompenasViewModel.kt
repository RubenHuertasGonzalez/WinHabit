package com.institutvidreres.winhabit.ui.recompensas

import androidx.lifecycle.ViewModel
import com.institutvidreres.winhabit.R

class RecompenasViewModel : ViewModel() {
    val personajesList = listOf(
        Recompensa("Dracula", R.drawable.dracula, "Dracula", 100),
        Recompensa("Genio", R.drawable.genio, "Genio", 150),
        Recompensa("Momia", R.drawable.momia, "Momia", 120),
        Recompensa("Orco", R.drawable.orco, "Orco", 200)
    )

    val personajesPremiumList = listOf(
        Recompensa("Rey", R.drawable.rey, "Rey", 250),
        Recompensa("Princesa", R.drawable.princesa, "Princesa", 180)
    )
}

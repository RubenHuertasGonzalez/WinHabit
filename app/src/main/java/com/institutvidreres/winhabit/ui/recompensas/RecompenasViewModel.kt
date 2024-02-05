package com.institutvidreres.winhabit.ui.recompensas

import androidx.lifecycle.ViewModel
import com.institutvidreres.winhabit.R

class RecompenasViewModel : ViewModel() {
    val personajesList = listOf(
        Recompensa("Dracula", R.drawable.dracula, "Dracula"),
        Recompensa("Genio", R.drawable.genio, "Genio"),
        Recompensa("Momia", R.drawable.momia, "Momia"),
        Recompensa("Orco", R.drawable.orco, "Orco")
    )

    val personajesPremiumList = listOf(
        Recompensa("Rey", R.drawable.rey, "Rey"),
        Recompensa("Princesa", R.drawable.princesa, "Princesa")
    )
}
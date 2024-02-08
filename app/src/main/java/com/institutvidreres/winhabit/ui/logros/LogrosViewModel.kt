package com.institutvidreres.winhabit.ui.logros

import androidx.lifecycle.ViewModel
import com.institutvidreres.winhabit.R

class LogrosViewModel : ViewModel() {
    val logrosList: List<LogrosItem> = listOf(
        LogrosItem("PRIMERAS TAREAS", "Completa un total de 2 tareas.", R.drawable.insignia_modified),
        LogrosItem("NOVATO DE LAS TAREAS", "Completa un total de 5 tareas", R.drawable.insignia_modified),
        LogrosItem("MAESTRO DE LAS TAREAS", "Completa un total de 10 tareas", R.drawable.insignia_modified),
        LogrosItem("REY DE LAS TAREAS", "Completa un total de 20 tareas", R.drawable.insignia_modified),
        LogrosItem("LEYENDA DE LAS TAREAS", "Completa un total de 50 tareas", R.drawable.insignia_modified),
    )
}

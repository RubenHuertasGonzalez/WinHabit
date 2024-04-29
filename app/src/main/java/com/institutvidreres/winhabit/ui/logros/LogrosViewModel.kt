package com.institutvidreres.winhabit.ui.logros

import androidx.lifecycle.ViewModel
import com.institutvidreres.winhabit.R

class LogrosViewModel : ViewModel() {
    val logrosList: List<LogrosItem> = listOf(
        LogrosItem("PRINCIPIANTE", "Alcanza el nivel 2", R.drawable.insignia_modified),
        LogrosItem("APRENDIZ", "Alcanza el nivel 5", R.drawable.insignia_modified),
        LogrosItem("EXPERTO", "Alcanza el nivel 10", R.drawable.insignia_modified),
        LogrosItem("MAESTRO WINHABIT", "Alcanza el nivel 20 - ¡Nivel Máximo!", R.drawable.insignia_modified),
        LogrosItem("ACUMULADOR NOVATO", "Guarda un total de 50 monedas", R.drawable.insignia_modified),
        LogrosItem("AHORRISTA INTERMEDIO", "Guarda un total de 100 monedas", R.drawable.insignia_modified),
        LogrosItem("MAESTRO DEL TESORO", "Guarda un total de 300 monedas", R.drawable.insignia_modified),
        LogrosItem("MAGNATE FINANCIERO", "Guarda un total de 500 monedas", R.drawable.insignia_modified),
        LogrosItem("ACUMULADOR DE RECOMPENSAS", "Compra un total de 3 recompensas", R.drawable.insignia_modified),
        LogrosItem("COLECCIONISTA EXPERIMENTADO", "Compra un total de 6 recompensas", R.drawable.insignia_modified),
        LogrosItem("MAGNATE DEL CANJE", "Compra un total de 12 recompensas", R.drawable.insignia_modified),
        LogrosItem("INICIADO EN LOGROS", "Completa 2 logros", R.drawable.insignia_modified),
        LogrosItem("AVANZADO EN LOGROS", "Completa 5 logros", R.drawable.insignia_modified),
        LogrosItem("MAESTRO EN LOGROS", "Completa 10 logros", R.drawable.insignia_modified),
    )
}

package com.institutvidreres.winhabit.ui.logros

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.institutvidreres.winhabit.R
import kotlinx.coroutines.tasks.await

class LogrosViewModel : ViewModel() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    val logrosList: MutableList<LogrosItem> = mutableListOf(
        LogrosItem("Nivel", "PRINCIPIANTE", "Alcanza el nivel 2", R.drawable.insignia_modified, 2),
        LogrosItem("Nivel", "APRENDIZ", "Alcanza el nivel 5", R.drawable.insignia_modified, 5),
        LogrosItem("Nivel", "EXPERTO", "Alcanza el nivel 10", R.drawable.insignia_modified, 10),
        LogrosItem("Nivel", "MAESTRO WINHABIT", "Alcanza el nivel 20 - ¡Nivel Máximo!", R.drawable.insignia_modified, 20),
        LogrosItem("Monedas", "ACUMULADOR NOVATO", "Guarda un total de 50 monedas", R.drawable.insignia_modified, 50),
        LogrosItem("Monedas", "AHORRISTA INTERMEDIO", "Guarda un total de 100 monedas", R.drawable.insignia_modified, 100),
        LogrosItem("Monedas", "MAESTRO DEL TESORO", "Guarda un total de 300 monedas", R.drawable.insignia_modified, 300),
        LogrosItem("Monedas", "MAGNATE FINANCIERO", "Guarda un total de 500 monedas", R.drawable.insignia_modified, 500),
        LogrosItem("Recompensas", "ACUMULADOR DE RECOMPENSAS", "Compra un total de 3 recompensas", R.drawable.insignia_modified, 3),
        LogrosItem("Recompensas", "COLECCIONISTA EXPERIMENTADO", "Compra un total de 6 recompensas", R.drawable.insignia_modified, 6),
        LogrosItem("Recompensas", "MAGNATE DEL CANJE", "Compra un total de 12 recompensas", R.drawable.insignia_modified, 12),
        LogrosItem("Logros", "INICIADO EN LOGROS", "Completa 2 logros", R.drawable.insignia_modified, 2),
        LogrosItem("Logros", "AVANZADO EN LOGROS", "Completa 5 logros", R.drawable.insignia_modified, 5),
        LogrosItem("Logros", "MAESTRO EN LOGROS", "Completa 10 logros", R.drawable.insignia_modified, 10)
    )

    suspend fun obtenerNivelUsuario(): Int {
        val userId = auth.currentUser?.uid ?: ""
        val documentSnapshot = db.collection("profiles").document(userId).get().await()
        return documentSnapshot.getLong("nivel")?.toInt() ?: 0
    }

    suspend fun obtenerMonedasUsuario(): Int {
        val userId = auth.currentUser?.uid ?: ""
        val documentSnapshot = db.collection("profiles").document(userId).get().await()
        return documentSnapshot.getLong("monedas")?.toInt() ?: 0
    }

    suspend fun obtenerEstadoLogrosUsuario() {
        val userId = auth.currentUser?.uid ?: ""
        val documentSnapshot = db.collection("profiles").document(userId).get().await()
        val logrosMap = documentSnapshot.data ?: return
        for (logro in logrosList) {
            logro.reclamado = logrosMap[logro.titulo]?.toString()?.toBoolean() ?: false
            if (logro.reclamado) {
                // Actualizar la imagen del logro si está reclamado
                logro.imagenResource = R.drawable.insignia
            }
        }
    }

    suspend fun reclamarRecompensa(logro: LogrosItem) {
        val userId = auth.currentUser?.uid ?: ""
        // Actualizar el estado del logro reclamado en el ViewModel
        logro.completado = true
        // Actualizar el estado del logro reclamado en Firebase
        val logroReclamadoRef = db.collection("profiles").document(userId)
        val data = hashMapOf(
            logro.titulo to true,
            "${logro.titulo}_imagen_url" to logro.imagenResource.toString()
        )
        logroReclamadoRef.set(data, SetOptions.merge()).await()

        // Actualizar las monedas del usuario
        val monedasActuales = obtenerMonedasUsuario() // Obtener las monedas actuales del usuario
        val nuevasMonedas = monedasActuales + 50 // Sumar 50 monedas
        val dataMonedas = hashMapOf(
            "monedas" to nuevasMonedas
        )
        logroReclamadoRef.set(dataMonedas, SetOptions.merge()).await() // Actualizar las monedas en Firebase
    }

    suspend fun verificarLogrosCompletados() {
        val nivelUsuario = obtenerNivelUsuario()
        val monedasUsuario = obtenerMonedasUsuario()

        logrosList.filter { it.tipo == "Nivel" }.forEach { logro ->
            if (logro.cantidad > 0) {
                logro.completado = nivelUsuario >= logro.cantidad
            }
        }

        logrosList.filter { it.tipo == "Monedas" }.forEach { logro ->
            if (logro.cantidad > 0) {
                logro.completado = monedasUsuario >= logro.cantidad
            }
        }
    }
}

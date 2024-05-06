package com.institutvidreres.winhabit.ui.logros

import android.util.Log
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
        LogrosItem("Nivel", "INICIADO", "Alcanza el nivel 2", R.drawable.insignia_modified, 2),
        LogrosItem("Nivel", "APRENDIZ", "Alcanza el nivel 5", R.drawable.insignia_modified, 5),
        LogrosItem("Nivel", "EXPERTO", "Alcanza el nivel 10", R.drawable.insignia_modified, 10),
        LogrosItem("Nivel", "MAESTRO WINHABIT", "Alcanza el nivel 20 - ¡Nivel Máximo!", R.drawable.insignia_modified, 20),
        LogrosItem("Tareas", "NOVATO DE TAREAS", "Realiza 10 tareas", R.drawable.insignia_modified, 10),
        LogrosItem("Tareas", "EXPERTO DE TAREAS", "Realiza 40 tareas", R.drawable.insignia_modified, 40),
        LogrosItem("Tareas", "MAESTRO DE TAREAS", "Realiza 100 tareas", R.drawable.insignia_modified, 100),
        LogrosItem("Tareas", "LEYENDA DE TAREAS", "Realiza 200 tareas", R.drawable.insignia_modified, 200),
        LogrosItem("Monedas", "ACUMULADOR NOVATO", "Guarda un total de 100 monedas", R.drawable.insignia_modified, 100),
        LogrosItem("Monedas", "AHORRISTA INTERMEDIO", "Guarda un total de 300 monedas", R.drawable.insignia_modified, 300),
        LogrosItem("Monedas", "MAESTRO DEL TESORO", "Guarda un total de 600 monedas", R.drawable.insignia_modified, 600),
        LogrosItem("Monedas", "MAGNATE FINANCIERO", "Guarda un total de 1200 monedas", R.drawable.insignia_modified, 1200),
        LogrosItem("VidasPerdidas", "PRIMER TROPIEZO", "Pierde tu primera vida :(", R.drawable.insignia_modified, 1),
        LogrosItem("VidasPerdidas", "RESILIENCIA", "Pierde 6 vidas", R.drawable.insignia_modified, 6),
        LogrosItem("VidasPerdidas", "CAÍDA EN LA PRODUCTIVIDAD", "Pierde 10 vidas", R.drawable.insignia_modified, 10),
        LogrosItem("Recompensas", "ACUMULADOR DE RECOMPENSAS", "Compra un total de 3 recompensas", R.drawable.insignia_modified, 3),
        LogrosItem("Recompensas", "COLECCIONISTA EXPERIMENTADO", "Compra un total de 6 recompensas", R.drawable.insignia_modified, 6),
        LogrosItem("Recompensas", "MAGNATE DEL CANJE", "Compra un total de 12 recompensas", R.drawable.insignia_modified, 12),
        LogrosItem("RecompensaPremium", "ABUNDANCIA", "Compra una recompensa premium", R.drawable.insignia_modified, 1),
        LogrosItem("RecompensaPremium", "EL ÉXITO ESTÁ EN EL AHORRO", "Compra todas las recompensas premium", R.drawable.insignia_modified, 3),
        LogrosItem("Logros", "INICIADO EN LOGROS", "Completa 3 logros", R.drawable.insignia_modified, 3),
        LogrosItem("Logros", "AVANZADO EN LOGROS", "Completa 7 logros", R.drawable.insignia_modified, 7),
        LogrosItem("Logros", "MAESTRO EN LOGROS", "Completa 13 logros", R.drawable.insignia_modified, 15),
        LogrosItem("Logros", "DOMINADOR ABSOLUTO", "Completa el juego y todos los logros", R.drawable.insignia_modified, 23)
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

    suspend fun obtenerVidasPerdidasUsuario(): Int {
        val userId = auth.currentUser?.uid ?: ""
        val documentSnapshot = db.collection("profiles").document(userId).get().await()
        return documentSnapshot.getLong("vidasPerdidas")?.toInt() ?: 0
    }

    suspend fun obtenerCantidadRecompensasCompradas(): Int {
        val userId = auth.currentUser?.uid ?: ""
        val documentSnapshot = db.collection("users").document(userId).get().await()
        val objetosComprados = documentSnapshot.get("objetosComprados") as? List<Any> ?: return 0
        val cantidadRecompensas = objetosComprados.size
        return cantidadRecompensas
    }

    suspend fun obtenerTareasCompletadas(): Int {
        val userId = auth.currentUser?.uid ?: ""
        val documentSnapshot = db.collection("profiles").document(userId).get().await()
        return documentSnapshot.getLong("tareasCompletadas")?.toInt() ?: 0
    }

    suspend fun obtenerCantidadRecompensasPremiumCompradas(): Int {
        val userId = auth.currentUser?.uid ?: ""
        val documentSnapshot = db.collection("users").document(userId).get().await()
        val objetosComprados = documentSnapshot.get("objetosComprados") as? List<Int> ?: return 0
        val cantidadRecompensasPremium = objetosComprados.count { it in listOf(12, 13, 14) }
        Log.d("LogrosViewModel", "Cantidad de recompensas premium compradas: $cantidadRecompensasPremium")
        return cantidadRecompensasPremium
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

    suspend fun contarLogrosCompletados(): Int {
        val userId = auth.currentUser?.uid ?: ""
        val documentSnapshot = db.collection("profiles").document(userId).get().await()
        val logrosMap = documentSnapshot.data ?: return 0
        // Contador para almacenar la cantidad total de logros completados
        var totalLogrosCompletados = 0
        // Iterar sobre el mapa de logros y contar aquellos que están marcados como completados
        for ((_, value) in logrosMap) {
            if (value is Boolean && value) {
                totalLogrosCompletados++
            }
        }
        return totalLogrosCompletados
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
        val vidasPerdidasUsuario = obtenerVidasPerdidasUsuario()
        val recompensasUsuario = obtenerCantidadRecompensasCompradas()
        val recompensasPremiumUsuario = obtenerCantidadRecompensasPremiumCompradas()
        val tareasCompletadas = obtenerTareasCompletadas()
        val logrosObtenidos = contarLogrosCompletados()

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

        logrosList.filter { it.tipo == "VidasPerdidas" }.forEach { logro ->
            if (logro.cantidad > 0) {
                logro.completado = vidasPerdidasUsuario >= logro.cantidad
            }
        }

        logrosList.filter { it.tipo == "Recompensas" }.forEach { logro ->
            if (logro.cantidad > 0) {
                logro.completado = recompensasUsuario >= logro.cantidad
            }
        }

        logrosList.filter { it.tipo == "RecompensaPremium" }.forEach { logro ->
            if (logro.cantidad > 0) {
                logro.completado = recompensasPremiumUsuario >= logro.cantidad
            }
        }

        logrosList.filter { it.tipo == "Tareas" }.forEach { logro ->
            if (logro.cantidad > 0) {
                logro.completado = tareasCompletadas >= logro.cantidad
            }
        }

        logrosList.filter { it.tipo == "Logros" }.forEach { logro ->
            if (logro.cantidad > 0) {
                logro.completado = logrosObtenidos >= logro.cantidad
            }
        }
    }
}

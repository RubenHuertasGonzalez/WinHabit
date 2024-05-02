package com.institutvidreres.winhabit.ui.recompensas

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.institutvidreres.winhabit.R
import com.institutvidreres.winhabit.repositori.RoomRepository
import kotlinx.coroutines.tasks.await

class RecompenasViewModel : ViewModel() {

    fun newRecompensa(context: Context, nombre: String, firebaseId: Int, imagenResId: Int, descripcion: String, precio: Int, userId: String) {

        var recompensa = Recompensa(nombre, firebaseId, imagenResId, descripcion, precio, userId)
        RoomRepository.insertRecompensa(context,recompensa)
    }

    private val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // Método para verificar si un objeto ha sido comprado por el usuario actual
    suspend fun verificarObjetoComprado(userID: String, objetoID: Int): Boolean {
        val usuarioRef = db.collection("users").document(userID)

        try {
            val usuarioDoc = usuarioRef.get().await()

            if (usuarioDoc.exists()) {
                val objetosComprados: List<Any>? = usuarioDoc.get("objetosComprados") as? List<Any>

                if (objetosComprados != null) {
                    return objetoID.toString() in objetosComprados.map { it.toString() }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

    // TODO: Para que quede mas optimizado y mejor se puede hacer que en vez de guardarlo todo en 'objetosComprados' en el firebase separarlo con el nombre: 'personajesComprados' y 'bannersComprados'

    val personajesList = listOf(
        Recompensa("Personaje", 6, R.drawable.payaso, "Payaso", 100, userId),
        Recompensa("Personaje", 7, R.drawable.dracula, "Dracula", 100, userId),
        Recompensa("Personaje", 8, R.drawable.genio, "Genio", 150, userId),
        Recompensa("Personaje", 9, R.drawable.momia, "Momia", 200, userId),
        Recompensa("Personaje", 10, R.drawable.orco, "Orco", 200, userId),
        Recompensa("Personaje", 11, R.drawable.zombi, "Zombie", 250, userId)
    )

    val personajesPremiumList = listOf(
        Recompensa("Personaje", 12, R.drawable.caballero, "Caballero", 300, userId),
        Recompensa("Personaje", 13, R.drawable.rey, "Rey", 500, userId),
        Recompensa("Personaje", 14, R.drawable.princesa, "Reina", 500, userId)
    )

    val bannersPerfil = listOf(
        Recompensa("Banner", 20, R.drawable.gradiante_azul, "Azulado", 120, userId),
        Recompensa("Banner", 21, R.drawable.gradiante_purpura, "Púrpura", 200, userId),
        Recompensa("Banner", 22, R.drawable.gradiante_rojo, "Carmesí", 200, userId)
    )

    val bannersMulticolorPerfil = listOf(
        Recompensa("Banner", 23, R.drawable.gradiante_verde_blanco, "Pureza Verde", 300, userId),
        Recompensa("Banner", 24, R.drawable.gradiante_azul_purpura, "Noche Neón", 300, userId),
        Recompensa("Banner", 25, R.drawable.gradiante_azul_rojo, "Carmesí Azulado", 400, userId),
        Recompensa("Banner", 29, R.drawable.gradiante_tropical, "Fruto Tropical", 400, userId),
        Recompensa("Banner", 26, R.drawable.gradiante_azul_purpura_rojo, "Fuego Violeta", 500, userId),
        Recompensa("Banner", 27, R.drawable.gradiante_dorado_negro, "Sombra Dorada", 1000, userId),
        Recompensa("Banner", 28, R.drawable.gradiante_radial_rojo_morado, "Aurora Primaveral", 1000, userId)
    )

    val vidasList = listOf(
        Recompensa("Vidas", 40, R.drawable.vida, "3 vidas", 30, userId),
        Recompensa("Vidas", 41, R.drawable.vidas, "10 vidas", 80, userId)
    )

    // En el ViewModel
    fun esRecompensaVida(recompensa: Recompensa): Boolean {
        return recompensa.nombre.contains("Vidas", ignoreCase = true)
    }

}

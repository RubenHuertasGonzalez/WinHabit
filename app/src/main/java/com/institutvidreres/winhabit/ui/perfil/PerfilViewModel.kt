package com.institutvidreres.winhabit.ui.perfil

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.institutvidreres.winhabit.repositori.RoomRepository
import com.institutvidreres.winhabit.ui.recompensas.Recompensa
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PerfilViewModel : ViewModel() {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _selectedItem = MutableLiveData<Recompensa>()

    //Seleccionar item de la base de dades (Toast mostrar per pantalla)
    fun setSelectedItem(item: Recompensa) {
        _selectedItem.value = item
    }
    fun getPersonajes(context: Context, usuarioId: String) : LiveData<List<Recompensa>>? {
        return RoomRepository.getPersonajes(context, usuarioId)
    }

    fun getBanners(context: Context, usuarioId: String) : LiveData<List<Recompensa>>? {
        return RoomRepository.getBanners(context, usuarioId)
    }

    suspend fun actualizarCharacter(characterId: Int) {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).update("character", characterId).await()
    }

    suspend fun actualizarBanner(bannerId: Int) {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).update("banner", bannerId).await()
    }

    suspend fun obtenerNombreUsuario(): String {
        val userId = auth.currentUser?.uid ?: ""
        val documentSnapshot = db.collection("users").document(userId).get().await()
        return documentSnapshot.getString("username") ?: ""
    }

    suspend fun obtenerCorreoUsuario(): String {
        val userId = auth.currentUser?.uid ?: ""
        val documentSnapshot = db.collection("users").document(userId).get().await()
        return documentSnapshot.getString("email") ?: ""
    }

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

    suspend fun obtenerTareasCompletadas(): Int {
        val userId = auth.currentUser?.uid ?: ""
        val documentSnapshot = db.collection("profiles").document(userId).get().await()
        return documentSnapshot.getLong("tareasCompletadas")?.toInt() ?: 0
    }

    suspend fun obtenerCantidadRecompensasCompradas(): Int {
        val userId = auth.currentUser?.uid ?: ""
        val documentSnapshot = db.collection("users").document(userId).get().await()
        val objetosComprados = documentSnapshot.get("objetosComprados") as? List<Any> ?: return 0
        val cantidadRecompensas = objetosComprados.size
        return cantidadRecompensas
    }
}
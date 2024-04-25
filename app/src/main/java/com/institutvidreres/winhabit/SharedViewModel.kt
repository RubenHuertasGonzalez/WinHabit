package com.institutvidreres.winhabit

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.institutvidreres.winhabit.tareas.Tarea
import com.institutvidreres.winhabit.ui.login.AuthActivity

class SharedViewModel : ViewModel() {

    // Crear Tareas
    private val _tareasList = MutableLiveData<List<Tarea>>()
    val tareasList: MutableLiveData<List<Tarea>> get() = _tareasList

    fun agregarTarea(tarea: Tarea) {
        val newList = (_tareasList.value ?: emptyList()) + tarea
        _tareasList.value = newList
    }

    val selectedImageUri = MutableLiveData<String>() // Uri imatge

    val selectedImageResId = MutableLiveData<Int>() // Id imatge

    val selectedBannerId = MutableLiveData<Int>() // Id del banner

    fun setSelectedImage(imageResId: Int) {
        selectedImageResId.value = imageResId
    }

    val totalVidas = MutableLiveData<Int>()
    var vidasPerdidas: Int = 0

    fun signOut() {
        // Aquí debes realizar el cierre de sesión en Firebase Auth
        FirebaseAuth.getInstance().signOut()
    }
}

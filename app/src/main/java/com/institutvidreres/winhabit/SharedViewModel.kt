package com.institutvidreres.winhabit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.institutvidreres.winhabit.tareas.Tarea

class SharedViewModel : ViewModel() {

    // Crear Tareas
    private val _tareasList = MutableLiveData<List<Tarea>>()
    val tareasList: MutableLiveData<List<Tarea>> get() = _tareasList

    fun agregarTarea(tarea: Tarea) {
        val newList = (_tareasList.value ?: emptyList()) + tarea
        _tareasList.value = newList
    }

    fun actualizarTareas(tareas: List<Tarea>) {
        _tareasList.value = tareas
    }

    val selectedImageUri = MutableLiveData<String>()

}

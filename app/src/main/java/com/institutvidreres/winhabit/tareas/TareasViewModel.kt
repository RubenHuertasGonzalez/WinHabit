package com.institutvidreres.winhabit.tareas

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TareasViewModel : ViewModel() {

    private val _tareasList = MutableLiveData<List<Tarea>>()
    val tareasList: MutableLiveData<List<Tarea>> get() = _tareasList

    fun agregarTarea(tarea: Tarea) {
        val newList = (_tareasList.value ?: emptyList()) + tarea
        _tareasList.value = newList
    }

    fun actualizarTareas(tareas: List<Tarea>) {
        _tareasList.value = tareas
    }
}


package com.institutvidreres.winhabit.tareas

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TareasViewModel : ViewModel() {

    private val _tareasList = MutableLiveData<List<Tarea>>()
    val tareasList: LiveData<List<Tarea>> get() = _tareasList

    fun agregarTarea(tarea: Tarea) {
        val newList = (_tareasList.value ?: emptyList()) + tarea
        _tareasList.value = newList
    }
}

package com.institutvidreres.winhabit.ui.perfil

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.institutvidreres.winhabit.repositori.RoomRepository
import com.institutvidreres.winhabit.ui.recompensas.Recompensa

class PerfilViewModel : ViewModel() {

    private val _selectedItem = MutableLiveData<Recompensa>()

    //Seleccionar item de la base de dades (Toast mostrar per pantalla)
    fun setSelectedItem(item: Recompensa) {
        _selectedItem.value = item
    }
    fun getPersonajes(context: Context) : LiveData<List<Recompensa>>? {
        return RoomRepository.getPersonajes(context)
    }
}
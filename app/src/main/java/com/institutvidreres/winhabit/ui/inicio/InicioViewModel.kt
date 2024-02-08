package com.institutvidreres.winhabit.ui.inicio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class InicioViewModel : ViewModel() {

    // Barra de Vida
    private val _healthBarWidth = MutableLiveData<Float>()
    val healthBarWidth: LiveData<Float> get() = _healthBarWidth

    init {
        // Valor inicial de la anchura de la barra de vida (por ejemplo, completamente llena)
        _healthBarWidth.value = 1.0f
    }

    fun actualizarAnchuraBarraVida(anchura: Float) {
        _healthBarWidth.value = anchura
    }
}
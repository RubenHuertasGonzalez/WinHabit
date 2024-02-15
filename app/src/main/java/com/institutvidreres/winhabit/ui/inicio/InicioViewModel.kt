package com.institutvidreres.winhabit.ui.inicio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class InicioViewModel : ViewModel() {
    // Barra de Vida
    private val _healthBarWidth = MutableLiveData<Float>()
    val healthBarWidth: LiveData<Float> get() = _healthBarWidth

    // Monedas del usuario
    private val _coinsUser = MutableLiveData<Int>()
    val coinsUser: LiveData<Int> get() = _coinsUser

    // Nivel del usuario
    private val _levelUser = MutableLiveData<Int>()
    val levelUser: LiveData<Int> get() = _levelUser

    //TODO Acabar guardado de experiencia del usuario cuando cambia el fragment
    private val _expUser = MutableLiveData<Int>()
    val expUser: LiveData<Int> get() = _expUser

    init {
        // Valores iniciales
        _healthBarWidth.value = 1.0f
        _coinsUser.value = 0 // Puedes inicializarlo con el valor que corresponda
        _levelUser.value = 1 // Puedes inicializarlo con el valor que corresponda
        _expUser.value = 0 // Puedes inicializarlo con el valor que corresponda
    }

    fun actualizarAnchuraBarraVida(anchura: Float) {
        _healthBarWidth.value = anchura
    }

    // Métodos para actualizar las monedas, la experiencia y el nivel
    fun actualizarMonedas(nuevasMonedas: Int) {
        _coinsUser.value = nuevasMonedas
    }

    fun actualizarExperiencia(nuevaExp: Int) {
        _expUser.value = nuevaExp
    }

    fun actualizarNivel(nuevoNivel: Int) {
        _levelUser.value = nuevoNivel
    }
}

package com.institutvidreres.winhabit.ui.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RecompenasViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "RECOMPENSAS"
    }
    val text: LiveData<String> = _text
}
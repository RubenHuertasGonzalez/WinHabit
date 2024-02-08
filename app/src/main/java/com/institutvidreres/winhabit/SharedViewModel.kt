package com.institutvidreres.winhabit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val selectedImageUri = MutableLiveData<String>()

    private val _healthBarVisible = MutableLiveData<Boolean>()

}

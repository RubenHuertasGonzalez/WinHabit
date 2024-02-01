package com.institutvidreres.winhabit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val _selectedCharacter = MutableLiveData<Int>()
    val selectedImageUri = MutableLiveData<String>()
    val selectedCharacter: LiveData<Int> get() = _selectedCharacter

    // Nueva LiveData para la imagen seleccionada
    private val _selectedCharacterImage = MutableLiveData<Int>()
    val selectedCharacterImage: LiveData<Int> get() = _selectedCharacterImage

    fun setSelectedCharacter(characterIndex: Int, imageResource: Int) {
        _selectedCharacter.value = characterIndex
        _selectedCharacterImage.value = imageResource
    }
}

package com.example.pick_e_eater.ui.filters

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FilterViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is a filters Fragment"
    }
    val text: LiveData<String> = _text
}
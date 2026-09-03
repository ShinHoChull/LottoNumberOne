package com.squirrel.lottonumberone.ui.number_select

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NumberViewModel: ViewModel() {

    private val _numberArray = MutableLiveData<MutableList<Int>>()
    val numberArray: LiveData<MutableList<Int>> get() = _numberArray

    init {
        _numberArray.value = mutableListOf<Int>()
    }

    fun setNumberArray(num: Int) {
        _numberArray.value?.add(num)
    }

}

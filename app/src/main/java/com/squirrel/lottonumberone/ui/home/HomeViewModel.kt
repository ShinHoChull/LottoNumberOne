package com.squirrel.lottonumberone.ui.home

import android.devicelock.DeviceId
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.squirrel.lottonumberone.config.Defines

class HomeViewModel: ViewModel() {

    private val _numberArray = MutableLiveData<MutableList<MutableList<Int>>>()
    val numberArray: LiveData<MutableList<MutableList<Int>>> get() = _numberArray

    private val _includingMixNumberArray = MutableLiveData<MutableList<MutableList<Int>>>()
    val includingMixNumberArray: LiveData<MutableList<MutableList<Int>>> get() = _includingMixNumberArray

    private val _excludingMixNumberArray = MutableLiveData<MutableList<MutableList<Int>>>()
    val excludingMixNumberArray: LiveData<MutableList<MutableList<Int>>> get() = _excludingMixNumberArray


    init {
        _numberArray.value = mutableListOf<MutableList<Int>>()
    }

    fun setNumberArray(num: MutableList<Int>): Boolean {
        val currentArray = _numberArray.value ?: mutableListOf()
        if (currentArray.size >= 5) {
            return false
        }
        currentArray.add(num)
        _numberArray.value = currentArray
        return true
    }

    /** 번호를 섞어주는 함수
    * @param list 섞을 번호 목록
     * @param mixCount 섞는 횟수
     */
    private fun mixMachine(list: MutableList<Int>, mixCount: Int = 10): MutableList<Int> {
        list.sort()

        for (row in 0..mixCount) {
            val ranNumber = (Math.random() * list.size).toInt()
            val fixRanNumber = (Math.random() * list.size).toInt()

            val changeNumber = list[ranNumber]

            list[ranNumber] = list[fixRanNumber]
            list[fixRanNumber] = changeNumber
        }

        return list
    }

    /** mixMachine에서 번호를 섞은 후 결과를 배열에 다시 담는다.
     * @param mixNumbers 섞을 번호 목록
     * @param basketList 결과를 담을 배열
     */
    private fun pushMixNumber(mixNumbers : MutableList<Int>): MutableList<MutableList<Int>> {
        val basketList: MutableList<MutableList<Int>> = mutableListOf()

        for (i in 0..5) {
            val temp = mixMachine(mixNumbers, mixNumbers.size * 10)

            val mixArray = mutableListOf<Int>()
            mixArray.add(temp[0])
            mixArray.add(temp[1])
            mixArray.add(temp[2])
            mixArray.add(temp[3])
            mixArray.add(temp[4])
            mixArray.add(temp[5])
            mixArray.sort()

            basketList.add(mixArray)
        }

        return basketList
    }

    /**
     * 등록된 번호를 섞기
     */
    fun setIncludingMixNumber() {
        if (_numberArray.value != null && _numberArray.value!!.size > 0) {
            val mixNumbers: MutableList<Int> = mutableListOf()

            //추출된 번호를 mixNubers 필드배열에 다 넣어준다.
            for (numbers in _numberArray.value!!) {
                for (row in numbers) {
                    if (!mixNumbers.contains(row)) mixNumbers.add(row)
                }
            }
            //번호 5개를 저장한다.
            _includingMixNumberArray.value = pushMixNumber(mixNumbers)
        }
    }

    /**
     * 등록된 번호를 제외하고 섞기
     */
    fun setExcludingMixNumber() {
        val mixNumbers: MutableList<Int> = mutableListOf()
        val includingNumbers = mutableListOf<Int>()

        //추출된 번호를 includingNumbers 에 다 넣어준다.
        for (numbers in _numberArray.value!!) {
            for (row in numbers) {
                if (!includingNumbers.contains(row)) includingNumbers.add(row)
            }
        }

       //includingNumbers 등록된 숫자를 제외하고 넣어준다 45까지
        for (num in 1..45) {
            if (!includingNumbers.contains(num)) mixNumbers.add(num)
        }

        _excludingMixNumberArray.value = pushMixNumber(mixNumbers)

    }


}
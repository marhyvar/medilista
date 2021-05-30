package com.example.medilista.edit

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medilista.database.Dosage
import com.example.medilista.database.MedicineDao
import com.example.medilista.formatAmount
import com.example.medilista.formatNumberPickerValue
import com.example.medilista.formatTime
import kotlinx.coroutines.launch

class EditDosageDetailsViewModel(
        private val dosageKey: Long = 0L,
        dataSource: MedicineDao) : ViewModel() {

    val database = dataSource

    private val dosage: LiveData<Dosage>
    fun getDosage() = dosage

    val hours = MutableLiveData<Int>()

    val minutes = MutableLiveData<Int>()

    val dosageValueFromPicker = MutableLiveData<String>()

    val dosageString = MutableLiveData<String>()

    val timeString = MutableLiveData<String>()

    private val _navigateToEditMed = MutableLiveData<Boolean>()
    val navigateToEditMed: LiveData<Boolean>
        get() = _navigateToEditMed

    init {
        dosage = database.getDosage(dosageKey)
        hours.value = dosage.value?.timeValueHours ?: 0
        minutes.value = dosage.value?.timeValueMinutes ?: 0
        dosageValueFromPicker.value = dosage.value?.amount.toString()
    }

    fun onPickerValueChange(value: Int) {
        dosageValueFromPicker.value = formatNumberPickerValue(value)
        dosageString.value = formatAmount(formatNumberPickerValue(value))
    }

    fun onTimePickerChange(hour: Int, minute: Int) {
        hours.value = hour
        minutes.value = minute
        timeString.value = formatTime(hour, minute)
    }

    fun onBackButtonEditClicked() {
        //fix navigation and update dosage
        _navigateToEditMed.value = true
    }

    fun onNavigatedtoEditMed() {
        _navigateToEditMed.value = false
    }
}
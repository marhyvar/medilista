package com.example.medilista.edit

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medilista.*
import com.example.medilista.database.Dosage
import com.example.medilista.database.MedicineDao
import kotlinx.coroutines.launch

class EditDosageDetailsViewModel(
        private val dosageKey: Long = 0L,
        dataSource: MedicineDao) : ViewModel() {

    val database = dataSource

    private val dosage: LiveData<Dosage>
    fun getDosage() = dosage

    val oldDosageText = MutableLiveData<String>()

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
        viewModelScope.launch {
            dosage.value?.let {
                if (!dosageValueFromPicker.value.isNullOrEmpty()) {
                    dosage.value!!.amount = dosageValueFromPicker.value!!.toDouble()
                }
                if (!hours.value.toString().isNullOrEmpty()) {
                    dosage.value!!.timeValueHours = hours.value!!
                }
                if (!minutes.value.toString().isNullOrEmpty()) {
                    dosage.value!!.timeValueMinutes = minutes.value!!
                }
                database.updateDosage(dosage.value!!)
            }
        }
        _navigateToEditMed.value = true
    }

    fun onNavigatedtoEditMed() {
        _navigateToEditMed.value = false
    }

    fun formatDosageToEdit(dosage: Dosage): String {
        val dosageText = combineAmountAndTimes(dosage.amount, dosage.timeValueHours, dosage.timeValueMinutes)
        return "Muokkaa annosta: $dosageText"
    }
}
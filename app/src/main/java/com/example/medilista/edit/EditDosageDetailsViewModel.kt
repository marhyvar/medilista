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

    var message = ""

    private var _showMessageEvent = MutableLiveData<Boolean>()


    val showMessageEvent: LiveData<Boolean>
        get() = _showMessageEvent

    fun doneShowingMessage() {
        _showMessageEvent.value = false
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
                var edit = false
                if (!dosageValueFromPicker.value.isNullOrEmpty()) {
                    dosage.value!!.amount = dosageValueFromPicker.value!!.toDouble()
                    edit = true
                }
                if (hasClockValueChanged(dosage.value?.timeValueHours, hours.value)) {
                    dosage.value!!.timeValueHours = hours.value!!
                    edit = true
                }
                if (hasClockValueChanged(dosage.value?.timeValueMinutes, minutes.value)) {
                    dosage.value!!.timeValueMinutes = minutes.value!!
                    edit = true
                }
                Log.i("testi", edit.toString())
                if (edit) {
                    database.updateDosage(dosage.value!!)
                    message = "Annostuksen tietoja on muokattu"
                    _showMessageEvent.value = true
                    _navigateToEditMed.value = true

                } else {
                    message = "Annostuksen tietojen muokkaus ei onnistunut"
                    _showMessageEvent.value = true
                }
            }
        }
    }

    fun onBackButtonNoEditClicked() {
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
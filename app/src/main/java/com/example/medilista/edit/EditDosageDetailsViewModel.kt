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
        dosage: Dosage,
        dataSource: MedicineDao) : ViewModel() {

    val database = dataSource

    private val _selectedDosage = MutableLiveData<Dosage>()
    val selectedDosage: LiveData<Dosage>
        get() = _selectedDosage

    val hours = MutableLiveData<Int>()

    val minutes = MutableLiveData<Int>()

    val dosageValueFromPicker = MutableLiveData<String>()

    val dosageString = MutableLiveData<String>()

    val timeString = MutableLiveData<String>()

    private val _navigateToEditMed = MutableLiveData<Boolean>()
    val navigateToEditMed: LiveData<Boolean>
        get() = _navigateToEditMed

    init {
        _selectedDosage.value = dosage
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
            _selectedDosage.value?.let {
                val id = _selectedDosage.value!!.dosageId
                if (id < 0) {
                    val amount = dosageValueFromPicker.value ?: ""
                    val valueHours = hours.value.toString()
                    val valueMinutes = minutes.value.toString()
                    val medId = _selectedDosage.value!!.dosageMedicineId
                    if (validateDosageListInput(amount, valueHours, valueMinutes)) {
                        val newDosage = Dosage(dosageMedicineId =medId, amount=amount.toDouble(),
                                timeValueHours=valueHours.toInt(), timeValueMinutes=valueMinutes.toInt())
                        database.insertDosage(newDosage)
                        message = "Lääkkeelle on lisätty uusi annostus"
                        _showMessageEvent.value = true
                        _navigateToEditMed.value = true
                    } else {
                        message = "Et voi jättää arvoja tyhjäksi"
                        _showMessageEvent.value = true
                    }
                } else {
                    var edit = false
                    if (!dosageValueFromPicker.value.isNullOrEmpty()) {
                        _selectedDosage.value!!.amount = dosageValueFromPicker.value!!.toDouble()
                        edit = true
                    }
                    if (hasClockValueChanged(_selectedDosage.value?.timeValueHours, hours.value)) {
                        _selectedDosage.value!!.timeValueHours = hours.value!!
                        edit = true
                    }
                    if (hasClockValueChanged(_selectedDosage.value?.timeValueMinutes, minutes.value)) {
                        _selectedDosage.value!!.timeValueMinutes = minutes.value!!
                        edit = true
                    }
                    Log.i("testi", edit.toString())
                    if (edit) {
                        database.updateDosage(_selectedDosage.value!!)
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
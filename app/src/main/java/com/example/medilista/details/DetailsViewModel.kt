package com.example.medilista.details

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.medilista.database.Medicine
import com.example.medilista.database.MedicineDao
import com.example.medilista.formatNumberPickerValue
import com.example.medilista.validateInputInMedicineDetails
import kotlinx.coroutines.launch

class DetailsViewModel(
        val database: MedicineDao,
        application: Application) : AndroidViewModel(application) {

    private val _navigateToMedicines = MutableLiveData<Boolean?>()
    val navigateToMedicines: LiveData<Boolean?>
        get() = _navigateToMedicines

    val name = MutableLiveData<String>()


    val strength = MutableLiveData<String>()

    val form = MutableLiveData<String>()

    val alarm = MutableLiveData<Boolean>(false)

    val onlyWhenNeeded = MutableLiveData<Boolean>(false)

    val dosageValueFromPicker = MutableLiveData<String>()


    val hours = MutableLiveData<Int>()


    val minutes = MutableLiveData<Int>()


    private val _navigateToDosage = MutableLiveData<Boolean>()
    val navigateToDosage: LiveData<Boolean>
        get() = _navigateToDosage

    private val _navigateToDetails = MutableLiveData<Boolean>()
    val navigateToDetails: LiveData<Boolean>
        get() = _navigateToDetails

    fun onNextButtonClicked() {
        _navigateToDosage.value = true
    }

    fun onNavigatedToDosage() {
        _navigateToDosage.value = false
    }

    fun onBackButtonClicked() {
        _navigateToDetails.value = true
    }

    fun onNavigatedToDetails() {
        _navigateToDetails.value = false
    }

    private var _showErrorEvent = MutableLiveData<Boolean>()


    val showErrorEvent: LiveData<Boolean>
        get() = _showErrorEvent

    fun doneShowingError() {
        _showErrorEvent.value = false
    }

    private suspend fun insert(medicine: Medicine) {
        database.insert(medicine)
    }

    fun finishedNavigating() {
        _navigateToMedicines.value = null
    }

    fun onPickerValueChange(value: Int) {
        dosageValueFromPicker.value = formatNumberPickerValue(value)
    }

    fun onTimePickerChange(hour: Int, minute: Int) {
        hours.value = hour
        minutes.value = minute
    }

    fun onSaveButtonClick() {
        viewModelScope.launch {
            val medName = name.value
            val medStrength = strength.value
            val medForm = form.value
            val medAlarm = alarm.value
            val medNeeded = onlyWhenNeeded.value

            if (validateInputInMedicineDetails(medName, medStrength, medForm)) {
                var medicine = Medicine(medicineName = medName!!, strength = medStrength!!,
                    form = medForm!!, alarm = medAlarm!!, takenWhenNeeded = medNeeded!!)
                insert(medicine)
                _navigateToMedicines.value = true
            } else {
                _showErrorEvent.value = true
            }

        }
    }
}
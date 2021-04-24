package com.example.medilista.details

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.medilista.database.Medicine
import com.example.medilista.database.MedicineDao
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

    private val _navigateToDosage = MutableLiveData<Boolean>()
    val navigateToDosage: LiveData<Boolean>
        get() = _navigateToDosage



    fun onNextButtonClicked() {
        _navigateToDosage.value = true
    }

    fun onNavigatedToDosage() {
        _navigateToDosage.value = false
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
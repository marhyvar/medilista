package com.example.medilista.details

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.medilista.database.Dosage
import com.example.medilista.database.Medicine
import com.example.medilista.database.MedicineDao
import com.example.medilista.formatNumberPickerValue
import com.example.medilista.validateDosageListInput
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


    private val dosageList = MutableLiveData<MutableList<Dosage>>()
    val list: LiveData<MutableList<Dosage>>
        get() = dosageList

    init {
        dosageList.value = ArrayList()
    }

    fun addDosageToList(dosage: Dosage) {
        dosageList.value?.add(dosage)
        dosageList.value = dosageList.value
    }

    fun onNextButtonClicked() {
        _navigateToDosage.value = true
    }

    fun onNavigatedToDosage() {
        _navigateToDosage.value = false
    }

    fun onBackButtonClicked() {
        val amount = dosageValueFromPicker.value ?: ""
        val valueHours = hours.value.toString()
        val valueMinutes = minutes.value.toString()

        if (validateDosageListInput(amount, valueHours, valueMinutes)) {
            val dosage = Dosage(-1, -1,
                    amount.toDouble(), hours.value!!, minutes.value!!)
            addDosageToList(dosage)
            Log.i("database", "dosage lisätty listaan")
        }
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
                val medicine = Medicine(medicineName = medName!!, strength = medStrength!!,
                    form = medForm!!, alarm = medAlarm!!, takenWhenNeeded = medNeeded!!)
                //val id = insert(medicine)
                insert(medicine)
                val id = database.getInsertedMedicineId()
                if (id != null) {
                    Log.i("database", id.toString())
                    val listSize = dosageList.value?.size ?: 0
                    if (listSize > 0) {
                        Log.i("database", "dosage päivitys")
                        insertDosagesForMedicineFromList(id)
                    }
                }

                _navigateToMedicines.value = true
                setEmptyValues()
            } else {
                _showErrorEvent.value = true
            }

        }
    }

    private suspend fun insertDosagesForMedicineFromList(id: Long) {
        val list = dosageList.value ?: arrayListOf()
        Log.i("database", list.get(0).amount.toString())
        list.forEach { item ->
            val dosage = item
            val newDosage = Dosage(dosageMedicineId = id, amount = dosage.amount,
                    timeValueHours = dosage.timeValueHours,
                    timeValueMinutes = dosage.timeValueMinutes)
            database.insertDosage(newDosage)
            Log.i("database", newDosage.timeValueHours.toString())
        }
        list.clear()
    }

    fun setEmptyValues() {
        name.value = ""
        strength.value = ""
        form.value = ""
        alarm.value = false
        onlyWhenNeeded.value = false
    }

}
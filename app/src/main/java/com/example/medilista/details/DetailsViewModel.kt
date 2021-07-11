package com.example.medilista.details

import android.util.Log
import androidx.lifecycle.*
import com.example.medilista.*
import com.example.medilista.database.Dosage
import com.example.medilista.database.Medicine
import com.example.medilista.database.MedicineDao
import kotlinx.coroutines.launch

class DetailsViewModel(
        val database: MedicineDao) : ViewModel() {

    private val _navigateToMedicines = MutableLiveData<Boolean?>()
    val navigateToMedicines: LiveData<Boolean?>
        get() = _navigateToMedicines

    val name = MutableLiveData<String>()

    val strength = MutableLiveData<String>()

    val alarm = MutableLiveData<Boolean>(false)

    val onlyWhenNeeded = MutableLiveData<Boolean>(false)

    val dosageValueFromPicker = MutableLiveData<String>()

    val hours = MutableLiveData<Int>()

    val minutes = MutableLiveData<Int>()

    val dosageString = MutableLiveData<String>()

    val timeString = MutableLiveData<String>()

    val validNameAndStrength = MediatorLiveData<Boolean>().apply {
        addSource(name) { nameData ->
            val strengthData = strength.value
            this.value = validateData(nameData, strengthData)
        }
        addSource(strength) {strengthData ->
            val nameData = name.value
            this.value = validateData(nameData, strengthData)
        }
    }

    private val _navigateToDosage = MutableLiveData<Boolean>()
    val navigateToDosage: LiveData<Boolean>
        get() = _navigateToDosage

    private val _navigateToDetails = MutableLiveData<Boolean>()
    val navigateToDetails: LiveData<Boolean>
        get() = _navigateToDetails

    private val dosageList = MutableLiveData<MutableList<Dosage>>()
    val list: LiveData<MutableList<Dosage>>
        get() = dosageList

    private val _formSelection = MutableLiveData<String>()
    val formSelection: LiveData<String>
        get() = _formSelection

    var message = ""

    init {
        dosageList.value = ArrayList()
        hours.value = 0
        minutes.value = 0
    }

    fun addDosageToList(dosage: Dosage) {
        dosageList.value?.add(dosage)
        dosageList.value = dosageList.value
    }

    fun clearDosageList() {
        dosageList.value?.clear()
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
        timeString.value = ""
        dosageString.value = ""
    }

    fun onNavigatedToDetails() {
        _navigateToDetails.value = false
    }

    private var _showMessageEvent = MutableLiveData<Boolean>()


    val showMessageEvent: LiveData<Boolean>
        get() = _showMessageEvent

    fun doneShowingMessage() {
        _showMessageEvent.value = false
    }

    private suspend fun insert(medicine: Medicine) {
        database.insert(medicine)
    }

    fun finishedNavigating() {
        _navigateToMedicines.value = null
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

    fun onSaveButtonClick() {
        viewModelScope.launch {
            val medName = name.value
            val medStrength = strength.value
            val medForm = _formSelection.value
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
                message = "Lääke tallennettu"
                _showMessageEvent.value = true
            } else {
                message = "Et voi jättää kenttiä tyhjäksi"
                _showMessageEvent.value = true
            }

        }
    }

    fun onCancelButtonClicked() {
        setEmptyValues()
        _navigateToMedicines.value = true
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
    }

    fun setEmptyValues() {
        name.value = ""
        strength.value = ""
        alarm.value = false
        onlyWhenNeeded.value = false
        setFormSelected("tabletti")
    }

    fun setFormSelected(formValueSelection: String) {
        _formSelection.value = formValueSelection
    }

    fun checkInput(inputName: MutableLiveData<String>): Boolean {
        val valid = validateString(inputName.value)
        if (!valid) {
            return false
        }
        return true
    }

}
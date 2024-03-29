package com.example.medilista.details

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.example.medilista.*
import com.example.medilista.database.Dosage
import com.example.medilista.database.Medicine
import com.example.medilista.database.MedicineDao
import kotlinx.coroutines.launch
import com.example.medilista.alarm.AlarmReceiver.Companion.scheduleNotification

class DetailsViewModel(
        val database: MedicineDao, application: Application) : AndroidViewModel(application) {

    private val _navigateToMedicines = MutableLiveData<Boolean?>()
    val navigateToMedicines: LiveData<Boolean?>
        get() = _navigateToMedicines

    val name = MutableLiveData<String?>()

    val strength = MutableLiveData<String?>()

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

    val validTimeAndAmountString = MediatorLiveData<Boolean>().apply {
        addSource(dosageString) { dosageData ->
            val timeData = timeString.value
            this.value = validateTimeAndAmount(dosageData, timeData)
        }
        addSource(timeString) {strengthData ->
            val dosageData = dosageString.value
            this.value = validateTimeAndAmount(dosageData, strengthData)
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

    private val _dosageCount = MutableLiveData<Int>()
    val dosageCount: LiveData<Int>
        get() = _dosageCount

    val idList = mutableListOf<Int>()

    var message = ""

    init {
        dosageList.value = ArrayList()
        hours.value = 0
        minutes.value = 0
        dosageString.value = "Et ole valinnut määrää."
        timeString.value= "Et ole valinnut aikaa."
        _dosageCount.value = 0
    }

    private fun addDosageToList(dosage: Dosage) {
        dosageList.value?.add(dosage)
        dosageList.value = sortDosageList(dosageList.value)?.toMutableList()
        _dosageCount.value = _dosageCount.value?.plus(1)
    }

    private fun removeDosageFromList(dosage: Dosage) {
        dosageList.value?.remove(dosage)
        dosageList.value = dosageList.value
        _dosageCount.value = _dosageCount.value?.minus(1)
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
            message = "Annos lisätty"
            _showMessageEvent.value = true
        }
        _navigateToDetails.value = true
        timeString.value = "Et ole valinnut aikaa."
        dosageString.value = "Et ole valinnut määrää."
    }

    fun onCancelAddDosageButtonClicked() {
        _navigateToDetails.value = true
        timeString.value = "Et ole valinnut aikaa."
        dosageString.value = "Et ole valinnut määrää."
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
            val medName = name.value?.trim() ?: ""
            val medStrength = strength.value?.trim() ?: ""
            val medForm = _formSelection.value
            val medAlarm = alarm.value
            val medNeeded = onlyWhenNeeded.value

            if (validateInputInMedicineDetails(medName, medStrength, medForm)) {
                val medicine = Medicine(medicineName = medName, strength = medStrength,
                    form = medForm!!, alarm = medAlarm!!, takenWhenNeeded = medNeeded!!)
                //val id = insert(medicine)
                insert(medicine)
                val id = database.getInsertedMedicineId()
                if (id != null) {
                    Log.i("ööö", id.toString())
                    val listSize = dosageList.value?.size ?: 0
                    if (listSize > 0) {
                        Log.i("ööö", "dosage päivitys")
                        insertDosagesForMedicineFromList(id, medAlarm)
                    }
                    if (medAlarm) {
                        scheduleAlarms()
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

    private suspend fun insertDosagesForMedicineFromList(id: Long, alarm: Boolean) {
        val list = dosageList.value ?: arrayListOf()
        Log.i("database", list.get(0).amount.toString())
        list.forEach { item ->
            val newDosage = Dosage(dosageMedicineId = id, amount = item.amount,
                    timeValueHours = item.timeValueHours,
                    timeValueMinutes = item.timeValueMinutes)
            database.insertDosage(newDosage)
            if (alarm) {
                val idInserted = database.getInsertedDosageId()
                Log.i("ööö", "id on:")
                Log.i("ööö", idInserted.toString())
                if (idInserted != null) {
                    idList.add(idInserted.toInt())
                }
            }
        }
    }


    private fun scheduleAlarms() {
        Log.i("ööö", "mentiin scheduleAlarms")
        val list = dosageList.value ?: arrayListOf()
        val medName = name.value
        val medStrength = strength.value
        val medForm = _formSelection.value
        list.forEachIndexed { index, dosage ->
            val message = createNotificationText(medName!!, medStrength!!, medForm!!, dosage.amount,
                    dosage.timeValueHours, dosage.timeValueMinutes)
            scheduleNotification(getApplication(), message, dosage.timeValueHours, dosage.timeValueMinutes, idList[index])
        }

    }

    private fun setEmptyValues() {
        name.value = null
        strength.value = null
        alarm.value = false
        onlyWhenNeeded.value = false
        setFormSelected("tabletti")
        clearDosageList()
        _dosageCount.value = 0
    }

    fun setFormSelected(formValueSelection: String) {
        _formSelection.value = formValueSelection
    }

    fun checkInput(inputName: MutableLiveData<String?>): Boolean {
        return validateString(inputName.value)
    }

    fun onDeleteDosageButtonClicked(dosage: Dosage) {
        Log.i("testi", dosage.amount.toString())
        removeDosageFromList(dosage)
        val text = combineAmountAndTimes(dosage.amount, dosage.timeValueHours, dosage.timeValueMinutes)
        message = "Annos poistettu: $text"
        _showMessageEvent.value = true
    }

}
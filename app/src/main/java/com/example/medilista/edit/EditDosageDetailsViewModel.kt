package com.example.medilista.edit

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.medilista.*
import com.example.medilista.alarm.AlarmReceiver
import com.example.medilista.alarm.AlarmReceiver.Companion.cancelAlarmNotification
import com.example.medilista.alarm.AlarmReceiver.Companion.scheduleNotification
import com.example.medilista.database.Dosage
import com.example.medilista.database.Medicine
import com.example.medilista.database.MedicineDao
import kotlinx.coroutines.launch

class EditDosageDetailsViewModel(
        dosage: Dosage,
        dataSource: MedicineDao, application: Application) : AndroidViewModel(application) {

    val database = dataSource

    private val _selectedDosage = MutableLiveData<Dosage>()
    val selectedDosage: LiveData<Dosage>
        get() = _selectedDosage

    private val medicine: LiveData<Medicine>
    fun getMedicine() = medicine

    val hours = MutableLiveData<Int>()

    val minutes = MutableLiveData<Int>()

    val dosageValueFromPicker = MutableLiveData<String>()

    val dosageString = MutableLiveData<String>()

    val timeString = MutableLiveData<String>()

    val newId = MutableLiveData<Int>()

    private val _navigateToEditMed = MutableLiveData<Boolean>()
    val navigateToEditMed: LiveData<Boolean>
        get() = _navigateToEditMed

    private val _visible = MutableLiveData<Boolean>()
    val visible: LiveData<Boolean>
        get() = _visible

    init {
        _selectedDosage.value = dosage
        _visible.value = dosage.dosageId >= 0
        medicine = database.get(dosage.dosageMedicineId)
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
                        newId.value = database.getInsertedDosageId()?.toInt()
                        if (medicine.value?.alarm == true) {
                            newId.value?.let {
                                scheduleAlarm()
                            }
                        }
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
                        if (medicine.value?.alarm == true) {
                            editAlarm(_selectedDosage.value!!)
                        }
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
        return if (_selectedDosage.value!!.dosageId < 0) {
            "Lisää uusi annos lääkkeelle"
        } else {
            "Muokkaa annosta: $dosageText"
        }
    }

    fun onDeleteDosageButtonClicked() {
        viewModelScope.launch {
            _selectedDosage.value?.let {
                val alarmState = database.getMedicineAlarm(it.dosageMedicineId)
                if (alarmState) {
                    Log.i("ööö", alarmState.toString())
                    cancelAlarm()
                }
                database.deleteDosage(it)
            }
            message = "Lääkkeen annostus on poistettu"
            _showMessageEvent.value = true
            _navigateToEditMed.value = true
        }
    }

    private fun cancelAlarm() {
        val dosage = _selectedDosage.value
        Log.i("ööö", "cancelAlarm")
        if (dosage != null) {
            cancelAlarmNotification(getApplication(), dosage.dosageId.toInt())
            Log.i("ööö", dosage.dosageId.toString())
            Log.i("ööö", "hälytys peruutettu")
        }
    }

    private fun scheduleAlarm() {
        val dosage = _selectedDosage.value
        val med = medicine.value
        Log.i("ööö", "scheduleAlarms")

        if (dosage != null) {
            val amount = dosageValueFromPicker.value?: ""
            val hours = hours.value.toString()
            val minutes = minutes.value.toString()
            val newId = newId.value
            if (med != null) {
                if (validateDosageListInput(amount, hours, minutes)) {
                    val message = createNotificationText(med.medicineName, med.strength, med.form,
                            amount.toDouble(), hours.toInt(), minutes.toInt())
                    if (newId != null) {
                        scheduleNotification(getApplication(), message, hours.toInt(),
                                minutes.toInt(), newId)
                        Log.i("ööö", "hälytys lisätty")
                    }

                }
            }
        }
    }

    private fun editAlarm(editedDos: Dosage) {
        val med = medicine.value
        val message = med?.let {
            createNotificationText(it.medicineName, it.strength, it.form,
                    editedDos.amount, editedDos.timeValueHours, editedDos.timeValueMinutes)
        }
        Log.i("ööö", "Edited text: $message")
        if (message != null) {
            scheduleNotification(getApplication(), message, editedDos.timeValueHours,
                    editedDos.timeValueMinutes, editedDos.dosageId.toInt())
        }
    }
}
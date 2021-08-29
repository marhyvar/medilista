package com.example.medilista.edit

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.medilista.alarm.AlarmReceiver
import com.example.medilista.createNotificationText
import com.example.medilista.database.Dosage
import com.example.medilista.database.Medicine
import com.example.medilista.database.MedicineDao
import com.example.medilista.database.MedicineWithDosages
import com.example.medilista.validateInputInMedicineDetails
import com.example.medilista.validateString
import kotlinx.coroutines.launch

class MedicineWithDosagesViewModel(
        private val medicineKey: Long = 0L,
        dataSource: MedicineDao, application: Application) : AndroidViewModel(application) {

    val database = dataSource

    private val _navigateToHome = MutableLiveData<Boolean?>()
    val navigateToHome: LiveData<Boolean?>
        get() = _navigateToHome

    private val _navigateToEditDosage = MutableLiveData<Dosage?>()
    val navigateToEditDosage
        get() = _navigateToEditDosage

    private val med: LiveData<MedicineWithDosages>

    val dos = database.getDosagesOfMedicine(medicineKey)


    fun getMed() = med

    var message = ""

    private val _isButtonActive = MutableLiveData<Boolean?>()
    val isButtonActive: LiveData<Boolean?>
        get() = _isButtonActive

    private val _saveMedicineEvent = MutableLiveData<Boolean>()
    val saveMedicineEvent: LiveData<Boolean>
        get() = _saveMedicineEvent

    private val _showMessageEvent = MutableLiveData<Boolean>()
    val showMessageEvent: LiveData<Boolean>
        get() = _showMessageEvent

    private val _formSelection = MutableLiveData<String>()
    val formSelection: LiveData<String>
        get() = _formSelection

    fun doneShowingMessage() {
        _showMessageEvent.value = false
    }

    init {
        med = database.getMedicineWithDosages(medicineKey)
        _isButtonActive.value = true
    }

    fun onNavigatedToHome() {
        _navigateToHome.value = false

    }

    fun onDeleteButtonClicked() {
        dos.value?.let {
            if (med.value?.Medicine?.alarm == true) {
                cancelAlarms()
            }
        }
        viewModelScope.launch {
            //med.value?.let { database.deleteMedicineData(medicineKey, it.Medicine) }
            med.value?.let {
                med.value?.dosages?.forEach{item ->
                    database.deleteDosage(item)
                }
                database.delete(med.value!!.Medicine)
                message = "Lääke on poistettu"
                _showMessageEvent.value = true
            }

            _navigateToHome.value = true
        }
    }


    fun onEditDosageButtonClicked(dosage: Dosage) {
        _navigateToEditDosage.value = dosage
    }

    fun onAddNewDosageButtonClicked() {
        val newDosage = Dosage(-1, medicineKey, 0.0, 0, 0 )
        _navigateToEditDosage.value = newDosage
    }

    fun onNavigatedToEditDosage() {
        _navigateToEditDosage.value = null
    }

    fun onSaveMedicineChangesClicked() {
        _saveMedicineEvent.value = true
    }

    fun saveMedicineChanges(name: String, strength: String, form: String, needed: Boolean, alarm: Boolean) {
        viewModelScope.launch {
            if (validateInputInMedicineDetails(name, strength, form)) {
                val changedMedicine = Medicine(medicineName = name, strength = strength, form = form,
                takenWhenNeeded = needed, alarm = alarm)

                med.value?.let {
                    val medicine = med.value!!.Medicine
                    Log.i("ööö", "vanha alarm: ${medicine.alarm}")
                    Log.i("ööö", "uusi alarm $alarm")
                    val oldMedicine = Medicine(medicineName = medicine.medicineName, strength = medicine.strength, form = medicine.form,
                            takenWhenNeeded = medicine.takenWhenNeeded, alarm = medicine.alarm)
                    if (oldMedicine == changedMedicine) {
                        message = "Et ole antanut uusia arvoja lääkkeelle"
                        _showMessageEvent.value = true
                    } else {

                        if (alarm != medicine.alarm) {
                            if (alarm) {
                                scheduleAlarms(changedMedicine)
                                Log.i("ööö", "laitetaan hälytykset")
                            } else {
                                cancelAlarms()
                                Log.i("ööö", "perutaan hälytykset")
                            }
                        } else if (medicine.alarm) {
                            scheduleAlarms(changedMedicine)
                        }

                        medicine.medicineName = name
                        medicine.strength = strength
                        medicine.form = form
                        medicine.takenWhenNeeded = needed
                        medicine.alarm = alarm
                        database.update(medicine)
                        message = "Lääkkeen tietoja on päivitetty"
                        _showMessageEvent.value = true
                    }
                }
            } else {
                message = "Et voi jättää kenttiä tyhjäksi"
                _showMessageEvent.value = true
            }
        }
        _saveMedicineEvent.value = false
    }

    fun setFormSelected(formValueSelection: String) {
        _formSelection.value = formValueSelection
    }

    fun checkInput(inputName: String?): Boolean {
        val valid = validateString(inputName)
        if (!valid) {
            _isButtonActive.value = false
            return false
        }
        _isButtonActive.value = true
        return true
    }

    private fun scheduleAlarms(changedMed: Medicine) {
        val dosages = dos.value
        val med = med.value?.Medicine
        Log.i("ööö", "scheduleAlarms listan koko:")
        Log.i("ööö", dosages?.size.toString())

        dosages?.forEach { dosage ->
            med?.let {
                val message = createNotificationText(changedMed.medicineName, changedMed.strength, changedMed.form,
                        dosage.amount, dosage.timeValueHours, dosage.timeValueMinutes)
                AlarmReceiver.scheduleNotification(getApplication(), message, dosage.timeValueHours,
                        dosage.timeValueMinutes, dosage.dosageId.toInt())
            }
        }
    }

    private fun cancelAlarms() {
        val dosages = dos.value
        Log.i("ööö", "cancelAlarms listan koko:")
        Log.i("ööö", dosages?.size.toString())
        dosages?.forEach { dosage ->
            AlarmReceiver.cancelAlarmNotification(getApplication(), dosage.dosageId.toInt())
            Log.i("ööö", "hälytys peruutettu")
        }
    }

}
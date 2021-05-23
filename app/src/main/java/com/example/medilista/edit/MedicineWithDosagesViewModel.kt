package com.example.medilista.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medilista.database.Dosage
import com.example.medilista.database.MedicineDao
import com.example.medilista.database.MedicineWithDosages
import kotlinx.coroutines.launch

class MedicineWithDosagesViewModel(
        private val medicineKey: Long = 0L,
        dataSource: MedicineDao) : ViewModel() {

    val database = dataSource

    private val _navigateToHome = MutableLiveData<Boolean?>()
    val navigateToHome: LiveData<Boolean?>
        get() = _navigateToHome

    private val med: LiveData<MedicineWithDosages>

    val dos = database.getDosagesOfMedicine(medicineKey)


    fun getMed() = med

    var message = ""

    private val _showMessageEvent = MutableLiveData<Boolean>()
    val showMessageEvent: LiveData<Boolean>
        get() = _showMessageEvent

    fun doneShowingMessage() {
        _showMessageEvent.value = false
    }

    init {
        med = database.getMedicineWithDosages(medicineKey)
    }

    fun onReturnButtonClicked() {
        _navigateToHome.value = true
    }

    fun onNavigatedToHome() {
        _navigateToHome.value = false

    }
    fun onDeleteButtonClicked() {
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

    fun onDeleteDosageButtonClicked(dosage: Dosage) {
        viewModelScope.launch {
            database.deleteDosage(dosage)
            message = "Lääkkeen annostus on poistettu"
            _showMessageEvent.value = true
        }
    }
}
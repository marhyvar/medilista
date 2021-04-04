package com.example.medilista.details

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.medilista.database.Medicine
import com.example.medilista.database.MedicineDao
import kotlinx.coroutines.launch

class DetailsViewModel(
        val database: MedicineDao,
        application: Application) : AndroidViewModel(application) {

    private val _navigateToMedicines = MutableLiveData<Boolean?>()
    val navigateToMedicines: LiveData<Boolean?>
        get() = _navigateToMedicines



    private suspend fun insert(medicine: Medicine) {
        database.insert(medicine)
    }

    fun finishedNavigating() {
        _navigateToMedicines.value = null
    }

    fun onSaveButtonClick() {
        viewModelScope.launch {
            var medicine = Medicine(medicineName = "Amlodipin", strength = "10 mg", form = "tabletti", alarm = false, takenWhenNeeded = false)
            insert(medicine)
            _navigateToMedicines.value = true
        }
    }
}
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

    val name = MutableLiveData<String>()


    val strength = MutableLiveData<String>()

    val form = MutableLiveData<String>()

    val alarm = MutableLiveData<Boolean>(false)

    val onlyWhenNeeded = MutableLiveData<Boolean>(false)

    private suspend fun insert(medicine: Medicine) {
        database.insert(medicine)
    }

    fun finishedNavigating() {
        _navigateToMedicines.value = null
    }

    fun onSaveButtonClick() {
        viewModelScope.launch {
            val med_name = name.value
            val med_strength = strength.value
            val med_form = form.value
            val med_alarm = alarm.value
            val med_needed = onlyWhenNeeded.value

            //dangerous double bang, to do: add null checks!!
            var medicine = Medicine(medicineName = med_name!!, strength = med_strength!!,
                    form = med_form!!, alarm = med_alarm!!, takenWhenNeeded = med_needed!!)
            insert(medicine)
            _navigateToMedicines.value = true
        }
    }
}
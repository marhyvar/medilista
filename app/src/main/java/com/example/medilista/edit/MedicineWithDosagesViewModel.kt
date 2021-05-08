package com.example.medilista.edit

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.medilista.database.Medicine
import com.example.medilista.database.MedicineDao

class MedicineWithDosagesViewModel(
        private val medicineKey: Long = 0L,
        dataSource: MedicineDao) : ViewModel() {

    val database = dataSource

    private val _navigateToHome = MutableLiveData<Boolean?>()
    val navigateToHome: LiveData<Boolean?>
        get() = _navigateToHome

    private val med: LiveData<Medicine>

    fun getMed() = med

    init {
        med = database.get(medicineKey)
    }

    fun onReturnButtonClicked() {
        _navigateToHome.value = true
    }

    fun onNavigatedToHome() {
        _navigateToHome.value = false

    }
}
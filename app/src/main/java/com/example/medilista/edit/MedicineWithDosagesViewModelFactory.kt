package com.example.medilista.edit

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medilista.database.MedicineDao

class MedicineWithDosagesViewModelFactory(
        private val medicineKey: Long,
        private val dataSource: MedicineDao,
        private val application: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MedicineWithDosagesViewModel::class.java)) {
            return MedicineWithDosagesViewModel(medicineKey, dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
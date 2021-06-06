package com.example.medilista.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medilista.database.MedicineDao

class EditDosageDetailsViewModelFactory (
    private val dosageKey: Long,
    private val dataSource: MedicineDao) : ViewModelProvider.Factory {
        @Suppress("unchecked_cast")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EditDosageDetailsViewModel::class.java)) {
                return EditDosageDetailsViewModel(dosageKey, dataSource) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
}
package com.example.medilista.medicines

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.medilista.database.MedicineDao
import com.example.medilista.formatMedicines

class MedicinesViewModel(
        val database: MedicineDao,
        application: Application) : AndroidViewModel(application) {

    val medicines = database.getAllMedicines()

    private val _navigateToDetails = MutableLiveData<Boolean>()
    val navigateToDetails: LiveData<Boolean>
        get() = _navigateToDetails

    private val _navigateToEditing = MutableLiveData<Long?>()
    val navigateToEditing
        get() = _navigateToEditing

    fun onFabClicked() {
        _navigateToDetails.value = true
    }

    fun onNavigatedToDetails() {
        _navigateToDetails.value = false
    }

    fun onCardClicked(id: Long){
        _navigateToEditing.value = id
    }

    fun onEditingNavigated() {
        _navigateToEditing.value = null
    }
}
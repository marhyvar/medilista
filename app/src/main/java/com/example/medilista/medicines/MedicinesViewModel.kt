package com.example.medilista.medicines

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.medilista.combineFormAmountAndTimes
import com.example.medilista.database.Dosage
import com.example.medilista.database.MedicineDao
import com.example.medilista.database.MedicineWithDosages

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

    private val _listExists = MutableLiveData<Boolean>()
    val listExists: LiveData<Boolean>
        get() = _listExists

    init {
        _listExists.value = true
    }

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

    fun determineVisibility(list: List<MedicineWithDosages>) {
        _listExists.value = list.isNotEmpty()
    }
}
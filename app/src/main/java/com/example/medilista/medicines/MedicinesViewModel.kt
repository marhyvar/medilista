package com.example.medilista.medicines

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.medilista.database.MedicineDao

class MedicinesViewModel(
        val database: MedicineDao,
        application: Application) : AndroidViewModel(application) {
    private val _navigateToDetails = MutableLiveData<Boolean>()
    val navigateToDetails: LiveData<Boolean>
        get() = _navigateToDetails

    fun onFabClicked() {
        _navigateToDetails.value = true
    }

    fun onNavigatedToDetails() {
        _navigateToDetails.value = false
    }
}
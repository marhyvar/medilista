package com.example.medilista.edit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.medilista.database.MedicineDao

class MedicineWithDosagesViewModel(
        private val medicineKey: Long = 0L,
        val database: MedicineDao) : ViewModel() {

    private val _navigateToHome = MutableLiveData<Boolean>()
    val navigateToHome: LiveData<Boolean>
        get() = _navigateToHome

    private val _id = MutableLiveData<String>()
    val id
        get() = _id

    init {
        _id.value = medicineKey.toString()
    }
}
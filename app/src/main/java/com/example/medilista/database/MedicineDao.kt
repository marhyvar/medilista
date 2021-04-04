package com.example.medilista.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MedicineDao {

    @Insert
    fun insert(medicine: Medicine)

    @Insert
    fun insertMedicineAndDosages(medicine: Medicine, dosages: List<Dosage>)

    @Update
    fun update(medicine: Medicine, dosages: List<Dosage>)

    @Delete
    fun delete(medicine: Medicine)

    @Query("SELECT * from medicine_table WHERE medicineId = :key")
    fun get(key: Long): Medicine?

    @Query("DELETE FROM medicine_table")
    fun clearAllMedicineData()

    @Query("SELECT * FROM medicine_table ORDER BY medicine_name")
    fun getMedicinesWithoutDosages(): LiveData<List<Medicine>>

    @Transaction
    @Query("SELECT * FROM medicine_table ORDER BY medicine_name")
    fun getAllMedicines(): LiveData<List<MedicineWithDosages>>
}
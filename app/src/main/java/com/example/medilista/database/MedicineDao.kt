package com.example.medilista.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MedicineDao {

    @Insert
    suspend fun insert(medicine: Medicine)

    @Insert
    suspend fun insertMedicineAndDosages(medicine: Medicine, dosages: List<Dosage>)

    @Update
    suspend fun update(medicine: Medicine)

    @Delete
    suspend fun delete(medicine: Medicine)

    @Query("SELECT * from medicine_table WHERE medicineId = :key")
    fun get(key: Long): LiveData<Medicine>

    @Query("DELETE FROM medicine_table")
    suspend fun clearAllMedicineData()

    @Query("SELECT * FROM medicine_table ORDER BY medicine_name")
    fun getMedicinesWithoutDosages(): LiveData<List<Medicine>>

    @Transaction
    @Query("SELECT * FROM medicine_table ORDER BY medicine_name")
    fun getAllMedicines(): LiveData<List<MedicineWithDosages>>

    @Transaction
    @Query("SELECT * FROM medicine_table WHERE medicineId = :key")
    fun getMedicineWithDosages(key: Long): LiveData<MedicineWithDosages>

    @Query("SELECT medicineId FROM medicine_table ORDER BY medicineId DESC LIMIT 1")
    suspend fun getInsertedMedicineId(): Long?

    @Insert
    suspend fun insertDosage(dosage: Dosage)

    @Update
    suspend fun updateDosage(dosage: Dosage)

    @Delete
    suspend fun deleteDosage(dosage: Dosage)

    @Query("SELECT * from dosage_table WHERE dosageId = :key")
    fun getDosage(key: Long): Dosage?

    @Query("SELECT * FROM dosage_table WHERE dosage_medicine_id = :key")
    fun getDosagesOfMedicine(key: Long): LiveData<List<Dosage>>

    @Query("SELECT * FROM dosage_table")
    fun getDos(): LiveData<List<Dosage>>

    @Query("DELETE from dosage_table WHERE dosage_medicine_id = :key")
    suspend fun deleteDosagesOfMedicine(key: Long)

    @Transaction
    suspend fun deleteMedicineData(key: Long, medicine: Medicine) {
        deleteDosagesOfMedicine(key)
        delete(medicine)
    }
}
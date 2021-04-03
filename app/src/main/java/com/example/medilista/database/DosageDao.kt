package com.example.medilista.database

import androidx.room.*

@Dao
interface DosageDao {

    @Insert
    fun insert(dosage: Dosage)

    @Update
    fun update(dosage: Dosage)

    @Delete
    fun delete(dosage: Dosage)

    @Query("SELECT * from dosage_table WHERE dosageId = :key")
    fun get(key: Long): Dosage?
}
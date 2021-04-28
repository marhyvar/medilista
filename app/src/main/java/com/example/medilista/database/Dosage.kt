package com.example.medilista.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "dosage_table")
data class Dosage (
        @PrimaryKey(autoGenerate = true)
        var dosageId: Long = 0L,
        @ColumnInfo(name = "dosage_medicine_id")
        var dosageMedicineId: Long,
        @ColumnInfo(name = "amount")
        var amount: Double,
        @ColumnInfo(name = "time_hours")
        var timeValueHours: Int,
        @ColumnInfo(name = "time_minutes")
        var timeValueMinutes: Int
) : Parcelable
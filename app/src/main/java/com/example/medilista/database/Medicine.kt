package com.example.medilista.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "medicine_table")
data class Medicine (
        @PrimaryKey(autoGenerate = true)
        var medicineId: Long = 0L,
        @ColumnInfo(name = "medicine_name")
        var medicineName: String,
        @ColumnInfo(name = "strength")
        var strength: String,
        @ColumnInfo(name = "form")
        var form: String,
        @ColumnInfo(name = "alarm")
        var alarm: Boolean,
        @ColumnInfo(name = "taken_when_needed")
        var takenWhenNeeded: Boolean
) : Parcelable
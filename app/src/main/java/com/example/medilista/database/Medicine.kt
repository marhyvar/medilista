package com.example.medilista.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicine_table")
data class Medicine (
        @PrimaryKey(autoGenerate = true)
        var medicineId: Long = 0L,
        @ColumnInfo(name = "medicine_name")
        var name: String,
        @ColumnInfo(name = "strength")
        var strength: Double,
        @ColumnInfo(name = "unit")
        var unit: String,
        @ColumnInfo(name = "alarm")
        var alarm: Boolean,
        @ColumnInfo(name = "taken_when_needed")
        var takenWhenNeeded: Boolean
)
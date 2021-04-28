package com.example.medilista.database

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MedicineWithDosages (
        @Embedded val Medicine: Medicine,
        @Relation(
                parentColumn = "medicineId",
                entityColumn = "dosage_medicine_id"
        )
        val dosages: List<Dosage>
) : Parcelable
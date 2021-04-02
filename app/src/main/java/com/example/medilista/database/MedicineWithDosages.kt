package com.example.medilista.database

import androidx.room.Embedded
import androidx.room.Relation

data class MedicineWithDosages (
        @Embedded val Medicine: Medicine,
        @Relation(
                parentColumn = "medicineId",
                entityColumn = "dosageMedicineId"
        )
        val dosages: List<Dosage>
)
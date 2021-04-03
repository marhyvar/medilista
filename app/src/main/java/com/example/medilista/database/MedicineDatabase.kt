package com.example.medilista.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Medicine::class, Dosage::class], version = 1,  exportSchema = false)
abstract class MedicineDatabase : RoomDatabase() {

    abstract val medicineDao: MedicineDao

    abstract val dosageDao: DosageDao

    companion object {

        @Volatile
        private var INSTANCE: MedicineDatabase? = null

        fun getInstance(context: Context): MedicineDatabase {

            synchronized(this) {
                var instance = INSTANCE
                // If null makes a new database instance.
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                            MedicineDatabase::class.java,
                            "medicine_database"
                    )
                            //lose all data if version updated
                            .fallbackToDestructiveMigration()
                            .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
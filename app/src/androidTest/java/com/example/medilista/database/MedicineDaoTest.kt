package com.example.medilista.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.medilista.getOrAwaitValue
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlinx.coroutines.test.runBlockingTest
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class MedicineDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: MedicineDatabase
    private lateinit var dao: MedicineDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                MedicineDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.medicineDao
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertMedicine() = runBlockingTest {
        val medicine = Medicine(1, "burana", "600 mg",
                "tabletti", false, true)
        dao.insert(medicine)
        val medicines = dao.getMedicinesWithoutDosages().getOrAwaitValue()
        assertThat(medicines).contains(medicine)
    }
}

//https://github.com/philipplackner/ShoppingListTestingYT/tree/TestingRoomDB
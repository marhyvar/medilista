package com.example.medilista

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class HelperFunctionsKtTest {

    @Test
    fun emptyMedicineNameValueReturnsFalse() {
        val result = validateInputInMedicineDetails(
                "",
                "10 mg",
                "tabletti"
        )
        assertThat(result).isFalse()
    }

    @Test
    fun emptyStrengthValueReturnsFalse() {
        val result = validateInputInMedicineDetails(
                "Amlodipin",
                "",
                "tabletti"
        )
        assertThat(result).isFalse()
    }

    @Test
    fun emptyFormValueReturnsFalse() {
        val result = validateInputInMedicineDetails(
                "Amlodipin",
                "10 mg",
                ""
        )
        assertThat(result).isFalse()
    }

    @Test
    fun validInputValuesReturnTrue() {
        val result = validateInputInMedicineDetails(
                "Amlodipin",
                "10 mg",
                "tabletti"
        )
        assertThat(result).isTrue()
    }

    @Test
    fun nullValuesReturnFalse() {
        val result = validateInputInMedicineDetails(
                null,
                null,
                null
        )
        assertThat(result).isFalse()
    }
}

//https://github.com/philipplackner/UnitTestingYT
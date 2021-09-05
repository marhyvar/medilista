package com.example.medilista

import com.example.medilista.database.Dosage
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

    @Test
    fun combineAmountAndTimesReturnsStringInRightForm() {
        val result = combineAmountAndTimes(1.0, 9, 8)
            assertThat(result).contains("määrä: 1; aika: 9:08")
    }

    @Test
    fun combineFormAmountAndTimesReturnsStringInRightForm() {
        val result = combineFormAmountAndTimes("tabletti", 2.0, 21, 0)
        assertThat(result).contains("2 tablettia klo 21:00")
    }

    @Test
    fun validateDosageListInputReturnsTrue() {
        val result = validateDosageListInput("3", "12", "49")
        assertThat(result).isTrue()
    }

    @Test
    fun validateDosageListInputReturnsFalse() {
        val result = validateDosageListInput("", "", "")
        assertThat(result).isFalse()
    }

    @Test
    fun validateDosageListInputReturnsFalseWhenEmptyAmount() {
        val result = validateDosageListInput("", "12", "49")
        assertThat(result).isFalse()
    }

    @Test
    fun validateDosageListInputReturnsFalseWhenEmptyHours() {
        val result = validateDosageListInput("3", "", "49")
        assertThat(result).isFalse()
    }

    @Test
    fun validateDosageListInputReturnsFalseWhenEmptyMinutes() {
        val result = validateDosageListInput("3", "12", "")
        assertThat(result).isFalse()
    }

    @Test
    fun hasClockValueChangedReturnsTrue() {
        val result = hasClockValueChanged(3, 12)
        assertThat(result).isTrue()
    }

    @Test
    fun hasClockValueChangedReturnsFalseWithNullValues() {
        val result = hasClockValueChanged(null, null)
        assertThat(result).isFalse()
    }

    @Test
    fun hasClockValueChangedReturnsFalseWithSameValues() {
        val result = hasClockValueChanged(3, 3)
        assertThat(result).isFalse()
    }

    @Test
    fun hasClockValueChangedReturnsFalseWithOtherValueNull() {
        val result = hasClockValueChanged(3, null)
        assertThat(result).isFalse()
    }

    @Test
    fun validateNotSelectedReturnsFalse() {
        val result = validateNotSelected("Et ole valinnut määrää")
        assertThat(result).isFalse()
    }

    @Test
    fun validateNotSelectedReturnsFalseWithNull() {
        val result = validateNotSelected(null)
        assertThat(result).isFalse()
    }

    @Test
    fun validateNotSelectedReturnsTrue() {
        val result = validateNotSelected("Määrä: 2.0")
        assertThat(result).isTrue()
    }

    @Test
    fun validateDataReturnsTrue() {
        val result = validateData("crestor", "10 mg")
        assertThat(result).isTrue()
    }

    @Test
    fun validateDataReturnsFalseWithEmptyName() {
        val result = validateData("", "10 mg")
        assertThat(result).isFalse()
    }

    @Test
    fun validateDataReturnsFalseWithEmptyStrength() {
        val result = validateData("crestor", "")
        assertThat(result).isFalse()
    }

    @Test
    fun validateDataReturnsFalseWithNullValues() {
        val result = validateData(null, null)
        assertThat(result).isFalse()
    }

    @Test
    fun stringValidationReturnsFalseWithTooLongText() {
        val result = validateString("iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii")
        assertThat(result).isFalse()
    }

    @Test
    fun listIsSortedCorrectly() {
        val list1 = mutableListOf<Dosage>(Dosage(1, 1, 2.0, 12, 30),
                Dosage(2, 1, 1.0, 10, 10),
                Dosage(3, 1, 1.0, 9, 47))
        val list2 = sortDosageList(list1)
            assertThat(list2!![0]).isEqualTo(Dosage(3, 1, 1.0, 9, 47))
    }

    @Test
    fun defineSpinnerPositionReturnsCorrectValue() {
        val form = "tippa"
        val result = defineSpinnerPosition("tippa")
        assertThat(result).isEqualTo(3)
    }

    @Test
    fun defineSpinnerPositionReturnsDefault() {
        val result = defineSpinnerPosition(null)
        assertThat(result).isEqualTo(0)
    }

}

//https://github.com/philipplackner/UnitTestingYT
package com.example.medilista.edit

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.medilista.combineAmountAndTimes
import com.example.medilista.combineNameAndStrength
import com.example.medilista.database.Dosage
import com.example.medilista.database.Medicine

@BindingAdapter("testi")
fun TextView.setDosageTestText(item: Dosage?) {
    item?.let {
        text = combineAmountAndTimes(item.amount, item.timeValueHours, item.timeValueMinutes)
    }
}

@BindingAdapter("med")
fun TextView.setNameText(item: Medicine?) {
    item?.let {
        text = combineNameAndStrength(item.medicineName, item.strength, item.form)
    }
}
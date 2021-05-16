package com.example.medilista.details

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.medilista.combineAmountAndTimes
import com.example.medilista.database.Dosage

@BindingAdapter("save")
fun TextView.setDosageSageText(item: Dosage?) {
    item?.let {
        text = combineAmountAndTimes(item.amount, item.timeValueHours, item.timeValueMinutes)
    }
}
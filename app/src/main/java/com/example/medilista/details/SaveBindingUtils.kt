package com.example.medilista.details

import android.widget.Button
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.medilista.combineAmountAndTimes
import com.example.medilista.database.Dosage

@BindingAdapter("save")
fun TextView.setDosageSaveText(item: Dosage?) {
    item?.let {
        text = combineAmountAndTimes(item.amount, item.timeValueHours, item.timeValueMinutes)
    }
}

@BindingAdapter("deleteButtonContentDescription")
fun Button.setContentDescriptionForDelete(item: Dosage?) {
    item?.let {
        val text = combineAmountAndTimes(item.amount, item.timeValueHours, item.timeValueMinutes)
        contentDescription = "Poista annos $text"
    }
}
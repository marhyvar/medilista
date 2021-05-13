package com.example.medilista.edit

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.medilista.database.Dosage

@BindingAdapter("testi")
fun TextView.setDosageTestText(item: Dosage?) {
    item?.let {
        text = item.amount.toString()
    }
}
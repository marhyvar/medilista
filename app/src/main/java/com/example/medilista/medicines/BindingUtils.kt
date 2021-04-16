package com.example.medilista.medicines

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.medilista.combineNameAndStrength
import com.example.medilista.database.Medicine
import com.example.medilista.determineIfAlarmOrNot
import com.example.medilista.determineIfNeededOrContinuous

@BindingAdapter("medicineNameAndStrength")
fun TextView.setMedicineNameAndStrength(item: Medicine?) {
    item?.let {
        text = combineNameAndStrength(item.medicineName, item.strength)
    }
}

@BindingAdapter("whenNeededOrContinuous")
fun TextView.setWhenNeededOrContinuous(item: Medicine?) {
    item?.let {
        text = determineIfNeededOrContinuous(item.takenWhenNeeded, context.resources)
    }
}

@BindingAdapter("alarmStatus")
fun TextView.setAlarmStatus(item: Medicine?) {
    item?.let {
        text = determineIfAlarmOrNot(item.alarm, context.resources)
    }
}
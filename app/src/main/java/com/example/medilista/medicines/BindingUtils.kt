package com.example.medilista.medicines

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.medilista.*
import com.example.medilista.database.Dosage
import com.example.medilista.database.Medicine
import com.example.medilista.database.MedicineWithDosages

@BindingAdapter("medicineNameAndStrength")
fun TextView.setMedicineNameAndStrength(item: Medicine?) {
    item?.let {
        text = combineNameAndStrength(item.medicineName, item.strength, item.form)
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

@BindingAdapter("alarmImage")
fun ImageView.setAlarmImage(item: Medicine?) {
    item?.let {
        setImageResource(when (item.alarm) {
            true -> R.drawable.alarm_on
            false -> R.drawable.alarm_off
        })
    }
}

@BindingAdapter(value = ["setDosages"])
fun RecyclerView.setDosages(medicineWithDosages: MedicineWithDosages?) {
    medicineWithDosages?.let {
        if (medicineWithDosages.dosages != null) {
            val dosageAdapter = DosageAdapter(medicineWithDosages.Medicine.form)
            dosageAdapter.submitList(sortDosageList(medicineWithDosages.dosages))
            adapter = dosageAdapter
        }
    }
}


@BindingAdapter("dosageText")
fun TextView.setDosageText(item: Dosage?) {
    item?.let {
        text = combineAmountAndTimes(item.amount, item.timeValueHours, item.timeValueMinutes)
    }
}
package com.example.medilista.medicines

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.medilista.combineNameAndStrength
import com.example.medilista.database.Dosage
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

@BindingAdapter(value = ["setDosages"])
fun RecyclerView.setDosages(dosages: List<Dosage>?) {
    if (dosages != null) {
        val dosageAdapter = DosageAdapter()
        dosageAdapter.submitList(dosages)

        adapter = dosageAdapter
    }
}
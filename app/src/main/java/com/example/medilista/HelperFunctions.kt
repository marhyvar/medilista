package com.example.medilista

import android.content.res.Resources
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.medilista.database.Medicine

fun formatMedicines(medicines: List<Medicine>, resources: Resources): Spanned {
    val sb = StringBuilder()
    sb.apply {
        append(resources.getString(R.string.title))
        medicines.forEach {
            append("<br>")
            append("\t${it.medicineName}<br>")
            append("\t${it.strength}<br>")
            append("\t${it.form}<br>")
        }
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        return Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_LEGACY)
    } else {
        return HtmlCompat.fromHtml(sb.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)
    }
}

fun combineNameAndStrength(name: String, strength: String): String {
    return "$name $strength"
}

fun determineIfNeededOrContinuous(value: Boolean, resources: Resources): String {
    return if (value) {
        resources.getString(R.string.when_needed)
    } else {
        resources.getString(R.string.countinuous)
    }
}

fun determineIfAlarmOrNot(value: Boolean, resources: Resources): String {
    return if (value) {
        resources.getString(R.string.alarm)
    } else {
        resources.getString(R.string.no_alarm)
    }
}

fun combineAmountAndTimes(amount: Double, hour: Int, min: Int): String {
    var minuteString = min.toString()
    val hourString = hour.toString()
    if (min < 10) {
        minuteString = "0${min.toString()}"
    }
    return "Määrä: ${amount.toString()}, klo $hourString:$minuteString"
}

fun validateInputInMedicineDetails(name: String?, strength: String?, form: String?): Boolean {
    if (name.isNullOrEmpty() || strength.isNullOrEmpty() || form.isNullOrEmpty()) {
        return false
    }
    return true
}

fun validateDosageListInput(amount: String, hours: String, minutes: String): Boolean {
    if (amount.isNullOrEmpty() || hours.isNullOrEmpty() || minutes.isNullOrEmpty()) {
        return false
    }
    return true
}

fun formatNumberPickerValue(value: Int): String {
    return (0.25 + value * 0.25).toString()
}

fun formatTime(hour: Int, min: Int): String {
    var minuteString = min.toString()
    val hourString = hour.toString()
    if (min < 10) {
        minuteString = "0${min.toString()}"
    }
    return "Valittu aika: $hourString:$minuteString"
}

class TextItemViewHolder(val textView: TextView): RecyclerView.ViewHolder(textView)
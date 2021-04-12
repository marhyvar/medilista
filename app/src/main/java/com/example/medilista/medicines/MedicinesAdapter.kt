package com.example.medilista.medicines

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.medilista.database.Medicine
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.example.medilista.*

class MedicinesAdapter: RecyclerView.Adapter<MedicinesAdapter.ViewHolder>() {
    var data = listOf<Medicine>()
        set(value) {
            field = value
            // save the new value by setting field = value
            notifyDataSetChanged()
        }
    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        val res = holder.itemView.context.resources
        val name = item.medicineName.toString()
        val strength = item.strength.toString()

        holder.medicine.text = combineNameAndStrength(name, strength)
        holder.ifNeeded.text = determineIfNeededOrContinuous(item.takenWhenNeeded, res)
        holder.alarm.text = determineIfAlarmOrNot(item.alarm, res)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
                .inflate(R.layout.list_item_medicine, parent, false)

        return ViewHolder(view)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val medicine: TextView = itemView.findViewById(R.id.med_name_text)
        val ifNeeded: TextView = itemView.findViewById(R.id.med_if_needed_text)
        val alarm: TextView = itemView.findViewById(R.id.alarm_text)
    }
}
package com.example.medilista.medicines

import android.content.res.Resources
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.medilista.database.Medicine
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.medilista.*

class MedicinesAdapter : ListAdapter<Medicine,
        MedicinesAdapter.ViewHolder>(MedicineDiffCallback()) {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView){
        val medicine: TextView = itemView.findViewById(R.id.med_name_text)
        val ifNeeded: TextView = itemView.findViewById(R.id.med_if_needed_text)
        val alarm: TextView = itemView.findViewById(R.id.alarm_text)

        fun bind(item: Medicine) {
            val res = itemView.context.resources
            val name = item.medicineName.toString()
            val strength = item.strength.toString()

            medicine.text = combineNameAndStrength(name, strength)
            ifNeeded.text = determineIfNeededOrContinuous(item.takenWhenNeeded, res)
            alarm.text = determineIfAlarmOrNot(item.alarm, res)
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_medicine, parent, false)
                return ViewHolder(view)
            }
        }
    }

    class MedicineDiffCallback : DiffUtil.ItemCallback<Medicine>() {
        override fun areItemsTheSame(oldItem: Medicine, newItem: Medicine): Boolean {
            return oldItem.medicineId == newItem.medicineId
        }

        override fun areContentsTheSame(oldItem: Medicine, newItem: Medicine): Boolean {
            return oldItem == newItem
        }
    }
}
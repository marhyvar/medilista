package com.example.medilista.medicines

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.medilista.database.Medicine
import android.view.LayoutInflater
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.medilista.*
import com.example.medilista.databinding.ListItemMedicineBinding

class MedicinesAdapter : ListAdapter<Medicine,
        MedicinesAdapter.ViewHolder>(MedicineDiffCallback()) {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ListItemMedicineBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: Medicine) {
            val res = itemView.context.resources
            val name = item.medicineName.toString()
            val strength = item.strength.toString()

            binding.medNameText.text = combineNameAndStrength(name, strength)
            binding.medIfNeededText.text = determineIfNeededOrContinuous(item.takenWhenNeeded, res)
            binding.alarmText.text = determineIfAlarmOrNot(item.alarm, res)
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemMedicineBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
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
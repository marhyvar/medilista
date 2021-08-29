package com.example.medilista.medicines

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.medilista.database.Medicine
import android.view.LayoutInflater
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.medilista.*
import com.example.medilista.database.MedicineWithDosages
import com.example.medilista.databinding.ListItemMedicineBinding

class MedicinesAdapter(val clickListener: MedIdListener) : ListAdapter<MedicineWithDosages,
        MedicinesAdapter.ViewHolder>(MedicineDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(clickListener,item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ListItemMedicineBinding) : RecyclerView.ViewHolder(binding.root){

        private val viewPool = RecyclerView.RecycledViewPool()

        fun bind(clickListener: MedIdListener, item: MedicineWithDosages) {
            binding.medicinewithdos = item
            binding.clickListener = clickListener
            binding.dosageList.setRecycledViewPool(viewPool)
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemMedicineBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    class MedicineDiffCallback : DiffUtil.ItemCallback<MedicineWithDosages>() {
        override fun areItemsTheSame(oldItem: MedicineWithDosages, newItem: MedicineWithDosages): Boolean {
            return oldItem.Medicine.medicineId == newItem.Medicine.medicineId
        }

        override fun areContentsTheSame(oldItem: MedicineWithDosages, newItem: MedicineWithDosages): Boolean {
            return oldItem == newItem
        }
    }
}

class MedIdListener(val clickListener: (medId: Long) -> Unit) {
    fun onClick(item: MedicineWithDosages) = clickListener(item.Medicine.medicineId)
}
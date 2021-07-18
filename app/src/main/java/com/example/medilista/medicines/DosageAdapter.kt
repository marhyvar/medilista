package com.example.medilista.medicines

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.medilista.combineFormAmountAndTimes
import com.example.medilista.database.Dosage
import com.example.medilista.databinding.ListItemDosageBinding

class DosageAdapter(val medForm: String) : ListAdapter<Dosage,
        DosageAdapter.ViewHolder>(DosageDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(medForm, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ListItemDosageBinding) : RecyclerView.ViewHolder(binding.root){


        fun bind(medForm: String, item: Dosage) {
            binding.dosage = item
            binding.dosagetext = combineFormAmountAndTimes(medForm, item.amount, item.timeValueHours, item.timeValueMinutes)
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemDosageBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    class DosageDiffCallback : DiffUtil.ItemCallback<Dosage>() {
        override fun areItemsTheSame(oldItem: Dosage, newItem: Dosage): Boolean {
            return oldItem.dosageId == newItem.dosageId
        }

        override fun areContentsTheSame(oldItem: Dosage, newItem: Dosage): Boolean {
            return oldItem == newItem
        }
    }
}
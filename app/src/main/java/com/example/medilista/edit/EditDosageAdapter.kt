package com.example.medilista.edit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.medilista.database.Dosage
import com.example.medilista.databinding.EditableItemDosageBinding

class EditDosageAdapter(val clickListenerEdit: DosageListenerEdit) : ListAdapter<Dosage,
        EditDosageAdapter.ViewHolder>(DosageDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(clickListenerEdit, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: EditableItemDosageBinding) : RecyclerView.ViewHolder(binding.root){


        fun bind(clickListenerEdit: DosageListenerEdit, item: Dosage) {
            binding.editdosage = item
            binding.clickListenerEdit = clickListenerEdit
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = EditableItemDosageBinding.inflate(layoutInflater, parent, false)
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

class DosageListener(val clickListener: (dosage: Dosage) -> Unit) {
    fun onClick(dosage: Dosage) = clickListener(dosage)
}

class DosageListenerEdit(val clickListenerEdit: (dosage: Dosage) -> Unit) {
    fun onClick(dosage: Dosage) = clickListenerEdit(dosage)
}

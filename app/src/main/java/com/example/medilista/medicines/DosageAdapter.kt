package com.example.medilista.medicines

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.medilista.database.Dosage
import com.example.medilista.databinding.ListItemDosageBinding

class DosageAdapter : ListAdapter<Dosage, CustomViewHolder>(Companion) {
    companion object : DiffUtil.ItemCallback<Dosage>() {
        override fun areItemsTheSame(oldItem: Dosage, newItem: Dosage): Boolean {
            return  oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Dosage, newItem: Dosage): Boolean {
            return  oldItem.dosageId == newItem.dosageId
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemDosageBinding.inflate(inflater, parent, false)

        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val currentDosage = getItem(position)
        val itemBinding = holder.binding as ListItemDosageBinding
        itemBinding.dosage = currentDosage
        itemBinding.executePendingBindings()
    }
}

class CustomViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root)
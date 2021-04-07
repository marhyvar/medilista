package com.example.medilista.medicines

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.medilista.TextItemViewHolder
import com.example.medilista.database.Medicine
import android.view.LayoutInflater
import android.widget.TextView
import com.example.medilista.R

class MedicinesAdapter: RecyclerView.Adapter<TextItemViewHolder>() {
    var data = listOf<Medicine>()
        set(value) {
            field = value
            // save the new value by setting field = value
            notifyDataSetChanged()
        }
    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: TextItemViewHolder, position: Int) {
        val item = data[position]
        holder.textView.text = item.medicineName.toString()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
                .inflate(R.layout.text_item_view, parent, false) as TextView

        return TextItemViewHolder(view)
    }
}
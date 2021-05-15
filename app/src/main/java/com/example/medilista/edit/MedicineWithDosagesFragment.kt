package com.example.medilista.edit

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.medilista.R
import com.example.medilista.database.MedicineDatabase
import com.example.medilista.databinding.FragmentMedicineWithDosagesBinding

class MedicineWithDosagesFragment: Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentMedicineWithDosagesBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_medicine_with_dosages, container, false)

        val application = requireNotNull(this.activity).application

        val arguments = MedicineWithDosagesFragmentArgs.fromBundle(requireArguments())

        val dataSource = MedicineDatabase.getInstance(application).medicineDao

        val viewModelFactory = MedicineWithDosagesViewModelFactory(arguments.medicineKey, dataSource)

        //don´t use this: val medicineWithDosagesViewModel: MedicineWithDosagesViewModel by activityViewModels { viewModelFactory }
        val medicineWithDosagesViewModel =
                ViewModelProvider(
                        this, viewModelFactory).get(MedicineWithDosagesViewModel::class.java)

        binding.medicineWithDosagesViewModel = medicineWithDosagesViewModel

        binding.lifecycleOwner = this

        val editDosageAdapter = EditDosageAdapter(DosageListener { dosage ->
            medicineWithDosagesViewModel.onDeleteDosageButtonClicked(dosage)
        })

        binding.dosagesListEditing.adapter = editDosageAdapter


        medicineWithDosagesViewModel.dos.observe(viewLifecycleOwner, Observer {
            it?.let {
                it.forEach{
                    Log.i("testi", it.timeValueHours.toString())
                }
                editDosageAdapter.submitList(it)
            }
        })

        medicineWithDosagesViewModel.navigateToHome.observe(viewLifecycleOwner, Observer {
            if (it == true) { // Observed state = true
                this.findNavController().navigate(MedicineWithDosagesFragmentDirections
                        .actionMedicineWithDosagesFragmentToMedicinesFragment()
                )
                medicineWithDosagesViewModel.onNavigatedToHome()
            }
        })

        return binding.root
    }
}
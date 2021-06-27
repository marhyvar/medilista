package com.example.medilista.edit

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.medilista.R
import com.example.medilista.database.MedicineDatabase
import com.example.medilista.databinding.FragmentMedicineWithDosagesBinding
import com.google.android.material.snackbar.Snackbar

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
        }, DosageListenerEdit { dosageId ->
            medicineWithDosagesViewModel.onEditDosageButtonClicked(dosageId)
        })

        val spinner: Spinner = binding.medFormSpinner
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
                application,
                R.array.form_array,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View?, position: Int, id: Long) {
                // An item was selected. Null check for configuration change etc.
                if (position >= 0) {
                    val value = parent.getItemAtPosition(position).toString()
                    medicineWithDosagesViewModel.setFormSelected(value)
                    Log.i("testi", value)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }


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

        medicineWithDosagesViewModel.showMessageEvent.observe(viewLifecycleOwner, Observer {
            if (it == true) { // Observed state = true
                val message = medicineWithDosagesViewModel.message
                Snackbar.make(
                        requireActivity().findViewById(android.R.id.content),
                        message,
                        Snackbar.LENGTH_SHORT // how long the message is displayed
                ).show()
                // Make sure the snackbar is shown once
                medicineWithDosagesViewModel.doneShowingMessage()
            }
        })

        medicineWithDosagesViewModel.saveMedicineEvent.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                val name = binding.editMedName.text.toString()
                val strength = binding.editMedStrength.text.toString()
                //val form = binding.editMedForm.text.toString()
                val form = medicineWithDosagesViewModel.formSelection.value!!
                val needed = binding.editTakenWhenNeeded.isChecked
                val alarm = binding.editAlarm.isChecked
                medicineWithDosagesViewModel.saveMedicineChanges(name, strength, form, needed, alarm)
            }
        })

        medicineWithDosagesViewModel.navigateToEditDosage.observe(viewLifecycleOwner, Observer { id ->
            id?.let {
                val navController = binding.root.findNavController()
                navController.navigate(MedicineWithDosagesFragmentDirections
                        .actionMedicineWithDosagesFragmentToEditDosageDetailsFragment(id))
                medicineWithDosagesViewModel.onNavigatedToEditDosage()
        }

        })

        return binding.root
    }
}
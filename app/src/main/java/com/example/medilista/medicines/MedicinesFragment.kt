package com.example.medilista.medicines

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.medilista.R
import com.example.medilista.database.MedicineDatabase
import com.example.medilista.databinding.FragmentMedicinesBinding


class MedicinesFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // reference to the binding object and inflate the fragment view
        val binding: FragmentMedicinesBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_medicines, container, false)

        val application = requireNotNull(this.activity).application

        val dataSource = MedicineDatabase.getInstance(application).medicineDao

        val viewModelFactory = MedicinesViewModelFactory(dataSource, application)

        val medicinesViewModel =
                ViewModelProvider(
                        this, viewModelFactory).get(MedicinesViewModel::class.java)

        binding.medicinesViewModel = medicinesViewModel

        binding.lifecycleOwner = this

        val adapter = MedicinesAdapter()
        binding.medicineList.adapter = adapter

        medicinesViewModel.medicines.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        medicinesViewModel.navigateToDetails.observe(viewLifecycleOwner,
            Observer<Boolean> { shouldNavigate ->
                if (shouldNavigate == true) {
                    val navController = binding.root.findNavController()
                    navController.navigate(R.id.action_medicinesFragment_to_detailsFragment)
                    medicinesViewModel.onNavigatedToDetails()
                }
            })
        return binding.root
    }
}
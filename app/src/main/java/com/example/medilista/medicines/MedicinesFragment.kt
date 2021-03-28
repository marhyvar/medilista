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
import com.example.medilista.databinding.FragmentMedicinesBinding


class MedicinesFragment : Fragment() {

    companion object {
        fun newInstance() = MedicinesFragment()
    }

    private lateinit var viewModel: MedicinesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMedicinesBinding.inflate(inflater)
        viewModel = ViewModelProvider(this).get(MedicinesViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.navigateToDetails.observe(viewLifecycleOwner,
            Observer<Boolean> { shouldNavigate ->
                if (shouldNavigate == true) {
                    val navController = binding.root.findNavController()
                    navController.navigate(R.id.action_medicinesFragment_to_detailsFragment)
                    viewModel.onNavigatedToDetails()
                }
            })
        return binding.root
    }
}
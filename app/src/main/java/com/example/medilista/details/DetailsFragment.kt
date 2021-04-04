package com.example.medilista.details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.medilista.R
import com.example.medilista.database.MedicineDatabase
import com.example.medilista.databinding.FragmentDetailsBinding

class DetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentDetailsBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_details, container, false)

        val application = requireNotNull(this.activity).application

        val dataSource = MedicineDatabase.getInstance(application).medicineDao

        val viewModelFactory = DetailsViewModelFactory(dataSource, application)

        val detailsViewModel =
                ViewModelProvider(
                        this, viewModelFactory).get(DetailsViewModel::class.java)

        binding.detailsViewModel = detailsViewModel

        binding.lifecycleOwner = this

        detailsViewModel.navigateToMedicines.observe(viewLifecycleOwner, Observer {
            if (it == true) { // Observed state = true
                this.findNavController().navigate(
                        R.id.action_detailsFragment_to_medicinesFragment)
                detailsViewModel.finishedNavigating()
            }
        })

        return binding.root
    }

}
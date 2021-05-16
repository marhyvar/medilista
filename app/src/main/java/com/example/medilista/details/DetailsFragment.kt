package com.example.medilista.details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.medilista.R
import com.example.medilista.database.MedicineDatabase
import com.example.medilista.databinding.FragmentDetailsBinding
import com.example.medilista.medicines.DosageAdapter
import com.google.android.material.snackbar.Snackbar

class DetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentDetailsBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_details, container, false)

        val application = requireNotNull(this.activity).application

        val dataSource = MedicineDatabase.getInstance(application).medicineDao

        val viewModelFactory = DetailsViewModelFactory(dataSource)

        //val detailsViewModel =
         //       ViewModelProvider(
         //               this, viewModelFactory).get(DetailsViewModel::class.java)

        val detailsViewModel: DetailsViewModel by activityViewModels { viewModelFactory }

        binding.detailsViewModel = detailsViewModel

        binding.lifecycleOwner = this

        val saveDosageAdapter = SaveDosageAdapter()

        binding.dosagesForSaving.adapter = saveDosageAdapter

        detailsViewModel.list.observe(viewLifecycleOwner, Observer {
            it?.let {
                saveDosageAdapter.submitList(it)
            }
        })

        detailsViewModel.navigateToDosage.observe(viewLifecycleOwner,
            Observer<Boolean> { shouldNavigate ->
                if (shouldNavigate == true) {
                    val navController = binding.root.findNavController()
                    navController.navigate(R.id.action_detailsFragment_to_dosageFragment)
                    detailsViewModel.onNavigatedToDosage()
                }
            })

        detailsViewModel.navigateToMedicines.observe(viewLifecycleOwner, Observer {
            if (it == true) { // Observed state = true
                this.findNavController().navigate(
                        R.id.action_detailsFragment_to_medicinesFragment)
                detailsViewModel.finishedNavigating()
            }
        })

        detailsViewModel.showErrorEvent.observe(viewLifecycleOwner, Observer {
            if (it == true) { // Observed state = true
                Snackbar.make(
                        requireActivity().findViewById(android.R.id.content),
                        getString(R.string.details_error),
                        Snackbar.LENGTH_SHORT // how long the message is displayed
                ).show()
                // Make sure the snackbar is shown once
                detailsViewModel.doneShowingError()
            }
        })

        return binding.root
    }

}
package com.example.medilista.details

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.lifecycle.Observer
import androidx.fragment.app.activityViewModels
import com.example.medilista.R
import com.example.medilista.database.MedicineDatabase
import com.example.medilista.databinding.FragmentDosageBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

class DosageFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentDosageBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_dosage, container, false)

        val application = requireNotNull(this.activity).application

        val dataSource = MedicineDatabase.getInstance(application).medicineDao

        val viewModelFactory = DetailsViewModelFactory(dataSource, application)

        val dosageViewModel: DetailsViewModel by activityViewModels { viewModelFactory }



        binding.dosageViewModel = dosageViewModel

        binding.lifecycleOwner = this

        binding.showTimePicker.setOnClickListener {
                    MaterialTimePicker.Builder()
                            .setTimeFormat(TimeFormat.CLOCK_24H)
                            .setHour(0)
                            .setMinute(0)
                            .build()
                            .apply {
                                addOnPositiveButtonClickListener {
                                    Log.i("picker", "$hour:$minute")
                                }
                                addOnDismissListener {
                                    Log.i("picker", "dismiss button click")
                                }
                            }
                            .show(parentFragmentManager, "")
        }

        dosageViewModel.navigateToMedicines.observe(viewLifecycleOwner, Observer {
            if (it == true) { // Observed state = true
                this.findNavController().navigate(
                    R.id.action_dosageFragment_to_medicinesFragment)
                dosageViewModel.finishedNavigating()
            }
        })

        dosageViewModel.showErrorEvent.observe(viewLifecycleOwner, Observer {
            if (it == true) { // Observed state = true
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    getString(R.string.details_error),
                    Snackbar.LENGTH_SHORT // how long the message is displayed
                ).show()
                // Make sure the snackbar is shown once
                dosageViewModel.doneShowingError()
            }
        })



        return binding.root
    }
}
package com.example.medilista.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.medilista.R
import com.example.medilista.database.MedicineDatabase
import com.example.medilista.databinding.FragmentDetailsBinding
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

        val spinner: Spinner = binding.medicineFormSpinner
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
                application,
                R.array.form_array,
                R.layout.spinner_selected_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
            val form = detailsViewModel.formSelection.value
            val spinnerPosition = adapter.getPosition(form)
            spinner.setSelection(spinnerPosition, false)
        }

        spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View?, position: Int, id: Long) {
                // An item was selected. Null check for configuration change etc.
                if (position >= 0) {
                    val value = parent.getItemAtPosition(position).toString()
                    detailsViewModel.setFormSelected(value)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        val saveDosageAdapter = SaveDosageAdapter(DosageListenerDelete { dosage ->
            detailsViewModel.onDeleteDosageButtonClicked(dosage)
        })

        binding.dosagesForSaving.adapter = saveDosageAdapter

        detailsViewModel.list.observe(viewLifecycleOwner, Observer {
            it?.let {
                saveDosageAdapter.submitList(it.toMutableList())
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
                detailsViewModel.clearDosageList()
            }
        })

        detailsViewModel.showMessageEvent.observe(viewLifecycleOwner, Observer {
            if (it == true) { // Observed state = true
                val message = detailsViewModel.message
                Snackbar.make(
                        requireActivity().findViewById(android.R.id.content),
                        //getString(R.string.details_error),
                        message,
                        Snackbar.LENGTH_SHORT // how long the message is displayed
                ).show()
                // Make sure the snackbar is shown once
                detailsViewModel.doneShowingMessage()
            }
        })

        binding.editMedicineName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val valid = detailsViewModel.checkInput(detailsViewModel.name)
                if (!valid) {
                    binding.medicineLayout.error = getString(R.string.mandatory)
                }
            }
        }

        binding.editMedicineName.doOnTextChanged { _, _, _, _ ->
            binding.medicineLayout.error = null
        }

        binding.editMedicineStrength.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val valid = detailsViewModel.checkInput(detailsViewModel.strength)
                if (!valid) {
                    binding.strengthLayout.error = getString(R.string.mandatory)
                }
            }
        }

        binding.editMedicineStrength.doOnTextChanged { _, _, _, _ ->
            binding.strengthLayout.error = null
        }

        return binding.root
    }

}
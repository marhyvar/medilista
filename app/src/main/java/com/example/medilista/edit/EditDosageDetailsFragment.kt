package com.example.medilista.edit

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.medilista.R
import com.example.medilista.database.MedicineDatabase
import com.example.medilista.databinding.FragmentEditDosageDetailsBinding
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

class EditDosageDetailsFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentEditDosageDetailsBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_edit_dosage_details, container, false)

        val application = requireNotNull(this.activity).application

        val dataSource = MedicineDatabase.getInstance(application).medicineDao

        val arguments = EditDosageDetailsFragmentArgs.fromBundle(requireArguments())

        val viewModelFactory = EditDosageDetailsViewModelFactory(arguments.dosageKey, dataSource)

        val editDosageDetailsViewModel =
                ViewModelProvider(
                        this, viewModelFactory).get(EditDosageDetailsViewModel::class.java)

        binding.editDosageDetailsViewModel = editDosageDetailsViewModel

        binding.lifecycleOwner = this

        binding.showTimePickerEdit.setOnClickListener {

            MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(12)
                    .setMinute(0)
                    .build()
                    .apply {
                        addOnPositiveButtonClickListener {
                            Log.i("testi", "$hour:$minute")
                            editDosageDetailsViewModel.onTimePickerChange(hour, minute)
                        }
                        addOnDismissListener {
                            Log.i("testi", "dismiss button click")
                        }
                    }
                    .show(parentFragmentManager, "")
        }

        val pickerValues = Array(80) { i -> (0.25 + i * 0.25).toString() } // 0.25 -> 20
        val picker = binding.numberPickerEdit
        picker.minValue = 0
        picker.maxValue = pickerValues.size -1 // 79
        picker.displayedValues = pickerValues

        editDosageDetailsViewModel.navigateToEditMed.observe(viewLifecycleOwner, Observer {
            if (it == true) { // Observed state = true
                val medId = editDosageDetailsViewModel.getDosage().value?.dosageMedicineId ?: -1
                this.findNavController().navigate(
                        EditDosageDetailsFragmentDirections
                                .actionEditDosageDetailsFragmentToMedicineWithDosagesFragment(medId))
                editDosageDetailsViewModel.onNavigatedtoEditMed()
            }
        })

        return binding.root
    }
}
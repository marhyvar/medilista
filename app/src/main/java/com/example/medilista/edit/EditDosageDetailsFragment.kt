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
import com.example.medilista.alarm.AlarmReceiver.Companion.cancelAlarmNotification
import com.example.medilista.alarm.AlarmReceiver.Companion.scheduleNotification
import com.example.medilista.combineNameAndStrength
import com.example.medilista.createNotificationText
import com.example.medilista.database.MedicineDatabase
import com.example.medilista.databinding.FragmentEditDosageDetailsBinding
import com.example.medilista.validateDosageListInput
import com.google.android.material.snackbar.Snackbar
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

        val viewModelFactory = EditDosageDetailsViewModelFactory(arguments.dosage, dataSource)

        val editDosageDetailsViewModel =
                ViewModelProvider(
                        this, viewModelFactory).get(EditDosageDetailsViewModel::class.java)

        binding.editDosageDetailsViewModel = editDosageDetailsViewModel

        binding.lifecycleOwner = this

        binding.showTimePickerEdit.setOnClickListener {
            val hours = editDosageDetailsViewModel.hours.value ?: 0
            val minutes = editDosageDetailsViewModel.minutes.value ?: 0
            MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(hours)
                    .setMinute(minutes)
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

        editDosageDetailsViewModel.selectedDosage.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.oldDosage.text = editDosageDetailsViewModel.formatDosageToEdit(it)
                editDosageDetailsViewModel.hours.value = it.timeValueHours
                editDosageDetailsViewModel.minutes.value = it.timeValueMinutes
            }
        })

        editDosageDetailsViewModel.getMedicine().observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.medicineTitle.text =
                    combineNameAndStrength(it.medicineName, it.strength, it.form)
            }
        })

        editDosageDetailsViewModel.navigateToEditMed.observe(viewLifecycleOwner, Observer {
            if (it == true) { // Observed state = true
                val medId = editDosageDetailsViewModel.selectedDosage.value?.dosageMedicineId ?: -1
                this.findNavController().navigate(
                        EditDosageDetailsFragmentDirections
                                .actionEditDosageDetailsFragmentToMedicineWithDosagesFragment(medId))
                editDosageDetailsViewModel.onNavigatedtoEditMed()
            }
        })

        editDosageDetailsViewModel.showMessageEvent.observe(viewLifecycleOwner, Observer {
            if (it == true) { // Observed state = true
                val message = editDosageDetailsViewModel.message
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    message,
                    Snackbar.LENGTH_SHORT // how long the message is displayed
                ).show()
                // Make sure the snackbar is shown once
                editDosageDetailsViewModel.doneShowingMessage()
            }
        })

        editDosageDetailsViewModel.visible.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                binding.deleteDosageButton.visibility = View.VISIBLE
            }
        })

        editDosageDetailsViewModel.cancelAlarm.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                val dosage = editDosageDetailsViewModel.selectedDosage.value
                Log.i("ööö", "cancelAlarm")
                if (dosage != null) {
                    cancelAlarmNotification(application, dosage.dosageId.toInt())
                    Log.i("ööö", dosage.dosageId.toString())
                    Log.i("ööö", "hälytys peruutettu")
                }
                editDosageDetailsViewModel.onAlarmCancelled()
            }
        })

        editDosageDetailsViewModel.scheduleAlarm.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                val dosage = editDosageDetailsViewModel.selectedDosage.value
                val med = editDosageDetailsViewModel.getMedicine().value
                Log.i("ööö", "scheduleAlarms")

                if (dosage != null) {
                    val amount = editDosageDetailsViewModel.dosageValueFromPicker.value?: ""
                    val hours = editDosageDetailsViewModel.hours.value.toString()
                    val minutes = editDosageDetailsViewModel.minutes.value.toString()
                    val newId = editDosageDetailsViewModel.newId.value
                    if (med != null) {
                        if (validateDosageListInput(amount, hours, minutes)) {
                            val message = createNotificationText(med.medicineName, med.strength, med.form,
                                amount.toDouble(), hours.toInt(), minutes.toInt())
                            if (newId != null) {
                                scheduleNotification(application, message, hours.toInt(),
                                    minutes.toInt(), newId)
                                Log.i("ööö", "hälytys lisätty")
                            }

                        }
                    }
                }
                editDosageDetailsViewModel.onAlarmScheduled()
            }
        })

        return binding.root
    }
}
package com.example.medilista.medicines

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.medilista.R
//import com.example.medilista.alarm.AlarmReceiver.Companion.schedulePushNotifications
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

        createNotificationChannel(getString(R.string.med_notification_channel_id), getString(R.string.med_notification_channel_name))
        //schedulePushNotifications(application)

        val medicinesViewModel =
                ViewModelProvider(
                        this, viewModelFactory).get(MedicinesViewModel::class.java)

        binding.medicinesViewModel = medicinesViewModel

        binding.lifecycleOwner = this

        val adapter = MedicinesAdapter(MedIdListener { medId ->
            medicinesViewModel.onCardClicked(medId)
        })

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

        medicinesViewModel.navigateToEditing.observe(viewLifecycleOwner, Observer {med ->
            med?.let {
                val navController = binding.root.findNavController()
                navController.navigate(MedicinesFragmentDirections
                        .actionMedicinesFragmentToMedicineWithDosagesFragment(med))
                medicinesViewModel.onEditingNavigated()
            }
        })

        return binding.root
    }

    private fun createNotificationChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.med_notification_channel_description)

            val notificationManager = requireActivity().getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)

        }
    }
}
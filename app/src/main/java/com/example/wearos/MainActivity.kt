package com.example.wearos

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.wearos.databinding.ActivityMainBinding
import java.util.*

class MainActivity : Activity() {

    private lateinit var updateUIWithLocation: () -> Unit
    private lateinit var textViewLocation: TextView
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set the time text
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        binding.timeText.text = String.format("%02d:%02d", hour, minute)

        // Get the location
        textViewLocation = binding.topText
        getLocation()

        // Set the temperature text
        val temperatureTextView = binding.temperatureText
        temperatureTextView.text = "20°C"

        // Send a notification
        val title = "WARNING"
        val message = "Pressure is changing alarmingly!!"
        sendNotification(title, message)
    }



    private fun updateUIWithLocation(locationName: String) {
        Log.d("Location", "hhhh1")
        Log.d("Location", "$locationName")
        binding.topText.text = "$locationName"
    }

    private fun getLocation() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (lastLocation != null) {
            val geocoder = Geocoder(applicationContext, Locale.getDefault())
            val addressList = geocoder.getFromLocation(lastLocation.latitude, lastLocation.longitude, 1)
            if (addressList!!.isNotEmpty()) {
                val locationName = "${addressList!![0].locality}, ${addressList[0].countryName}"
                updateUIWithLocation(locationName)
            }
        }
        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                Log.d("Location", "hhhh3")
                val geocoder = Geocoder(applicationContext, Locale.getDefault())
                val addressList = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                if (addressList!!.isNotEmpty()) {
                    Log.d("Location", "hhhh2")
                    val locationName = "${addressList!![0].locality}, ${addressList[0].countryName}"
                    updateUIWithLocation(locationName)
                }
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
    }



    private fun sendNotification(title: String, message: String) {
        // Create a notification channel for Android 8.0 and higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "channel_id"
            val channelName = "Channel Name"
            val channelDescription = "Channel Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }

            // Register the channel with the system
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Create the notification
        val builder = NotificationCompat.Builder(applicationContext, "channel_id")
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.warningblack)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        // Show the notification
        with(NotificationManagerCompat.from(applicationContext)) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                return
            }
            notify(0, builder.build())
        }
    }
}
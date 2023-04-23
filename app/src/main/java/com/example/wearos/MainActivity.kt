package com.example.wearos

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import com.example.wearos.databinding.ActivityMainBinding
import java.util.*

class MainActivity : Activity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val textView = TextView(this)
        textView.text = "08/04 SAT ~ San Francisco"
        textView.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set the time text
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        binding.timeText.text = String.format("%02d:%02d", hour, minute)

    }
}
package com.example.plantbuddy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.plantbuddy.core.ReminderManager
import com.example.plantbuddy.model.Plant

class ReminderDisplayActivity : AppCompatActivity() {
    private var plantName: String? = null
    private var plantUrl: String? = null
    private var alarmId:Int? = null

    private lateinit var coverImageIV: ImageView
    private lateinit var plantNameTv: TextView
    private lateinit var waterButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder_display)

        plantName = intent?.getStringExtra("name")
        plantUrl = intent?.getStringExtra("url")
        alarmId = intent?.getIntExtra("alarmId", -1)
        initViews()
        initUI()
    }

    private fun initViews()
    {
        coverImageIV = findViewById(R.id.cover_photo)
        plantNameTv = findViewById(R.id.tv_plant_name)
        waterButton = findViewById(R.id.btn_water_plant)
    }

    private fun initUI()
    {
        Glide.with(this)
            .load(plantUrl)
            .centerCrop()
            .into(coverImageIV);
        plantNameTv.text = plantName
        waterButton.setOnClickListener {
            cancelAlarm()
        }
    }

    private fun cancelAlarm()
    {
        if(alarmId == null || alarmId == -1)
        {
            //TODO: show error message
            return;
        }

        ReminderManager.cancelAlarm(this, alarmId!!)
    }
}
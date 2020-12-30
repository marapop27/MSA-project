package com.example.plantbuddy

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.example.plantbuddy.core.ReminderManager
import com.example.plantbuddy.helpers.showMessageSnackbar
import kotlinx.android.synthetic.main.activity_reminder_display.*


class ReminderDisplayActivity : AppCompatActivity() {
    private var plantId: String? = null
    private var plantName: String? = null
    private var plantUrl: String? = null
    private var alarmId:Int? = null

    private lateinit var coverImageIV: ImageView
    private lateinit var plantNameTv: TextView
    private lateinit var waterButton: Button

    private lateinit var vibrator: Vibrator

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }
        else
        {
            window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder_display)

        plantId = intent?.getStringExtra("plantId")
        plantName = intent?.getStringExtra("name")
        plantUrl = intent?.getStringExtra("url")
        alarmId = intent?.getIntExtra("alarmId", -1)

        initViews()
        initUI()
    }

    override fun onResume() {
        super.onResume()
        vibratePhone();
    }
    override fun onPause() {
        super.onPause()
        vibrator.cancel()
    }

    private fun vibratePhone()
    {
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val pattern = longArrayOf(100, 200, 100, 200, 100, 200, 100, 200, 100, 100, 100, 100, 100, 200, 100, 200, 100, 200, 100, 200, 100, 100, 100, 100, 100, 200, 100, 200, 100, 200, 100, 200, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 50, 50, 100, 800)

        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0))
        } else {
            //deprecated in API 26
            vibrator.vibrate(pattern, 0)
        }
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
            finish()
        }
    }

    private fun cancelAlarm()
    {
        if(alarmId == null || alarmId == -1)
        {
            //TODO: show error message
            return;
        }

        vibrator.cancel()
        ReminderManager.cancelAlarm(this, plantId, alarmId!!)
    }
}
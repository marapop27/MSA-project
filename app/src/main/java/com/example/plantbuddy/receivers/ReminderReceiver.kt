package com.example.plantbuddy.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.plantbuddy.ReminderDisplayActivity
import com.example.plantbuddy.model.Plant

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val reminderIntent = Intent(context, ReminderDisplayActivity::class.java)
        val plantName= intent?.getStringExtra("name")
        val plantImage = intent?.getStringExtra("url")
        val alarmId = intent?.getIntExtra("alarmId", -1)
        reminderIntent.putExtra("name", plantName)
        reminderIntent.putExtra("url", plantImage)
        reminderIntent.putExtra("alarmId", alarmId)
        context?.startActivity(reminderIntent)
    }
}
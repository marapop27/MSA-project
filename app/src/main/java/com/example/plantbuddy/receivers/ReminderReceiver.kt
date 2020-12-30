package com.example.plantbuddy.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.plantbuddy.ReminderDisplayActivity
import com.example.plantbuddy.model.Plant

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val reminderIntent = Intent(context, ReminderDisplayActivity::class.java)
        reminderIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        val plantId= intent?.getStringExtra("plantId")
        val plantName= intent?.getStringExtra("name")
        val plantImage = intent?.getStringExtra("url")
        val alarmId = intent?.getIntExtra("alarmId", -1)

        reminderIntent.putExtra("plantId", plantId)
        reminderIntent.putExtra("name", plantName)
        reminderIntent.putExtra("url", plantImage)
        reminderIntent.putExtra("alarmId", alarmId)
        context?.startActivity(reminderIntent)
    }
}
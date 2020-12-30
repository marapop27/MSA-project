package com.example.plantbuddy.core

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.plantbuddy.model.Plant
import com.example.plantbuddy.receivers.ReminderReceiver
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.FirebaseDatabase
import java.time.Duration
import java.util.*

object ReminderManager {

    var database = FirebaseDatabase.getInstance()
    val reference = database.getReference("plants")

    fun setAlarmForPlant(context: Context, plant: Plant, alarmId:Int=-1): Boolean
    {
        var alarmAuxId = (0..9999).random()
        if(alarmId != -1)
        {
            alarmAuxId = alarmId
        }
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, ReminderReceiver::class.java).let { intent ->
            intent.putExtra("plantId", plant.plantId)
            intent.putExtra("name", plant.plantName)
            intent.putExtra("url", plant.imageUrl)
            intent.putExtra("freq", plant.wateringFreq)
            PendingIntent.getBroadcast(context, alarmAuxId, intent, 0)
        }

        val plantMinute = plant.startTime?.substring(3, 5)?.toInt()
        val plantHour = plant.startTime?.substring(0, 2)?.toInt()
        if(plantHour == null || plantMinute == null)
        {
            return false;
        }
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR, plantHour)
            set(Calendar.MINUTE, plantMinute)
        }

        alarmMgr?.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            1000 * 60 * (plant.wateringFreq?.toLong() ?: 1),
            alarmIntent
        )

        reference.child(plant.plantId!!).child("alarmId").setValue(alarmAuxId)
        return true
    }

    fun cancelAlarm(context: Context, plantId: String?, alarmId: Int)
    {
        val alarmIntent: PendingIntent = PendingIntent.getBroadcast(
            context, alarmId,
            Intent(context, ReminderReceiver::class.java),
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmMgr.cancel(alarmIntent)
        reference.child(plantId!!).child("alarmId").setValue(-1)
    }
}
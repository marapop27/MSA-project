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

    fun setAlarmForPlant(context: Context, plant: Plant, alarmId:Int=-1)
    {
        var alarmAuxId = (0..9999).random()
        if(alarmId != -1)
        {
            alarmAuxId = alarmId
        }

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
            return;
        }

        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR, plantHour)
            set(Calendar.MINUTE, plantMinute)
        }

        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmMgr.setRepeating(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            calendar.timeInMillis,
            ///3600000 -> 1 hour in millisecons
            ///24 - > number of hour in 1 day
            ///3600000 * 24 * (plant.wateringFreq?.toLong() ?: 1) -> 1 day * water frequency days
            3600000 * 24 * (plant.wateringFreq?.toLong() ?: 1),
            alarmIntent
        )

        reference.child(plant.plantId!!).child("alarmId").setValue(alarmAuxId)
        return
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
package com.example.plantbuddy.core

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.plantbuddy.model.Plant
import com.example.plantbuddy.receivers.ReminderReceiver
import java.util.*

object ReminderManager {

    fun setAlarmForPlant(context: Context, plant: Plant): Boolean
    {
        val alarmId = (0..9999).random()
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, ReminderReceiver::class.java).let { intent ->
            intent.putExtra("name", plant.plantName)
            intent.putExtra("url", plant.imageUrl)
            intent.putExtra("alarmId", alarmId)
            PendingIntent.getBroadcast(context, alarmId, intent, 0)
        }

        val plantMinute = plant.startTime?.substring(3, 5)?.toInt()
        val plantHour = plant.startTime?.substring(0, 2)?.toInt()
        if(plantHour == null || plantMinute == null)
        {
            return false;
        }
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
//            set(Calendar.HOUR, plantHour)
//            set(Calendar.MINUTE, plantMinute)
            add(Calendar.MINUTE, 1)
        }

        alarmMgr?.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            1000 * 60 * 2,
            alarmIntent
        )

        //Alarms was set with success
        return true
    }

    fun cancelAlarm(context: Context, alarmId:Int)
    {
        val alarmIntent: PendingIntent = PendingIntent.getBroadcast(
            context, alarmId,
            Intent(context, ReminderReceiver::class.java),
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmMgr.cancel(alarmIntent)
    }
}
package com.example.plantbuddy

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.plantbuddy.core.UserCore
import com.example.plantbuddy.receivers.ReminderReceiver
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var myPlantButton: Button
    lateinit var addNewPlantButton: Button
    lateinit var seeRemindersButton: Button
    lateinit var settingsButton: Button
    lateinit var weatherButton: Button
    lateinit var locationButton: Button
    lateinit var logoutButton: Button
    lateinit var username: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()

        myPlantButton.setOnClickListener {
            val intent = Intent(this, MyPlantsActivity::class.java)
            startActivity(intent)
        }

        addNewPlantButton.setOnClickListener {
            val intent = Intent(this, AddNewPlantsActivity::class.java)
            startActivity(intent)
        }

        seeRemindersButton.setOnClickListener {
            val intent = Intent(this, SeeRemindersActivity::class.java)
            startActivity(intent)
        }

        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        weatherButton.setOnClickListener {
            val intent = Intent(this, WeatherActivity::class.java)
            startActivity(intent)
        }

        locationButton.setOnClickListener {
            val intent = Intent(this, LocationActivity::class.java)
            startActivity(intent)
        }

        logoutButton.setOnClickListener{
            signOut()
        }
    }

    public override fun onResume(){
        super.onResume()
        setUsername()
    }

    private fun signOut() {
        UserCore.signOut()

        Toast.makeText(
            this@MainActivity, "logout",
            Toast.LENGTH_SHORT
        ).show()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun setUsername() {
        if(UserCore.checkIfLoggedIn())
            username.text=UserCore.user?.email;
        else
            username.text="not logged in"
    }

    private fun initializeViews()
    {
        myPlantButton = findViewById(R.id.buttonMyPlants)
        addNewPlantButton = findViewById(R.id.buttonAddNewPlants)
        seeRemindersButton = findViewById(R.id.buttonSeeReminders)
        settingsButton = findViewById(R.id.buttonSettings)
        weatherButton = findViewById(R.id.buttonWeather)
        locationButton = findViewById(R.id.buttonLocation)
        logoutButton = findViewById(R.id.buttonLogoutMain)
        username = findViewById(R.id.userView)
    }
}




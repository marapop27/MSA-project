package com.example.plantbuddy

import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    lateinit var myPlantButton: Button
    lateinit var addNewPlantButton: Button
    lateinit var seeRemindersButton: Button
    lateinit var settingsButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
//        }
        initializeViews();

        myPlantButton.setOnClickListener {
            val intent = Intent(this, MyPlantsActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
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
    }

    private fun initializeViews()
    {
        myPlantButton = findViewById(R.id.buttonMyPlants)
        addNewPlantButton = findViewById(R.id.buttonAddNewPlants)
        seeRemindersButton = findViewById(R.id.buttonSeeReminders)
        settingsButton = findViewById(R.id.buttonSettings)
    }
}
package com.example.plantbuddy

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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    lateinit var myPlantButton: Button
    lateinit var addNewPlantButton: Button
    lateinit var seeRemindersButton: Button
    lateinit var settingsButton: Button
    lateinit var logoutButton: Button
    lateinit var username: TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
        val currentUser = auth.currentUser
        checkIfLoggedIn(currentUser)
        setContentView(R.layout.activity_main)

//        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
//        }
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

        logoutButton.setOnClickListener{
            signOut()
        }
    }

    public override fun onResume(){
        super.onResume()
        username.text="not logged in"
        val currentUser = auth.currentUser
            if(currentUser != null)
                updateUI(currentUser)
    }

    private fun signOut() {
        auth.signOut()

        Toast.makeText(
            this@MainActivity, "logout",
            Toast.LENGTH_SHORT
        ).show()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun updateUI(firebaseUser: FirebaseUser) {
        username.text=firebaseUser.getEmail();
    }

    private fun initializeViews()
    {
        myPlantButton = findViewById(R.id.buttonMyPlants)
        addNewPlantButton = findViewById(R.id.buttonAddNewPlants)
        seeRemindersButton = findViewById(R.id.buttonSeeReminders)
        settingsButton = findViewById(R.id.buttonSettings)
        logoutButton = findViewById(R.id.buttonLogoutMain)
        username = findViewById(R.id.userView)
    }

    private fun checkIfLoggedIn(user: FirebaseUser?) {
        if (user == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}




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
    lateinit var loginButton: Button
    lateinit var logoutButton: Button
    lateinit var username: TextView
//    lateinit var check: TextView
//    var i:Int = 0
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
//        }
        initializeViews();

        myPlantButton.setOnClickListener {
//            i++
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

        loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        logoutButton.setOnClickListener{
            signOut()
        }
    }

//    public override fun onStart() {
//        super.onStart()
//        // Check if user is signed in (non-null) and update UI accordingly.
//        val currentUser = auth.currentUser
//        if(currentUser != null)
//            updateUI(currentUser)
//    }
    public override fun onResume(){
        super.onResume()
//        check.text = "not logged"
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
    }

    private fun updateUI(firebaseUser: FirebaseUser) {
//        check.text=i.toString()
        username.text=firebaseUser.getEmail();
    }

    private fun initializeViews()
    {
        myPlantButton = findViewById(R.id.buttonMyPlants)
        addNewPlantButton = findViewById(R.id.buttonAddNewPlants)
        seeRemindersButton = findViewById(R.id.buttonSeeReminders)
        settingsButton = findViewById(R.id.buttonSettings)
        loginButton = findViewById(R.id.buttonLoginMain)
        logoutButton = findViewById(R.id.buttonLogoutMain)
        username = findViewById(R.id.userView)
//        check = findViewById(R.id.check)
    }
}




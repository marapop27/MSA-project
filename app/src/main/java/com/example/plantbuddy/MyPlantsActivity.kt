package com.example.plantbuddy

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plantbuddy.core.ReminderManager
import com.example.plantbuddy.helpers.showMessageSnackbar
import com.example.plantbuddy.model.Plant
import com.example.plantbuddy.recyclerView.FirebasePlantsRecyclerViewAdapter
import com.example.plantbuddy.recyclerView.TopSpacingItemDecoration
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_my_plants.*

class MyPlantsActivity : AppCompatActivity(), FirebasePlantsRecyclerViewAdapter.Interaction {

    private lateinit var firebasePlantsAdapter: FirebasePlantsRecyclerViewAdapter
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_plants)

        progressBar = findViewById(R.id.progressBar);
        val backButton = findViewById<ImageView>(R.id.btn_toolbar_back);
        backButton.setOnClickListener {
            onBackPressed();
        }

        initRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        progressBar.visibility = View.VISIBLE
        firebasePlantsAdapter.startListening()


    }

    override fun onPause() {
        super.onPause()
        firebasePlantsAdapter.stopListening()
    }

    private fun initRecyclerView(){
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val rootRef = FirebaseDatabase.getInstance().reference
        val plantReference = rootRef.child("plants")
            .orderByChild("userId")
            .equalTo(currentUser?.uid)

        val options: FirebaseRecyclerOptions<Plant> =
            FirebaseRecyclerOptions.Builder<Plant>()
                .setQuery(plantReference, Plant::class.java)
                .build()
        firebasePlantsAdapter = FirebasePlantsRecyclerViewAdapter(options, this)

        recycler_view.apply {
            layoutManager = LinearLayoutManager(this@MyPlantsActivity)
            val topSpacingDecoration = TopSpacingItemDecoration(30)
            addItemDecoration(topSpacingDecoration)
            adapter = firebasePlantsAdapter
        }
    }

    override fun onDeletePlant(plant: Plant) {
        val rootRef = FirebaseDatabase.getInstance().reference
        rootRef.child("plants").child(plant.plantId.toString()).removeValue().addOnSuccessListener {
            showMessageSnackbar(findViewById<CoordinatorLayout>(R.id.coordinatorLayout), "${plant.plantName} was deleted!")
        }
    }

    override fun onEditPlant(plant: Plant) {
        val intent = Intent(this, AddNewPlantsActivity :: class.java)
        intent.putExtra("plant", plant)
        startActivity(intent)
    }

    override fun onSetReminder(plant: Plant) {
        ReminderManager.setAlarmForPlant(this, plant)
        showMessageSnackbar(coordinatorLayout, "Reminder was set!")
    }

    override fun onDataChanged() {
        progressBar.visibility = View.GONE
    }
}
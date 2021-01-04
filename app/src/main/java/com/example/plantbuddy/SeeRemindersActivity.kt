package com.example.plantbuddy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plantbuddy.core.ReminderManager
import com.example.plantbuddy.core.UserCore
import com.example.plantbuddy.model.Plant
import com.example.plantbuddy.recyclerView.RemindersRecyclerViewAdapter
import com.example.plantbuddy.recyclerView.TopSpacingItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_my_plants.*

class SeeRemindersActivity : AppCompatActivity(), RemindersRecyclerViewAdapter.Interaction {
    private lateinit var reminderAdapter: RemindersRecyclerViewAdapter
    private lateinit var progressBarContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_see_reminders)

        progressBarContainer = findViewById(R.id.progress_bar_container);
        var backButton = findViewById<ImageView>(R.id.btn_toolbar_back);
        backButton.setOnClickListener {
            onBackPressed();
        }

        initRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        progressBarContainer.visibility = View.VISIBLE
    }

    private fun initRecyclerView(){
        reminderAdapter = RemindersRecyclerViewAdapter(this)
        recycler_view.apply {
            layoutManager = LinearLayoutManager(this@SeeRemindersActivity)
            val topSpacingDecoration = TopSpacingItemDecoration(30)
            addItemDecoration(topSpacingDecoration)
            adapter = reminderAdapter
        }

        val rootRef = FirebaseDatabase.getInstance().reference
        rootRef.child("plants").orderByChild("userId").equalTo(UserCore.user?.uid)
            .addValueEventListener(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    var plantList = ArrayList<Plant>()
                    for (postSnapshot in snapshot.children) {
                        var plant = postSnapshot.getValue(Plant::class.java);
                        if(plant == null || plant.alarmId == -1)
                        {
                            continue
                        }
                        plantList.add(plant)
                    }

                    reminderAdapter.itemList = plantList
                    reminderAdapter.notifyDataSetChanged()
                    progressBarContainer.visibility = View.GONE
                }
            })
    }

    override fun onDeleteAlarm(plant: Plant) {
        ReminderManager.cancelAlarm(this, plant.plantId, plant.alarmId)
    }

    override fun onDataChanged() {
        progressBarContainer.visibility = View.GONE
    }
}
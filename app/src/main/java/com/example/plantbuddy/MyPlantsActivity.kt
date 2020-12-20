package com.example.plantbuddy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plantbuddy.model.Plant
import com.example.plantbuddy.recyclerView.BlogRecyclerAdapter
import com.example.plantbuddy.recyclerView.DataSource
import com.example.plantbuddy.recyclerView.TopSpacingItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_my_plants.*
import kotlinx.android.synthetic.main.toolbar_with_back_button.*

class MyPlantsActivity : AppCompatActivity() {

    private lateinit var blogAdapter: BlogRecyclerAdapter
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_plants)

        var backButton = findViewById<ImageView>(R.id.btn_toolbar_back);
        backButton.setOnClickListener {
            onBackPressed();
        }

//        deletePlant("-MOx5YqnsoJVYY2o8Tn0")

        getPlants()

        initRecyclerView()
        addDataSet()
    }

    private fun getPlants() {
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val rootRef = FirebaseDatabase.getInstance().reference
        val plantReference = rootRef.child("plants")
                                    .orderByChild("userId")
                                    .equalTo(currentUser?.uid)
        val plantListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    val planName = ds.child("plantName").getValue(String::class.java)
                    Log.d("plantutza", planName)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("Read from Firebase Error", databaseError.getMessage())
            }
        }
        plantReference.addListenerForSingleValueEvent(plantListener)
    }

    private fun updatePlant() {
        val intent = Intent(this, UpdatePlantActivity::class.java)
        startActivity(intent)
    }

    private fun deletePlant(plantId: String) {
        val rootRef = FirebaseDatabase.getInstance().reference
        rootRef.child("plants").child(plantId).removeValue()
        Log.d("deleteplantutza", "yes")
    }

    private fun addDataSet(){
        val data = DataSource.createDataSet()
        blogAdapter.submitList(data)
    }

    private fun initRecyclerView(){

        recycler_view.apply {
            layoutManager = LinearLayoutManager(this@MyPlantsActivity)
            val topSpacingDecoration = TopSpacingItemDecoration(30)
            addItemDecoration(topSpacingDecoration)
            blogAdapter = BlogRecyclerAdapter()
            adapter = blogAdapter
        }
    }
}
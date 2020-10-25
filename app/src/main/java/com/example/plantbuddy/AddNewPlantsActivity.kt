package com.example.plantbuddy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class AddNewPlantsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_plants)

        var backButton = findViewById<ImageView>(R.id.btn_toolbar_back);
        backButton.setOnClickListener {
            onBackPressed();
        }
    }
}
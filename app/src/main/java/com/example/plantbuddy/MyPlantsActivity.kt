package com.example.plantbuddy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.ImageView
import kotlinx.android.synthetic.main.toolbar_with_back_button.*

class MyPlantsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_plants)

        var backButton = findViewById<ImageView>(R.id.btn_toolbar_back);
        backButton.setOnClickListener {
            onBackPressed();
        }
    }
}
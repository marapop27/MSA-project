package com.example.plantbuddy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class SeeRemindersActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_see_reminders)

        var backButton = findViewById<ImageView>(R.id.btn_toolbar_back);
        backButton.setOnClickListener {
            onBackPressed();
        }
    }


}
package com.aideo.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class PrivacyPolicy : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)

        var backButton = findViewById<TextView>(R.id.back_button)
        backButton.setOnClickListener{
            finish()
        }



    }
}
package com.aideo.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class ContentCreators : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content_creators)


        var backButton = findViewById<TextView>(R.id.back_button)
        backButton.setOnClickListener{
            finish()
        }
    }
}
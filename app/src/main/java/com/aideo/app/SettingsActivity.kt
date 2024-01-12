package com.aideo.app

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class SettingsActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        var privacyPolicy = findViewById<TextView>(R.id.privacyPolicy)

        privacyPolicy.setOnClickListener{
            var intent = Intent(this@SettingsActivity, PrivacyPolicy::class.java)
            startActivity(intent)
        }


        var contentCreators = findViewById<TextView>(R.id.content_creators)

        contentCreators.setOnClickListener{
            var intent = Intent(this@SettingsActivity, ContentCreators::class.java)
            startActivity(intent)
        }



        var feedback = findViewById<TextView>(R.id.feedback)

        feedback.setOnClickListener{
            var intent = Intent(this@SettingsActivity, FeedBackActivity::class.java)
            startActivity(intent)
        }


        var backButton = findViewById<TextView>(R.id.back_button)
        backButton.setOnClickListener{
            finish()
        }
    }
}
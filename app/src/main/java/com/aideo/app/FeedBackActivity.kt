package com.aideo.app

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class FeedBackActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_back)

        var back_button = findViewById<TextView>(R.id.back_button)
        back_button.setOnClickListener{
            finish()
        }

        var submit = findViewById<TextView>(R.id.submit)
        var  message = findViewById<TextView>(R.id.et_message)

        var  list = arrayOf("connect@aideo.in")


        submit.setOnClickListener {
            sendEmailDirectly("Feedback", message.text.toString(), list)
        }
    }



    fun sendEmailDirectly(subject: String, message: String, recipients: Array<String>) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")

        // Set the email recipients
        intent.putExtra(Intent.EXTRA_EMAIL, recipients)

        // Set the email subject
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)

        // Set the email message
        intent.putExtra(Intent.EXTRA_TEXT, message)

        try {
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
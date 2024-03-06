package com.android.myapplication

import android.content.ContentUris
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat.IntentBuilder
import com.aideo.app.R
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File

class ImageViewerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_viewer)
        var path: String? = null
        val imageView = findViewById<ImageView>(R.id.imageView)
        val intent = intent
        if (intent != null) {
            Glide.with(this@ImageViewerActivity).load(intent.getStringExtra("video"))
                .placeholder(R.drawable.ic_baseline_broken_image_24).into(imageView)
            path = intent.getStringExtra("image")
        }
        val share = findViewById<ImageButton>(R.id.shareImage)
        val finalPath = path
        share.setOnClickListener { v: View? ->
            IntentBuilder(this@ImageViewerActivity).setStream(
                Uri.parse(finalPath)
            ).setType("video/*").setChooserTitle("Share Image").startChooser()
        }
        val delete = findViewById<ImageButton>(R.id.deleteImage)
        delete.setOnClickListener {
            val alertDialogBuilder = MaterialAlertDialogBuilder(this@ImageViewerActivity)
            alertDialogBuilder.setMessage("Are you sure you want to delete this image ?")
            alertDialogBuilder.setPositiveButton("Yes") { dialog, which ->
                val projection = arrayOf(MediaStore.Images.Media._ID)
                val selection = MediaStore.Images.Media.DATA + " = ?"
                val selectionArgs = arrayOf(File(finalPath).absolutePath)
                val queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                val contentResolver = contentResolver
                val cursor =
                    contentResolver.query(queryUri, projection, selection, selectionArgs, null)
                if (cursor!!.moveToFirst()) {
                    val id =
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                    val deleteUri =
                        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    try {
                        contentResolver.delete(deleteUri, null, null)
                        val delete1 = File(finalPath).delete()
                        Log.e("TAG", delete1.toString() + "")
                        Toast.makeText(
                            this@ImageViewerActivity,
                            "Deleted Successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@ImageViewerActivity,
                            "Error Deleting Video",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(this@ImageViewerActivity, "File Not Find", Toast.LENGTH_SHORT)
                        .show()
                }
                cursor.close()
            }
            alertDialogBuilder.setNegativeButton("No") { dialog, which -> dialog.dismiss() }
            alertDialogBuilder.show()
        }
    }
}
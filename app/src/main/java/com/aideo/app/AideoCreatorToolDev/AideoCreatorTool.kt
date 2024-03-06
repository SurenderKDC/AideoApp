package com.aideo.app.AideoCreatorToolDev

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aideo.app.R
import com.android.myapplication.Image
import com.android.myapplication.ImageAdapter
import java.io.File

class AideoCreatorTool : AppCompatActivity() {
    var recyclerView: RecyclerView? = null
    var arrayList = ArrayList<Image>()
    private val activityResultLauncher = registerForActivityResult(
        RequestPermission()
    ) { result: Boolean ->
        if (result) {
            images
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.aideo_creator_tool)
        recyclerView = findViewById(R.id.recycler)
        recyclerView!!.setLayoutManager(LinearLayoutManager(this@AideoCreatorTool))
        recyclerView!!.setHasFixedSize(true)
        if (ActivityCompat.checkSelfPermission(
                this@AideoCreatorTool,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            activityResultLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } else if (ActivityCompat.checkSelfPermission(
                this@AideoCreatorTool,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            activityResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            images
        }
    }

    override fun onResume() {
        super.onResume()
        images
    }

    private val images: Unit
        private get() {
            arrayList.clear()
            val filePath = "/storage/emulated/0/Pictures/AideoProject"
            val file = File(filePath)
            val files = file.listFiles()
            if (files != null) {
                for (file1 in files)
                {
                    Log.d("file name","${file1}")

                    if (file1.path.endsWith(".png")
                        || file1.path.endsWith(".jpg")
                        || file1.path.endsWith(".mp4")
                        || file1.path.endsWith(".mp3"))
                    {
                        arrayList.add(Image(file1.name, file1.path, file1.length()))
                    }
                }
            }
            val adapter = ImageAdapter(this@AideoCreatorTool, arrayList)
            recyclerView!!.adapter = adapter
        }
}
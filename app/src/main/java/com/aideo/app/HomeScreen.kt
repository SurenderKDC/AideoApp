package com.aideo.app

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aideo.app.Adapters.OuterRecyclerViewAdapter
import com.aideo.app.Models.SportsApi
import com.aideo.app.Models.Topic
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.Locale

class HomeScreen : AppCompatActivity() {
    private val videos = ArrayList<Topic>()
    private lateinit var recyclerView: RecyclerView

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 2
    var currentCityName : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)

        var settings = findViewById<TextView>(R.id.settings)
        settings.setOnClickListener{
            var intent = Intent(this@HomeScreen, SettingsActivity::class.java)
            startActivity(intent)
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        recyclerView = findViewById(R.id.outerRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        var back_button = findViewById<TextView>(R.id.back_button)
        back_button.setOnClickListener{
            finish()
        }

        getLocation()
    }

    private fun getTopics(cityName: String): ArrayList<Topic> {
        var list = ArrayList<Topic>()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://cmsbe.aideo.in/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        Log.d("MainActivity", "Name: 1")

        val api = retrofit.create(SportsApi::class.java)
        val call = api.getSportsTopic(cityName)

        Log.d("MainActivity", "Name: 2")

        call.enqueue(object : Callback<ArrayList<Topic>> {
            override fun onResponse(call: Call<ArrayList<Topic>>, response: Response<ArrayList<Topic>>) {
                if (response.isSuccessful) {
                    val sportsTopics = response.body()
                    if (sportsTopics != null)
                    {
                        Log.d("mainActivity Rj", "Name: length ${sportsTopics}")

                        list = sportsTopics


                        recyclerView.adapter = OuterRecyclerViewAdapter(sportsTopics)

                        Log.d("MainActivity", "Name: 4")

                        for (sportsTopic in sportsTopics) {
                            Log.d("MainActivity", "Name: ${sportsTopic.name}")
                            Log.d("MainActivity", "Description: ${sportsTopic.description}")
                            // Access other properties as needed
                        }
                    }
                }
                else {
                    Log.e("MainActivity", " Suri API call failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ArrayList<Topic>>, t: Throwable) {
                Log.e("MainActivity", " Suri API call failed: ${t.message}")
            }
        })

        return list;
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location != null) {

                        Log.d("location fetch","${location}")

                        val geocoder = Geocoder(this, Locale.getDefault())

                        try {
                            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                            if (addresses!!.isNotEmpty()) {
                                val cityName = addresses[0]?.locality
                                if (cityName != null) {

                                    Log.d("home apicalling api", "$cityName")

                                    currentCityName = cityName.toLowerCase()

                                    getTopics(cityName.toLowerCase())
                                }
                            }
                        } catch (e: IOException) {
                            // Handle the exception
                        }

                        this.apply {
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }
}
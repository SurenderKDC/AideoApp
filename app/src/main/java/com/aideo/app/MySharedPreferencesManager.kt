package com.aideo.app

import android.content.Context
import android.content.SharedPreferences

class MySharedPreferencesManager(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun saveString(key: String, value: String) {
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    // Add more methods for other data types as needed
}
















//
//
//package com.aideo.app
//
//import android.Manifest
//import android.annotation.SuppressLint
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.graphics.Bitmap
//import android.graphics.drawable.Drawable
//import android.location.Geocoder
//import android.location.Location
//import android.location.LocationManager
//import android.location.LocationRequest
//import android.media.MediaMetadataRetriever
//import android.media.MediaPlayer
//import android.net.Uri
//import android.os.Bundle
//import android.os.CountDownTimer
//import android.os.Environment
//import android.os.Handler
//import android.os.Looper
//import android.provider.Settings
//import android.util.Log
//import android.view.MotionEvent
//import android.view.View
//import android.view.WindowManager
//import android.widget.RelativeLayout
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.constraintlayout.widget.ConstraintLayout
//import androidx.core.app.ActivityCompat
//import androidx.core.content.FileProvider
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import androidx.viewpager2.widget.ViewPager2
//import com.bumptech.glide.Glide
//import com.bumptech.glide.request.target.Target
//import com.bumptech.glide.load.DataSource
//import com.bumptech.glide.load.engine.DiskCacheStrategy
//import com.bumptech.glide.load.engine.GlideException
//import com.bumptech.glide.request.RequestListener
//import com.bumptech.glide.request.RequestOptions
//import com.aideo.app.Adapters.ClickFunctionality
//import com.aideo.app.Adapters.StatusAdapter
//import com.aideo.app.Adapters.VideoAdapter
//import com.aideo.app.ApiCalling.Audio
//import com.aideo.app.ApiCalling.Background
//import com.aideo.app.ApiCalling.ContentData
//import com.aideo.app.ApiCalling.Image
//import com.aideo.app.ApiCalling.PlaylistData
//import com.aideo.app.ApiCalling.PrefsVideoResponse
//import com.aideo.app.ApiCalling.ReloadApiCall
//import com.aideo.app.ApiCalling.Segment
//import com.aideo.app.ApiCalling.SingleVideoCallApiWithId
//import com.aideo.app.ApiCalling.Video
//import com.aideo.app.ApiCalling.VideoContentsApi
//import com.aideo.app.ApiCalling.logsApi
//import com.aideo.app.Models.PostRequest
//import com.aideo.app.Models.PostResponse
//import com.aideo.app.databinding.ActivityMainBinding
//import com.google.android.exoplayer2.MediaItem
//import com.google.android.exoplayer2.Player
//import com.google.android.exoplayer2.SimpleExoPlayer
//import com.google.android.exoplayer2.source.ConcatenatingMediaSource
//import com.google.android.exoplayer2.source.ProgressiveMediaSource
//import com.google.android.exoplayer2.ui.PlayerView
//import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
//import com.google.android.gms.location.FusedLocationProviderClient
//import com.google.android.gms.location.LocationCallback
//import com.google.android.gms.location.LocationServices
//import com.google.gson.Gson
//import com.google.gson.JsonArray
//import com.google.gson.JsonElement
//import com.google.gson.JsonObject
//import com.malkinfo.viewpager2.ZoomOutPageTransformer
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.GlobalScope
//import kotlinx.coroutines.launch
//import okhttp3.OkHttpClient
//import org.json.JSONArray
//import org.json.JSONObject
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import java.io.File
//import java.io.FileOutputStream
//import java.io.IOException
//import java.util.Locale
//import java.util.concurrent.TimeUnit
//
//val baseUrlForMedia : String = "https://aideobe.kdcstaging.in/controllers/Content/uploads/"
//val baseUrlForImage : String = "https://aideobe.kdcstaging.in/"
//var GlobleSplash = 1
//
//class MainActivity : AppCompatActivity() , ClickFunctionality {
//    private lateinit var binding: ActivityMainBinding
//    private var player: SimpleExoPlayer? = null
//
//    private var currentVideoPosition : Int = 0
//    private var adapterPosition : Int = 0
//    lateinit var showBackVideo : RelativeLayout
//    private var mediaPlayer: MediaPlayer? = null
//    private var middleMediaPlayer : MediaPlayer? = null
//    lateinit var playerView : PlayerView
//    private var exoplayerUrlPosition = -1
//    var firstVideo : Int = 0
//
//    var countDownTimer: CountDownTimer? = null
//
//    var middleVideoPlaying = 0
//
//    var recyclerView : RecyclerView? = null
//
//    var screenWidthDp : Int = 0
//
//    var adapter : StatusAdapter? = null
//    var mediaSourceCantat : ConcatenatingMediaSource? = null
//    var pageNum : Int = 1
//    private var locationRequest: LocationRequest? = null
//    private var locationCallback: LocationCallback? = null
//    private val handler = Handler()
//    private val delayMillis = 1000L
//    var watch_duration : Int = 0
//    var last_duration : Int = 0
//    var currentCityName : String = ""
//    private lateinit var mFusedLocationClient: FusedLocationProviderClient
//    private val permissionId = 2
//    var isActivityOpened = 1
//    var currentInterval = 0.0
//    var locationenabled = 1;
//
//    override fun onCreate(savedInstanceState: Bundle?)
//    {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
//
//        val displayMetrics = resources.displayMetrics
//        val screenWidthPx = displayMetrics.widthPixels
//        val density = displayMetrics.density
//        screenWidthDp = screenWidthPx
//
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//
//        mediaSourceCantat = ConcatenatingMediaSource()
//
//        Log.d("screenWidthDp", "${screenWidthDp}")
//
//        playerView = findViewById<PlayerView>(R.id.player_view)
//
//        videos.clear()
//
//        mediaPlayer = MediaPlayer()
//
//        middleMediaPlayer = MediaPlayer()
//
//        showBackVideo = findViewById(R.id.showBackVideo)
//
//        loadPrefsData()
//
//        val receivedData : String? = intent?.getStringExtra("key")
//
//        Log.d("received data", "${receivedData}")
//
//        val uri: Uri? = intent.data
//        uri?.let {
//            // Handle the deep link URL and extract any relevant data
//            var host = it.host
//            var path = it.path
//
//            path = path?.replace("/","")
//
//            getSingleVideoWithId(path.toString())
//
//        }
//
//        if(receivedData.toString() != "null" && receivedData.toString() != "")
//        {
//            CoroutineScope(Dispatchers.Main).launch {
//
//                getSingleVideoWithId(receivedData.toString())
//
//                getLocation()
//            }
//        }
//        else
//        {
//            getLocation()
//        }
//
//
//
//
//        // Create a new SimpleExoPlayer instance
//        player = SimpleExoPlayer.Builder(this).build()
//
//        var home_button = findViewById<TextView>(R.id.home_button)
//
//        home_button.setOnClickListener{
//            stopMiddlePlayback()
//            stopPlayback()
//
//            if(player != null)
//            {
//                player?.pause()
//                player?.playWhenReady = false
//            }
//
//
//            isActivityOpened = 0
//
//            var intent = Intent(this@MainActivity, HomeScreen::class.java)
//            startActivity(intent)
//        }
//
//        recyclerView = findViewById<RecyclerView>(R.id.status_recyclerview)
//
//        startRepeatingMethodCalls()
//
//        startRepeatingForProgress()
//
//        binding.tvShare.setOnClickListener(View.OnClickListener {
//            shareFile(videos[adapterPosition].thumbnail.toString(), videos[adapterPosition].id.toString());
//        })
//
//        binding.tvShare2.setOnClickListener(View.OnClickListener {
//            shareFile(videos[adapterPosition].thumbnail.toString(), videos[adapterPosition].id.toString());
//        })
//
//        binding.tvReadMore.setOnClickListener {
//            try {
//                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(videos[adapterPosition].callToAction))
//                startActivity(browserIntent)
//            }
//            catch (e : Exception){}
//        }
//
//        binding.viewPager.setOnTouchListener { _, event ->
//            when (event.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    // Handle touch down event
//
//                    Log.d("Handle touch down event", "Handle touch down event")
//                }
//                MotionEvent.ACTION_UP -> {
//                    // Handle touch up event
//
//                    Log.d("Handle touch up event", "Handle touch up event")
//                }
//                // Handle other touch events if needed
//            }
//            true // Return true to indicate that the touch event is consumed
//        }
//
//
//
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
//        )
//
//        // we used the postDelayed(Runnable, time) method
//        // to send a message with a delayed time.
//        //Normal Handler is deprecated , so we have to change the code little bit
//
//
//        if(GlobleSplash == 1)
//        {
//            binding.splashScreen.visibility = View.VISIBLE
//        }
//    }
//
//    fun loadPrefsData()
//    {
//        val responseData = MySharedPreferencesManager(this@MainActivity).getString("VideoResponse", "suri")
//
//        Log.d("prefs respo", "${responseData}")
//
//
//        try {
//            val playlist = Gson().fromJson(responseData, PrefsVideoResponse::class.java)
//
//            var playlistData = playlist.playListData
//
//
//            if (playlistData != null)
//            {
//                playlistData.shuffle()
//
//                for (data in playlistData)
//                {
//                    if(data.contentData != null)
//                    {
//                        var obj = JSONObject(data.contentData)
//                        var segmentsArray: JSONArray = obj.optJSONArray("Segments")
//                        var backgroundJson = obj.getJSONObject("Background")
//
//
//                        val segments = mutableListOf<Segment>()
//
//                        // Iterate over the JSONArray elements
//                        for (i in 0 until segmentsArray.length()) {
//                            // Get the segment object at the current index
//                            val segmentObject: JSONObject = segmentsArray.getJSONObject(i)
//
//                            // Extract the properties from the segment object
//                            val chapter: Int = segmentObject.getInt("Chapter")
//                            val imageObject: JSONObject? = segmentObject.optJSONObject("Image")
//                            val audioObject: JSONObject? = segmentObject.optJSONObject("Audio")
//                            val videoObject: JSONObject? = segmentObject.optJSONObject("Video")
//
//                            // Parse the image object if it exists
//                            val image: Image? = imageObject?.let {
//                                val source: String = "${baseUrlForMedia}${data._id}/"+ it.getString("Source")
//                                val interval: Int = it.getInt("Interval")
//                                Image(source, interval)
//                            }
//
//                            // Parse the audio object if it exists
//                            val video: Video? = videoObject?.let {
//                                val source: String = "${baseUrlForMedia}${data._id}/"+ it.getString("Source")
//                                Video(source)
//                            }
//
//                            val audio: Audio? = audioObject?.let {
//                                val source: String = "${baseUrlForMedia}${data._id}/"+ it.getString("Source")
//                                Audio("suri",source)
//                            }
//
//                            val segment = Segment(chapter, image, video, audio)
//                            segments.add(segment)
//                        }
//
//                        val videoJson = backgroundJson.optJSONObject("Video")
//                        val audioJson = backgroundJson.optJSONObject("Audio")
//                        val imageJson = backgroundJson.optJSONObject("Image")
//
//
//                        val videoSource: Video? = videoJson?.let {
//                            val source: String = "${baseUrlForMedia}${data._id}/"+ it.getString("Source")
//                            Video(source)
//                        }
//
//                        val audioSource: Audio? = audioJson?.let {
//                            val source: String = "${baseUrlForMedia}${data._id}/"+ it.getString("Source")
//                            Audio("suri",source)
//                        }
//
//                        val imageSource: Image? = imageJson?.let {
//                            val source: String = "${baseUrlForMedia}${data._id}/"+ it.getString("Source")
//                            val interval: Int = it.getInt("Interval")
//                            Image(source, interval)
//                        }
//
//
//                        val background = Background(
//                            video = videoSource,
//                            audio = audioSource,
//                            image = imageSource,
//                            exoplayerUrlPosition = 0
//                        )
//
//                        if(data.liveStatus != "Disabled")
//                        {
//                            var contentData = ContentData(version = 1, currentIndex = 0, background = background, segments = segments, id = data._id, title = data.title, description = data.description,  thumbnail = baseUrlForImage + "" + data.thumbnail, tagsData = data.tags, callToAction = data.callToAction)
//                            videos.add(contentData)
//                        }
//                    }
//                }
//
//
//            }
//        }
//        catch (e : Exception)
//        {
//
//        }
//
//
//        Log.d("prefs respo", "${responseData}")
//
//        preparePlayer(videos)
//
//        binding.viewPager.adapter = VideoAdapter(this@MainActivity, videos, this@MainActivity)
//        binding.viewPager.setPageTransformer(ZoomOutPageTransformer())
//
//        adapter = StatusAdapter(videos, adapterPosition, screenWidthDp)
//        recyclerView?.adapter = adapter
//        recyclerView?.layoutManager = LinearLayoutManager(this@MainActivity,LinearLayoutManager.HORIZONTAL, false)
//
//        setupPageChangeCallback()
//
//        binding.splashScreen.visibility = View.GONE
//        GlobleSplash = 0
//    }
//
//    fun getSingleVideoWithId(id : String) : ArrayList<ContentData> {
//        val timeoutInSeconds = 30L // Adjust this value as needed
//
//        val okHttpClient = OkHttpClient.Builder()
//            .connectTimeout(timeoutInSeconds, TimeUnit.SECONDS)
//            .readTimeout(timeoutInSeconds, TimeUnit.SECONDS)
//            .writeTimeout(timeoutInSeconds, TimeUnit.SECONDS)
//            .build()
//
//        val retrofit = Retrofit.Builder()
//            .baseUrl("https://aideobe.kdcstaging.in/api/v1/")
//            .addConverterFactory(GsonConverterFactory.create())
//            .client(okHttpClient)
//            .build()
//
//        val api = retrofit.create(SingleVideoCallApiWithId::class.java)
//        val call = api.getVideoContentsTopic(id)
//
//
//        Log.e("Api calling start", "suri rathore")
//
//        call.enqueue(object : Callback<PlaylistData> {
//            override fun onResponse(call: Call<PlaylistData>, response: Response<PlaylistData>) {
//                if (response.isSuccessful)
//                {
//                    Log.d("rakesh 1", "rakesh ${id}   ${response.body()}")
//
//                    val playlistData = response.body()
//                    if (playlistData != null)
//                    {
//                        if(playlistData.contentData != null)
//                        {
//                            var obj = JSONObject(playlistData.contentData)
//                            var segmentsArray: JSONArray = obj.optJSONArray("Segments")
//                            var backgroundJson = obj.getJSONObject("Background")
//
//
//
//                            val segments = mutableListOf<Segment>()
//
//                            // Iterate over the JSONArray elements
//                            for (i in 0 until segmentsArray.length()) {
//                                // Get the segment object at the current index
//                                val segmentObject: JSONObject = segmentsArray.getJSONObject(i)
//
//                                // Extract the properties from the segment object
//                                val chapter: Int = segmentObject.getInt("Chapter")
//                                val imageObject: JSONObject? = segmentObject.optJSONObject("Image")
//                                val audioObject: JSONObject? = segmentObject.optJSONObject("Audio")
//                                val videoObject: JSONObject? = segmentObject.optJSONObject("Video")
//
//                                // Parse the image object if it exists
//                                val image: Image? = imageObject?.let {
//                                    val source: String = "${baseUrlForMedia}${playlistData._id}/"+ it.getString("Source")
//                                    val interval: Int = it.getInt("Interval")
//                                    Image(source, interval)
//                                }
//
//                                // Parse the audio object if it exists
//                                val video: Video? = videoObject?.let {
//                                    val source: String = "${baseUrlForMedia}${playlistData._id}/"+ it.getString("Source")
//                                    Video(source)
//                                }
//
//                                val audio: Audio? = audioObject?.let {
//                                    val source: String = "${baseUrlForMedia}${playlistData._id}/"+ it.getString("Source")
//                                    Audio("suri",source)
//                                }
//
//                                val segment = Segment(chapter, image, video, audio)
//                                segments.add(segment)
//                            }
//
//                            val videoJson = backgroundJson.optJSONObject("Video")
//                            val audioJson = backgroundJson.optJSONObject("Audio")
//                            val imageJson = backgroundJson.optJSONObject("Image")
//
//
//                            val videoSource: Video? = videoJson?.let {
//                                val source: String = "${baseUrlForMedia}${playlistData._id}/"+ it.getString("Source")
//                                Video(source)
//                            }
//
//                            val audioSource: Audio? = audioJson?.let {
//                                val source: String = "${baseUrlForMedia}${playlistData._id}/"+ it.getString("Source")
//                                Audio("suri",source)
//                            }
//
//                            val imageSource: Image? = imageJson?.let {
//                                val source: String = "${baseUrlForMedia}${playlistData._id}/"+ it.getString("Source")
//                                val interval: Int = it.getInt("Interval")
//                                Image(source, interval)
//                            }
//
//
//                            val background = Background(
//                                video = videoSource,
//                                audio = audioSource,
//                                image = imageSource,
//                                exoplayerUrlPosition = 0
//                            )
//
//                            if(playlistData.liveStatus != "Disabled")
//                            {
//                                var contentData = ContentData(version = 1, currentIndex = 0, background = background, segments = segments, id = playlistData._id, title = playlistData.title, description = playlistData.description, thumbnail = baseUrlForImage + "" +playlistData.thumbnail, tagsData = playlistData.tags, callToAction = playlistData.callToAction)
//                                videos.add(contentData)
//                            }
//                        }
//                    }
//                }
//                else {
//                    Log.e("MainActivity", " Suri API call failed: ${response.code()}")
//                }
//            }
//
//            override fun onFailure(call: Call<PlaylistData>, t: Throwable) {
//                Log.e("MainActivity", " Suri rj API call failed: ${t.message}")
//            }
//        })
//
//        return videos;
//    }
//
//    fun getPlaylistContent(cityName : String) : ArrayList<ContentData> {
//
//        val timeoutInSeconds = 30L // Adjust this value as needed
//
//        val okHttpClient = OkHttpClient.Builder()
//            .connectTimeout(timeoutInSeconds, TimeUnit.SECONDS)
//            .readTimeout(timeoutInSeconds, TimeUnit.SECONDS)
//            .writeTimeout(timeoutInSeconds, TimeUnit.SECONDS)
//            .build()
//
//
//        val retrofit = Retrofit.Builder()
//            .baseUrl("https://aideobe.kdcstaging.in/api/v1/")
//            .addConverterFactory(GsonConverterFactory.create())
//            .client(okHttpClient)
//            .build()
//
//
//        val api = retrofit.create(VideoContentsApi::class.java)
//        val call = api.getVideoContentsTopic(cityName)
//
//        Log.e("Api suri start", "suri rathore")
//
//        call.enqueue(object : Callback<JsonElement> {
//            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
//
//                Log.e("Api suri end", "suri end ")
//
//                if (response.isSuccessful)
//                {
//                    try {
//                        MySharedPreferencesManager(this@MainActivity).saveString("VideoResponse", "{\"playListData\":${response.body().toString()}}")
//                    }
//                    catch (e : Exception) { }
//                }
//                else
//                {
//                    Log.e("MainActivity", " Suri API call failed: ${response.code()}")
//                }
//            }
//
//            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
//                Log.e("MainActivity", " Suri rj API call failed: ${t.message}")
//            }
//        })
//        return videos;
//    }
//
//    private fun setupPageChangeCallback() {
//        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//
//            // Triggered when you select a new page
//            override fun onPageSelected(position: Int) {
//                super.onPageSelected(position)
//
//
//                Log.d("onPageSelected","${position}")
//
//                stopMiddlePlayback()
//                stopPlayback()
//
//                if(isActivityOpened == 1)
//                {
//
//                    videos[position].currentIndex = 0
//                    middleVideoPlaying = 0
//
//                    showBackVideo.visibility = View.GONE
//                    binding.imageView.visibility = View.GONE
//
//                    try{
//
//                        adapter = StatusAdapter(videos, position, screenWidthDp)
//                        recyclerView?.adapter = adapter
//
//
//                        GlobalScope.launch(Dispatchers.IO) {
//                            adapter?.allItemUpdateAdapter()
//                            adapter?.updateAdapter(videos[position].currentIndex, 0.0)
//                        }
//
//                        if(videos[position].segments?.get(videos[position].currentIndex)?.image?.source != null)
//                        {
//                            yourMethod(position)
//                        }
//                    }
//                    catch (e :Exception){}
//
//
//                    if(videos[position].background?.video?.source != null)
//                    {
//                        Log.d("back video called","${videos[position].background?.video?.source}")
//
//                        // video
//                        currentVideoPosition = currentVideoPosition + 1
//
//                        videos[position].background?.exoplayerUrlPosition?.let {
//                            player?.seekToDefaultPosition(
//                                it
//                            )
//                        }
//                        player?.playWhenReady = true
//                        player?.play()
//                        player!!.repeatMode =  Player.REPEAT_MODE_ONE
//                        showBackVideo.visibility = View.VISIBLE
//                    }
//                    else
//                    {
//                        player?.playWhenReady = false
//                        player?.pause()
//                        showBackVideo.visibility = View.GONE
//                    }
//
//                    try{
//                        if(videos[position].segments?.get(videos[position].currentIndex)?.video?.source != null)
//                        {
//                            showMiddleVideo(videos[position].segments?.get(videos[position].currentIndex)!!.exoPlayerUrlPos, position)
//                        }
//                    }
//                    catch (e :Exception){}
//
//                    GlobalScope.launch(Dispatchers.IO) {
//                        try {
//                            if (videos[position].segments?.get(videos[position].currentIndex)?.audio?.source != null) {
//                                setMiddleVideoTrack(videos[position].segments?.get(videos[position].currentIndex)?.audio?.source.toString())
//                            }
//                        } catch (e: Exception) {
//                        }
//                    }
//
//
//                    GlobalScope.launch(Dispatchers.IO) {
//                        try {
//                            if (videos[position].background?.audio?.source != null) {
//                                playNextTrack(videos[position].background?.audio?.source.toString())
//                            }
//                        } catch (e: Exception) {
//                        }
//                    }
//                }
//
//
//                if(videos[position].callToAction != null && videos[position].callToAction.toString() != "")
//                {
//                    binding.share2.visibility = View.VISIBLE
//                    binding.share1.visibility = View.GONE
//                }
//                else
//                {
//                    binding.share2.visibility = View.GONE
//                    binding.share1.visibility = View.VISIBLE
//                }
//
//                adapterPosition = position
//            }
//
//            /**
//             * This method will be invoked when the current page is scrolled,
//             * either as part of a programmatically initiated
//             * smooth scroll or a user initiated touch scroll.
//             */
//            override fun onPageScrolled(
//                position: Int,
//                positionOffset: Float,
//                positionOffsetPixels: Int
//            ) {
//                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
//
//                Log.d("onPageScrolled","${position}")
//
//                binding.imageView.visibility = View.GONE
//            }
//
//
//
//
//            /**
//             * Called when the scroll state changes.
//             * Useful for discovering when the user begins dragging,
//             * when a fake drag is started, when the pager is automatically settling
//             * to the current page, or when it is fully stopped/idle.
//             * state can be one of SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING or SCROLL_STATE_SETTLING.
//             */
//            override fun onPageScrollStateChanged(state: Int) {
//                super.onPageScrollStateChanged(state)
//
//                Log.d("onPageScrollState","${state}")
//
//            }
//        }
//        )
//    }
//
//    override fun onBackPressed() {
//        super.onBackPressed()
//    }
//
//    private fun preparePlayer(videoUrls: ArrayList<ContentData>) {
//
//        val dataSourceFactory = DefaultDataSourceFactory(this, "your_user_agent")
//
//        for (videoUrl in videoUrls)
//        {
//            if(firstVideo == 0 && videoUrl.background?.video?.source == null)
//            {
//                firstVideo = 1
//                val mediaItem = MediaItem.fromUri("https://aideobe.kdcstaging.in/controllers/Content/uploads/64c4eec976feff29b693a7c5/1.mp4")
//                val mediaSourceItem = ProgressiveMediaSource.Factory(dataSourceFactory)
//                    .createMediaSource(mediaItem)
//                mediaSourceCantat?.addMediaSource(mediaSourceItem)
//            }
//            else
//            {
//
//                Log.d("videos url " , "${videoUrl.background?.video?.source}")
//
//                val mediaItem = MediaItem.fromUri("${videoUrl.background?.video?.source}")
//                val mediaSourceItem = ProgressiveMediaSource.Factory(dataSourceFactory)
//                    .createMediaSource(mediaItem)
//                mediaSourceCantat?.addMediaSource(mediaSourceItem)
//            }
//
//            exoplayerUrlPosition = exoplayerUrlPosition + 1
//
//            videoUrl.background?.exoplayerUrlPosition = exoplayerUrlPosition
//
//        }
//
//        for (videoUrl in videoUrls)
//        {
//            if(videoUrl.segments != null)
//            {
//                for (segments in videoUrl.segments)
//                {
//                    if(segments.video?.source != null)
//                    {
//
//                        Log.d("videos url " , "${segments.video.source}")
//
//                        val mediaItem = MediaItem.fromUri("${segments.video.source}")
//                        val mediaSourceItem = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
//                        mediaSourceCantat?.addMediaSource(mediaSourceItem)
//
//                        exoplayerUrlPosition = exoplayerUrlPosition + 1
//                        segments.exoPlayerUrlPos = exoplayerUrlPosition
//                    }
//                }
//            }
//        }
//
//        // Prepare the player with the concatenated media source
//        player?.setMediaSource(mediaSourceCantat!!)
//
//        player?.repeatMode =  Player.REPEAT_MODE_ONE
//
//        player?.prepare()
//
//        player?.addListener(object : Player.Listener {
//            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
//                when (playbackState) {
//                    Player.STATE_IDLE -> {
//                        // Player is idle
//                    }
//                    Player.STATE_BUFFERING -> {
//                        // Player is buffering
//                    }
//                    Player.STATE_READY -> {
//                        // Player is ready to play
//                    }
//                    Player.STATE_ENDED -> {
//                        // Playback ended
//                    }
//                }
//            }
//
//            override fun onPositionDiscontinuity(reason: Int) {
//                Log.d("onPositionDiscontinuit ", "${reason}")
//
//                if(reason == 0 && middleVideoPlaying == 1)
//                {
//                    showBackVideo.visibility = View.GONE
//                    middleVideoPlaying = 0
//
//                    Log.d("surender kumar ", "${reason}")
//
//                    var model = videos[adapterPosition]
//
//                    model.currentIndex++
//
//                    adapter?.updateAdapter(model.currentIndex,0.0)
//
//
//                    try {
//                        if((videos[adapterPosition].segments!!.size ) == model.currentIndex)
//                        {
//                            model.currentIndex = 0
//                            adapter?.allItemUpdateAdapter()
//
//
//                            binding.imageView.visibility = View.GONE
//
//                            binding.viewPager.currentItem = adapterPosition + 1
//                        }
//                    }
//                    catch (e : Exception){}
//
//
//                    player?.pause()
//                    player?.playWhenReady = false
//
//                    try {
//                        if (videos[adapterPosition].segments?.get(model.currentIndex)?.image != null) {
//                            yourMethod(adapterPosition)
//                        }
//                    }catch (e : Exception){}
//
//                    try {
//                        if (videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)?.video?.source != null) {
//                            showMiddleVideo(videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)!!.exoPlayerUrlPos,adapterPosition)
//                        }
//                    } catch (e : Exception) {}
//                }
//            }
//        }
//        )
//    }
//
//    private fun reloadPreparePlayer(videoUrls: ArrayList<ContentData>) {
//
//        val dataSourceFactory = DefaultDataSourceFactory(this, "your_user_agent")
//
//        for (videoUrl in videoUrls)
//        {
//            if(firstVideo == 0 && videoUrl.background?.video?.source == null)
//            {
//                firstVideo = 1
//                val mediaItem = MediaItem.fromUri("https://aideobe.kdcstaging.in/controllers/Content/uploads/64a93158013061b463045fd3/ShopQuote1.mp4")
//                val mediaSourceItem = ProgressiveMediaSource.Factory(dataSourceFactory)
//                    .createMediaSource(mediaItem)
//                mediaSourceCantat?.addMediaSource(mediaSourceItem)
//            }
//            else
//            {
//                val mediaItem = MediaItem.fromUri("${videoUrl.background?.video?.source}")
//                val mediaSourceItem = ProgressiveMediaSource.Factory(dataSourceFactory)
//                    .createMediaSource(mediaItem)
//                mediaSourceCantat?.addMediaSource(mediaSourceItem)
//            }
//
//            exoplayerUrlPosition = exoplayerUrlPosition + 1
//
//        }
//
//        for (videoUrl in videoUrls)
//        {
//            if(videoUrl.segments != null)
//            {
//                for (segments in videoUrl.segments)
//                {
//                    if(segments.video?.source != null)
//                    {
//                        val mediaItem = MediaItem.fromUri("${segments.video.source}")
//                        val mediaSourceItem = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
//                        mediaSourceCantat?.addMediaSource(mediaSourceItem)
//
//                        exoplayerUrlPosition = exoplayerUrlPosition + 1
//                        segments.exoPlayerUrlPos = exoplayerUrlPosition
//                    }
//                }
//            }
//        }
//
//        // Prepare the player with the concatenated media source
//        player?.setMediaSource(mediaSourceCantat!!)
//
//    }
//
//    override fun onStart() {
//        super.onStart()
//        playerView.player = player
//
//        isActivityOpened = 1
//    }
//
//    override fun onRestart() {
//        super.onRestart()
//
//        Log.d("onRestart called", "onRestart")
//
//        isActivityOpened = 1
//
//        var model = videos[adapterPosition]
//        model.currentIndex = 0
//
//        adapter?.allItemUpdateAdapter()
//
//        if(videos[adapterPosition].background?.video?.source != null)
//        {
//            // video
//            currentVideoPosition = currentVideoPosition + 1
//            player?.seekToDefaultPosition(adapterPosition)
//            player?.playWhenReady = true
//            player?.play()
//            player!!.repeatMode =  Player.REPEAT_MODE_ONE
//            showBackVideo.visibility = View.VISIBLE
//        }
//
//
//        try{
//            if(videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)?.image?.source != null)
//            {
//                yourMethod(adapterPosition)
//            }
//        }
//        catch (e :Exception){}
//
//
//        try{
//            if(videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)?.video?.source != null)
//            {
//                showMiddleVideo(videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)!!.exoPlayerUrlPos,adapterPosition)
//            }
//        }
//        catch (e :Exception){}
//
//
//        try{
//            if(videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)?.audio?.source != null)
//            {
//                GlobalScope.launch(Dispatchers.IO)
//                {
//                    setMiddleVideoTrack(videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)?.audio?.source.toString())
//                }
//            }
//        }
//        catch (e :Exception){}
//
//        try{
//            if (videos[adapterPosition].background?.audio?.source != null) {
//                playNextTrack(videos[adapterPosition].background?.audio?.source.toString())
//            }
//        }
//        catch (e : Exception){}
//
//    }
//
//    override fun onResume() {
//        super.onResume()
//
//        Log.d("onResume called", "onResume")
//
//        try{
//            if(locationenabled == 0)
//            {
//                locationenabled = 1
//                isActivityOpened = 1
//
//                var model = videos[adapterPosition]
//                model.currentIndex = 0
//
//                adapter?.allItemUpdateAdapter()
//
//                if(videos[adapterPosition].background?.video?.source != null)
//                {
//                    // video
//                    currentVideoPosition = currentVideoPosition + 1
//                    player?.seekToDefaultPosition(adapterPosition)
//                    player?.playWhenReady = true
//                    player?.play()
//                    player!!.repeatMode =  Player.REPEAT_MODE_ONE
//                    showBackVideo.visibility = View.VISIBLE
//                }
//
//
//                try{
//                    if(videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)?.image?.source != null)
//                    {
//                        yourMethod(adapterPosition)
//                    }
//                }
//                catch (e :Exception){}
//
//
//                try{
//                    if(videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)?.video?.source != null)
//                    {
//                        showMiddleVideo(videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)!!.exoPlayerUrlPos,adapterPosition)
//                    }
//                }
//                catch (e :Exception){}
//
//
//                try{
//                    if(videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)?.audio?.source != null)
//                    {
//                        GlobalScope.launch(Dispatchers.IO) {
//                            setMiddleVideoTrack(videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)?.audio?.source.toString())
//                        }
//                    }
//                }
//                catch (e :Exception){}
//
//                try{
//                    if (videos[adapterPosition].background?.audio?.source != null) {
//                        GlobalScope.launch(Dispatchers.IO) {
//                            playNextTrack(videos[adapterPosition].background?.audio?.source.toString())
//                        }
//                    }
//                }
//                catch (e : Exception){}
//            }
//
//        }
//        catch (e : Exception){}
//    }
//
//    override fun onPause() {
//        super.onPause()
//
//        isActivityOpened = 0
//
//        if(player != null) {
//            player?.playWhenReady = false
//            player?.pause()
//        }
//
//        if(countDownTimer != null)
//        {
//            countDownTimer!!.cancel()
//        }
//
//        stopPlayback()
//
//        stopMiddlePlayback()
//    }
//
//    override fun onStop() {
//        super.onStop()
//
//        isActivityOpened = 0
//
//        if(player != null) {
//            player?.playWhenReady = false
//            player?.pause()
//        }
//
//        if(countDownTimer != null)
//        {
//            countDownTimer!!.cancel()
//        }
//
//
//        stopPlayback()
//
//        stopMiddlePlayback()
//    }
//
//    private fun playNextTrack(url: String) {
//        try {
//            mediaPlayer?.reset()
//            mediaPlayer?.setDataSource(url)
//            mediaPlayer?.prepare()
//            mediaPlayer?.isLooping = true
//            mediaPlayer?.start()
//        }
//        catch (e : Exception){}
//    }
//
//    private fun setMiddleVideoTrack(url: String) {
//
//        try {
//            middleMediaPlayer?.reset()
//            middleMediaPlayer?.setDataSource(url)
//            middleMediaPlayer?.prepare()
//            middleMediaPlayer?.isLooping = true
//            middleMediaPlayer?.start()
//        } catch (e : Exception){}
//    }
//
//    fun stopPlayback() {
//        try {
//            mediaPlayer?.stop()
//            mediaPlayer?.reset()
//        }
//        catch (e : Exception){}
//    }
//
//    fun stopMiddlePlayback() {
//        try {
//            middleMediaPlayer?.stop()
//            middleMediaPlayer?.reset()
//        }
//        catch (e : Exception){}
//    }
//
//    fun showMiddleVideo(middleVideoPosition:Int,position: Int) {
//        middleVideoPlaying = 1
//
//        try {
//            binding.imageView.visibility = View.GONE
//            showBackVideo.visibility = View.VISIBLE
//            player?.seekToDefaultPosition(middleVideoPosition)
//            player?.playWhenReady = true
//            player?.play()
//
//            try {
//                val msec = MediaPlayer.create(this, Uri.parse(videos[position].segments?.get(videos[position].currentIndex)?.video?.source)).duration
//                currentInterval = msec.toDouble()
//            }
//            catch (e : Exception){}
//        }catch (e : Exception){}
//
//    }
//
//    fun yourMethod(position:Int) {
//
//
//
//        if(countDownTimer != null)
//        {
//            countDownTimer!!.cancel()
//        }
//
//        val model = videos[position]
//
//        var interval = videos[position].segments?.get(model.currentIndex)?.image?.interval!!.toLong()
//
//        currentInterval = interval.toDouble()
//
//        if(videos[position].segments?.get(model.currentIndex)?.audio?.source != null)
//        {
//            var mp3Duration = getMp3Duration(videos[position].segments?.get(model.currentIndex)?.audio?.source.toString())
//
//            if(interval < mp3Duration)
//            {
//                interval = mp3Duration
//
//                currentInterval = interval.toDouble()
//            }
//        }
//
//        try {
//
//            GlobalScope.launch(Dispatchers.IO) {
//                val drawable = Glide.with(this@MainActivity)
//                    .load(videos[position].segments?.get(model.currentIndex)?.image?.source)
//                    .placeholder(R.drawable.plaeholder_image)
//                    .listener(object : com.bumptech.glide.request.RequestListener<Drawable> {
//                        override fun onLoadFailed(
//                            e: GlideException?,
//                            model: Any?,
//                            target: Target<Drawable>?,
//                            isFirstResource: Boolean
//                        ): Boolean {
//
//                            return false
//                        }
//
//                        override fun onResourceReady(
//                            resource: Drawable?,
//                            model: Any?,
//                            target: Target<Drawable>?, // Specify the type argument here (Drawable)
//                            dataSource: com.bumptech.glide.load.DataSource?,
//                            isFirstResource: Boolean
//                        ): Boolean {
//
//                            try {
//
//                                val model = videos[position]
//
//                                if (videos[position].segments?.get(model.currentIndex + 1)?.image?.source != null) {
//
//                                    Glide.with(this@MainActivity)
//                                        .load(videos[position].segments?.get(model.currentIndex + 1)?.image?.source)
//                                        .preload(500, 500)
//                                }
//                            } catch (e: Exception) {
//                            }
//
//                            return false
//                        }
//                    })
//                    .submit()
//                    .get()
//
//                launch(Dispatchers.Main) {
//                    binding.imageView.visibility = View.VISIBLE
//                    binding.imageView.setImageDrawable(drawable)
//                }
//            }
//        }
//        catch (e: Exception) {
//        }
//
//
//        countDownTimer = object : CountDownTimer(interval, interval) {
//            override fun onTick(millisUntilFinished: Long) {
//                // This method is called every intervalInMillis (1 second in this case)
//                val secondsRemaining = millisUntilFinished / 1000
//                // Update UI to show remaining time (e.g., update a TextView)
//            }
//
//            override fun onFinish() {
//                model.currentIndex++
//
//                Log.d("surender kumar error", "onFinish")
//
//
//                if((videos[adapterPosition].segments!!.size ) == model.currentIndex)
//                {
//                    model.currentIndex = 0
//                    adapter?.allItemUpdateAdapter()
//
//                    binding.imageView.visibility = View.GONE
//
//                    binding.viewPager.currentItem = adapterPosition + 1
//                }
//
//
//                if(middleVideoPlaying == 0)
//                {
//                    adapter?.updateAdapter(model.currentIndex,0.0)
//
//                    try {
//                        if (videos[adapterPosition].segments?.get(model.currentIndex)?.image != null) {
//                            yourMethod(adapterPosition)
//                        }
//
//                        if(videos[position].segments?.get(videos[position].currentIndex)?.video?.source != null)
//                        {
//                            showMiddleVideo(videos[position].segments?.get(videos[position].currentIndex)!!.exoPlayerUrlPos,adapterPosition)
//                        }
//
//                        if(videos[position].segments?.get(videos[position].currentIndex)?.audio?.source != null)
//                        {
//                            GlobalScope.launch(Dispatchers.IO) {
//                                setMiddleVideoTrack(videos[position].segments?.get(videos[position].currentIndex)?.audio?.source.toString())
//                            }
//                        }
//                    } catch (e: Exception) {
//                        model.currentIndex = 0
//                        adapter?.allItemUpdateAdapter()
//                        if (videos[position].segments?.get(model.currentIndex)?.image != null) {
//                            yourMethod(position)
//                        }
//                    }
//                }
//            }
//        }
//
//        // Start the countdown timer
//        if(countDownTimer != null)
//        {
//            countDownTimer!!.start()
//        }
//    }
//
//    fun getMp3Duration(filePath: String): Long {
//        val retriever = MediaMetadataRetriever()
//        retriever.setDataSource(filePath)
//
//        val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
//        val duration = durationStr?.toLongOrNull() ?: 0
//
//        retriever.release()
//
//        return duration
//    }
//
//    private fun startRepeatingMethodCalls() {
//        handler.postDelayed(object : Runnable {
//            override fun run() {
//                // Call the method
//                watch_duration = watch_duration + 1
//
//                Log.d("after every ", "${watch_duration}")
//
//
//
////                var dur =  currentInterval / 1000
////
////                try {
////                        var model = videos[adapterPosition]
////
////                    adapter?.updateProgress(model.currentIndex,(((screenWidthDp / videos[adapterPosition].segments!!.size) - 10)/dur).toInt())
////                }
////                catch (e: Exception){}
//
//                // Schedule the next call after the delay
//                handler.postDelayed(this, delayMillis)
//            }
//        }, delayMillis)
//    }
//
//    private fun startRepeatingForProgress() {
//        handler.postDelayed(object : Runnable {
//            override fun run() {
//                // Call the method
//
//                if(locationenabled == 1)
//                {
//                    var dur =  currentInterval / 100
//
//                    try {
//                        var model = videos[adapterPosition]
//
//                        adapter?.updateProgress(model.currentIndex,(((screenWidthDp / videos[adapterPosition].segments!!.size) -  12)/dur))
//                    }
//                    catch (e: Exception){}
//                }
//
//                // Schedule the next call after the delay
//                handler.postDelayed(this, 100)
//            }
//        }, 100)
//    }
//
//    @SuppressLint("MissingPermission", "SetTextI18n")
//    private fun getLocation() {
//        if (checkPermissions()) {
//            if (isLocationEnabled()) {
//                if (ActivityCompat.checkSelfPermission(
//                        this,
//                        Manifest.permission.ACCESS_FINE_LOCATION
//                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                        this,
//                        Manifest.permission.ACCESS_COARSE_LOCATION
//                    ) != PackageManager.PERMISSION_GRANTED
//                ) {
//                    // TODO: Consider calling
//                    //    ActivityCompat#requestPermissions
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission. See the documentation
//                    // for ActivityCompat#requestPermissions for more details.
//                    return
//                }
//                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
//                    val location: Location? = task.result
//                    if (location != null) {
//
//                        Log.d("location fetch","${location}")
//
//                        val geocoder = Geocoder(this, Locale.getDefault())
//
//                        try {
//                            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
//                            if (addresses!!.isNotEmpty()) {
//                                val cityName = addresses[0]?.locality
//                                if (cityName != null) {
//
//                                    Log.d("apicalling api", "$cityName")
//
//                                    currentCityName = cityName.toLowerCase()
//
//                                    getPlaylistContent(cityName.toLowerCase())
//                                }
//                            }
//                        } catch (e: IOException) {
//                            // Handle the exception
//                        }
//
//                        this.apply {
//                        }
//                    }
//                }
//            } else {
//                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
//                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//                startActivity(intent)
//            }
//        } else {
//            requestPermissions()
//        }
//    }
//
//    private fun isLocationEnabled(): Boolean {
//        val locationManager: LocationManager =
//            getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
//            LocationManager.NETWORK_PROVIDER
//        )
//    }
//
//    private fun checkPermissions(): Boolean {
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED &&
//            ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            return true
//        }
//        return false
//    }
//
//    private fun requestPermissions() {
//        ActivityCompat.requestPermissions(
//            this,
//            arrayOf(
//                Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ),
//            permissionId
//        )
//    }
//
//    @SuppressLint("MissingSuperCall")
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        if (requestCode == permissionId) {
//            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//
//                locationenabled = 0
//                getLocation()
//            }
//        }
//    }
//
//
//    private fun shareFile(imageUrl : String, videoId : String)
//    {
//        val requestOptions = RequestOptions().apply {
//            skipMemoryCache(true)
//            diskCacheStrategy(DiskCacheStrategy.NONE)
//        }
//
//        Glide.with(this)
//            .asBitmap()
//            .load(imageUrl)
//            .apply(requestOptions)
//            .listener(object : RequestListener<Bitmap> {
//                override fun onLoadFailed(
//                    e: GlideException?,
//                    model: Any?,
//                    target: com.bumptech.glide.request.target.Target<Bitmap>?,
//                    isFirstResource: Boolean
//                ): Boolean {
//                    return false
//                }
//
//                override fun onResourceReady(
//                    resource: Bitmap?,
//                    model: Any?,
//                    target: com.bumptech.glide.request.target.Target<Bitmap>?,
//                    dataSource: DataSource?,
//                    isFirstResource: Boolean
//                ): Boolean {
//                    resource?.let {
//                        val imageFile = File(
//                            getExternalFilesDir(Environment.DIRECTORY_PICTURES),
//                            "image.jpg"
//                        )
//                        val outputStream = FileOutputStream(imageFile)
//                        resource.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
//                        outputStream.flush()
//                        outputStream.close()
//
//                        shareImage(imageFile, videoId)
//                    }
//                    return true
//                }
//            })
//            .submit()
//    }
//
//    private fun shareImage(imageFile: File, videoId:String) {
//        val shareIntent = Intent(Intent.ACTION_SEND)
//        shareIntent.type = "image/*"
//        val uri = FileProvider.getUriForFile(
//            this,
//            "com.aideo.yourapp.fileprovider",
//            imageFile
//        )
//
//        val shareBody = "https://aideo.kdcstaging.in/$videoId"
//
//        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
//        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
//        startActivity(Intent.createChooser(shareIntent, "Share Image"))
//
//
//        locationenabled = 0
//    }
//
//    override fun clickOnLeftSide() {
//        var model = videos[adapterPosition]
//
//        if(model.currentIndex > 0)
//        {
//            model.currentIndex--
//        }
//
//        if((videos[adapterPosition].segments!!.size) == model.currentIndex)
//        {
//            model.currentIndex = 0
//            adapter?.allItemUpdateAdapter()
//
//
//            binding.imageView.visibility = View.GONE
//
//            binding.viewPager.currentItem = adapterPosition + 1
//        }
//
//        adapter?.updateAdapter(model.currentIndex,0.0)
//
//        if(middleVideoPlaying == 1)
//        {
//            middleVideoPlaying = 0
//            player!!.pause()
//            player!!.playWhenReady = false
//            showBackVideo.visibility = View.GONE
//        }
//
//
//
//        try{
//            if (videos[adapterPosition].segments?.get(model.currentIndex)?.image != null) {
//                yourMethod(adapterPosition)
//            }
//        }
//        catch (e : Exception){}
//
//
//
//        try{
//            if(videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)?.video?.source != null)
//            {
//                showMiddleVideo(videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)!!.exoPlayerUrlPos,adapterPosition)
//            }
//        }
//        catch (e : Exception){}
//
//
//        try{
//            if(videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)?.audio?.source != null)
//            {
//                GlobalScope.launch(Dispatchers.IO) {
//                    setMiddleVideoTrack(videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)?.audio?.source.toString())
//                }
//            }
//        }
//        catch (e : Exception){}
//    }
//
//    override fun clickOnRightSide() {
//        var model = videos[adapterPosition]
//
//        model.currentIndex++
//
//        if((videos[adapterPosition].segments!!.size) == model.currentIndex)
//        {
//            model.currentIndex = 0
//            adapter?.allItemUpdateAdapter()
//
//
//            binding.imageView.visibility = View.GONE
//
//            binding.viewPager.currentItem = adapterPosition + 1
//        }
//
//        adapter?.updateAdapter(model.currentIndex,0.0)
//
//        if(middleVideoPlaying == 1)
//        {
//            middleVideoPlaying = 0
//            try{
//                player!!.pause()
//                player!!.playWhenReady = false
//
//                showBackVideo.visibility = View.GONE
//
//            }
//            catch (e : Exception){}
//        }
//
//
//
//
//
//        try{
//            if (videos[adapterPosition].segments?.get(model.currentIndex)?.image != null) {
//                yourMethod(adapterPosition)
//            }
//        }
//        catch (e : Exception){}
//
//
//        try{
//            if(videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)?.video?.source != null)
//            {
//                showMiddleVideo(videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)!!.exoPlayerUrlPos,adapterPosition)
//            }
//        }
//        catch (e : Exception){}
//
//
//        try{
//            if(videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)?.audio?.source != null)
//            {
//                GlobalScope.launch(Dispatchers.IO) {
//                    setMiddleVideoTrack(videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)?.audio?.source.toString())
//                }
//            }
//        }
//        catch (e : Exception){}
//
//    }
//
//}
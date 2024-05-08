package com.aideo.app

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.location.LocationRequest
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.aideo.app.Adapters.ClickFunctionality
import com.aideo.app.Adapters.StatusAdapter
import com.aideo.app.Adapters.VideoAdapter
import com.aideo.app.ApiCalling.Audio
import com.aideo.app.ApiCalling.Background
import com.aideo.app.ApiCalling.ContentData
import com.aideo.app.ApiCalling.Image
import com.aideo.app.ApiCalling.PlaylistData
import com.aideo.app.ApiCalling.PrefsVideoResponse
import com.aideo.app.ApiCalling.Segment
import com.aideo.app.ApiCalling.SingleVideoCallApiWithId
import com.aideo.app.ApiCalling.Video
import com.aideo.app.ApiCalling.VideoContentsApi
import com.aideo.app.ApiCalling.logsApi
import com.aideo.app.Models.PostRequest
import com.aideo.app.Models.PostResponse
import com.aideo.app.databinding.ActivityMainBinding
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.malkinfo.viewpager2.ZoomOutPageTransformer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

class HemePlayerScreen : AppCompatActivity() , ClickFunctionality {
    private lateinit var binding: ActivityMainBinding
    private var player: SimpleExoPlayer? = null

    private val reloadVideos = ArrayList<ContentData>()

    private var currentVideoPosition : Int = 0
    private var adapterPosition : Int = 0
    lateinit var showBackVideo : RelativeLayout
    private var mediaPlayer: MediaPlayer? = null
    private var middleMediaPlayer : MediaPlayer? = null
    lateinit var playerView : PlayerView
    private var exoplayerUrlPosition = -1
    var firstVideo : Int = 0

    var countDownTimer: CountDownTimer? = null

    var middleVideoPlaying = 0

    var recyclerView : RecyclerView? = null

    var screenWidthDp : Int = 0

    var adapter : StatusAdapter? = null
    var viewPagerAdapter : VideoAdapter? = null

    var mediaSourceCantat : ConcatenatingMediaSource? = null

    var imageLoaded : Int = 1

    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null

    private val handler = Handler()
    private val delayMillis = 1000L

    var watch_duration : Int = 0
    var last_duration : Int = 0

    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private val permissionId = 2

    var isActivityOpened = 1

    var currentInterval = 0.0

    var locationenabled = 1

    var viewPagerList = ArrayList<Int>()

    var isUpdateVidoes = 0

    var prepareNextVideo = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        for(i in 0..250)
        {
            viewPagerList.add(i)
        }


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val displayMetrics = resources.displayMetrics
        val screenWidthPx = displayMetrics.widthPixels
        val density = displayMetrics.density
        screenWidthDp = screenWidthPx

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        mediaSourceCantat = ConcatenatingMediaSource()

        Log.d("screenWidthDp", "${screenWidthDp}")

        playerView = findViewById<PlayerView>(R.id.player_view)

        mediaPlayer = MediaPlayer()

        middleMediaPlayer = MediaPlayer()

        showBackVideo = findViewById(R.id.showBackVideo)

        val receivedData : String? = intent?.getStringExtra("key")

        Log.d("received data", "${receivedData}")

        val uri: Uri? = intent.data
        uri?.let {
            // Handle the deep link URL and extract any relevant data
            var host = it.host
            var path = it.path

            path = path?.replace("/","")

            getSingleVideoWithId(path.toString())

        }

        if(receivedData.toString() != "null" && receivedData.toString() != "")
        {
            getSingleVideoWithId(receivedData.toString())
        }

        // Create a new SimpleExoPlayer instance
        player = SimpleExoPlayer.Builder(this).build()

        var home_button = findViewById<TextView>(R.id.home_button)

        home_button.setOnClickListener{
            stopMiddlePlayback()
            stopPlayback()

            if(player != null)
            {
                player?.pause()
                player?.playWhenReady = false
            }

            isActivityOpened = 0

            var intent = Intent(this, HomeScreen::class.java)
            startActivity(intent)
        }

        recyclerView = findViewById<RecyclerView>(R.id.status_recyclerview)

        startRepeatingMethodCalls()

        startRepeatingForProgress()

        binding.buttonSubmit.setOnClickListener(View.OnClickListener {

            hideKeyboard()

            if(binding.editTextPassword.text.toString() == "12345")
            {
                binding.editTextPassword.setText("")
                videos[adapterPosition].privateType = false
                loadNewPage(adapterPosition)
            }
            else
            {
                binding.editTextPassword.setText("")

                Toast.makeText(this@HemePlayerScreen, "Please Enter a Valid Password", Toast.LENGTH_LONG).show()
            }



        })

        binding.visible.setOnClickListener(View.OnClickListener {
            binding.editTextPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

            binding.editTextPassword.setSelection(binding.editTextPassword.text.length)

            binding.visible.visibility = View.GONE
            binding.invisible.visibility = View.VISIBLE
        })

        binding.invisible.setOnClickListener(View.OnClickListener {
            binding.editTextPassword.inputType = InputType.TYPE_CLASS_TEXT

            binding.editTextPassword.setSelection(binding.editTextPassword.text.length)

            binding.visible.visibility = View.VISIBLE
            binding.invisible.visibility = View.GONE
        })

        binding.tvShare.setOnClickListener(View.OnClickListener {
            shareFile(videos[adapterPosition].thumbnail.toString(), videos[adapterPosition].id.toString());
        })

        binding.tvShare2.setOnClickListener(View.OnClickListener {
            shareFile(videos[adapterPosition].thumbnail.toString(), videos[adapterPosition].id.toString());
        })

        binding.tvReadMore.setOnClickListener {
            try {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(videos[adapterPosition].callToAction))
                startActivity(browserIntent)
            }
            catch (e : Exception){}
        }

        binding.viewPager.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Handle touch down event

                    Log.d("Handle touch down event", "Handle touch down event")
                }
                MotionEvent.ACTION_UP -> {
                    // Handle touch up event

                    Log.d("Handle touch up event", "Handle touch up event")
                }
                // Handle other touch events if needed
            }
            true // Return true to indicate that the touch event is consumed
        }

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        var splash = findViewById<ConstraintLayout>(R.id.splash_screen)

        if(GlobleSplash == 1)
        {
            splash.visibility = View.GONE
        }
        else
        {
            splash.visibility = View.GONE
        }
    }

    fun getSingleVideoWithId(id : String) : ArrayList<ContentData> {

        videos.clear()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://cmsbe.aideo.in/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(SingleVideoCallApiWithId::class.java)
        val call = api.getVideoContentsTopic(id)

        call.enqueue(object : Callback<PlaylistData> {
            override fun onResponse(call: Call<PlaylistData>, response: Response<PlaylistData>) {
                if (response.isSuccessful)
                {
                    Log.d("rakesh 1", "rakesh ${id}   ${response.body()}")

                    val playlistData = response.body()
                    if (playlistData != null)
                    {
                        if(playlistData.contentData != null)
                        {
                            var obj = JSONObject(playlistData.contentData)
                            var segmentsArray: JSONArray = obj.optJSONArray("Segments")
                            var backgroundJson = obj.getJSONObject("Background")



                            val segments = mutableListOf<Segment>()

                            // Iterate over the JSONArray elements
                            for (i in 0 until segmentsArray.length()) {
                                // Get the segment object at the current index
                                val segmentObject: JSONObject = segmentsArray.getJSONObject(i)

                                // Extract the properties from the segment object
                                val chapter: Int = segmentObject.getInt("Chapter")
                                val imageObject: JSONObject? = segmentObject.optJSONObject("Image")
                                val audioObject: JSONObject? = segmentObject.optJSONObject("Audio")
                                val videoObject: JSONObject? = segmentObject.optJSONObject("Video")

                                // Parse the image object if it exists
                                val image: Image? = imageObject?.let {
                                    val source: String = "${baseUrlForMedia}${playlistData._id}/"+ it.getString("Source")
                                    val interval: Int = it.getInt("Interval")
                                    Image(source, interval)
                                }

                                // Parse the audio object if it exists
                                val video: Video? = videoObject?.let {
                                    val source: String = "${baseUrlForMedia}${playlistData._id}/"+ it.getString("Source")
                                    Video(source)
                                }

                                val audio: Audio? = audioObject?.let {
                                    val source: String = "${baseUrlForMedia}${playlistData._id}/"+ it.getString("Source")
                                    Audio("suri",source)
                                }

                                val segment = Segment(chapter, image, video, audio)
                                segments.add(segment)
                            }

                            val videoJson = backgroundJson.optJSONObject("Video")
                            val audioJson = backgroundJson.optJSONObject("Audio")
                            val imageJson = backgroundJson.optJSONObject("Image")


                            try {
                                val videoSource: Video? = videoJson?.let {
                                    val source: String = "${baseUrlForMedia}${playlistData._id}/"+ it.getString("Source")
                                    Video(source)
                                }

                                val audioSource: Audio? = audioJson?.let {
                                    val source: String = "${baseUrlForMedia}${playlistData._id}/"+ it.getString("Source")
                                    Audio("suri",source)
                                }

                                val imageSource: Image? = imageJson?.let {
                                    val source: String = "${baseUrlForMedia}${playlistData._id}/"+ it.getString("Source")
                                    val interval: Int = it.getInt("Interval")
                                    Image(source, interval)
                                }


                                val background = Background(
                                    video = videoSource,
                                    audio = audioSource,
                                    image = imageSource,
                                    exoplayerUrlPosition = 0,
                                    forNextUrlPosition = 0
                                )

                                if(playlistData.liveStatus != "Disabled")
                                {
                                    var contentData = ContentData(version = 1, currentIndex = 0, background = background, segments = segments, id = playlistData._id, title = playlistData.title, description = playlistData.description, thumbnail = baseUrlForImage + "" +playlistData.thumbnail, tagsData = playlistData.tags, callToAction = playlistData.callToAction, privateType = playlistData.privateType)
                                    videos.add(0,contentData)
                                }
                            }
                            catch (e : Exception){}

                        }

                        getPlaylistContent(currentCityName)

                    }
                }
                else {
                    Log.e("MainActivity", " Suri API call failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<PlaylistData>, t: Throwable) {
                Log.e("MainActivity", " Suri rj API call failed: ${t.message}")
            }
        })

        return videos;
    }

    fun getPlaylistContent(cityName : String) : ArrayList<ContentData> {

        val timeoutInSeconds = 30L // Adjust this value as needed

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(timeoutInSeconds, TimeUnit.SECONDS)
            .readTimeout(timeoutInSeconds, TimeUnit.SECONDS)
            .writeTimeout(timeoutInSeconds, TimeUnit.SECONDS)
            .build()


        val retrofit = Retrofit.Builder()
            .baseUrl("https://cmsbe.aideo.in/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()


        val api = retrofit.create(VideoContentsApi::class.java)
        val call = api.getVideoContentsTopic(cityName, reloadApiCount)

        Log.e("Api suri start", "suri rathore")

        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {

                Log.e("Api suri end", "suri end")

                if (response.isSuccessful)
                {
                    try {
                        val playlist = Gson().fromJson("{\"playListData\":${response.body().toString()}}", PrefsVideoResponse::class.java)

                        var playlistData = playlist.playListData

                        if (playlistData != null)
                        {

                            var startIndex = 0
                            var endIndex = 10

                            for(i in 0..playlistData.size)
                            {
                                try {
                                    if(endIndex < playlistData.size)
                                    {
                                        val sublistToShuffle = playlistData.subList(startIndex, endIndex)

                                        // Shuffle the sublist
                                        sublistToShuffle.shuffle()

                                        startIndex = startIndex + 10
                                        endIndex = endIndex + 10
                                    }
                                }
                                catch (e : Exception){}
                            }


                            for (data in playlistData) {
                                if (data.contentData != null) {
                                    var obj = JSONObject(data.contentData)
                                    var segmentsArray: JSONArray = obj.optJSONArray("Segments")
                                    var backgroundJson = obj.getJSONObject("Background")


                                    val segments = mutableListOf<Segment>()

                                    // Iterate over the JSONArray elements
                                    for (i in 0 until segmentsArray.length()) {
                                        // Get the segment object at the current index
                                        val segmentObject: JSONObject = segmentsArray.getJSONObject(i)

                                        // Extract the properties from the segment object
                                        val chapter: Int = segmentObject.getInt("Chapter")
                                        val imageObject: JSONObject? = segmentObject.optJSONObject("Image")
                                        val audioObject: JSONObject? = segmentObject.optJSONObject("Audio")
                                        val videoObject: JSONObject? = segmentObject.optJSONObject("Video")

                                        // Parse the image object if it exists
                                        val image: Image? = imageObject?.let {
                                            val source: String =
                                                "${baseUrlForMedia}${data._id}/" + it.getString("Source")
                                            val interval: Int = it.getInt("Interval")
                                            Image(source, interval)
                                        }

                                        // Parse the audio object if it exists
                                        val video: Video? = videoObject?.let {
                                            val source: String =
                                                "${baseUrlForMedia}${data._id}/" + it.getString("Source")
                                            Video(source)
                                        }

                                        val audio: Audio? = audioObject?.let {
                                            val source: String =
                                                "${baseUrlForMedia}${data._id}/" + it.getString("Source")
                                            Audio("suri", source)
                                        }

                                        val segment = Segment(chapter, image, video, audio)
                                        segments.add(segment)
                                    }

                                    val videoJson = backgroundJson.optJSONObject("Video")
                                    val audioJson = backgroundJson.optJSONObject("Audio")
                                    val imageJson = backgroundJson.optJSONObject("Image")


                                    try{
                                        val videoSource: Video? = videoJson?.let {
                                            val source: String =
                                                "${baseUrlForMedia}${data._id}/" + it.getString("Source")
                                            Video(source)
                                        }

                                        val audioSource: Audio? = audioJson?.let {
                                            val source: String =
                                                "${baseUrlForMedia}${data._id}/" + it.getString("Source")
                                            Audio("suri", source)
                                        }

                                        val imageSource: Image? = imageJson?.let {
                                            val source: String =
                                                "${baseUrlForMedia}${data._id}/" + it.getString("Source")
                                            val interval: Int = it.getInt("Interval")
                                            Image(source, interval)
                                        }


                                        val background = Background(
                                            video = videoSource,
                                            audio = audioSource,
                                            image = imageSource,
                                            exoplayerUrlPosition = 0,
                                            forNextUrlPosition = 0
                                        )

                                        if (data.liveStatus != "Disabled") {
                                            var contentData = ContentData(
                                                version = 1,
                                                currentIndex = 0,
                                                background = background,
                                                segments = segments,
                                                id = data._id,
                                                title = data.title,
                                                description = data.description,
                                                thumbnail = baseUrlForImage + "" + data.thumbnail,
                                                tagsData = data.tags,
                                                callToAction = data.callToAction,
                                                privateType = data.privateType
                                            )
                                            videos.add(contentData)
                                        }
                                    }
                                    catch (e : Exception){}


                                }
                            }

                            preparePlayer(videos)

                            viewPagerAdapter = VideoAdapter(this@HemePlayerScreen, viewPagerList, this@HemePlayerScreen)
                            binding.viewPager.adapter = viewPagerAdapter
                            binding.viewPager.setPageTransformer(ZoomOutPageTransformer())

                            if(videos.size > 0)
                            {
                                viewPagerAdapter?.updateTextValue(videos[adapterPosition].title.toString())
                            }


                            if(videos.size > 0)
                            {
                                if(videos[0].title != null)
                                {
                                    viewPagerAdapter?.updateTextValue(videos[0].title.toString())
                                }
                            }

                            adapter = StatusAdapter(viewPagerList, adapterPosition, screenWidthDp)
                            recyclerView?.adapter = adapter
                            recyclerView?.layoutManager = LinearLayoutManager(this@HemePlayerScreen, LinearLayoutManager.HORIZONTAL, false)


                            setupPageChangeCallback()

                        }
                    }
                    catch (e : Exception){}

                }
                else
                {
                    Log.e("MainActivity", " Suri API call failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.e("MainActivity", " Suri rj API call failed: ${t.message}")
            }
        })
        return videos;
    }

    fun getReloadContent(cityName : String) : ArrayList<ContentData> {

        reloadApiCount = reloadApiCount + 1

        val timeoutInSeconds = 30L // Adjust this value as needed

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(timeoutInSeconds, TimeUnit.SECONDS)
            .readTimeout(timeoutInSeconds, TimeUnit.SECONDS)
            .writeTimeout(timeoutInSeconds, TimeUnit.SECONDS)
            .build()


        val retrofit = Retrofit.Builder()
            .baseUrl("https://cmsbe.aideo.in/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()


        val api = retrofit.create(VideoContentsApi::class.java)
        val call = api.getVideoContentsTopic(cityName, reloadApiCount)

        Log.e("Api suri start", "suri rathore")

        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {

                Log.e("Api suri end", "suri end")

                if (response.isSuccessful)
                {
                    try {
                        val playlist = Gson().fromJson("{\"playListData\":${response.body().toString()}}", PrefsVideoResponse::class.java)

                        var playlistData = playlist.playListData

                        if (playlistData != null)
                        {

                            var startIndex = 0
                            var endIndex = 10

                            for(i in 0..playlistData.size)
                            {
                                try {
                                    if(endIndex < playlistData.size)
                                    {
                                        val sublistToShuffle = playlistData.subList(startIndex, endIndex)

                                        // Shuffle the sublist
                                        sublistToShuffle.shuffle()

                                        startIndex = startIndex + 10
                                        endIndex = endIndex + 10
                                    }
                                }
                                catch (e : Exception){}
                            }


                            for (data in playlistData) {
                                if (data.contentData != null) {
                                    var obj = JSONObject(data.contentData)
                                    var segmentsArray: JSONArray = obj.optJSONArray("Segments")
                                    var backgroundJson = obj.getJSONObject("Background")


                                    val segments = mutableListOf<Segment>()

                                    // Iterate over the JSONArray elements
                                    for (i in 0 until segmentsArray.length()) {
                                        // Get the segment object at the current index
                                        val segmentObject: JSONObject = segmentsArray.getJSONObject(i)

                                        // Extract the properties from the segment object
                                        val chapter: Int = segmentObject.getInt("Chapter")
                                        val imageObject: JSONObject? = segmentObject.optJSONObject("Image")
                                        val audioObject: JSONObject? = segmentObject.optJSONObject("Audio")
                                        val videoObject: JSONObject? = segmentObject.optJSONObject("Video")

                                        // Parse the image object if it exists
                                        val image: Image? = imageObject?.let {
                                            val source: String =
                                                "${baseUrlForMedia}${data._id}/" + it.getString("Source")
                                            val interval: Int = it.getInt("Interval")
                                            Image(source, interval)
                                        }

                                        // Parse the audio object if it exists
                                        val video: Video? = videoObject?.let {
                                            val source: String =
                                                "${baseUrlForMedia}${data._id}/" + it.getString("Source")
                                            Video(source)
                                        }

                                        val audio: Audio? = audioObject?.let {
                                            val source: String =
                                                "${baseUrlForMedia}${data._id}/" + it.getString("Source")
                                            Audio("suri", source)
                                        }

                                        val segment = Segment(chapter, image, video, audio)
                                        segments.add(segment)
                                    }

                                    val videoJson = backgroundJson.optJSONObject("Video")
                                    val audioJson = backgroundJson.optJSONObject("Audio")
                                    val imageJson = backgroundJson.optJSONObject("Image")


                                    try{
                                        val videoSource: Video? = videoJson?.let {
                                            val source: String =
                                                "${baseUrlForMedia}${data._id}/" + it.getString("Source")
                                            Video(source)
                                        }

                                        val audioSource: Audio? = audioJson?.let {
                                            val source: String =
                                                "${baseUrlForMedia}${data._id}/" + it.getString("Source")
                                            Audio("suri", source)
                                        }

                                        val imageSource: Image? = imageJson?.let {
                                            val source: String =
                                                "${baseUrlForMedia}${data._id}/" + it.getString("Source")
                                            val interval: Int = it.getInt("Interval")
                                            Image(source, interval)
                                        }


                                        val background = Background(
                                            video = videoSource,
                                            audio = audioSource,
                                            image = imageSource,
                                            exoplayerUrlPosition = 0,
                                            forNextUrlPosition = 0
                                        )

                                        if (data.liveStatus != "Disabled") {
                                            var contentData = ContentData(
                                                version = 1,
                                                currentIndex = 0,
                                                background = background,
                                                segments = segments,
                                                id = data._id,
                                                title = data.title,
                                                description = data.description,
                                                thumbnail = baseUrlForImage + "" + data.thumbnail,
                                                tagsData = data.tags,
                                                callToAction = data.callToAction,
                                                privateType = data.privateType
                                            )
                                            videos.add(contentData)
                                        }
                                    }
                                    catch (e : Exception){}


                                }
                            }


                            isUpdateVidoes = 1

                        }
                    }
                    catch (e : Exception){}

                }
                else
                {
                    Log.e("MainActivity", " Suri API call failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.e("MainActivity", " Suri rj API call failed: ${t.message}")
            }
        })
        return videos;
    }

    private fun setupPageChangeCallback() {
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            // Triggered when you select a new page
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                Log.d("onPageSelected","${position}")

                if(position >= videos.size)
                {
                    binding.viewPager.currentItem = 0
                }
                else
                {
                    loadNewPage(position)

                    adapterPosition = position
                }
            }

            /**
             * This method will be invoked when the current page is scrolled,
             * either as part of a programmatically initiated
             * smooth scroll or a user initiated touch scroll.
             */
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)

                if(imageLoaded == 0)
                {
                    imageLoaded = 1
                    binding.imageView.visibility = View.GONE

                    Handler(Looper.getMainLooper()).postDelayed({
                        imageLoaded = 0
                    }, 3000)
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)

                Log.d("onPageScrollState","${state}")

            }
        }
        )
    }

    fun callLogsApi(contentId : String, city : String, tagIds : List<String>, watchDuration : String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://cmsbe.aideo.in/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()



        val postRequest = PostRequest(
            contentId = contentId,
            city = city,
            tagIds = tagIds,
            watchDuration = watch_duration.toString()
        )
        watch_duration = 0


        Log.d("logs app postRequest ", " $postRequest")

        val api = retrofit.create(logsApi::class.java)
        val call = api.getVideoContentsTopic(postRequest)

        call.enqueue(object : Callback<PostResponse> {
            override fun onResponse(call: Call<PostResponse>, response: Response<PostResponse>) {
            }

            override fun onFailure(call: Call<PostResponse>, t: Throwable) {
                Log.e("MainActivity", " Suri rj API call failed: ${t.message}")
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun preparePlayer(videoUrls: ArrayList<ContentData>) {

        val dataSourceFactory = DefaultDataSourceFactory(this, "your_user_agent${reloadApiCount}")

        for (currPosition in 0..(videoUrls.size - 1))
        {

            // Background Video

            if(firstVideo == 0 && videoUrls[currPosition].background?.video?.source == null)
            {
                firstVideo = 1

                val mediaItem = MediaItem.fromUri("https://cmsbe.aideo.in/controllers/Content/uploads/65e6e8f0020854301254f7d1/1.mp4")
                val mediaSourceItem = ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(mediaItem)
                mediaSourceCantat?.addMediaSource(mediaSourceItem)
            }
            else
            {
                val mediaItem = MediaItem.fromUri("${videoUrls[currPosition].background?.video?.source}")
                val mediaSourceItem = ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(mediaItem)
                mediaSourceCantat?.addMediaSource(mediaSourceItem)
            }

            exoplayerUrlPosition = exoplayerUrlPosition + 1

            videoUrls[currPosition].background?.exoplayerUrlPosition = exoplayerUrlPosition


            // for next video

            try
            {
                if(currPosition <= (videoUrls.size - 2))
                {
                    if(videoUrls[currPosition +1].background?.video?.source != null)
                    {
                        val mediaItem = MediaItem.fromUri("${videoUrls[currPosition + 1].background?.video?.source}")
                        val mediaSourceItem = ProgressiveMediaSource.Factory(dataSourceFactory)
                            .createMediaSource(mediaItem)
                        mediaSourceCantat?.addMediaSource(mediaSourceItem)

                        exoplayerUrlPosition = exoplayerUrlPosition + 1

                        videoUrls[currPosition + 1].background?.forNextUrlPosition = exoplayerUrlPosition
                    }
                }
            }
            catch (e : Exception){}


            // Segment Video

            if(videoUrls[currPosition].segments != null)
            {
                for (segments in videoUrls[currPosition].segments!!)
                {
                    if(segments.video?.source != null)
                    {
                        val mediaItem = MediaItem.fromUri("${segments.video.source}")
                        val mediaSourceItem = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
                        mediaSourceCantat?.addMediaSource(mediaSourceItem)

                        exoplayerUrlPosition = exoplayerUrlPosition + 1
                        segments.exoPlayerUrlPos = exoplayerUrlPosition
                    }
                }
            }

        }
        // Prepare the player with the concatenated media source
        player?.setMediaSource(mediaSourceCantat!!)

        player?.repeatMode =  Player.REPEAT_MODE_ONE

        player?.prepare()

        player?.addListener(object : Player.Listener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                when (playbackState) {
                    Player.STATE_IDLE -> {
                        // Player is idle
                    }
                    Player.STATE_BUFFERING -> {
                        // Player is buffering
                    }
                    Player.STATE_READY -> {
                        // Player is ready to play
                    }
                    Player.STATE_ENDED -> {
                        // Playback ended
                    }
                }
            }

            override fun onPositionDiscontinuity(reason: Int) {

                if(reason == 0 && middleVideoPlaying == 1 && prepareNextVideo == 0)
                {
                    prepareNextVideo = 1

                    Handler().postDelayed({
                        prepareNextVideo = 0
                    }, 1000)

                    showBackVideo.visibility = View.GONE
                    middleVideoPlaying = 0

                    Log.d("surender kumar onPosi", "${reason}")

                    var model = videos[adapterPosition]

                    model.currentIndex++

                    adapter?.updateAdapter(model.currentIndex,0.0)

                    try {
                        if((videos[adapterPosition].segments!!.size ) == model.currentIndex)
                        {
                            model.currentIndex = 0
                            adapter?.allItemUpdateAdapter()


                            binding.imageView.visibility = View.GONE

                            binding.viewPager.currentItem = adapterPosition + 1
                        }
                    }
                    catch (e : Exception){}


                    player?.pause()
                    player?.playWhenReady = false

                    try {
                        if (videos[adapterPosition].segments?.get(model.currentIndex)?.image != null) {
                            yourMethod(adapterPosition)
                        }
                    }catch (e : Exception){}

                    try {
                        if (videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)?.video?.source != null) {
                            showMiddleVideo(videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)!!.exoPlayerUrlPos
                                ,adapterPosition,
                                videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)?.video?.source.toString())
                        }
                    } catch (e : Exception) {}
                }


            }
        }
        )
    }

    override fun onStart() {
        super.onStart()
        playerView.player = player

        isActivityOpened = 1
    }

    override fun onRestart() {
        super.onRestart()

        Log.d("onRestart called", "onRestart")

        if(videos.size > adapterPosition)
        {
            if(videos[adapterPosition].privateType == false)
            {
                isActivityOpened = 1

                var model = videos[adapterPosition]
                model.currentIndex = 0

                adapter?.allItemUpdateAdapter()

                if(videos[adapterPosition].background?.video?.source != null)
                {
                    // video
                    currentVideoPosition = currentVideoPosition + 1
                    player?.seekToDefaultPosition(adapterPosition)
                    player?.playWhenReady = true
                    player?.play()
                    player!!.repeatMode =  Player.REPEAT_MODE_ONE
                    showBackVideo.visibility = View.VISIBLE
                }


                try{
                    if(videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)?.image?.source != null)
                    {
                        yourMethod(adapterPosition)
                    }
                }
                catch (e :Exception){}


                try{
                    if(videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)?.video?.source != null)
                    {
                        showMiddleVideo(videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)!!.exoPlayerUrlPos
                            ,adapterPosition
                        ,videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)?.video?.source.toString())
                    }
                }
                catch (e :Exception){}


                try{
                    if(videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)?.audio?.source != null)
                    {
                        GlobalScope.launch(Dispatchers.IO)
                        {
                            setMiddleVideoTrack(videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)?.audio?.source.toString(), adapterPosition)
                        }
                    }
                }
                catch (e :Exception){}

                try{
                    if (videos[adapterPosition].background?.audio?.source != null) {
                        playNextTrack(videos[adapterPosition].background?.audio?.source.toString())
                    }
                }
                catch (e : Exception){}
            }
        }
    }

    override fun onResume() {
        super.onResume()

        Log.d("onResume called", "onResume")


        if(videos.size > adapterPosition)
        {
            if(videos[adapterPosition].privateType == false) {
                try {
                    if (locationenabled == 0) {
                        locationenabled = 1
                        isActivityOpened = 1

                        var model = videos[adapterPosition]
                        model.currentIndex = 0

                        adapter?.allItemUpdateAdapter()

                        if (videos[adapterPosition].background?.video?.source != null) {
                            // video
                            currentVideoPosition = currentVideoPosition + 1
                            player?.seekToDefaultPosition(adapterPosition)
                            player?.playWhenReady = true
                            player?.play()
                            player!!.repeatMode = Player.REPEAT_MODE_ONE
                            showBackVideo.visibility = View.VISIBLE
                        }


                        try {
                            if (videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)?.image?.source != null) {
                                yourMethod(adapterPosition)
                            }
                        } catch (e: Exception) {
                        }


                        try {
                            if (videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)?.video?.source != null) {
                                showMiddleVideo(
                                    videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)!!.exoPlayerUrlPos,
                                    adapterPosition,
                                    videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)?.video?.source.toString()
                                )
                            }
                        } catch (e: Exception) {
                        }


                        try {
                            if (videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)?.audio?.source != null) {
                                GlobalScope.launch(Dispatchers.IO) {
                                    setMiddleVideoTrack(videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)?.audio?.source.toString(), adapterPosition)
                                }
                            }
                        } catch (e: Exception) {
                        }

                        try {
                            if (videos[adapterPosition].background?.audio?.source != null) {
                                GlobalScope.launch(Dispatchers.IO) {
                                    playNextTrack(videos[adapterPosition].background?.audio?.source.toString())
                                }
                            }
                        } catch (e: Exception) {
                        }
                    }

                } catch (e: Exception) {
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()

        isActivityOpened = 0

        if(player != null) {
            player?.playWhenReady = false
            player?.pause()
        }

        if(countDownTimer != null)
        {
            countDownTimer!!.cancel()
        }

        stopPlayback()

        stopMiddlePlayback()
    }

    override fun onStop() {
        super.onStop()

        isActivityOpened = 0

        if(player != null) {
            player?.playWhenReady = false
            player?.pause()
        }

        if(countDownTimer != null)
        {
            countDownTimer!!.cancel()
        }


        stopPlayback()

        stopMiddlePlayback()
    }

    private fun playNextTrack(url: String) {
        try {
            if(videos[adapterPosition].privateType == false)
            {
                mediaPlayer?.reset()
                mediaPlayer?.setDataSource(url)
                mediaPlayer?.prepare()
                mediaPlayer?.isLooping = true
                mediaPlayer?.start()
            }
        }
        catch (e : Exception){}
    }

    private fun setMiddleVideoTrack(url: String, position : Int) {
        try {
            if(videos.size > position)
            {
                runOnUiThread {
                    if (videos[position].privateType == false) {
                        middleMediaPlayer?.reset()
                        middleMediaPlayer?.setDataSource(url)
                        middleMediaPlayer?.prepare()
                        middleMediaPlayer?.isLooping = true
                        middleMediaPlayer?.start()
                    }
                }
            }

            try {
                val msec = MediaPlayer.create(
                    this,
                    Uri.parse(videos[position].segments?.get(videos[position].currentIndex)?.video?.source)
                ).duration
                currentInterval = msec.toDouble()
            } catch (e: Exception) {
            }

        } catch (e : Exception){}
    }

    fun stopPlayback() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.reset()
        }
        catch (e : Exception){}
    }

    fun stopMiddlePlayback() {
        try {
            middleMediaPlayer?.stop()
            middleMediaPlayer?.reset()
        }
        catch (e : Exception){}
    }

    fun showMiddleVideo(middleVideoPosition:Int,position: Int, videoUrlPre : String) {
        middleVideoPlaying = 1

        Log.d("meddle video int", "${videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)?.video?.interval}")

        if(videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)?.video?.interval != null)
        {
            if(videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)?.video!!.interval == 0.0)
            {
                val durationCalculator = VideoDurationCalculator(object : VideoDurationCalculator.OnVideoDurationListener {
                    override fun onDurationCalculated(duration: Long?) {

                        if (duration != null) {
                            val durationSeconds = duration


                            try {
                                if(videos[adapterPosition].segments!![videos[adapterPosition].currentIndex].video?.interval != null)
                                {
                                    videos[adapterPosition].segments!![videos[adapterPosition].currentIndex].video!!.interval = durationSeconds.toDouble()
                                }

                                if(videos[position].privateType == false) {
                                    try {
                                        runOnUiThread {
                                            binding.imageView.visibility = View.GONE
                                            showBackVideo.visibility = View.VISIBLE
                                            player?.seekToDefaultPosition(middleVideoPosition)
                                            player?.playWhenReady = true
                                            player?.play()
                                        }
                                    } catch (e: Exception) {
                                    }
                                }
                            }
                            catch (e : Exception){}


                            try {
                                if(videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex + 1)?.video?.source != null)
                                {
                                    nextSegmentVideoDuration(videos[adapterPosition].currentIndex + 1, adapterPosition, videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex + 1)?.video?.source.toString())
                                }
                            }
                            catch (e : Exception){}

                        } else {
                            println("Failed to retrieve video duration")
                        }
                    }

                    override fun onError(error: Exception) {
                        error.printStackTrace()
                    }
                })

                durationCalculator.execute(videoUrlPre)
            }
            else
            {
                if(videos.size > position)
                {
                    if(videos[position].privateType == false) {
                        try {
                            runOnUiThread {
                                binding.imageView.visibility = View.GONE
                                showBackVideo.visibility = View.VISIBLE
                                player?.seekToDefaultPosition(middleVideoPosition)
                                player?.playWhenReady = true
                                player?.play()
                            }
                        } catch (e: Exception) {
                        }

                        try {
                            if(videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex + 1)?.video?.source != null)
                            {
                                nextSegmentVideoDuration(videos[adapterPosition].currentIndex + 1, adapterPosition, videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex + 1)?.video?.source.toString())
                            }
                        }
                        catch (e : Exception){}
                    }
                }
            }
        }
        else
        {
            val durationCalculator = VideoDurationCalculator(object : VideoDurationCalculator.OnVideoDurationListener {
                override fun onDurationCalculated(duration: Long?) {

                    Log.d("start pickup duration " , "3 ${videoUrlPre}")

                    if (duration != null) {
                        val durationSeconds = duration

                        Log.d("start pickup duration " , "4")

                        try {
                            if(videos[adapterPosition].segments!![videos[adapterPosition].currentIndex].video?.interval != null)
                            {
                                Log.d("start pickup duration " , "5")
                                videos[adapterPosition].segments!![videos[adapterPosition].currentIndex].video!!.interval = durationSeconds.toDouble()
                            }

                            Log.d("start pickup duration " , "6")

                            if(videos.size > position)
                            {
                                if(videos[position].privateType == false) {
                                    try {
                                        runOnUiThread {
                                            binding.imageView.visibility = View.GONE
                                            showBackVideo.visibility = View.VISIBLE
                                            player?.seekToDefaultPosition(middleVideoPosition)
                                            player?.playWhenReady = true
                                            player?.play()
                                        }
                                    } catch (e: Exception) {
                                    }
                                }
                            }
                        }
                        catch (e : Exception){}


                        try {
                            if(videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex + 1)?.video?.source != null)
                            {
                                nextSegmentVideoDuration(videos[adapterPosition].currentIndex + 1, adapterPosition, videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex + 1)?.video?.source.toString())
                            }
                        }
                        catch (e : Exception){}

                    } else {
                        println("Failed to retrieve video duration")
                    }
                }

                override fun onError(error: Exception) {
                    error.printStackTrace()
                }
            })

            durationCalculator.execute(videoUrlPre)
        }
    }

    fun yourMethod(position:Int) {

        if(countDownTimer != null)
        {
            countDownTimer!!.cancel()
        }

        val model = videos[position]

        var interval = videos[position].segments?.get(model.currentIndex)?.image?.interval!!.toLong()

        currentInterval = interval.toDouble()

        if(videos[position].segments?.get(model.currentIndex)?.audio?.source != null)
        {
            var mp3Duration = getMp3Duration(videos[position].segments?.get(model.currentIndex)?.audio?.source.toString())

            if(interval < mp3Duration)
            {
                interval = mp3Duration

                currentInterval = interval.toDouble()
            }
        }

        try {
            Glide.with(this@HemePlayerScreen)
                .load(videos[position].segments?.get(model.currentIndex)?.image?.source)
                .placeholder(R.drawable.plaeholder_image)
                .into(binding.imageView)
                 binding.imageView.visibility = View.VISIBLE
        }
        catch (e: Exception) {
        }


        countDownTimer = object : CountDownTimer(interval, interval) {
            override fun onTick(millisUntilFinished: Long) {
                // This method is called every intervalInMillis (1 second in this case)
                val secondsRemaining = millisUntilFinished / 1000
                // Update UI to show remaining time (e.g., update a TextView)
            }

            override fun onFinish() {
                model.currentIndex++

                Log.d("surender kumar error", "onFinish")

                if(videos.size > adapterPosition)
                {
                    if((videos[adapterPosition].segments!!.size ) == model.currentIndex)
                    {
                        model.currentIndex = 0
                        adapter?.allItemUpdateAdapter()

                        binding.viewPager.currentItem = adapterPosition + 1
                    }

                    if(middleVideoPlaying == 0)
                    {
                        adapter?.updateAdapter(model.currentIndex,0.0)

                        try {
                            if (videos[adapterPosition].segments?.get(model.currentIndex)?.image != null) {
                                yourMethod(adapterPosition)
                            }

                            if(videos[position].segments?.get(videos[position].currentIndex)?.video?.source != null)
                            {
                                showMiddleVideo(videos[position].segments?.get(videos[position].currentIndex)!!.exoPlayerUrlPos,adapterPosition,videos[position].segments?.get(videos[position].currentIndex)?.video?.source.toString())
                            }

                            if(videos[position].segments?.get(videos[position].currentIndex)?.audio?.source != null)
                            {
                                GlobalScope.launch(Dispatchers.IO) {
                                    setMiddleVideoTrack(videos[position].segments?.get(videos[position].currentIndex)?.audio?.source.toString(), position)
                                }
                            }
                        } catch (e: Exception) {
                            model.currentIndex = 0
                            adapter?.allItemUpdateAdapter()
                            if (videos[position].segments?.get(model.currentIndex)?.image != null) {
                                yourMethod(position)
                            }
                        }
                    }
                }

                preloadNextSegmentImage(adapterPosition)
            }
        }

        if(countDownTimer != null)
        {
            countDownTimer!!.start()
        }
    }

    fun getMp3Duration(filePath: String): Long {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(filePath)

        val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val duration = durationStr?.toLongOrNull() ?: 0

        retriever.release()

        return duration
    }

    private fun startRepeatingMethodCalls() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                // Call the method

                if(videos.size > adapterPosition)
                {
                    if(videos[adapterPosition].privateType == false)
                    {
                        watch_duration = watch_duration + 1
                        Log.d("after every ", "${watch_duration}")
                    }

                }
                handler.postDelayed(this, delayMillis)
            }
        }, delayMillis)
    }

    private fun startRepeatingForProgress() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                // Call the method

                Log.d("video progress status", "middleVideoPlaying = ${middleVideoPlaying} , player = ${player}")

                if(middleVideoPlaying == 1 && player != null)
                {
                    if(player!!.isPlaying)
                    {
                        try {
                            if(videos.size > adapterPosition) {
                                var model = videos[adapterPosition]

                                if (model.privateType == false && model.segments!![model.currentIndex].video!!.interval != 0.0) {


                                    if (locationenabled == 1) {
                                        var dur = model.segments!![model.currentIndex].video!!.interval / 100

                                        try {
                                            adapter?.updateProgress(
                                                model.currentIndex,
                                                (((screenWidthDp / model.segments!!.size) - 12) / dur)
                                            )
                                        } catch (e: Exception) {
                                        }
                                    }
                                }
                            }
                        }
                        catch (e : Exception){}
                    }
                }
                else
                {
                    try {
                        if(videos.size > adapterPosition) {
                            if (videos[adapterPosition].privateType == false) {

                                if (locationenabled == 1) {
                                    var dur = currentInterval / 100

                                    try {
                                        var model = videos[adapterPosition]

                                        adapter?.updateProgress(
                                            model.currentIndex,
                                            (((screenWidthDp / videos[adapterPosition].segments!!.size) - 12) / dur)
                                        )
                                    } catch (e: Exception) {
                                    }
                                }
                            }
                        }
                    }
                    catch (e : Exception){}
                }


                // Schedule the next call after the delay
                handler.postDelayed(this, 100)
            }
        }, 100)
    }

    private fun shareFile(imageUrl : String, videoId : String) {
        val requestOptions = RequestOptions().apply {
            skipMemoryCache(true)
            diskCacheStrategy(DiskCacheStrategy.NONE)
        }

        Glide.with(this)
            .asBitmap()
            .load(imageUrl)
            .apply(requestOptions)
            .listener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Bitmap>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Bitmap>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    resource?.let {
                        val imageFile = File(
                            getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                            "image.jpg"
                        )
                        val outputStream = FileOutputStream(imageFile)
                        resource.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                        outputStream.flush()
                        outputStream.close()

                        shareImage(imageFile, videoId)
                    }
                    return true
                }
            })
            .submit()
    }

    private fun shareImage(imageFile: File, videoId:String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/*"
        val uri = FileProvider.getUriForFile(
            this,
            "com.aideo.yourapp.fileprovider",
            imageFile
        )

        val shareBody = "https://cmsbe.aideo.in/share/$videoId"

        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(shareIntent, "Share Image"))


        locationenabled = 0
    }

    override fun clickOnLeftSide() {
        if(videos.size > adapterPosition)
        {
            if(videos[adapterPosition].privateType == false)
            {
                var model = videos[adapterPosition]

                if(model.currentIndex > 0)
                {
                    model.currentIndex--
                }

                if((videos[adapterPosition].segments!!.size) == model.currentIndex)
                {
                    model.currentIndex = 0
                    adapter?.allItemUpdateAdapter()


                    binding.imageView.visibility = View.GONE

                    binding.viewPager.currentItem = adapterPosition + 1
                }

                adapter?.updateAdapter(model.currentIndex,0.0)

                if(middleVideoPlaying == 1)
                {
                    middleVideoPlaying = 0
                    player!!.pause()
                    player!!.playWhenReady = false
                    showBackVideo.visibility = View.GONE
                }



                try{
                    if (videos[adapterPosition].segments?.get(model.currentIndex)?.image != null) {
                        yourMethod(adapterPosition)
                    }
                }
                catch (e : Exception){}



                try{
                    if(videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)?.video?.source != null)
                    {
                        showMiddleVideo(videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)!!.exoPlayerUrlPos,adapterPosition,
                            videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)?.video?.source.toString())
                    }
                }
                catch (e : Exception){}


                try{
                    if(videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)?.audio?.source != null)
                    {
                        GlobalScope.launch(Dispatchers.IO) {
                            setMiddleVideoTrack(videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)?.audio?.source.toString(), adapterPosition)
                        }
                    }
                }
                catch (e : Exception){}
            }
        }
    }

    override fun clickOnRightSide() {

        if(videos.size > adapterPosition)
        {
            if(videos[adapterPosition].privateType == false) {

                var model = videos[adapterPosition]

                model.currentIndex++

                if ((videos[adapterPosition].segments!!.size) == model.currentIndex) {
                    model.currentIndex = 0
                    adapter?.allItemUpdateAdapter()


                    binding.imageView.visibility = View.GONE

                    binding.viewPager.currentItem = adapterPosition + 1
                }

                adapter?.updateAdapter(model.currentIndex, 0.0)

                if (middleVideoPlaying == 1) {
                    middleVideoPlaying = 0
                    try {
                        player!!.pause()
                        player!!.playWhenReady = false

                        showBackVideo.visibility = View.GONE

                    } catch (e: Exception) {
                    }
                }





                try {
                    if (videos[adapterPosition].segments?.get(model.currentIndex)?.image != null) {
                        yourMethod(adapterPosition)
                    }
                } catch (e: Exception) {
                }


                try {
                    if (videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)?.video?.source != null) {
                        showMiddleVideo(
                            videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)!!.exoPlayerUrlPos,
                            adapterPosition,videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)?.video?.source.toString()

                        )
                    }
                } catch (e: Exception) {
                }


                try {
                    if (videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)?.audio?.source != null) {
                            setMiddleVideoTrack(videos[adapterPosition].segments?.get(videos[adapterPosition].currentIndex)?.audio?.source.toString(), adapterPosition)
                    }
                } catch (e: Exception) {
                }

            }
        }

        preloadNextSegmentImage(adapterPosition)
    }

    fun loadNewPage(position : Int){
        if(isUpdateVidoes == 1)
        {
            isUpdateVidoes = 0
            preparePlayer(videos)
        }


        stopMiddlePlayback()
        stopPlayback()

        try {
            if(videos.size > position)
            {
                viewPagerAdapter?.updateTextValue(videos[position].title.toString())
            }
        }
        catch (e : Exception){}


        try {
            if(videos[position].privateType == true)
            {
                binding.securityLayout.visibility = View.VISIBLE

                binding.imageView.visibility = View.GONE

                player?.playWhenReady = false
                player?.pause()
                showBackVideo.visibility = View.GONE


                try {
                    adapter = StatusAdapter(viewPagerList, position, screenWidthDp)
                    recyclerView?.adapter = adapter

                    GlobalScope.launch(Dispatchers.IO) {
                        adapter?.allItemUpdateAdapter()
                        adapter?.updateAdapter(videos[position].currentIndex, 0.0)
                    }
                }
                catch (e : Exception){}
            }
            else
            {
                binding.securityLayout.visibility = View.GONE
                if(isActivityOpened == 1)
                {
                    videos[position].currentIndex = 0
                    middleVideoPlaying = 0

                    showBackVideo.visibility = View.GONE
                    binding.imageView.visibility = View.GONE

                    try{
                        adapter = StatusAdapter(viewPagerList, position, screenWidthDp)
                        recyclerView?.adapter = adapter

                        if(videos[position].segments?.get(videos[position].currentIndex)?.image?.source != null)
                        {


                            yourMethod(position)
                        }
                        else
                        {
                            binding.imageView.visibility = View.GONE
                        }

                        GlobalScope.launch(Dispatchers.IO) {
                            adapter?.allItemUpdateAdapter()
                            adapter?.updateAdapter(videos[position].currentIndex, 0.0)
                        }
                    }
                    catch (e :Exception){}


                    if(videos[position].background?.video?.source != null)
                    {
                        Log.d("back video called","${videos[position].background?.video?.source}")

                        // video
                        currentVideoPosition = currentVideoPosition + 1


                        if(watch_duration < 30)
                        {
                            runOnUiThread {
                                videos[position].background?.forNextUrlPosition?.let {
                                    player?.seekToDefaultPosition(
                                        it
                                    )

                                    player?.playWhenReady = true
                                    player?.play()
                                    player!!.repeatMode =  Player.REPEAT_MODE_ONE
                                    showBackVideo.visibility = View.VISIBLE
                                }
                            }
                        }
                        else
                        {
                            runOnUiThread {
                                videos[position].background?.exoplayerUrlPosition?.let {
                                    player?.seekToDefaultPosition(
                                        it
                                    )
                                    player?.playWhenReady = true
                                    player?.play()
                                    player!!.repeatMode =  Player.REPEAT_MODE_ONE
                                    showBackVideo.visibility = View.VISIBLE
                                }
                            }
                        }
                    }
                    else
                    {
                        player?.playWhenReady = false
                        player?.pause()
                        showBackVideo.visibility = View.GONE
                    }

                    try{
                        if(videos[position].segments?.get(videos[position].currentIndex)?.video?.source != null)
                        {
                            showMiddleVideo(videos[position].segments?.get(videos[position].currentIndex)!!.exoPlayerUrlPos, position,videos[position].segments?.get(videos[position].currentIndex)?.video?.source.toString())
                        }
                    }
                    catch (e :Exception){}

                    GlobalScope.launch(Dispatchers.IO) {
                        try {
                            if (videos[position].segments?.get(videos[position].currentIndex)?.audio?.source != null) {
                                setMiddleVideoTrack(videos[position].segments?.get(videos[position].currentIndex)?.audio?.source.toString(), position)
                            }
                        } catch (e: Exception) {
                        }
                    }


                    GlobalScope.launch(Dispatchers.IO) {
                        try {
                            if (videos[position].background?.audio?.source != null) {
                                playNextTrack(videos[position].background?.audio?.source.toString())
                            }
                        } catch (e: Exception) {
                        }
                    }


                    val orientation = resources.configuration.orientation
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE)
                    {
                        binding.share2.visibility = View.GONE
                        binding.tvShare2.visibility = View.GONE
                        binding.tvReadMore.visibility = View.GONE
                    }
                    else
                    {

                        // Get the existing layout parameters
                        val layoutParams = binding.showBackVideo.layoutParams as ConstraintLayout.LayoutParams
                        val layoutParamsImage = binding.imageInLandscape.layoutParams as ConstraintLayout.LayoutParams


                        // Set the new margins in pixels (you can adjust these values)
                        val newStartMargin = 0
                        val newTopMargin = 150
                        val newEndMargin = 0
                        val newBottomMargin = 150

                        // Set the new margins
                        layoutParams.setMargins(newStartMargin, newTopMargin, newEndMargin, newBottomMargin)
                        layoutParamsImage.setMargins(newStartMargin, newTopMargin, newEndMargin, newBottomMargin)

                        // Apply the updated layout parameters to the view
                        binding.showBackVideo.layoutParams = layoutParams
                        binding.imageInLandscape.layoutParams = layoutParamsImage


                        if(videos[position].callToAction != null && videos[position].callToAction.toString() != "")
                        {
                            binding.share2.visibility = View.VISIBLE
                            binding.share1.visibility = View.GONE
                        }
                        else
                        {
                            binding.tvReadMore.visibility = View.GONE
                            binding.share2.visibility = View.GONE
                            binding.share1.visibility = View.VISIBLE
                        }
                    }


                    if(position != 0)
                    {
                        var list : ArrayList<String> = ArrayList()
                        for(tagsData in videos[position - 1].tagsData!!)
                        {
                            list.add(tagsData._id.toString())
                        }

                        try
                        {
                            GlobalScope.launch(Dispatchers.IO)
                            {
                                callLogsApi(videos[position - 1].id.toString(), currentCityName, list, last_duration.toString())
                            }
                        }
                        catch (e : Exception){}
                    }

                    if((videos.size - 2) <= position)
                    {
                        getReloadContent(currentCityName)
                    }

                    preloadNextAideoImage(position)

                    try
                    {
                        if(videos[position + 1].segments!![0].video?.source != null)
                        {
                            nextAideoSegmentDuration(0,position + 1, videos[position + 1].segments!![0].video?.source.toString())
                        }
                    }
                    catch (e : Exception){}
                }

                videos[position].isViewed = 1
            }
        }
        catch (e : Exception){}

        if((videos.size - 2) <= position) {
            GlobalScope.launch(Dispatchers.IO)
            {
                getReloadContent(currentCityName)
            }
        }

    }

    fun preloadNextAideoImage(position: Int) {
        try{
            if(videos[position + 1].segments?.size != null)
            {
                if(videos[position + 1].segments?.get(0)?.image?.source != null)
                {
                    Glide.with(this@HemePlayerScreen)
                        .load(videos[position + 1].segments?.get(0)?.image?.source)
                        .placeholder(R.drawable.plaeholder_image)
                        .into(binding.nextAideoImage)
                }
            }
        }
        catch (e : Exception){}
    }

    fun preloadNextSegmentImage(position: Int) {

        Log.d("Next segment api called", "suri 0")

        var model = videos[position]

        try{
            if(videos[position].segments?.size != null)
            {
                if(videos[position].segments!!.size > model.currentIndex)
                {
                    if(videos[position].segments?.get(model.currentIndex + 1)?.image?.source != null)
                    {

                        Log.d("Next segment api called", "suri 1")

                        Glide.with(this@HemePlayerScreen)
                            .load(videos[position].segments?.get(model.currentIndex + 1)?.image?.source)
                            .placeholder(R.drawable.plaeholder_image)
                            .into(binding.nextSegemnt)
                    }
                }
            }
        }
        catch (e : Exception){}
    }

    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun nextAideoSegmentDuration(segmentPosition:Int,adapPosi: Int, videoUrlPre : String) {

        var preModel = videos[adapPosi]

        if(preModel.segments?.get(segmentPosition)?.video?.interval != null)
        {
            if(preModel.segments?.get(segmentPosition)?.video!!.interval == 0.0)
            {
                val durationCalculator = VideoDurationCalculator(object : VideoDurationCalculator.OnVideoDurationListener {
                    override fun onDurationCalculated(duration: Long?) {

                        if (duration != null) {
                            val durationSeconds = duration

                            try {
                                if(preModel.segments!![segmentPosition].video?.interval != null)
                                {
                                    preModel.segments!![segmentPosition].video!!.interval = durationSeconds.toDouble()
                                }
                            }
                            catch (e : Exception){}

                        } else {

                        }
                    }

                    override fun onError(error: Exception) {
                        error.printStackTrace()
                    }
                })

                durationCalculator.execute(videoUrlPre)
            }
        }
        else
        {
            val durationCalculator = VideoDurationCalculator(object : VideoDurationCalculator.OnVideoDurationListener {
                override fun onDurationCalculated(duration: Long?) {

                    if (duration != null) {
                        val durationSeconds = duration

                        try {
                            if(preModel.segments!![segmentPosition].video?.interval != null)
                            {
                                preModel.segments!![segmentPosition].video!!.interval = durationSeconds.toDouble()
                            }
                        }
                        catch (e : Exception){}

                    } else {
                    }
                }

                override fun onError(error: Exception) {
                    error.printStackTrace()
                }
            })

            durationCalculator.execute(videoUrlPre)
        }
    }

    fun nextSegmentVideoDuration(segmentPosition:Int,adapPosi: Int, videoUrlPre : String) {

        var preModel = videos[adapPosi]

        if(preModel.segments?.get(segmentPosition)?.video?.interval != null)
        {
            if(preModel.segments?.get(segmentPosition)?.video!!.interval == 0.0)
            {
                val durationCalculator = VideoDurationCalculator(object : VideoDurationCalculator.OnVideoDurationListener {
                    override fun onDurationCalculated(duration: Long?) {

                        if (duration != null) {
                            val durationSeconds = duration

                            try {
                                if(preModel.segments!![segmentPosition].video?.interval != null)
                                {
                                    preModel.segments!![segmentPosition].video!!.interval = durationSeconds.toDouble()
                                }
                            }
                            catch (e : Exception){}

                        } else {

                        }
                    }

                    override fun onError(error: Exception) {
                        error.printStackTrace()
                    }
                })

                durationCalculator.execute(videoUrlPre)
            }
        }
        else
        {
            val durationCalculator = VideoDurationCalculator(object : VideoDurationCalculator.OnVideoDurationListener {
                override fun onDurationCalculated(duration: Long?) {

                    if (duration != null) {
                        val durationSeconds = duration

                        try {
                            if(preModel.segments!![segmentPosition].video?.interval != null)
                            {
                                preModel.segments!![segmentPosition].video!!.interval = durationSeconds.toDouble()
                            }
                        }
                        catch (e : Exception){}

                    } else {
                    }
                }

                override fun onError(error: Exception) {
                    error.printStackTrace()
                }
            })

            durationCalculator.execute(videoUrlPre)
        }
    }
}
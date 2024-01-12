package com.aideo.app.ApiCalling

import com.aideo.app.Models.PostRequest
import com.aideo.app.Models.PostResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface logsApi {
    @POST("mobile/logs")
    fun getVideoContentsTopic(@Body request: PostRequest): Call<PostResponse>
}
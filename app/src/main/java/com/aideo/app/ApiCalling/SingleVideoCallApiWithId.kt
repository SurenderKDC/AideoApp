package com.aideo.app.ApiCalling

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface SingleVideoCallApiWithId {
    @GET("mobile/content/{id}")
    fun getVideoContentsTopic(@Path("id") id: String): Call<PlaylistData>
}
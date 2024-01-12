package com.aideo.app.ApiCalling

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ReloadApiCall
{
    @GET("mobile/content/place/{id}")
    fun getVideoContentsTopic(@Path("id") id: String): Call<ArrayList<PlaylistData>>
}
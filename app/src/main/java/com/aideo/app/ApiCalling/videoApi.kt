package com.aideo.app.ApiCalling

import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface VideoContentsApi {
    @GET("mobile/content/place/{id}")
    fun getVideoContentsTopic(@Path("id") id: String, @Query("no") no: Int): Call<JsonElement>
}
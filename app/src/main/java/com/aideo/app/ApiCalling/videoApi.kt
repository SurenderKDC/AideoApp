package com.aideo.app.ApiCalling

import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface VideoContentsApi {
    @GET("mobile/content/place/{id}?page=1&perPage=300")
    fun getVideoContentsTopic(@Path("id") id: String): Call<JsonElement>
}
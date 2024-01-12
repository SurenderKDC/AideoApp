package com.aideo.app.Models

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path


interface SportsApi {
    @GET("mobile/topic/{id}")
    fun getSportsTopic(@Path("id") id: String): Call<ArrayList<Topic>>
}


/*

Q1 what is android ?
Answer.



 */
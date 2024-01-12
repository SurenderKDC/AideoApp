package com.aideo.app.Models

data class PostRequest(
    val contentId: String,
    val city: String,
    val tagIds: List<String>,
    val watchDuration: String
)


data class PostResponse(
    val contentId: String,
    val city: String,
    val tagIds: List<String>,
    val watchDuration: Int,
    val _id: String,
    val __v: Int
)
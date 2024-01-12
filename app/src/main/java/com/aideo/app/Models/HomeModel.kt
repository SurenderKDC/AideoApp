package com.aideo.app.Models

data class Topic(
    val _id: String,
    val name: String,
    val status: String,
    val tags: List<Tag>,
    val description: String,
    val content: List<Content>,
    val position: Int,
    val createdDate: String,
    val __v: Int,
    val configType: String
)

data class Tag(
    val _id: String
)

data class Content(
    val contentId: String,
    val title: String,
    val thumbnail: String
)
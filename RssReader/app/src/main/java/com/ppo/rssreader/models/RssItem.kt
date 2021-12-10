package com.ppo.rssreader.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RssItem (
    val title: String,
    val link: String,
    val description: String,
    val pubDate: String,
    val enclosure: String
)
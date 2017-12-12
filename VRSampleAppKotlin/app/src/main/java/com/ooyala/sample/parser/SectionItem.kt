package com.ooyala.sample.parser

import com.google.gson.annotations.SerializedName


data class SectionItem(

        @SerializedName("title")
        val title: String,

        @SerializedName("videos")
        val videos: List<VideoItem>
)
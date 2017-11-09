package com.ooyala.sample.parser

import com.google.gson.annotations.SerializedName


data class VideoItem (
        @SerializedName("title")
        val title: String,

        @SerializedName("embed-code")
        val embedCode: String,

        @SerializedName("provider-code")
        val pCode: String? = "BzY2syOq6kIK6PTXN7mmrGVSJEFj",

        @SerializedName("ad-type")
        val adType: AdType
)
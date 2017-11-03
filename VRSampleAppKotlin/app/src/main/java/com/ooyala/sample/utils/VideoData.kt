package com.ooyala.sample.utils

data class VideoData(
        val type: VideoItemType,
        val title: String,
        val hasIma: Boolean?,
        val embedCode: String?,
        val pCode: String? = "BzY2syOq6kIK6PTXN7mmrGVSJEFj",
        val domain: String? = "http://www.ooyala.com"
)
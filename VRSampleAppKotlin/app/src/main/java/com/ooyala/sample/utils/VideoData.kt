package com.ooyala.sample.utils

import com.ooyala.sample.parser.AdType
import java.io.Serializable

data class VideoData(
        val type: VideoItemType,
        val title: String,
        val adType: AdType,
        val embedCode: String?,
        val pCode: String? = "BzY2syOq6kIK6PTXN7mmrGVSJEFj",
        val domain: String? = "http://www.ooyala.com"
) : Serializable
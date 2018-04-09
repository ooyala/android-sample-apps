package com.ooyala.sample.parser

import android.content.res.AssetManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ooyala.sample.utils.VideoData
import com.ooyala.sample.utils.VideoItemType
import java.nio.charset.Charset


class VideoJSONParser (
        val mGson: Gson? = Gson()
) {
  public fun getVideoList(assetManager: AssetManager): List<VideoData> {
    val mainJSON = getJSON(assetManager)
    val sections = getSections(mainJSON)
    return getVideoDataList(sections)
  }

  private fun getJSON(assetManager: AssetManager): String {
    val inputStream = assetManager.open("data.json")
    val size = inputStream.available()
    val buffer = ByteArray(size)
    inputStream.read(buffer)
    inputStream.close()
    return String(buffer, Charset.forName("UTF-8"))
  }

  private fun getSections(json: String): List<SectionItem>? {
    val sectionType = object : TypeToken<List<SectionItem>>(){}.type
     return mGson?.fromJson(json, sectionType)
  }

  private fun getVideoDataList(sectionItems: List<SectionItem>?): List<VideoData> {
    val videoDataList = ArrayList<VideoData>()
    for (item in sectionItems!!) {
      videoDataList.add(VideoData(VideoItemType.SECTION, item.title, AdType.NOADS, null))
      item.videos.mapTo(videoDataList) {
        if (it.pCode != null) {
          VideoData(VideoItemType.VIDEO, it.title, it.adType, it.embedCode, it.pCode)
        } else {
          VideoData(VideoItemType.VIDEO, it.title, it.adType, it.embedCode)
        }
      }
    }
    return videoDataList
  }

}
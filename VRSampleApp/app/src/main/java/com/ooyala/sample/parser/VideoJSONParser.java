package com.ooyala.sample.parser;


import android.content.res.AssetManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ooyala.sample.utils.VideoData;
import com.ooyala.sample.utils.VideoItemType;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class VideoJSONParser {

  private Gson mGson;

  public VideoJSONParser() {
    mGson = new GsonBuilder().serializeNulls().create();
  }

  public List<VideoData> getVideoData(AssetManager assetManager) {
    String mainJSON = getMainJSON(assetManager);
    SectionItem[] items = parseItems(mainJSON);
    return convertToVideoData(items);
  }


  private String getMainJSON(AssetManager manager) {
    String json = null;
    try {
      InputStream is = manager.open("com/ooyala/sample/parser/data.json");
      int size = is.available();
      byte[] buffer = new byte[size];
      is.read(buffer);
      is.close();
      json = new String(buffer, "UTF-8");
    } catch (IOException ex) {
      Log.e("JSONParser", "Failed to read json from assets with error: " + ex.getMessage());
      return null;
    }
    return json;
  }


  private SectionItem[] parseItems(String mainJson) {
    return mGson.fromJson(mainJson, SectionItem[].class);
  }

  private List<VideoData> convertToVideoData(SectionItem[] items) {
    List<VideoData> datas = new ArrayList<>();
    for (SectionItem item : items) {
      datas.add(new VideoData(VideoItemType.SECTION, item.getTitle(), null, null));
      for (VideoItem videoItem : item.getVideoItemList()) {
        datas.add(new VideoData(VideoItemType.VIDEO, videoItem.getTitle(), videoItem.getType(), videoItem.getEmbedCode(), videoItem.getpCode()));
      }
    }
    return datas;
  }
}

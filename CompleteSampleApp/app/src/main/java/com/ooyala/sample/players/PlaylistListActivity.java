package com.ooyala.sample.players;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.ooyala.android.Utils;
import com.ooyala.android.util.DebugMode;
import com.ooyala.sample.R;
import com.ooyala.sample.utils.ImageDownloader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
  PlaylistListActivity shows how to query from Ooyala's Playlist API and display its contents
 */
public class PlaylistListActivity extends ListActivity {
  private static final String TAG = "PlaylistListActivity";

  private String PCODE;
  private String PLAYLIST_ID;
  private String DOMAIN;

  private JSONObject rootJson;
  private static final ExecutorService executor = Executors.newFixedThreadPool(2);

  /*
    The list adapter to populate the channel list view.
   */
  class OoyalaVideoListAdapter extends SimpleAdapter {
    private ImageDownloader imageDownloader = new ImageDownloader();

    public OoyalaVideoListAdapter(Context context, List<? extends Map<String, ?>> data, int resource,
                                  String[] from, int[] to) {
      super(context, data, resource, from, to);
    }

    @Override
    public void setViewImage(ImageView v, String value) {
      imageDownloader.download(value, v);
    }
  }

    private class PlaylistTask implements Runnable {
    private String pcode;
    private String playlistId;

    PlaylistTask(String pcode, String playlistId) {
      this.playlistId = playlistId;
      this.pcode = pcode;
    }

    public void run() {
      try {
        String apiRequestString = "http://player.ooyala.com/api/v1/playlist/" + pcode + "/" + playlistId;

        //Use two Ooyala utilities to get content and convert it to JSON.  View the decompiled code for implementation details
        String jsonString = Utils.getUrlContent(new URL(apiRequestString), 10000, 10000);
        rootJson = Utils.objectFromJSON(jsonString);
        DebugMode.logI(TAG, jsonString);

        runOnUiThread(new Runnable() {
          public void run() {
            setListAdapter(new OoyalaVideoListAdapter(
                    PlaylistListActivity.this, getData(), R.layout.embed_list_item, new String[] {
                    "title", "thumbnail", "duration" }, new int[] { R.id.asset_title, R.id.asset_thumbnail,
                    R.id.asset_duration }));
            getListView().setTextFilterEnabled(false);
          }
        });
      } catch (Exception ex) {
        DebugMode.logE(TAG, "Error" + ex.getLocalizedMessage(), ex);
      }
    }
  }

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    DOMAIN = getIntent().getExtras().getString("domain");
    PCODE = "c0cTkxOqALQviQIGAHWY5hP0q9gU";
    PLAYLIST_ID = "4fef485d588a4a818f913db2089a3a7a";

    // Retreieve the playlist info.  Note the hard-coded PCode and Playlist ID
    PlaylistTask task = new PlaylistTask(PCODE, PLAYLIST_ID);
    executor.submit(task);
  }


  /*
  Convert the rootJSON from the API request to the data taht can be used for the ListView
   */
  protected List<Map<String, Object>> getData() {
    List<Map<String, Object>> myData = new ArrayList<Map<String, Object>>();
    if (rootJson == null) {
      return myData;
    }

    try {
      JSONArray arr = rootJson.getJSONArray("data");
      for (int i = 0; i < arr.length(); i++) {
        JSONObject entry = arr.getJSONObject(i);

        addItem(myData, entry.getString("name"),  (int)(entry.getDouble("duration") * 1000), entry.getString("image"),
                browseIntent(entry.getString("embed_code"), entry.getString("name"), "1sb2cxOr-DiMvOnEvUWmmSggx6rJ", DOMAIN));
      }
    }
    catch (Exception e) {

    }
    return myData;
  }

  protected Intent browseIntent(String embedCode, String title, String pcode, String domain) {
    Intent result = new Intent();
    result.setClass(this, BasicPlaybackVideoPlayerActivity.class);
    result.putExtra("embed_code", embedCode);
    result.putExtra("selection_name", title);
    result.putExtra("pcode", pcode);
    result.putExtra("domain", domain);
    return result;
  }

  protected void addItem(List<Map<String, Object>> data, String name, int duration, String thumbnail,
      Intent intent) {
    Map<String, Object> temp = new HashMap<String, Object>();
    temp.put("title", name);
    temp.put("duration", timeStringFromMillis(duration, true));
    temp.put("thumbnail", thumbnail);
    temp.put("intent", intent);
    data.add(temp);
  }

  @Override
  @SuppressWarnings("unchecked")
  protected void onListItemClick(ListView l, View v, int position, long id) {
    Map<String, Object> map = (Map<String, Object>) l.getItemAtPosition(position);
    Intent intent = (Intent) map.get("intent");
    startActivity(intent);
  }

  private String timeStringFromMillis(int millis, boolean includeHours) {
    return DateUtils.formatElapsedTime(millis / 1000);
  }
}

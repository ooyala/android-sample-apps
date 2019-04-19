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

import com.ooyala.android.OoyalaAPIClient;
import com.ooyala.android.OoyalaException;

import com.ooyala.android.PlayerDomain;
import com.ooyala.android.item.Channel;
import com.ooyala.android.item.ContentItem;
import com.ooyala.android.item.Video;
import com.ooyala.android.util.DebugMode;
import com.ooyala.sample.R;
import com.ooyala.sample.utils.ImageDownloader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
  ChannelContentTreePlayerActivity shows how to use ooyalaapiclient to retrieve
  videos from a channel and show the preview image, title and duration for each video.
 */
public class ChannelContentTreePlayerActivity extends ListActivity {
  private static final String TAG = "ChannelContentTreePlayerActivity";

  private String DOMAIN;
  private Channel rootItem;
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

  private class ContentTreeTask implements Runnable {
    private List<String> embedCodes;
    private String pcode;
    private String domain;

    ContentTreeTask(List<String> embedCodes, String pcode, String domain) {
      this.embedCodes = embedCodes;
      this.pcode = pcode;
      this.domain = domain;

    }

    public void run() {
      ContentItem item = null;
      try {
        OoyalaAPIClient api = new OoyalaAPIClient(null, null, pcode, new PlayerDomain(domain));
        item = api.contentTree(embedCodes);
        if (item != null && item instanceof Channel) {
          rootItem = (Channel) item;
          runOnUiThread(new Runnable() {
            public void run() {
              setListAdapter(new OoyalaVideoListAdapter(
                  ChannelContentTreePlayerActivity.this, getData(), R.layout.embed_list_item, new String[] {
                  "title", "thumbnail", "duration" }, new int[] { R.id.asset_title, R.id.asset_thumbnail,
                  R.id.asset_duration }));
                  getListView().setTextFilterEnabled(false);
            }
          });

        }
      } catch (OoyalaException ex) {
        DebugMode.logE(TAG, "Error" + ex.getLocalizedMessage(), ex);
      }
    }
  }

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    String embedCode = getIntent().getExtras().getString("embed_code");
    DOMAIN = getIntent().getExtras().getString("domain");
    List<String> embedCodes = new ArrayList<String>();
    embedCodes.add(embedCode);
    // try to retrieve the content tree.
    ContentTreeTask task = new ContentTreeTask(embedCodes, getIntent().getExtras().getString("pcode"), DOMAIN);
    executor.submit(task);

  }

  protected List<Map<String, Object>> getData() {
    List<Map<String, Object>> myData = new ArrayList<Map<String, Object>>();
    if (rootItem == null) {
      return myData;
    }

    for (Video v : rootItem.getVideos()) {
      addItem(myData, v.getTitle(), v.getDuration(), v.getPromoImageURL(),
          browseIntent(v.getEmbedCode(), v.getTitle(), v.getAssetPCode(), DOMAIN));
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

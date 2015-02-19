package com.ooyala.sample.players;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.ooyala.android.DebugMode;
import com.ooyala.android.OoyalaAPIClient;
import com.ooyala.android.OoyalaException;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.apis.ContentTreeCallback;
import com.ooyala.android.item.Channel;
import com.ooyala.android.item.ContentItem;
import com.ooyala.android.item.Video;
import com.ooyala.sample.R;
import com.ooyala.sample.utils.ImageDownloader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChannelBrowserActivity extends ListActivity {
  private static final String TAG = "ChannelBrowserActivity";

  public static final String PCODE = "R2d3I6s06RyB712DN0_2GsQS-R-Y";
  public static final String APIKEY = "R2d3I6s06RyB712DN0_2GsQS-R-Y.nCFrd";
  public static final String SECRETKEY = "UpmPCeDJspTKqLHO5IyZSRbsSiC7AM_rAqGztDRN";
  public static final String PLAYERDOMAIN = "http://www.ooyala.com";

  public static OoyalaAPIClient api = new OoyalaAPIClient(APIKEY, SECRETKEY, PCODE, new PlayerDomain(PLAYERDOMAIN));

  private Channel rootItem = null;

  class MyContentTreeCallback implements ContentTreeCallback {
    private ChannelBrowserActivity _self;

    public MyContentTreeCallback(ChannelBrowserActivity self) {
      _self = self;
    }

    @Override
    public void callback(ContentItem item, OoyalaException ex) {
      if (ex != null) {
        DebugMode.logE(TAG, "can not find content tree from api");
        return;
      }
      if (item != null && item instanceof Channel) {
        rootItem = (Channel) item;
        setListAdapter(new OoyalaVideoListAdapter(_self, getData(), R.layout.embed_list_item, new String[] {
            "title", "thumbnail", "duration" }, new int[] { R.id.asset_title, R.id.asset_thumbnail,
            R.id.asset_duration }));
        getListView().setTextFilterEnabled(false);

      } else {
        Log.e(TAG, "Should not be here!");
      }
    }
  }

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

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    String embedCode = getIntent().getExtras().getString("embed_code");
    List<String> embedCodes = new ArrayList<String>();
    embedCodes.add(embedCode);
    api.contentTree(embedCodes, new MyContentTreeCallback(this));
  }

  protected List<Map<String, Object>> getData() {
    if (rootItem == null) return null;
    List<Map<String, Object>> myData = new ArrayList<Map<String, Object>>();

    for (Video v : rootItem.getVideos()) {
      addItem(myData, v.getTitle(), v.getDuration(), v.getPromoImageURL(50, 50),
          browseIntent(v.getEmbedCode()));
    }
    return myData;
  }

  protected Intent browseIntent(String embedCode) {
    Intent result = new Intent();
    result.setClass(this, PlayerDetailActivity.class);
    result.putExtra("com.ooyala.embedcode", embedCode);
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
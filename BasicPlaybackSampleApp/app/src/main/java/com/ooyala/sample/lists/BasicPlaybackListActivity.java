package com.ooyala.sample.lists;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ooyala.sample.R;
import com.ooyala.sample.players.BasicPlaybackVideoPlayerActivity;
import com.ooyala.sample.utils.PlayerSelectionOption;

import java.util.LinkedHashMap;
import java.util.Map;

public class BasicPlaybackListActivity extends Activity implements OnItemClickListener {
  public final static String getName() {
    return "Basic Playback";
  }

  private static Map<String, PlayerSelectionOption> selectionMap;
  ArrayAdapter<String> selectionAdapter;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle(getName());

    selectionMap = new LinkedHashMap<String, PlayerSelectionOption>();
    //Populate the embed map
    selectionMap.put( "4:3 Aspect Ratio", new PlayerSelectionOption("FwaXZjcjrkydIftLal2cq9ymQMuvjvD8", BasicPlaybackVideoPlayerActivity.class) );
    selectionMap.put( "MP4 Video", new PlayerSelectionOption("h4aHB1ZDqV7hbmLEv4xSOx3FdUUuephx", BasicPlaybackVideoPlayerActivity.class) );
    selectionMap.put( "HLS Video", new PlayerSelectionOption("Y1ZHB1ZDqfhCPjYYRbCEOz0GR8IsVRm1", BasicPlaybackVideoPlayerActivity.class) );
    selectionMap.put( "VOD with CCs", new PlayerSelectionOption("92cWp0ZDpDm4Q8rzHfVK6q9m6OtFP-ww", BasicPlaybackVideoPlayerActivity.class) );

    selectionMap.put("VAST2 Ad Pre-roll", new PlayerSelectionOption("Zlcmp0ZDrpHlAFWFsOBsgEXFepeSXY4c", BasicPlaybackVideoPlayerActivity.class));
    selectionMap.put("VAST2 Ad Mid-roll", new PlayerSelectionOption("pncmp0ZDp7OKlwTPJlMZzrI59j8Imefa", BasicPlaybackVideoPlayerActivity.class));
    selectionMap.put("VAST2 Ad Post-roll", new PlayerSelectionOption("Zpcmp0ZDpaB-90xK8MIV9QF973r1ZdUf", BasicPlaybackVideoPlayerActivity.class));
    selectionMap.put("VAST2 Ad Wrapper", new PlayerSelectionOption("pqaWp0ZDqo17Z-Dn_5YiVhjcbQYs5lhq", BasicPlaybackVideoPlayerActivity.class));
    selectionMap.put("Ooyala Ad Pre-roll", new PlayerSelectionOption("M4cmp0ZDpYdy8kiL4UD910Rw_DWwaSnU", BasicPlaybackVideoPlayerActivity.class));
    selectionMap.put("Ooyala Ad Mid-roll", new PlayerSelectionOption("xhcmp0ZDpnDB2-hXvH7TsYVQKEk_89di", BasicPlaybackVideoPlayerActivity.class));
    selectionMap.put("Ooyala Ad Post-roll", new PlayerSelectionOption("Rjcmp0ZDr5yFbZPEfLZKUveR_2JzZjMO", BasicPlaybackVideoPlayerActivity.class));
    selectionMap.put("Multi Ad combination", new PlayerSelectionOption("Ftcmp0ZDoz8tALmhPcN2vMzCdg7YU9lc", BasicPlaybackVideoPlayerActivity.class));
    // vast3.0 assets
    selectionMap.put("VAST3 2 Podded Ad", new PlayerSelectionOption("tjN2h1MDE65xMHMqNvvU0fYVBi6sFl1M", BasicPlaybackVideoPlayerActivity.class));
    selectionMap.put("VAST3 Ad Overlay", new PlayerSelectionOption("NuMGp1MDE6EKUv7rAmkSQ3iuLU5N5Ik0", BasicPlaybackVideoPlayerActivity.class));
    selectionMap.put("VAST3 Ad With All Of The New Events", new PlayerSelectionOption("x3MWp1MDE6yIedkirT0to1t1uy2oA8y3", BasicPlaybackVideoPlayerActivity.class));
    selectionMap.put("VAST3 AD With Icon", new PlayerSelectionOption("1qNGp1MDE6GBl7PkPlWA_FA9NJDhBp_I", BasicPlaybackVideoPlayerActivity.class));
    selectionMap.put("VAST3 Podded with Skippable", new PlayerSelectionOption("1uNGp1MDE6DJElG8YeQJ8D7tuuJo9T-t", BasicPlaybackVideoPlayerActivity.class));
    selectionMap.put("VAST3 Skippable Ad", new PlayerSelectionOption("1oNGp1MDE6V512gRnkTcz2o3RnFDSxNZ", BasicPlaybackVideoPlayerActivity.class));
    selectionMap.put("VAST3 Skippable Ad Long", new PlayerSelectionOption("55MjA5MTE6iZTRzrTtLoxUy78YbffT2G", BasicPlaybackVideoPlayerActivity.class));

    setContentView(com.ooyala.sample.R.layout.list_activity_layout);

    //Create the adapter for the ListView
    selectionAdapter = new ArrayAdapter<String>(this, R.layout.list_activity_list_item);
    for(String key : selectionMap.keySet()) {
      selectionAdapter.add(key);
    }
    selectionAdapter.notifyDataSetChanged();

    //Load the data into the ListView
    ListView selectionListView = (ListView) findViewById(R.id.mainActivityListView);
    selectionListView.setAdapter(selectionAdapter);
    selectionListView.setOnItemClickListener(this);
  }

  @Override
  public void onItemClick(AdapterView<?> l, View v, int pos, long id) {
    PlayerSelectionOption selection =  selectionMap.get(selectionAdapter.getItem(pos));
    Class<? extends Activity> selectedClass = selection.getActivity();

    //Launch the correct activity with the embed code as an extra
    Intent intent = new Intent(this, selectedClass);
    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    intent.putExtra("embed_code", selection.getEmbedCode());
    intent.putExtra("selection_name", selectionAdapter.getItem(pos));
    startActivity(intent);
    return;
  }
}
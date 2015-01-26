package com.ooyala.sample.lists;

import java.util.LinkedHashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ooyala.sample.R;
import com.ooyala.sample.players.AspectRatioVideoPlayerActivity;
import com.ooyala.sample.players.HLSVideoPlayerActivity;
import com.ooyala.sample.players.MP4VideoPlayerActivity;
import com.ooyala.sample.players.VODVideoPlayerActivity;
import com.ooyala.sample.utils.PlayerSelectionOption;

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
    selectionMap.put( AspectRatioVideoPlayerActivity.getName(), new PlayerSelectionOption("FwaXZjcjrkydIftLal2cq9ymQMuvjvD8", AspectRatioVideoPlayerActivity.class) );
    selectionMap.put( MP4VideoPlayerActivity.getName(), new PlayerSelectionOption("h4aHB1ZDqV7hbmLEv4xSOx3FdUUuephx", MP4VideoPlayerActivity.class) );
    selectionMap.put( HLSVideoPlayerActivity.getName(), new PlayerSelectionOption("Y1ZHB1ZDqfhCPjYYRbCEOz0GR8IsVRm1", HLSVideoPlayerActivity.class) );
    selectionMap.put( VODVideoPlayerActivity.getName(), new PlayerSelectionOption("92cWp0ZDpDm4Q8rzHfVK6q9m6OtFP-ww", VODVideoPlayerActivity.class) );
    
    
//    embedMap.put("MP4 Video",    "h4aHB1ZDqV7hbmLEv4xSOx3FdUUuephx");
//    embedMap.put("VOD with CCs", "92cWp0ZDpDm4Q8rzHfVK6q9m6OtFP-ww");
//    embedMap.put("WV-MP4",       "N3ZnF1ZDo2cUf0JIIFMaxv-gKgmF6Dvv");
//    embedMap.put("Avatar Widevine", "54NzJ4NTpMvOAm3p-rdp3qttbqkRad9I"); // WVM
//    embedMap.put("Channel",      "ozNTJ2ZDqvPWyXTriQF_Ovcd1VuKHGdH");
//    embedMap.put("Channel with pre-roll",    "FncDB0YTrvdMGK3Sva1NUmeQMuB33wbV");
//    embedMap.put("Multiple Google IMA",      "4wbjhoYTp6oRqD6lslRn0xYVTbm2GBzh");
//    embedMap.put("Video with Initial Time",    "Y1ZHB1ZDqfhCPjYYRbCEOz0GR8IsVRm1");

    setContentView(com.ooyala.sample.R.layout.list_activity_layout);

    //Create the adapter for the ListView
    selectionAdapter = new ArrayAdapter<String>(this, R.layout.list_activity_list_item);
    selectionAdapter.addAll(selectionMap.keySet());
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
    startActivity(intent);
    return;
  }
}
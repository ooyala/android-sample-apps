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

import com.ooyala.sample.players.ChannelContentTreePlayerActivity;
import com.ooyala.sample.players.PlaylistListActivity;
import com.ooyala.sample.utils.PlayerSelectionOption;

import java.util.LinkedHashMap;
import java.util.Map;

/*
  The list of channels to be browsed
  Channel ContentTree Player Activity will be launched if a channel is selected.
*/

public class OoyalaAPIListActivity extends Activity implements OnItemClickListener {
  public static String getName() {
    return "OoyalaAPI Sample";
  }
  private static final String TAG = "OoyalaAPIListActivity";
  private static final String CHANNEL_CODE = "txaGRiMzqQZSmFpMML92QczdIYUrcYVe";
  private static Map<String, PlayerSelectionOption> selectionMap;
  ArrayAdapter<String> selectionAdapter;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    selectionMap = new LinkedHashMap<String, PlayerSelectionOption>();
    //Populate the embed map
    selectionMap.put("ContentTree for Channel", new PlayerSelectionOption(CHANNEL_CODE, "c0cTkxOqALQviQIGAHWY5hP0q9gU", "http://www.ooyala.com", ChannelContentTreePlayerActivity.class) );
    selectionMap.put("Playlist API", new PlayerSelectionOption("", "", "http://www.ooyala.com", PlaylistListActivity.class) );

    setContentView(R.layout.list_activity_layout);

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
    intent.putExtra("pcode", selection.getPcode());
    intent.putExtra("domain", selection.getDomain());
    startActivity(intent);
    return;
  }
}
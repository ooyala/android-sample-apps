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
import com.ooyala.sample.players.ChangeVideoPlayerActivity;
import com.ooyala.sample.players.CustomControlsPlayerActivity;
import com.ooyala.sample.players.InsertAdPlayerActivity;
import com.ooyala.sample.players.PlayWithInitialTimePlayerActivity;
import com.ooyala.sample.players.PluginPlayerActivity;
import com.ooyala.sample.utils.PlayerSelectionOption;

public class AdvancedPlaybackListActivity extends Activity implements OnItemClickListener {
  public final static String getName() {
    return "Advanced Playback";
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
    selectionMap.put(PlayWithInitialTimePlayerActivity.getName(), new PlayerSelectionOption("Y1ZHB1ZDqfhCPjYYRbCEOz0GR8IsVRm1", PlayWithInitialTimePlayerActivity.class) );
    selectionMap.put(InsertAdPlayerActivity.getName(), new PlayerSelectionOption("Y1ZHB1ZDqfhCPjYYRbCEOz0GR8IsVRm1", InsertAdPlayerActivity.class) );
    selectionMap.put(ChangeVideoPlayerActivity.getName(), new PlayerSelectionOption("Y1ZHB1ZDqfhCPjYYRbCEOz0GR8IsVRm1", ChangeVideoPlayerActivity.class) );
    selectionMap.put(PluginPlayerActivity.getName(), new PlayerSelectionOption("lrZmRiMzrr8cP77PPW0W8AsjjhMJ1BBe", PluginPlayerActivity.class));
    selectionMap.put(CustomControlsPlayerActivity.getName(), new PlayerSelectionOption("Y1ZHB1ZDqfhCPjYYRbCEOz0GR8IsVRm1", CustomControlsPlayerActivity.class) );

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
    startActivity(intent);
    return;
  }
}
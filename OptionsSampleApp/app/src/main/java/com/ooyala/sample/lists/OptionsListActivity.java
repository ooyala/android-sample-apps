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
import com.ooyala.sample.players.CuePointsOptionsFreewheelPlayerActivity;
import com.ooyala.sample.players.PreloadOptionsPlayerActivity;
import com.ooyala.sample.players.PreloadWithInitTimePlayerActivity;
import com.ooyala.sample.players.ServerConfiguredTVRatingsPlayerActivity;
import com.ooyala.sample.players.TimeoutOptionsPlayerActivity;
import com.ooyala.sample.utils.PlayerSelectionOption;

import java.util.LinkedHashMap;
import java.util.Map;

public class OptionsListActivity extends Activity implements OnItemClickListener {
  public final static String getName() {
    return "Player Configuration with Options";
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
    selectionMap.put("CuePoints and AdsControl Options", new PlayerSelectionOption("NqcGg4bzoOmMiV35ZttQDtBX1oNQBnT-", CuePointsOptionsFreewheelPlayerActivity.class)); 
    selectionMap.put("Preload and PromoImage Options", new PlayerSelectionOption("NqcGg4bzoOmMiV35ZttQDtBX1oNQBnT-", PreloadOptionsPlayerActivity.class)); 
    selectionMap.put("Preload and Promo Options with Initial Time", new PlayerSelectionOption("Y1ZHB1ZDqfhCPjYYRbCEOz0GR8IsVRm1", PreloadWithInitTimePlayerActivity.class));
    selectionMap.put("Timeout Options", new PlayerSelectionOption("NqcGg4bzoOmMiV35ZttQDtBX1oNQBnT-", TimeoutOptionsPlayerActivity.class));
    selectionMap.put("Server-Side TV Ratings", new PlayerSelectionOption("c4eHZjcjqNetoCDCmzY_ApifO3qBuWpi", ServerConfiguredTVRatingsPlayerActivity.class));
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
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
import com.ooyala.sample.players.SecurePlayerEHLSPlayerActivity;
import com.ooyala.sample.players.SecurePlayerOPTPlayerActivity;
import com.ooyala.sample.players.SecurePlayerOptionsPlayerActivity;
import com.ooyala.sample.players.SecurePlayerPlayerActivity;
import com.ooyala.sample.players.SecurePlayerPrePersonalizedPlayerActivity;
import com.ooyala.sample.utils.PlayerSelectionOption;

public class SecurePlayerListActivity extends Activity implements OnItemClickListener {
  public final static String getName() {
    return "SecurePlayer Playback";
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
    selectionMap.put( "Ooyala-Ingested Playready Smooth VOD", new PlayerSelectionOption("5jNzJuazpFtKmloYZQmgPeC_tqDKHX9r", "FoeG863GnBL4IhhlFC1Q2jqbkH9m", "http://www.ooyala.com", SecurePlayerPlayerActivity.class) );
    selectionMap.put( "Playready HLS VOD with Closed Captions", new PlayerSelectionOption("xrcGYydDq1wU7nSmX7AQB3Uq4Fu3BjuE", "FoeG863GnBL4IhhlFC1Q2jqbkH9m", "http://www.ooyala.com", SecurePlayerPlayerActivity.class) );
    selectionMap.put( "Microsoft-Ingested Playready Smooth VOD", new PlayerSelectionOption("V2NWk2bTpI1ac0IaicMaFuMcIrmE9U-_", "FoeG863GnBL4IhhlFC1Q2jqbkH9m", "http://www.ooyala.com", SecurePlayerPlayerActivity.class) );
    selectionMap.put( "Microsoft-Ingested Clear Smooth VOD", new PlayerSelectionOption("1nNGk2bTq5ECsz5cRlZ4ONAAk96drr6T", "FoeG863GnBL4IhhlFC1Q2jqbkH9m", "http://www.ooyala.com", SecurePlayerPlayerActivity.class) );
    selectionMap.put( "Ooyala-Ingested Clear HLS VOD", new PlayerSelectionOption("Y1ZHB1ZDqfhCPjYYRbCEOz0GR8IsVRm1", "c0cTkxOqALQviQIGAHWY5hP0q9gU", "http://www.ooyala.com", SecurePlayerPlayerActivity.class) );
    selectionMap.put( "Ooyala Sample Encrypted HLS VOD", new PlayerSelectionOption("ZtZmtmbjpLGohvF5zBLvDyWexJ70KsL-", "c0cTkxOqALQviQIGAHWY5hP0q9gU", "http://www.ooyala.com", SecurePlayerEHLSPlayerActivity.class) );
    selectionMap.put( "Pre-Personalize", new PlayerSelectionOption("5jNzJuazpFtKmloYZQmgPeC_tqDKHX9r", "FoeG863GnBL4IhhlFC1Q2jqbkH9m", "http://www.ooyala.com", SecurePlayerPrePersonalizedPlayerActivity.class) );
    selectionMap.put( "VisualOn Configuration Options", new PlayerSelectionOption("Y1ZHB1ZDqfhCPjYYRbCEOz0GR8IsVRm1", "c0cTkxOqALQviQIGAHWY5hP0q9gU", "http://www.ooyala.com", SecurePlayerOptionsPlayerActivity.class) );
    selectionMap.put( "Playready HLS with Ooyala Player Token", new PlayerSelectionOption("Y2YmRyMzE6Z3IhqoN8YNXIWALVIiqNXr", "FoeG863GnBL4IhhlFC1Q2jqbkH9m", "http://www.ooyala.com", SecurePlayerOPTPlayerActivity.class) );

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
    intent.putExtra("pcode", selection.getPcode());
    intent.putExtra("domain", selection.getDomain());
    startActivity(intent);
    return;
  }
}
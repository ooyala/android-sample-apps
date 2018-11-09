package com.ooyala.sample.lists;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ooyala.sample.R;
import com.ooyala.sample.players.GeoBlockingActivity;
import com.ooyala.sample.utils.PlayerSelectionOption;

import java.util.LinkedHashMap;
import java.util.Map;

public class GeoBlockingListActivity  extends Activity implements AdapterView.OnItemClickListener {

  public static String getName() {
    return "Geo blocking samples";
  }

  private static Map<String, PlayerSelectionOption> selectionMap;
  ArrayAdapter<String> selectionAdapter;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.list_activity_layout);

    setTitle(getName());

    selectionMap = new LinkedHashMap<>();
    //ATTENTION: Those examples are from staging, because of this don't forget to switch player environment to STAGING
    selectionMap.put("Check false", new PlayerSelectionOption("NnY3B4ZDE6Syqv5IufZjL74vCFlbh1P4", "BzY2syOq6kIK6PTXN7mmrGVSJEFj", "http://www.ooyala.com", GeoBlockingActivity.class));
    selectionMap.put("Check true", new PlayerSelectionOption("toY3B4ZDE61Uo-yUnEdMlwJ0uCgfbmpk", "BzY2syOq6kIK6PTXN7mmrGVSJEFj", "http://www.ooyala.com", GeoBlockingActivity.class) );

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
    intent.putExtra("className",this.getClass().getSimpleName());
    intent.putExtra("selection_name", selectionAdapter.getItem(pos));
    intent.putExtra("pcode", selection.getPcode());
    intent.putExtra("domain", selection.getDomain());
    startActivity(intent);
    return;
  }
}

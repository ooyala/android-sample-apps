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
import com.ooyala.sample.players.PreconfiguredFreewheelPlayerActivity;
import com.ooyala.sample.utils.PlayerSelectionOption;

import java.util.LinkedHashMap;
import java.util.Map;

public class FreewheelListActivity extends Activity implements OnItemClickListener {
  public final static String getName() {
    return "Freewheel Integration";
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
    selectionMap.put("Freewheel Preroll", new PlayerSelectionOption("Q5MXg2bzq0UAXXMjLIFWio_6U0Jcfk6v", PreconfiguredFreewheelPlayerActivity.class) );
    selectionMap.put("Freewheel Midroll", new PlayerSelectionOption("NwcGg4bzrwxc6rqAZbYij4pWivBsX57a", PreconfiguredFreewheelPlayerActivity.class) );
    selectionMap.put("Freewheel Postroll", new PlayerSelectionOption("NmcGg4bzqbeqXO_x9Rfj5IX6gwmRRrse", PreconfiguredFreewheelPlayerActivity.class) );
    selectionMap.put("Freewheel PreMidPost", new PlayerSelectionOption("NqcGg4bzoOmMiV35ZttQDtBX1oNQBnT-", PreconfiguredFreewheelPlayerActivity.class) );
    selectionMap.put("Freewheel Overlay", new PlayerSelectionOption("NucGg4bzrVrilZrMdlSA9tyg6Vty46DN", PreconfiguredFreewheelPlayerActivity.class) );
    selectionMap.put("Freewheel Multi Midroll", new PlayerSelectionOption("htdnB3cDpMzXVL7fecaIWdv9rTd125As", PreconfiguredFreewheelPlayerActivity.class) );
    selectionMap.put("Freewheel PreMidPost Overlay", new PlayerSelectionOption("NscGg4bzpO9s5rUMyW-AAfoeEA7CX6hP", PreconfiguredFreewheelPlayerActivity.class) );

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
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
import com.ooyala.sample.players.NielsenDefaultPlayerActivity;
import com.ooyala.sample.utils.PlayerSelectionOption;

import java.util.LinkedHashMap;
import java.util.Map;

public class NielsenListActivity extends Activity implements OnItemClickListener {
  public final static String getName() {
    return "Nielsen Analytics";
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
    selectionMap.put( "ID3-Demo", new PlayerSelectionOption("84aDVmcTqN3FrdLXClZgJq-GfFEDhS1a", NielsenDefaultPlayerActivity.class) );
    selectionMap.put( "ID3-TravelEast", new PlayerSelectionOption("Y5aHlyczqJaJ2Mh6BNWLXfpcmxOaKzcx", NielsenDefaultPlayerActivity.class) );
    selectionMap.put( "ID3-Live", new PlayerSelectionOption("p4ZXNwdDrfdg2vz04LdpbRg94XXb7d_c", NielsenDefaultPlayerActivity.class) );
    selectionMap.put( "CMS-Demo", new PlayerSelectionOption("M3bmM3czp1j9horxoTLGaJtgLmW57u4F", NielsenDefaultPlayerActivity.class) );
    selectionMap.put( "CMS-NoAds", new PlayerSelectionOption("FzYjJzczo3_M3OjkeIta-IIFcPGSGxci", NielsenDefaultPlayerActivity.class) );
    selectionMap.put( "CMS-WithAds", new PlayerSelectionOption("x3YjJzczqREV-5RDiemsrdqki1FYu2NT", NielsenDefaultPlayerActivity.class) );
    selectionMap.put( "CMS-Live", new PlayerSelectionOption("RuZXNwdDpcWdrXskPkw73Mosq6sw6Fux", NielsenDefaultPlayerActivity.class) );

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
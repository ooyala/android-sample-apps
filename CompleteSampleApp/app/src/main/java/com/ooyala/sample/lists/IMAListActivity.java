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
import com.ooyala.sample.players.CustomConfiguredIMAPlayerActivity;
import com.ooyala.sample.players.PreconfiguredIMAPlayerActivity;
import com.ooyala.sample.utils.PlayerSelectionOption;

public class IMAListActivity extends Activity implements OnItemClickListener {
  public final static String getName() {
    return "Google IMA Integration";
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
    selectionMap.put("IMA Ad-Rules Preroll", new PlayerSelectionOption("EzZ29lcTq49IswgZYkMknnU4Ukb9PQMH", "R2NDYyOhSRhYj0UrUVgcdWlFVP-H", "http://www.ooyala.com", PreconfiguredIMAPlayerActivity.class) );
    selectionMap.put("IMA Ad-Rules Midroll", new PlayerSelectionOption("VlaG9lcTqeUU18adfd1DVeQ8YekP3H4l", "R2NDYyOhSRhYj0UrUVgcdWlFVP-H", "http://www.ooyala.com", PreconfiguredIMAPlayerActivity.class) );
    selectionMap.put("IMA Ad-Rules Postroll", new PlayerSelectionOption("BnaG9lcTqLXQNyod7ON8Yv3eDas2Oog6", "R2NDYyOhSRhYj0UrUVgcdWlFVP-H", "http://www.ooyala.com", PreconfiguredIMAPlayerActivity.class) );
    selectionMap.put("IMA Podded Preroll", new PlayerSelectionOption("1wNjE3cDox0G3hQIWxTjsZ8MPUDLSkDY", "R2NDYyOhSRhYj0UrUVgcdWlFVP-H", "http://www.ooyala.com", PreconfiguredIMAPlayerActivity.class) );
    selectionMap.put("IMA Podded Midroll", new PlayerSelectionOption("1yNjE3cDodUEfUfp2WNzHkCZCMb47MUP", "R2NDYyOhSRhYj0UrUVgcdWlFVP-H", "http://www.ooyala.com", PreconfiguredIMAPlayerActivity.class) );
    selectionMap.put("IMA Podded Postroll", new PlayerSelectionOption("1sNjE3cDoN3ZewFm1238ce730J4BMrEJ", "R2NDYyOhSRhYj0UrUVgcdWlFVP-H", "http://www.ooyala.com", PreconfiguredIMAPlayerActivity.class) );
    selectionMap.put("IMA Podded Pre-Mid-Post", new PlayerSelectionOption("ZrOTE3cDoXo2sLOWzQPxjS__M-Qk32Co", "R2NDYyOhSRhYj0UrUVgcdWlFVP-H", "http://www.ooyala.com", PreconfiguredIMAPlayerActivity.class) );
    selectionMap.put("IMA Skippable", new PlayerSelectionOption("FhbGRjbzq8tfaoA3dhfxc2Qs0-RURJfO", "R2NDYyOhSRhYj0UrUVgcdWlFVP-H", "http://www.ooyala.com", PreconfiguredIMAPlayerActivity.class) );
    selectionMap.put("IMA Pre, Mid and Post Skippable", new PlayerSelectionOption("10NjE3cDpj8nUzYiV1PnFsjC6nEvPQAE", "R2NDYyOhSRhYj0UrUVgcdWlFVP-H", "http://www.ooyala.com", PreconfiguredIMAPlayerActivity.class) );
    selectionMap.put("IMA Application-Configured", new PlayerSelectionOption("Y1ZHB1ZDqfhCPjYYRbCEOz0GR8IsVRm1", "c0cTkxOqALQviQIGAHWY5hP0q9gU", "http://www.ooyala.com", CustomConfiguredIMAPlayerActivity.class) );

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
    intent.putExtra("pcode", selection.getPcode());
    intent.putExtra("domain", selection.getDomain());
    startActivity(intent);
    return;
  }
}
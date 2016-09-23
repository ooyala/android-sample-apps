package com.ooyala.sample.lists;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ooyala.sample.R;
import com.ooyala.sample.players.NPAWDefaultPlayerActivity;
import com.ooyala.sample.utils.PlayerSelectionOption;
import com.ooyala.sample.utils.youbora.YouboraConfigManager;

import java.util.LinkedHashMap;
import java.util.Map;

public class NPAWYouboraListActivity extends Activity implements OnItemClickListener {
  public final static String getName() {
    return "NPAW Youbura Integration";
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
    selectionMap.put("4:3 Aspect Ratio", new PlayerSelectionOption("FwaXZjcjrkydIftLal2cq9ymQMuvjvD8", "c0cTkxOqALQviQIGAHWY5hP0q9gU", "http://www.ooyala.com", NPAWDefaultPlayerActivity.class));
    selectionMap.put("MP4 Video", new PlayerSelectionOption("c4cGVibjpwhBnz14x3UUUin1Oyr8_qC5", "c0cTkxOqALQviQIGAHWY5hP0q9gU", "http://www.ooyala.com", NPAWDefaultPlayerActivity.class));
    selectionMap.put("HLS Video", new PlayerSelectionOption("Y1ZHB1ZDqfhCPjYYRbCEOz0GR8IsVRm1", "c0cTkxOqALQviQIGAHWY5hP0q9gU", "http://www.ooyala.com", NPAWDefaultPlayerActivity.class));
    selectionMap.put("VOD with CCs", new PlayerSelectionOption("92cWp0ZDpDm4Q8rzHfVK6q9m6OtFP-ww", "c0cTkxOqALQviQIGAHWY5hP0q9gU", "http://www.ooyala.com", NPAWDefaultPlayerActivity.class));

    selectionMap.put("VAST2 Ad Pre-roll", new PlayerSelectionOption("Zlcmp0ZDrpHlAFWFsOBsgEXFepeSXY4c", "BidTQxOqebpNk1rVsjs2sUJSTOZc", "http://www.ooyala.com", NPAWDefaultPlayerActivity.class));
    selectionMap.put("VAST2 Ad Mid-roll", new PlayerSelectionOption("pncmp0ZDp7OKlwTPJlMZzrI59j8Imefa", "BidTQxOqebpNk1rVsjs2sUJSTOZc", "http://www.ooyala.com", NPAWDefaultPlayerActivity.class));
    selectionMap.put("VAST2 Ad Post-roll", new PlayerSelectionOption("Zpcmp0ZDpaB-90xK8MIV9QF973r1ZdUf", "BidTQxOqebpNk1rVsjs2sUJSTOZc", "http://www.ooyala.com", NPAWDefaultPlayerActivity.class));
    setContentView(R.layout.list_activity_layout);

    //Create the adapter for the ListView
    selectionAdapter = new ArrayAdapter<String>(this, R.layout.list_activity_list_item);
    for (String key : selectionMap.keySet()) {
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
    PlayerSelectionOption selection = selectionMap.get(selectionAdapter.getItem(pos));
    Class<? extends Activity> selectedClass = selection.getActivity();

    //Launch the correct activity with the embed code as an extra
    Intent intent = new Intent(this, selectedClass);
    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    intent.putExtra("embed_code", selection.getEmbedCode());
    intent.putExtra("pcode", selection.getPcode());
    intent.putExtra("domain", selection.getDomain());
    intent.putExtra("selection_name", selectionAdapter.getItem(pos));
    startActivity(intent);
    return;
  }


  /*
   * Create the Options menu, which you can use to configure all Youbora configuration in the app
   */
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main_menu, menu);
    return true;
  }

  /*
   * Handle the display of YouboraConfigActivity and resetting the configuration
   */
  @Override
  public boolean onMenuItemSelected(int featureId, MenuItem item) {
    switch (item.getItemId()) {
      case R.id.youbora_config_show:
        YouboraConfigManager.showConfig(getApplicationContext());
        return true;
      case R.id.youbora_config_reset:
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            YouboraConfigManager.resetPreferences(getApplicationContext());
          }
        });
        builder.setNegativeButton("No", null);
        builder.setMessage("Do you want to restore the default Youbora config?");
        builder.setTitle("Restore defaults");

        AlertDialog dialog = builder.create();

        dialog.show();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }
}
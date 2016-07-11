package com.ooyala.sample.lists;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.gnzlt.AndroidVisionQRReader.QRActivity;
import com.ooyala.sample.R;
import com.ooyala.sample.players.BasicPlaybackVideoPlayerActivity;
import com.ooyala.sample.players.MultiVideosPlaybackVideoPlayerActivity;
import com.ooyala.sample.utils.PlayerSelectionOption;

import java.util.LinkedHashMap;
import java.util.Map;

public class BasicPlaybackListActivity extends AppCompatActivity implements OnItemClickListener {
  public final static String getName() {
    return "Basic Playback";
  }
  public final static int QR_REQUEST = 1111;

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
    selectionMap.put( "4:3 Aspect Ratio", new PlayerSelectionOption("FwaXZjcjrkydIftLal2cq9ymQMuvjvD8", BasicPlaybackVideoPlayerActivity.class) );
    selectionMap.put( "MP4 Video", new PlayerSelectionOption("h4aHB1ZDqV7hbmLEv4xSOx3FdUUuephx", BasicPlaybackVideoPlayerActivity.class) );
    selectionMap.put( "HLS Video", new PlayerSelectionOption("Y1ZHB1ZDqfhCPjYYRbCEOz0GR8IsVRm1", BasicPlaybackVideoPlayerActivity.class) );
    selectionMap.put( "VOD with CCs", new PlayerSelectionOption("92cWp0ZDpDm4Q8rzHfVK6q9m6OtFP-ww", BasicPlaybackVideoPlayerActivity.class) );

    selectionMap.put("VAST Ad Pre-roll", new PlayerSelectionOption("Zlcmp0ZDrpHlAFWFsOBsgEXFepeSXY4c", BasicPlaybackVideoPlayerActivity.class));
    selectionMap.put("VAST Ad Mid-roll", new PlayerSelectionOption("pncmp0ZDp7OKlwTPJlMZzrI59j8Imefa", BasicPlaybackVideoPlayerActivity.class));
    selectionMap.put("VAST Ad Post-roll", new PlayerSelectionOption("Zpcmp0ZDpaB-90xK8MIV9QF973r1ZdUf", BasicPlaybackVideoPlayerActivity.class));
    selectionMap.put("VAST Ad Wrapper", new PlayerSelectionOption("pqaWp0ZDqo17Z-Dn_5YiVhjcbQYs5lhq", BasicPlaybackVideoPlayerActivity.class));
    selectionMap.put("Ooyala Ad Pre-roll", new PlayerSelectionOption("M4cmp0ZDpYdy8kiL4UD910Rw_DWwaSnU", BasicPlaybackVideoPlayerActivity.class));
    selectionMap.put("Ooyala Ad Mid-roll", new PlayerSelectionOption("xhcmp0ZDpnDB2-hXvH7TsYVQKEk_89di", BasicPlaybackVideoPlayerActivity.class));
    selectionMap.put("Ooyala Ad Post-roll", new PlayerSelectionOption("Rjcmp0ZDr5yFbZPEfLZKUveR_2JzZjMO", BasicPlaybackVideoPlayerActivity.class));
    selectionMap.put("Multi Ad combination", new PlayerSelectionOption("Ftcmp0ZDoz8tALmhPcN2vMzCdg7YU9lc", BasicPlaybackVideoPlayerActivity.class));

    selectionMap.put("Multi Video combination(HLS + MP4)", new PlayerSelectionOption("Y1ZHB1ZDqfhCPjYYRbCEOz0GR8IsVRm1", MultiVideosPlaybackVideoPlayerActivity.class));
    selectionMap.put("Scan Code", new PlayerSelectionOption("", QRActivity.class));

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

    if (selectedClass.equals(QRActivity.class)) {
      // launch QR scanner
      Intent qrScanIntent = new Intent(this, QRActivity.class);
      startActivityForResult(qrScanIntent, QR_REQUEST);

    } else {
      launchPlayer(selection, selectionAdapter.getItem(pos));
    }
    return;
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == QR_REQUEST) {
      if (resultCode == RESULT_OK) {
        String embedCode = data.getStringExtra(QRActivity.EXTRA_QR_RESULT);
        PlayerSelectionOption selection = new PlayerSelectionOption(embedCode, BasicPlaybackVideoPlayerActivity.class);
        launchPlayer(selection, "Scan: " + embedCode);
      } else {
        Toast.makeText(getApplicationContext(), "QR Code Scan failed", Toast.LENGTH_LONG).show();
      }
    }
  }

  private void launchPlayer(PlayerSelectionOption selection, String title) {
    //Launch the correct activity with the embed code as an extra
    Intent intent = new Intent(this, selection.getActivity());
    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    intent.putExtra("embed_code", selection.getEmbedCode());
    intent.putExtra("selection_name", title);
    startActivity(intent);
  }
}
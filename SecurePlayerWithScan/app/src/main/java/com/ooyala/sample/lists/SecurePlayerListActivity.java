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
import com.ooyala.sample.players.SecurePlayerEHLSPlayerActivity;
import com.ooyala.sample.players.SecurePlayerOptionsPlayerActivity;
import com.ooyala.sample.players.SecurePlayerPlayerActivity;
import com.ooyala.sample.utils.CompletePlayerSelectionOption;
import com.ooyala.sample.utils.PlayerSelectionOption;

import java.util.LinkedHashMap;
import java.util.Map;

public class SecurePlayerListActivity extends AppCompatActivity implements OnItemClickListener {
  public final static String getName() {
    return "SecurePlayer Playback";
  }
  public final static int QR_REQUEST = 1122;

  private final static String DOMAIN = "http://ooyala.com";
  private final static String PCODE = "FoeG863GnBL4IhhlFC1Q2jqbkH9m";

  private static Map<String, PlayerSelectionOption> selectionMap;
  ArrayAdapter<String> selectionAdapter;

  private CompletePlayerSelectionOption selectedOption;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle(getName());

    selectionMap = new LinkedHashMap<String, PlayerSelectionOption>();
    //Populate the embed map
    selectionMap.put( "Ooyala-Ingested Playready Smooth VOD",
        new CompletePlayerSelectionOption("5jNzJuazpFtKmloYZQmgPeC_tqDKHX9r", PCODE, DOMAIN, SecurePlayerPlayerActivity.class) );
    selectionMap.put( "Playready HLS VOD with Closed Captions",
        new CompletePlayerSelectionOption("xrcGYydDq1wU7nSmX7AQB3Uq4Fu3BjuE", PCODE, DOMAIN, SecurePlayerPlayerActivity.class) );
    selectionMap.put( "Microsoft-Ingested Playready Smooth VOD",
        new CompletePlayerSelectionOption("V2NWk2bTpI1ac0IaicMaFuMcIrmE9U-_", PCODE, DOMAIN, SecurePlayerPlayerActivity.class) );
    selectionMap.put( "Microsoft-Ingested Clear Smooth VOD",
        new CompletePlayerSelectionOption("1nNGk2bTq5ECsz5cRlZ4ONAAk96drr6T", PCODE, DOMAIN, SecurePlayerPlayerActivity.class) );
    selectionMap.put( "Ooyala-Ingested Clear HLS VOD",
        new CompletePlayerSelectionOption("Y1ZHB1ZDqfhCPjYYRbCEOz0GR8IsVRm1", PCODE, DOMAIN, SecurePlayerPlayerActivity.class) );
    selectionMap.put( "Ooyala Sample Encrypted HLS VOD",
        new CompletePlayerSelectionOption("ZtZmtmbjpLGohvF5zBLvDyWexJ70KsL-", PCODE, DOMAIN, SecurePlayerEHLSPlayerActivity.class) );
    selectionMap.put( "VisualOn Configuration Options",
        new CompletePlayerSelectionOption("Y1ZHB1ZDqfhCPjYYRbCEOz0GR8IsVRm1", PCODE, DOMAIN, SecurePlayerOptionsPlayerActivity.class) );
    selectionMap.put( "Scan code", new CompletePlayerSelectionOption("", "", "", QRActivity.class) );

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
    CompletePlayerSelectionOption selection =
        (CompletePlayerSelectionOption)selectionMap.get(selectionAdapter.getItem(pos));
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
        String result = data.getStringExtra(QRActivity.EXTRA_QR_RESULT);
        String[] codes = result.split(";");
        String embedCode = codes[0];
        String pcode = codes.length > 1 ? codes[1] : PCODE;
        CompletePlayerSelectionOption selection =
            new CompletePlayerSelectionOption(embedCode, pcode, DOMAIN, SecurePlayerPlayerActivity.class);
        launchPlayer(selection, "Scan: " + embedCode);
      } else {
        Toast.makeText(getApplicationContext(), "QR Code Scan failed", Toast.LENGTH_LONG).show();
      }
    }
  }

  private void launchPlayer(CompletePlayerSelectionOption selection, String title) {
    //Launch the correct activity with the embed code as an extra
    Intent intent = new Intent(this, selection.getActivity());
    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    intent.putExtra("embed_code", selection.getEmbedCode());
    intent.putExtra("pcode", selection.getPcode());
    intent.putExtra("selection_name", title);
    startActivity(intent);
  }
}
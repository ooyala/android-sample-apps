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
import com.ooyala.sample.players.AdobePassSampleAppAcitivity;
import com.ooyala.sample.players.OoyalaPlayerTokenPlayerActivity;
import com.ooyala.sample.utils.PlayerSelectionOption;

import java.util.LinkedHashMap;
import java.util.Map;

public class ContentProtectionListActivity extends Activity implements OnItemClickListener {
  public final static String getName() {
    return "Content Protection";
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
//    selectionMap.put("Adobe Pass Integration", new PlayerSelectionOption("VybW5lODrJ0uM9FBo7XTT6TNjTJfr_7G", "B3MDExOuTldXc1CiXbzAauYN7Iui", "http://www.ooyala.com", AdobePassSampleAppAcitivity.class) );
   // selectionMap.put("Ooyala Player Token", new PlayerSelectionOption("0yMjJ2ZDosUnthiqqIM3c8Eb8Ilx5r52", "c0cTkxOqALQviQIGAHWY5hP0q9gU", "http://www.ooyala.com", OoyalaPlayerTokenPlayerActivity.class) );
    selectionMap.put("OPT HLS", new PlayerSelectionOption("NjZ3B3ZDE6hN_sIoXotGDlI1s2cDLUwH", "BjcWYyOu1KK2DiKOkF41Z2k0X57l", "http://www.ooyala.com", OoyalaPlayerTokenPlayerActivity.class) );
    selectionMap.put("OPT DASH+CENC", new PlayerSelectionOption("UwMWN3ZDE6OHhkeOM55sBp_y7hM9S5l1", "BjcWYyOu1KK2DiKOkF41Z2k0X57l", "http://www.ooyala.com", OoyalaPlayerTokenPlayerActivity.class) );
    selectionMap.put("GEO DASH CENC", new PlayerSelectionOption("U0NWN3ZDE6m1dv7MANXArUyZXM25aTiJ", "BjcWYyOu1KK2DiKOkF41Z2k0X57l", "http://www.ooyala.com", OoyalaPlayerTokenPlayerActivity.class) );
    selectionMap.put("GEO HLS", new PlayerSelectionOption("JkOGN3ZDE6_QxkKCqUxi07r7wtrFCaYC", "BjcWYyOu1KK2DiKOkF41Z2k0X57l", "http://www.ooyala.com", OoyalaPlayerTokenPlayerActivity.class) );
    selectionMap.put("Concurrent eHls V4", new PlayerSelectionOption("BkcmI1ZTE62Oy-RvkIL3iwlwlZxxfgAZ", "BjcWYyOu1KK2DiKOkF41Z2k0X57l", "http://www.ooyala.com", OoyalaPlayerTokenPlayerActivity.class) );
    selectionMap.put("Concurrent DASH", new PlayerSelectionOption("UxYWN3ZDE6Aa-65vFEX3b31roAipFXq4", "BjcWYyOu1KK2DiKOkF41Z2k0X57l", "http://www.ooyala.com", OoyalaPlayerTokenPlayerActivity.class) );
    selectionMap.put("DeviceLimit Dash CENC", new PlayerSelectionOption("J2ZHB3ZDE6cR8Ik_qTzj3x7frRPPfMzd", "BjcWYyOu1KK2DiKOkF41Z2k0X57l", "http://www.ooyala.com", OoyalaPlayerTokenPlayerActivity.class) );
    selectionMap.put("DeviceLimit eHls V3", new PlayerSelectionOption("BodGI1ZTE67aGi1BR88Wv_bW2eSD8TC8", "BjcWYyOu1KK2DiKOkF41Z2k0X57l", "http://www.ooyala.com", OoyalaPlayerTokenPlayerActivity.class) );
    selectionMap.put("Flight time Dash", new PlayerSelectionOption("V2N3B3ZDE6a7aCv-znaB8Y62iGsXDpn-", "BjcWYyOu1KK2DiKOkF41Z2k0X57l", "http://www.ooyala.com", OoyalaPlayerTokenPlayerActivity.class) );
    selectionMap.put("Flight time HLS", new PlayerSelectionOption("Jzd2I1ZTE6vbfQZppb7qpuFD9ahMFtyZ", "BjcWYyOu1KK2DiKOkF41Z2k0X57l", "http://www.ooyala.com", OoyalaPlayerTokenPlayerActivity.class) );
    selectionMap.put("Azure Dash", new PlayerSelectionOption("JvZ292ZDE6vK9LHah1cUAjzSDvjoZyb9", "hjZ2gyOn8Y0z7pvLjDdvaibqJ-dR", "http://www.ooyala.com", OoyalaPlayerTokenPlayerActivity.class) );
    selectionMap.put("DeviceControl HLS", new PlayerSelectionOption("pzMWM1ZTE66-DrUrqAN_VWpsw0dkNR9s", "BjcWYyOu1KK2DiKOkF41Z2k0X57l", "http://www.ooyala.com", OoyalaPlayerTokenPlayerActivity.class) );
    setContentView(R.layout.list_activity_layout);

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
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
import com.ooyala.sample.players.PlaybackSpeedActivity;
import com.ooyala.sample.utils.SsaiSelectionOption;
import com.ooyala.sample.utils.Utils;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class PlaybackSpeedListActivity extends Activity implements OnItemClickListener {
  private static final String TAG = PlaybackSpeedListActivity.class.getSimpleName();

  private static final String DOMAIN = "http://www.ooyala.com";
  private Map<String, SsaiSelectionOption> selectionMap;
  private ArrayAdapter<String> selectionAdapter;

  public static String getName() {
    return PlaybackSpeedListActivity.class.getSimpleName();
  }

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.list_activity_layout);
    setTitle(getName());

    selectionMap = new LinkedHashMap<>();
    // VR assets
    String pcode = "BzY2syOq6kIK6PTXN7mmrGVSJEFj";
    selectionMap.put("[No ADS] V360", new SsaiSelectionOption("FwOG5mZDE66kvaxH6EyZpj0iJ2AxBj_v", pcode, DOMAIN, PlaybackSpeedActivity.class));
    selectionMap.put("[IMA ADS 360] Pre-Mid-Post skippable", new SsaiSelectionOption("Z4Y2UyZDE6bi5ZhPJE860W8GcE3z6WkE", pcode, DOMAIN, PlaybackSpeedActivity.class));
    selectionMap.put("[Ooyala ADS 360] Pre-roll", new SsaiSelectionOption("k5YW1lZDE66qkVK2I-Em409mREbGmw3f", pcode, DOMAIN, PlaybackSpeedActivity.class));
    selectionMap.put("[VAST ADS 360] Pre-Mid-Post", new SsaiSelectionOption("5uc21lZDE6uxI-13aXuCJm2RumePzyCe", pcode, DOMAIN, PlaybackSpeedActivity.class));

    // 2D assets
    selectionMap.put("[No ADS] 2D", new SsaiSelectionOption("VzZHd2YzE612sYDbk2UyuurOXrLgsVx9", pcode, DOMAIN, PlaybackSpeedActivity.class));
    selectionMap.put("[IMA ADS 2D] Pre-Mid-Post", new SsaiSelectionOption("tsd282ZDE6ntnpHkMcWP4MnuBQXR2PAw", pcode, DOMAIN, PlaybackSpeedActivity.class));
    selectionMap.put("[Ooyala ADS 2D] Pre-roll", new SsaiSelectionOption("dmNXA2ZDE636pnqwBZiB0askbt9JHYwQ", pcode, DOMAIN, PlaybackSpeedActivity.class));
    selectionMap.put("[VAST ADS 2D] Podded 2Pre-2Mid-2Post", new SsaiSelectionOption("lzNHA2ZDE6_oDV04VtY7sg4Pr7jGErfG", pcode, DOMAIN, PlaybackSpeedActivity.class));

    // SSAI assets with Ads
    pcode = "ZsdGgyOnugo44o442aALkge_dVVK";
    selectionMap.put("[SSAI ADS] VOD - Ooyala Pulse", new SsaiSelectionOption("l5bm11ZjE6VFJyNE2iE6EKpCBVSRroAF", pcode, DOMAIN, PlaybackSpeedActivity.class, getPulseParams()));
    selectionMap.put("[SSAI ADS] VOD - DFP", new SsaiSelectionOption("13bm11ZjE6Wl7CQ2iKPH_Z1VpspHGOud", pcode, DOMAIN, PlaybackSpeedActivity.class, getDfpParams()));

    //Create the adapter for the ListView
    selectionAdapter = new ArrayAdapter<>(this, R.layout.list_activity_list_item);
    for(String key : selectionMap.keySet()) {
      selectionAdapter.add(key);
    }
    selectionAdapter.notifyDataSetChanged();

    //Load the data into the ListView
    ListView selectionListView = findViewById(R.id.mainActivityListView);
    selectionListView.setAdapter(selectionAdapter);
    selectionListView.setOnItemClickListener(this);
  }

  @Override
  public void onItemClick(AdapterView<?> l, View v, int pos, long id) {
    SsaiSelectionOption selection =  selectionMap.get(selectionAdapter.getItem(pos));
    Class<? extends Activity> selectedClass = selection.getActivity();

    //Launch the correct activity with the embed code as an extra
    Intent intent = new Intent(this, selectedClass);
    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    intent.putExtra("embed_code", selection.getEmbedCode());
    intent.putExtra("selection_name", selectionAdapter.getItem(pos));
    intent.putExtra("pcode", selection.getPcode());
    intent.putExtra("domain", selection.getDomain());
    intent.putExtra("player_params", selection.getPlayerParams());
    startActivity(intent);
    return;
  }

  private String getDfpParams() {
    String dfpParamsFromJson = getParams("dfpPlayerParams.json");
    return dfpParamsFromJson;
  }

  private String getPulseParams() {
    String pulseParams = getParams("ooyalaPulsePlayerParams.json");
    return pulseParams;
  }

  private String getParams(String resource) {
    ClassLoader classLoader = this.getClass().getClassLoader();
    JSONObject json = Utils.getResourceAsJsonObject(classLoader, resource);
    String params = json == null ? "" : json.toString();
    return params;
  }
}

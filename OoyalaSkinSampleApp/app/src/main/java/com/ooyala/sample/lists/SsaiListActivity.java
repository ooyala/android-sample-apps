package com.ooyala.sample.lists;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ooyala.sample.R;
import com.ooyala.sample.players.SsaiPlayerActivity;
import com.ooyala.sample.utils.SsaiSelectionOption;
import com.ooyala.sample.utils.Utils;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;


public class SsaiListActivity extends Activity implements AdapterView.OnItemClickListener {
  private static final int LOAD_REACT_BUNDLE_PERMISSION_REQ_CODE = 666;

  public final static String getName() {
    return "SSAI Playback";
  }

  private static Map<String, SsaiSelectionOption> selectionMap;
  ArrayAdapter<String> selectionAdapter;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle(getName());
    String playerParamsLiveTeam = "{\"videoplaza-ads-manager\":{\"metadata\":{\"all_ads\":[{\"position\":\"7\"}],\"playerLevelCuePoints\":\"30,10,20\",\"playerLevelShares\":\"nab\",\"playerLevelTags\":\"me,mw,fe,fw\",\"vpDomain\":\"live-team\"}}}";
    String playerParamsVideoPlaza = "{\"videoplaza-ads-manager\":{\"metadata\":{\"all_ads\":[{\"position\":\"7\"}],\"playerLevelCuePoints\":\"10,20,30\",\"playerLevelShares\":\"ssai-playback\",\"playerLevelTags\":\"ssai\",\"vpDomain\":\"live-team.videoplaza.tv\"}}}";

    ClassLoader classLoader = this.getClass().getClassLoader();
    JSONObject dataFromJson = Utils.getResourceAsJsonObject(classLoader, "ssaiPlayerParams.json");
    String paramsFromJson = dataFromJson == null ? "" : dataFromJson.toString();

    JSONObject dfpFromJson = Utils.getResourceAsJsonObject(classLoader, "dfpPlayerParams.json");
    String dfpParamsFromJson = dfpFromJson == null ? "" : dfpFromJson.toString();

    JSONObject pulseFromJson = Utils.getResourceAsJsonObject(classLoader, "ooyalaPulsePlayerParams.json");
    String pulseParamsFromJson = pulseFromJson == null ? "" : pulseFromJson.toString();

    selectionMap = new LinkedHashMap<>();
    selectionMap.put("Live - Ooyala Pulse", new SsaiSelectionOption("lkb2cyZjE6wp94YSGIEjm6Em1yH0P3zT", "RpOWUyOq86gFq-STNqpgzhzIcXHV", "http://www.ooyala.com", SsaiPlayerActivity.class, playerParamsLiveTeam));
    selectionMap.put("Player Params from JSON", new SsaiSelectionOption("ltZ3l5YjE6lUAvBdflvcDQ-zti8q8Urd", "RpOWUyOq86gFq-STNqpgzhzIcXHV", "http://www.ooyala.com", SsaiPlayerActivity.class, paramsFromJson));
    selectionMap.put("Player Params - vpDomain:live-team", new SsaiSelectionOption("ltZ3l5YjE6lUAvBdflvcDQ-zti8q8Urd", "RpOWUyOq86gFq-STNqpgzhzIcXHV", "http://www.ooyala.com", SsaiPlayerActivity.class, playerParamsLiveTeam));
    selectionMap.put("Player Params - vpDomain:videoplaza", new SsaiSelectionOption("ltZ3l5YjE6lUAvBdflvcDQ-zti8q8Urd", "RpOWUyOq86gFq-STNqpgzhzIcXHV", "http://www.ooyala.com", SsaiPlayerActivity.class, playerParamsVideoPlaza));
    selectionMap.put("VOD - Ooyala Pulse", new SsaiSelectionOption("l5bm11ZjE6VFJyNE2iE6EKpCBVSRroAF", "ZsdGgyOnugo44o442aALkge_dVVK", "http://www.ooyala.com", SsaiPlayerActivity.class, pulseParamsFromJson));
    selectionMap.put("VOD - DFP", new SsaiSelectionOption("13bm11ZjE6Wl7CQ2iKPH_Z1VpspHGOud", "ZsdGgyOnugo44o442aALkge_dVVK", "http://www.ooyala.com", SsaiPlayerActivity.class, dfpParamsFromJson));
    selectionMap.put("Without Player Params", new SsaiSelectionOption("ltZ3l5YjE6lUAvBdflvcDQ-zti8q8Urd", "RpOWUyOq86gFq-STNqpgzhzIcXHV", "http://www.ooyala.com", SsaiPlayerActivity.class));

    setContentView(R.layout.list_activity_layout);

    //Create the adapter for the ListView
    selectionAdapter = new ArrayAdapter<>(this, R.layout.list_activity_list_item);
    for (String key : selectionMap.keySet()) {
      selectionAdapter.add(key);
    }
    selectionAdapter.notifyDataSetChanged();

    //Load the data into the ListView
    ListView selectionListView = (ListView) findViewById(R.id.mainActivityListView);
    selectionListView.setAdapter(selectionAdapter);
    selectionListView.setOnItemClickListener(this);
  }

  private void showDevOptionsDialog() {
    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
    startActivityForResult(intent, LOAD_REACT_BUNDLE_PERMISSION_REQ_CODE);
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
  }
}
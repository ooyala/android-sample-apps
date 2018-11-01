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
import com.ooyala.sample.utils.PlayerSelectionOption;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
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
   * This is used to store information of a Ssai sample activity for use in a Map or List
   *
   */
  class SsaiSelectionOption extends PlayerSelectionOption {
    private String playerParams;

    public SsaiSelectionOption(String embedCode, String pcode, String domain, Class<? extends Activity> activity) {
      this(embedCode, pcode, domain, activity, "");
    }

    public SsaiSelectionOption(String embedCode, String pcode, String domain, Class<? extends Activity> activity, String playerParams) {
      super(embedCode, pcode, domain, activity);
      this.playerParams = playerParams;
    }

    public String getPlayerParams() {
      return playerParams;
    }

    public void setPlayerParams(String playerParams) {
      this.playerParams = playerParams;
    }
  }

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle(getName());
    String playerParamsLiveTeam = "{\"videoplaza-ads-manager\":{\"metadata\":{\"all_ads\":[{\"position\":\"7\"}],\"playerLevelCuePoints\":\"30,10,20\",\"playerLevelShares\":\"nab\",\"playerLevelTags\":\"me,mw,fe,fw\",\"vpDomain\":\"live-team\"}}}";
    String playerParamsVideoPlaza = "{\"videoplaza-ads-manager\":{\"metadata\":{\"all_ads\":[{\"position\":\"7\"}],\"playerLevelCuePoints\":\"10,20,30\",\"playerLevelShares\":\"ssai-playback\",\"playerLevelTags\":\"ssai\",\"vpDomain\":\"live-team.videoplaza.tv\"}}}";

    JSONObject dataFromJson = getResourceAsJsonObject("ssaiPlayerParams.json");
    String paramsFromJson = dataFromJson == null ? "" : dataFromJson.toString();

    JSONObject dfpFromJson = getResourceAsJsonObject("dfpPlayerParams.json");
    String dfpParamsFromJson = dfpFromJson == null ? "" : dfpFromJson.toString();

    JSONObject pulseFromJson = getResourceAsJsonObject("ooyalaPulsePlayerParams.json");
    String pulseParamsFromJson = pulseFromJson == null ? "" : pulseFromJson.toString();

    selectionMap = new LinkedHashMap<>();
    selectionMap.put("Live - Ooyala Pulse", new SsaiSelectionOption("lkb2cyZjE6wp94YSGIEjm6Em1yH0P3zT", "RpOWUyOq86gFq-STNqpgzhzIcXHV", "http://www.ooyala.com", SsaiPlayerActivity.class, playerParamsLiveTeam));
    selectionMap.put("Live - DFP", new SsaiSelectionOption("s2M213ZDE6A-DU2Tr-k0DI-8PgnFIcmU", "RpOWUyOq86gFq-STNqpgzhzIcXHV", "http://www.ooyala.com", SsaiPlayerActivity.class, dfpParamsFromJson));
    selectionMap.put("Player Params from JSON", new SsaiSelectionOption("ltZ3l5YjE6lUAvBdflvcDQ-zti8q8Urd", "RpOWUyOq86gFq-STNqpgzhzIcXHV", "http://www.ooyala.com", SsaiPlayerActivity.class, paramsFromJson));
    selectionMap.put("Player Params - vpDomain:live-team", new SsaiSelectionOption("ltZ3l5YjE6lUAvBdflvcDQ-zti8q8Urd", "RpOWUyOq86gFq-STNqpgzhzIcXHV", "http://www.ooyala.com", SsaiPlayerActivity.class, playerParamsLiveTeam));
    selectionMap.put("Player Params - vpDomain:videoplaza", new SsaiSelectionOption("ltZ3l5YjE6lUAvBdflvcDQ-zti8q8Urd", "RpOWUyOq86gFq-STNqpgzhzIcXHV", "http://www.ooyala.com", SsaiPlayerActivity.class, playerParamsVideoPlaza));
    selectionMap.put("VOD - Ooyala Pulse", new SsaiSelectionOption("l5bm11ZjE6VFJyNE2iE6EKpCBVSRroAF", "ZsdGgyOnugo44o442aALkge_dVVK", "http://www.ooyala.com", SsaiPlayerActivity.class, pulseParamsFromJson));
    selectionMap.put("VOD - DFP", new SsaiSelectionOption("ZhbzNiZzE64qnD5YMEnoi2DcwqSfH4z8", "ZsdGgyOnugo44o442aALkge_dVVK", "http://www.ooyala.com", SsaiPlayerActivity.class, dfpParamsFromJson));
    selectionMap.put("Without Player Params", new SsaiSelectionOption("ltZ3l5YjE6lUAvBdflvcDQ-zti8q8Urd", "RpOWUyOq86gFq-STNqpgzhzIcXHV", "http://www.ooyala.com", SsaiPlayerActivity.class));
    selectionMap.put("VOD - Ooyala Pulse - CC DFXP", new SsaiSelectionOption("8yZXE0ZzE6596qdcG58aTnvzADUBLC2I", "ZsdGgyOnugo44o442aALkge_dVVK", "http://www.ooyala.com", SsaiPlayerActivity.class, pulseParamsFromJson));
    selectionMap.put("VOD - DFP - CC DFXP", new SsaiSelectionOption("54ZHE0ZzE6JQBu_T099L8NWvzzqsnrKG", "ZsdGgyOnugo44o442aALkge_dVVK", "http://www.ooyala.com", SsaiPlayerActivity.class, dfpParamsFromJson));

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

  /**
   * Get a JSON from the resources directory as a JSONObject
   *
   * @param resource File to fetch
   * @return the resource as JSONObject
   */
  private JSONObject getResourceAsJsonObject(String resource)  {
    ClassLoader classLoader = this.getClass().getClassLoader();
    InputStream is = classLoader.getResourceAsStream(resource);
    try {
      int size = is.available();
      byte[] buffer = new byte[size];
      is.read(buffer);
      is.close();
      return new JSONObject(new String(buffer, "UTF-8"));
    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return null;
  }
}
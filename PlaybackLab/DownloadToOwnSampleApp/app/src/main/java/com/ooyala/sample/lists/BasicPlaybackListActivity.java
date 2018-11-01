package com.ooyala.sample.lists;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ooyala.sample.R;
import com.ooyala.sample.players.OfflineDownloadActivity;
import com.ooyala.sample.players.OfflineSkinPlayerActivity;
import com.ooyala.sample.players.OoyalaSkinOPTPlayerActivity;
import com.ooyala.sample.utils.PlayerSelectionOption;

import java.util.LinkedHashMap;
import java.util.Map;

public class BasicPlaybackListActivity extends Activity implements OnItemClickListener {
  private static final int LOAD_REACT_BUNDLE_PERMISSION_REQ_CODE = 666;

  public final static String getName() {
    return "Basic Playback";
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
    String activityName = getCallingActivity().getClassName();
    if (activityName.contains("AddAssetActivity")) {
      selectionMap.put("Widevine DASH Stream", new PlayerSelectionOption(getIntent().getStringExtra("embed_code"), getIntent().getStringExtra("pcode"), getIntent().getStringExtra("api_key"), getIntent().getStringExtra("secret_key"), getIntent().getStringExtra("account_id"), "http://ooyala.com", OoyalaSkinOPTPlayerActivity.class));
      selectionMap.put("Widevine DASH Downloader", new PlayerSelectionOption(getIntent().getStringExtra("embed_code"), getIntent().getStringExtra("pcode"), getIntent().getStringExtra("api_key"), getIntent().getStringExtra("secret_key"), getIntent().getStringExtra("account_id"), "http://ooyala.com", OfflineDownloadActivity.class));
      selectionMap.put("Widevine DASH Offline Player", new PlayerSelectionOption(getIntent().getStringExtra("embed_code"), getIntent().getStringExtra("pcode"), getIntent().getStringExtra("api_key"), getIntent().getStringExtra("secret_key"), getIntent().getStringExtra("account_id"), "http://ooyala.com", OfflineSkinPlayerActivity.class));
    }
    else {
      //Populate the embed map
      selectionMap.put("Widevine DASH Stream", new PlayerSelectionOption("Q1cG85NTE6Df3A95XMMbKGsPg6yaEZGm", "BjcWYyOu1KK2DiKOkF41Z2k0X57l","BjcWYyOu1KK2DiKOkF41Z2k0X57l.0I-V4", "1ysC3V-g8m9V3QoGJwRa_fXcfi_G2ZV778m17pux", "dulari_qa","http://ooyala.com", OoyalaSkinOPTPlayerActivity.class));
      selectionMap.put("Widevine DASH Downloader", new PlayerSelectionOption("Q1cG85NTE6Df3A95XMMbKGsPg6yaEZGm", "BjcWYyOu1KK2DiKOkF41Z2k0X57l","BjcWYyOu1KK2DiKOkF41Z2k0X57l.0I-V4", "1ysC3V-g8m9V3QoGJwRa_fXcfi_G2ZV778m17pux", "dulari_qa","http://ooyala.com", OfflineDownloadActivity.class));
      selectionMap.put("Widevine DASH Offline Player", new PlayerSelectionOption("Q1cG85NTE6Df3A95XMMbKGsPg6yaEZGm", "BjcWYyOu1KK2DiKOkF41Z2k0X57l", "BjcWYyOu1KK2DiKOkF41Z2k0X57l.0I-V4", "1ysC3V-g8m9V3QoGJwRa_fXcfi_G2ZV778m17pux", "dulari_qa","http://ooyala.com", OfflineSkinPlayerActivity.class));
    }

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

    //Force request android.permission.SYSTEM_ALERT_WINDOW
    // that is ignored in Manifest on Marshmallow devices.
    if(Build.VERSION_CODES.M == Build.VERSION.SDK_INT) {
      showDevOptionsDialog();
    }
  }

  private void showDevOptionsDialog() {
    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
    startActivityForResult(intent, LOAD_REACT_BUNDLE_PERMISSION_REQ_CODE);
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
    intent.putExtra("api_key", selection.getApiKey());
    intent.putExtra("secret_key", selection.getSecretKey());
    intent.putExtra("account_id", selection.getAccountId());
    intent.putExtra("domain", selection.getDomain());
    intent.putExtra("selection_name", selectionAdapter.getItem(pos));
    startActivity(intent);
    return;
  }
}

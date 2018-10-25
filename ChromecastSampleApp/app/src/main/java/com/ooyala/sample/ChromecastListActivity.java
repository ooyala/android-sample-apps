package com.ooyala.sample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.media.widget.MiniControllerFragment;

public class ChromecastListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
  private static final String TAG = "ChromecastListActivity";

  ChromecastPlayerSelectionOption[] videoList;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    Log.d(TAG, "onCreate()");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.start_view);
    setupActionBar();
    videoList = getVideoList();

    MiniControllerFragment miniControllerFragment = (MiniControllerFragment) getSupportFragmentManager().findFragmentById(R.id.cast_mini_controller);
    //Create the adapter for the ListView data
    ArrayAdapter<String> selectionAdapter = new ArrayAdapter<String>(this, R.layout.list_activity_list_item);
    for (ChromecastPlayerSelectionOption video : videoList) {
      selectionAdapter.add(video.title);
    }
    selectionAdapter.notifyDataSetChanged();

    ListView listView = findViewById(R.id.listView);
    listView.setAdapter(selectionAdapter);
    listView.setOnItemClickListener(this);

  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    final Intent intent = new Intent(this, videoList[position].activity);
    SharedPreferences lastChosenParams = getSharedPreferences("LastChosenParams", MODE_PRIVATE);
    lastChosenParams
        .edit()
        .putString("embedcode", videoList[position].embedCode)
        .putString("secondEmbedCode", videoList[position].secondEmbedCode)
        .putString("pcode", videoList[position].pcode)
        .putString("domain", videoList[position].domain)
        .apply();
    startActivity(intent);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    boolean createOptionsMenu = super.onCreateOptionsMenu(menu);
    getMenuInflater().inflate(R.menu.browse, menu);
    CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), menu,
        R.id.media_route_menu_item);
    return createOptionsMenu;
  }

  private void setupActionBar() {
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
  }

  /**
   * Generate the list of all videos for the list
   */
  private ChromecastPlayerSelectionOption[] getVideoList() {
    return new ChromecastPlayerSelectionOption[]{
      new ChromecastPlayerSelectionOption("HLS Asset", "Y1ZHB1ZDqfhCPjYYRbCEOz0GR8IsVRm1", "c0cTkxOqALQviQIGAHWY5hP0q9gU", "http://www.ooyala.com", ChromecastPlayerActivity.class),
      new ChromecastPlayerSelectionOption("MP4 Video", "h4aHB1ZDqV7hbmLEv4xSOx3FdUUuephx", "c0cTkxOqALQviQIGAHWY5hP0q9gU", "http://www.ooyala.com", ChromecastPlayerActivity.class),
      new ChromecastPlayerSelectionOption("VOD CC", "92cWp0ZDpDm4Q8rzHfVK6q9m6OtFP-ww", "c0cTkxOqALQviQIGAHWY5hP0q9gU", "http://www.ooyala.com", ChromecastPlayerActivity.class),
      new ChromecastPlayerSelectionOption("Encrypted HLS Asset", "ZtZmtmbjpLGohvF5zBLvDyWexJ70KsL-", "c0cTkxOqALQviQIGAHWY5hP0q9gU", "http://www.ooyala.com", ChromecastPlayerActivity.class),
      // Will play Playready Smooth on Chromecast, Clear HLS on device
      new ChromecastPlayerSelectionOption("Playready Smooth, Clear HLS Backup", "pkMm1rdTqIAxx9DQ4-8Hyp9P_AHRe4pt", "FoeG863GnBL4IhhlFC1Q2jqbkH9m", "http://www.ooyala.com", ChromecastPlayerActivity.class),
      new ChromecastPlayerSelectionOption("2 Assets autoplayed", "Y1ZHB1ZDqfhCPjYYRbCEOz0GR8IsVRm1", "92cWp0ZDpDm4Q8rzHfVK6q9m6OtFP-ww", "c0cTkxOqALQviQIGAHWY5hP0q9gU", "http://www.ooyala.com", ChromecastPlayerActivity.class),

      //These asset will not be configured correctly. To test your OPT-enabled assets, you need:
      // 1. an OPT-enabled embed code (set here)
      // 2. the correlating PCode (set in the PlayerViewController)
      // 3. an API Key and Secret for the provider to locally-sign the authorization (set in the PlayerViewController)
      new ChromecastPlayerSelectionOption("Ooyala Player Token Asset (unconfigured)", "0yMjJ2ZDosUnthiqqIM3c8Eb8Ilx5r52", "c0cTkxOqALQviQIGAHWY5hP0q9gU", "http://www.ooyala.com", ChromecastPlayerActivity.class),
      new ChromecastPlayerSelectionOption("Concurrent Streams (unconfigured)", "pwc3J0dTpAL7gMLFNVt2ks2v8j3qOKCS", "FoeG863GnBL4IhhlFC1Q2jqbkH9m", "http://www.ooyala.com", ChromecastPlayerActivity.class),
      new ChromecastPlayerSelectionOption("Barebones Player Activity Demo (HLS Asset)", "Y1ZHB1ZDqfhCPjYYRbCEOz0GR8IsVRm1", "c0cTkxOqALQviQIGAHWY5hP0q9gU", "http://www.ooyala.com", ChromecastPlayerActivity.class),

    };
  }
}

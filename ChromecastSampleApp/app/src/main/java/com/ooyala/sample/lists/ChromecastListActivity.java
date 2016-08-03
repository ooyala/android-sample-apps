package com.ooyala.sample.lists;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ooyala.sample.players.ChromecastBarebonesPlayerActivity;
import com.ooyala.sample.utils.ChromecastPlayerSelectionOption;
import com.ooyala.sample.players.ChromecastPlayerActivity;
import com.ooyala.sample.R;

import com.google.android.libraries.cast.companionlibrary.widgets.MiniController;
import com.ooyala.android.castsdk.CastManager;

public class ChromecastListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
  private static final String TAG = "ChromecastListActivity";

  private MiniController defaultMiniController;
  ChromecastPlayerSelectionOption[] videoList;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    Log.d(TAG, "onCreate()");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.start_view);

    CastManager.getCastManager().getVideoCastManager().setStopOnDisconnect(false);

    // create the array of videos
    videoList = getVideoList();

    //Create the adapter for the ListView data
    ArrayAdapter<String> selectionAdapter = new ArrayAdapter<String>(this, R.layout.list_activity_list_item);
    for(ChromecastPlayerSelectionOption video : videoList) {
      selectionAdapter.add(video.title);
    }
    selectionAdapter.notifyDataSetChanged();

    //Populate the listView
    ListView listView = (ListView) findViewById(R.id.listView);
    listView.setAdapter(selectionAdapter);
    listView.setOnItemClickListener(this);

    //Restyle the CCL-provided minicontroller to have dark text
    defaultMiniController = (MiniController) findViewById(R.id.miniController1);
    TextView title = (TextView)defaultMiniController.findViewById(R.id.title_view);
    title.setTextColor(Color.BLACK);
    TextView subtitle = (TextView)defaultMiniController.findViewById(R.id.subtitle_view);
    subtitle.setTextColor(Color.GRAY);

    //Add the minicontroller to the list of minicontrolers managed by CCL
    CastManager.getVideoCastManager().addMiniController(defaultMiniController);

// Uncomment it if you want to activate the customized sample app in our sample app
//    customizedMiniController = (OOMiniController) findViewById(R.id.miniController2);
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    final Intent intent = new Intent(this, videoList[position].activity);
    intent.putExtra("embedcode", videoList[position].embedCode);
    intent.putExtra("embedcode2", videoList[position].embedCode2);
    intent.putExtra("pcode", videoList[position].pcode);
    intent.putExtra("domain", videoList[position].domain);
    startActivity(intent);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    getMenuInflater().inflate(R.menu.main, menu);
    CastManager.getVideoCastManager().addMediaRouterButton(menu, R.id.media_route_menu_item);
    return true;
  }

  @Override
  protected void onStart() {
    Log.d(TAG, "onStart()");
    super.onStart();
  }
  
  @Override
  protected void onDestroy() {
    Log.d(TAG, "onDestroy()");
    CastManager.getVideoCastManager().removeMiniController(defaultMiniController);
    super.onDestroy();
  }

  @Override
  public void onResume() {
    super.onResume();
    if (CastManager.getCastManager().isInCastMode()) {
      defaultMiniController.setVisibility(View.VISIBLE);
    }
// Uncomment it if you want to activate the customized sample app in our sample app
//      castManager.addMiniController(customizedMiniController);
//      this.customizedMiniController.show();
    //onPause and onResume, call CCL code to support Cast Notifications
    CastManager.getCastManager().onResume();
    Log.d(TAG, "onResume()");
  }
  
  @Override
  public void onPause() {
    Log.d(TAG, "onPause()");
    //onPause and onResume, call CCL code to support Cast Notifications
    CastManager.getCastManager().onPause();
    super.onPause();
  }

  /**
    Generate the list of all videos for the list
   */
  private ChromecastPlayerSelectionOption[] getVideoList() {
    return new ChromecastPlayerSelectionOption[] {
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
      new ChromecastPlayerSelectionOption("Barebones Player Activity Demo (HLS Asset)", "Y1ZHB1ZDqfhCPjYYRbCEOz0GR8IsVRm1", "c0cTkxOqALQviQIGAHWY5hP0q9gU", "http://www.ooyala.com", ChromecastBarebonesPlayerActivity.class),

    };
  }
}

package com.ooyala.sample.ChromecastSampleApp;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.RemoteControlClient;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.libraries.cast.companionlibrary.widgets.IMiniController;
import com.google.android.libraries.cast.companionlibrary.widgets.MiniController;
import com.ooyala.android.castsdk.CastManager;

import java.util.List;

public class ChromecastListActivity extends ActionBarActivity {
  private RemoteControlClient remoteControlClient;
  private static final String TAG = "ChromecastListActivity";

  private MiniController defaultMiniController;
  private IMiniController customizedMiniController;
  private List<Integer> castViewImages;
  ChromecastPlayerSelectionOption[] videoList;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    Log.d(TAG, "onCreate()");
    super.onCreate(savedInstanceState);

    CastManager.getCastManager().getVideoCastManager().setStopOnDisconnect(false);
    setContentView(R.layout.start_view);

    videoList = new ChromecastPlayerSelectionOption[] {
        new ChromecastPlayerSelectionOption("HLS Asset", "Y1ZHB1ZDqfhCPjYYRbCEOz0GR8IsVRm1", "FoeG863GnBL4IhhlFC1Q2jqbkH9m", "http://www.ooyala.com"),
        new ChromecastPlayerSelectionOption("MP4 Video", "h4aHB1ZDqV7hbmLEv4xSOx3FdUUuephx", "FoeG863GnBL4IhhlFC1Q2jqbkH9m", "http://www.ooyala.com"),
        new ChromecastPlayerSelectionOption("VOD CC", "92cWp0ZDpDm4Q8rzHfVK6q9m6OtFP-ww", "FoeG863GnBL4IhhlFC1Q2jqbkH9m", "http://www.ooyala.com"),
        new ChromecastPlayerSelectionOption("Encrypted HLS Asset", "ZtZmtmbjpLGohvF5zBLvDyWexJ70KsL-", "FoeG863GnBL4IhhlFC1Q2jqbkH9m", "http://www.ooyala.com"),
        // Will play Playready Smooth on Chromecast, Clear HLS on device
        new ChromecastPlayerSelectionOption("Playready Smooth, Clear HLS Backup", "pkMm1rdTqIAxx9DQ4-8Hyp9P_AHRe4pt", "FoeG863GnBL4IhhlFC1Q2jqbkH9m", "http://www.ooyala.com"),


        //These asset will not be configured correctly. To test your OPT-enabled assets, you need:
        // 1. an OPT-enabled embed code (set here)
        // 2. the correlating PCode (set in the PlayerViewController)
        // 3. an API Key and Secret for the provider to locally-sign the authorization (set in the PlayerViewController)
        new ChromecastPlayerSelectionOption("Ooyala Player Token Asset (unconfigured)", "0yMjJ2ZDosUnthiqqIM3c8Eb8Ilx5r52", "FoeG863GnBL4IhhlFC1Q2jqbkH9m", "http://www.ooyala.com"),
        new ChromecastPlayerSelectionOption("Concurrent Streams (unconfigured)", "pwc3J0dTpAL7gMLFNVt2ks2v8j3qOKCS", "FoeG863GnBL4IhhlFC1Q2jqbkH9m", "http://www.ooyala.com"),

    };
    //Create the adapter for the ListView
    ArrayAdapter<String> selectionAdapter = new ArrayAdapter<String>(this, R.layout.list_activity_list_item);
    for(ChromecastPlayerSelectionOption video : videoList) {
      selectionAdapter.add(video.title);
    }
    selectionAdapter.notifyDataSetChanged();

    ListView listView = (ListView) findViewById(R.id.listView);
    listView.setAdapter(selectionAdapter);

    final Intent intent = new Intent(this, ChromecastPlayerActivity.class);
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        intent.putExtra("embedcode", videoList[position].embedCode);
        intent.putExtra("pcode", videoList[position].pcode);
        intent.putExtra("domain", videoList[position].domain);
        startActivity(intent);
      }
    });
    
    ActionBar actionBar = getSupportActionBar();
    actionBar.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    getSupportActionBar().setDisplayShowTitleEnabled(false);

    defaultMiniController = (MiniController) findViewById(R.id.miniController1);
    CastManager.getVideoCastManager().addMiniController(defaultMiniController);

// Uncomment it if you want to activate the customized sample app in our sample app
//    customizedMiniController = (OOMiniController) findViewById(R.id.miniController2);
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
    CastManager.getCastManager().onResume();
    Log.d(TAG, "onResume()");
  }
  
  @Override
  public void onPause() {
    Log.d(TAG, "onPause()");
    CastManager.getCastManager().onPause();
    super.onPause();
  }
}

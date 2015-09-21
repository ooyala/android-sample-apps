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

import com.ooyala.android.castsdk.CastManager;
import com.ooyala.android.castsdk.CastMiniController;

import java.util.List;

public class ChromecastListActivity extends ActionBarActivity {
  
  public static int activatedActivity = 0;
  
  private RemoteControlClient remoteControlClient;
  private static final String TAG = "ChromecastListActivity";
  private final String NAMESPACE = "urn:x-cast:ooyala";
  private final String APP_ID = "4172C76F";
  private CastManager castManager;
  private CastMiniController defaultMiniController;
  private CastMiniController customizedMiniController;
  private List<Integer> castViewImages;
  ChromecastPlayerSelectionOption[] videoList;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    Log.d(TAG, "onCreate()");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.start_view);
    try {
      castManager = CastManager.initialize(this, APP_ID, NAMESPACE);
    }
    catch( CastManager.CastManagerInitializationException cie ) {
      throw new RuntimeException( cie );
    }
    castManager.getDataCastManager().setStopOnDisconnect(false);
    castManager.setNotificationMiniControllerLayout(R.layout.oo_default_notification);
    castManager.setNotificationImageResourceId(R.drawable.ic_ooyala);

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
        intent.putExtra("embedcode",videoList[position].embedCode);
        intent.putExtra("pcode",videoList[position].pcode);
        intent.putExtra("domain",videoList[position].domain);
        startActivity(intent);
      }
    });
    
    ActionBar actionBar = getSupportActionBar();
    actionBar.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    getSupportActionBar().setDisplayShowTitleEnabled(false);
    
    
    defaultMiniController = (CastMiniController) findViewById(R.id.miniController1);

// Uncomment it if you want to activate the customized sample app in our sample app
//    customizedMiniController = (OOMiniController) findViewById(R.id.miniController2);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    castManager.addCastButton(this, menu);
    return true;
  }

  @Override
  protected void onStop() {
    Log.d(TAG, "onStop()");
    super.onStop();
    if (ChromecastListActivity.activatedActivity == 0 && castManager != null && castManager.isInCastMode()) {
      castManager.createNotificationService(this);
      castManager.registerLockScreenControls(this);
    }
  }
  
  @Override
  protected void onRestart() {
    Log.d(TAG, "onRestart()");
    super.onRestart();
    castManager.destroyNotificationService(this);
    castManager.unregisterLockScreenControls();
  }

  
  @Override
  protected void onStart() {
    Log.d(TAG, "onStart()");
    super.onStart();
  }
  
  @Override
  protected void onDestroy() {
    Log.d(TAG, "onDestroy()");
    super.onDestroy();
  }

  @Override
  public void onResume() {
    super.onResume();
    ChromecastListActivity.activatedActivity++;
    if (castManager != null && castManager.isInCastMode()){
      castManager.addMiniController( defaultMiniController );
      defaultMiniController.show();
// Uncomment it if you want to activate the customized sample app in our sample app
//      castManager.addMiniController(customizedMiniController);
//      this.customizedMiniController.show();
      castManager.onResume(this);
    }
    Log.d(TAG, "onResume()");
  }
  
  @Override
  public void onPause() {
    super.onPause();
    ChromecastListActivity.activatedActivity--;
    castManager.removeMiniController( defaultMiniController );
    defaultMiniController.dismiss();
    Log.d(TAG, "onPause()");
  }
  

}

package com.ooyala.chromecastsampleapp;

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
  private CastMiniController defualtMiniController;
  private CastMiniController customizedMiniController;
  private List<Integer> castViewImages;
  Video[] videoList;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    Log.d(TAG, "onCreate()");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.start_view);
    castManager = CastManager.initialize(this, APP_ID, NAMESPACE);
    castManager.setStopOnDisconnect(false);
    castManager.setNotificationMiniControllerLayout(R.layout.oo_default_notification);
    castManager.setNotificationImageResourceId(R.drawable.ic_ooyala);

    videoList = new Video[] {
        new Video(R.drawable.chromecast_test_1, "HLS Asset(modified listview)", "wxaWd5bTrJFI--Ga7TgbJtzcPrbzENBV"),
        new Video(R.drawable.dog_movie, "DOGMOVIE", "IzNGg3bzoHHjEfnJP-fj2jB0-oci0Jnm"),
        new Video(R.drawable.happy_fit2, "HAPPYFIT2", "xiNmg3bzpFkkwsYqkb5UtGvNOpcwiOCS"),
        new Video(R.drawable.weird_dad, "WEIRDAD", "Y4OWg3bzoNtSZ9TOg3wl9BPUspXZiMYc"),
        new Video(R.drawable.heinz, "HEINZ", "o0OWg3bzrLBNfadaXSaCA7HbknPLFRPP"),
        new Video(R.drawable.clear_ehls_high, "remote_hls_baseline_vod", "FndjQydTr_aPzVwEEGDSR9CwzIPWjAlQ"),
        new Video(R.drawable.clear_ehls_high, "clear_ehls_high", "MyZjYydTqIR435DzaFUqqrrRg8HdQypx"),
        new Video(R.drawable.elephants_dream, "ElephantsDream (HLS high Does not work.)", "Nqc2d4bzoG4MidnEcgKAwVqWd_ug3Hos")
    };
    //Create the adapter for the ListView
    ArrayAdapter<String> selectionAdapter = new ArrayAdapter<String>(this, R.layout.list_activity_list_item);
    for(Video video : videoList) {
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
        intent.putExtra("icon",videoList[position].icon);
        startActivity(intent);
      }
    });
    
    ActionBar actionBar = getSupportActionBar();
    actionBar.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    getSupportActionBar().setDisplayShowTitleEnabled(false);
    
    
    defualtMiniController = (CastMiniController) findViewById(R.id.miniController1);

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
    castManager.setCurrentContext(this);
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
      castManager.addMiniController(defualtMiniController);
      defualtMiniController.show();
// Uncomment it if you want to activate the customized sample app in our sample app
//      castManager.addMiniController(customizedMiniController);
//      this.customizedMiniController.show();
      castManager.onResume();
      castManager.deregisterOoyalaPlayer();
    }
    Log.d(TAG, "onResume()");
  }
  
  @Override
  public void onPause() {
    super.onPause();
    ChromecastListActivity.activatedActivity--;
    castManager.removeMiniController(defualtMiniController);
    Log.d(TAG, "onPause()");
  }
  

}

package com.ooyala.android.freewheelsampleapp;


import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.freewheelsdk.OoyalaFreewheelManager;
import com.ooyala.android.ui.OptimizedOoyalaPlayerLayoutController;

public class FreewheelSampleAppActivity extends Activity implements Observer {

  final String PCODE  = "5idHc6Pt1kJ18w4u9Q5jEwAQDYCH";
  final String DOMAIN = "http://www.ooyala.com";

  private OptimizedOoyalaPlayerLayoutController playerLayoutController;
  private OoyalaFreewheelManager freewheelManager;
  OoyalaPlayer player;

  private Spinner embedSpinner;
  private Button setButton;
  private Map<String, String> embedMap;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);
    playerLayoutController = new OptimizedOoyalaPlayerLayoutController(
        playerLayout, PCODE, new PlayerDomain(DOMAIN));
    player = playerLayoutController.getPlayer();
    player.addObserver(this);

    embedMap = new HashMap<String, String>();
    embedMap.put("Freewheel Preroll", "Q5MXg2bzq0UAXXMjLIFWio_6U0Jcfk6v");
    embedMap.put("Freewheel Midroll", "NwcGg4bzrwxc6rqAZbYij4pWivBsX57a");
    embedMap.put("Freewheel Postroll", "NmcGg4bzqbeqXO_x9Rfj5IX6gwmRRrse");
    embedMap.put("Freewheel PreMidPost", "NqcGg4bzoOmMiV35ZttQDtBX1oNQBnT-");
    embedMap.put("Freewheel Overlay", "NucGg4bzrVrilZrMdlSA9tyg6Vty46DN");
    embedMap.put("Freewheel Multi Midroll", "htdnB3cDpMzXVL7fecaIWdv9rTd125As");
    embedMap.put("Freewheel PreMidPost Overlay",
        "NscGg4bzpO9s5rUMyW-AAfoeEA7CX6hP");

    embedSpinner = (Spinner) findViewById(R.id.embedSpinner);
    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        android.R.layout.simple_spinner_item);
    //Update the spinner with the embed map
    for (String key : embedMap.keySet()) {
      adapter.add(key);
    }
    adapter.notifyDataSetChanged();
    embedSpinner.setAdapter(adapter);
    setButton = (Button) findViewById(R.id.setButton);
    setButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View arg0) {
        Object embedKey = embedSpinner.getSelectedItem();
        if (embedKey == null) {
          return;
        }
        String embedCode = embedMap.get(embedKey.toString());
        if (player.setEmbedCode(embedCode)) {
          player.play();
        } else {
          Log.d(this.getClass().getName(), "Something Went Wrong!");
        }
      }
    });

    //Initialize Freewheel Ad Manager
    freewheelManager = new OoyalaFreewheelManager(this, playerLayoutController);

    //Set Freewheel parameters. Note that these are optional, and override configurations set in Backlot or in Ooyala internals
    Map<String, String> freewheelParameters = new HashMap<String, String>();
    //freewheelParameters.put("fw_android_mrm_network_id",  "90750");
    freewheelParameters.put("fw_android_ad_server", "http://g1.v.fwmrm.net/");
    freewheelParameters.put("fw_android_player_profile",  "90750:ooyala_android");
    freewheelParameters.put("FRMSegment",  "channel=TEST;subchannel=TEST;section=TEST;mode=online;player=ooyala;beta=n");
    //freewheelParameters.put("fw_android_site_section_id", "ooyala_test_site_section");
    //freewheelParameters.put("fw_android_video_asset_id",  "ooyala_test_video_with_bvi_cuepoints");

    freewheelManager.overrideFreewheelParameters(freewheelParameters);
  }

  @Override
  protected void onStop() {
    super.onStop();
    if (playerLayoutController.getPlayer() != null) {
      playerLayoutController.getPlayer().suspend();
    }
  }

  @Override
  protected void onRestart() {
    super.onRestart();
    if (playerLayoutController.getPlayer() != null) {
      playerLayoutController.getPlayer().resume();
    }
  }


  @Override
  public void update(Observable arg0, Object arg1) {
    if (arg1 == OoyalaPlayer.TIME_CHANGED_NOTIFICATION) {
      return;
    }
    Log.d(FreewheelSampleAppActivity.class.getSimpleName(), "Notification Received: " + arg1 + " - state: " + player.getState());
  }
}
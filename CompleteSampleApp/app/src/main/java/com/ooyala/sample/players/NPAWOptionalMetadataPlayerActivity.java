package com.ooyala.sample.players;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.npaw.plugin.Youbora;
import com.npaw.plugin.YouboraMetadata;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.sample.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * This activity illustrates Ooyala's Integration with NPAW Youbora Quality of Service tools, as
 * well as how to override the metadata that is passed by default to Youbora
 * You can find more information on Ooyala's Support website, or from your CSM
 *
 */
public class NPAWOptionalMetadataPlayerActivity extends Activity implements Observer {
  final String TAG = this.getClass().toString();

  String EMBED = null;
  final String PCODE  = "R2d3I6s06RyB712DN0_2GsQS-R-Y";
  final String DOMAIN = "http://ooyala.com";

  final String NPAW_ACCOUNT_ID = "ooyalaqa";
  final String NPAW_USER = "qa_android_ooyala";

  protected OoyalaPlayerLayoutController playerLayoutController;
  protected OoyalaPlayer player;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle(getIntent().getExtras().getString("selection_name"));
    setContentView(R.layout.player_simple_layout);

    EMBED = getIntent().getExtras().getString("embed_code");

    //Initialize the player
    OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);
    playerLayoutController = new OoyalaPlayerLayoutController(playerLayout, PCODE, new PlayerDomain(DOMAIN));
    player = playerLayoutController.getPlayer();
    player.addObserver(this);

 		/*
		 * Youbora plugin initialization
		 */
    Youbora.init(NPAW_ACCOUNT_ID, NPAW_USER, getApplicationContext(), player);

    YouboraMetadata metadata = new YouboraMetadata();
    /*
		 * Youbora optional parameters
		 */
    metadata.setParam1("ooyala1");
    metadata.setParam2("ooyala2");
    metadata.setCdn("Akamai");
    metadata.setIp("8.8.4.4");
    metadata.setIsp("Telefonica");
    metadata.setTransaction("ooyala-transaction");
    metadata.setResource("http://1234");

    /*
     * Youbora metadata overrides
     */
    Map<String, String> metadataMap = new HashMap<String, String>();
    metadataMap.put("title", "custom title");
    metadataMap.put("genre", "genre");
    metadataMap.put("language", "langugae");
    metadataMap.put("year", "year");
    metadataMap.put("cast", "cast");
    metadataMap.put("director", "director");
    metadataMap.put("owner", "owner");
    metadataMap.put("duration", "10000");
    metadataMap.put("parental", "parental");
    metadataMap.put("price", "1000");
    metadataMap.put("rating", "100");
    metadataMap.put("audioType", "6");
    metadataMap.put("audioChannels", "2");

    /*
     * Youbora device type overrides
     */
    Map<String, String> deviceMap = new HashMap<String, String>();
    deviceMap.put("manufacturer", "manufacturer");
    deviceMap.put("type", "type");
    deviceMap.put("year", "year");
    deviceMap.put("firmware", "firmware");

    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put("filename", "filename");
    properties.put("content_id", "content_id");
    properties.put("transaction_type", "transaction_type");
    properties.put("quality", "quality");
    properties.put("content_type", "content_type");
    properties.put("custom property1", "custom property 1");

    /*
     * Put the override maps into the properties before updating
     */
    properties.put("content_metadata", metadataMap);
    properties.put("device", deviceMap);

    metadata.setProperties(properties);
    Youbora.updateYouboraMetadata(metadata);

    if (player.setEmbedCode(EMBED)) {
      player.play();
    }

  }

  @Override
  protected void onStop() {
    super.onStop();
    Log.d(TAG, "Player Activity Stopped");
    if (player != null) {
      player.suspend();
    }

		/*
		 * It is important to call stopSession in order to stop tracking the view,
		 * when the activity is gone
		 */
    Youbora.stopSession();
  }

  @Override
  protected void onRestart() {
    super.onRestart();
    Log.d(TAG, "Player Activity Restarted");
    if (player != null) {
      player.resume();
    }

		/*
		 * When the activity is restarted (e.g. coming from background), it is
		 * important to call to restartSession in order to track it again
		 */
    Youbora.restartSession();
  }

  /**
   * Listen to all notifications from the OoyalaPlayer
   */
  @Override
  public void update(Observable arg0, Object arg1) {
    if (arg1 == OoyalaPlayer.TIME_CHANGED_NOTIFICATION) {
      return;
    }
    Log.d(TAG, "Notification Received: " + arg1 + " - state: " + player.getState());
  }

}

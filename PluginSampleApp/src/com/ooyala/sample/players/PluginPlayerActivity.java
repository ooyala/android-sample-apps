package com.ooyala.sample.players;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.pluginsampleapp.R;
import com.ooyala.sample.utils.SampleAdPlugin;

public class PluginPlayerActivity extends Activity {
	  private String EMBED = "lrZmRiMzrr8cP77PPW0W8AsjjhMJ1BBe";
	  private final String PCODE = "R2d3I6s06RyB712DN0_2GsQS-R-Y";
	  private final String DOMAIN = "http://www.ooyala.com";
	  OoyalaPlayer player;

	  /**
	   * Called when the activity is first created.
	   */
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.player_simple_layout);
	    
	    EMBED = getIntent().getExtras().getString("embed_code");

	    OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);
	    OoyalaPlayerLayoutController playerLayoutController = new OoyalaPlayerLayoutController(
	        playerLayout, PCODE, new PlayerDomain(DOMAIN));
	    player = playerLayoutController.getPlayer();
	    SampleAdPlugin plugin = new SampleAdPlugin(this, player);
	    player.registerPlugin(plugin);
	    if (player.setEmbedCode(EMBED)) {
	      player.play();
	    } else {
	      Log.d(this.getClass().getName(), "Something Went Wrong!");
	    }
	  }

	  @Override
	  protected void onStop() {
	    super.onStop();
	    if (player != null) {
	      player.suspend();
	    }
	  }

	  @Override
	  protected void onRestart() {
	    super.onRestart();
	    if (player != null) {
	      player.resume();
	    }
	  }

	}

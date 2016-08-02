package com.ooyala.sample.players;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.item.Stream;
import com.ooyala.android.item.UnbundledVideo;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.sample.R;

import java.util.Observable;
import java.util.Observer;

public class UnbundledPlayerActivity extends Activity {
  final String TAG = this.getClass().toString();

  String EMBED = null;
  String PCODE = null;
  String DOMAIN = null;

  int count=0;

  protected OoyalaPlayerLayoutController playerLayoutController;
  protected OoyalaPlayer player;

  public static String getName() {
    return "Unbundled";
  }

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle(getIntent().getExtras().getString("selection_name"));
    PCODE = getIntent().getExtras().getString("pcode");
    DOMAIN = getIntent().getExtras().getString("domain");
    setContentView(R.layout.player_simple_layout);

    //Initialize the player
    OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);
    player = new OoyalaPlayer(PCODE, new PlayerDomain(DOMAIN));
    playerLayoutController = new OoyalaPlayerLayoutController(playerLayout, player);

    final String url = getIntent().getExtras().getString("embed_code");
    Stream s = new Stream( url, Stream.DELIVERY_TYPE_MP4 );
    UnbundledVideo u = new UnbundledVideo( s );
    final boolean success = player.setUnbundledVideo( u );
    if (success) {
      //Uncomment for Auto-Play
      //player.play();
    }
    else {
      Log.e(TAG, "Asset Failure");
    }
  }

  @Override
  protected void onStop() {
    super.onStop();
    Log.d(TAG, "Player Activity Stopped");
    if (player != null) {
      player.suspend();
    }
  }

  @Override
  protected void onRestart() {
    super.onRestart();
    Log.d(TAG, "Player Activity Restarted");
    if (player != null) {
      player.resume();
    }
  }

}

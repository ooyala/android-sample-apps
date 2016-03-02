package com.ooyala.sample.players;

import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.sample.R;

/**
 * This activity illustrates how you can insert Ooyala and VAST advertisements programmatically
 * through the SDK
 *
 * If you want to play an advertisement immediately, you can set the time of your Ad Spot to
 * the current playhead time
 *
 *
 */
public class ChangeVideoPlayerActivity extends Activity implements Observer {
  public final static String getName() {
    return "Change Video Programatically";
  }
  final String TAG = this.getClass().toString();

  String EMBED = null;
  String EMBED_TWO = "h4aHB1ZDqV7hbmLEv4xSOx3FdUUuephx";
  final String PCODE  = "R2d3I6s06RyB712DN0_2GsQS-R-Y";
  final String DOMAIN = "http://ooyala.com";

  protected OoyalaPlayerLayoutController playerLayoutController;
  protected OoyalaPlayer player;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle(getName());
    setContentView(R.layout.player_double_button_layout);

    EMBED = getIntent().getExtras().getString("embed_code");

    //Initialize the player
    OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);
    player = new OoyalaPlayer(PCODE, new PlayerDomain(DOMAIN));
    playerLayoutController = new OoyalaPlayerLayoutController(playerLayout, player);
    player.addObserver(this);

    if (player.setEmbedCode(EMBED)) {
      player.play();
    }

    /** DITA_START:<ph id="insert_ad_vast"> **/
    //Setup the left button, which will immediately insert a VAST advertisement
    Button leftButton = (Button) findViewById(R.id.doubleLeftButton);
    leftButton.setText("Play Video 1");
    leftButton.setOnClickListener( new OnClickListener() {

      @Override
      public void onClick(View v) {
    	  player.setEmbedCode(EMBED);
          player.play();
      }
    });
    /** DITA_END:</ph> **/

    //Setup the right button, which will immediately insert an Ooyala advertisement
    Button rightButton = (Button) findViewById(R.id.doubleRightButton);
    rightButton.setText("Play Video 2");
    rightButton.setOnClickListener( new OnClickListener() {

      @Override
      public void onClick(View v) {
        player.setEmbedCode(EMBED_TWO);
        player.play();
      }
    });
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

  /**
   * Listen to all notifications from the OoyalaPlayer
   */
  @Override
  public void update(Observable arg0, Object argN) {
    final String arg1 = ((OoyalaNotification)argN).getName();
    if (arg1 == OoyalaPlayer.TIME_CHANGED_NOTIFICATION_NAME) {
      return;
    }
    Log.d(TAG, "Notification Received: " + arg1 + " - state: " + player.getState());
  }

}

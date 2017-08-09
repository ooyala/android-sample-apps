package com.ooyala.sample.players;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.sample.R;
import com.ooyala.sample.utils.AdobePassLoginController;
import com.ooyala.sample.utils.OnAuthorizationChangedListener;

import java.util.Observable;
import java.util.Observer;

import static com.ooyala.android.OoyalaPlayer.State.*;


public class AdobePassSampleAppAcitivity extends Activity implements OnAuthorizationChangedListener, Observer {
  final String TAG = this.getClass().toString();

  String EMBED = null;
  String PCODE = null;
  String DOMAIN = null;
  private AdobePassLoginController adobePassController;

  protected OoyalaPlayerLayoutController playerLayoutController;
  protected OoyalaPlayer player;


  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.player_double_button_layout);
    setTitle(getIntent().getExtras().getString("selection_name"));

    adobePassController = new AdobePassLoginController(this, "ooyala",
        getResources().openRawResource(R.raw.adobepass), "adobepass", this);
    adobePassController.checkAuth();

    EMBED = getIntent().getExtras().getString("embed_code");
    PCODE = getIntent().getExtras().getString("pcode");
    DOMAIN = getIntent().getExtras().getString("domain");

    //Initialize the player
    OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);

    Options options = new Options.Builder().setUseExoPlayer(true).build();
    player = new OoyalaPlayer(PCODE, new PlayerDomain(DOMAIN), options);
    playerLayoutController = new OoyalaPlayerLayoutController(playerLayout, player);
    player.addObserver(this);

    Button loginButton = (Button)findViewById(R.id.doubleLeftButton);
    loginButton.setText("Login");
    loginButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        Button loginButton = (Button) v;
        if (loginButton.getText().equals("Login")) {
          adobePassController.login();
        } else {
          adobePassController.logout();
        }
      }
    });
    Button setEmbedCodeButton = (Button)findViewById(R.id.doubleRightButton);
    setEmbedCodeButton.setText("SetEmbedCode");
    setEmbedCodeButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        playerLayoutController.getPlayer().setEmbedCode(EMBED);
        playerLayoutController.getPlayer().play();
      }
    });
  }

  @Override
  protected void onStop() {
    super.onStop();
    if (null != player && player.getState() == PLAYING) {
      player.suspend();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (null != player) {
      player.resume();
    }
  }

  @Override
  public void authChanged(Boolean authorized) {
    Button loginButton = (Button) findViewById(R.id.doubleLeftButton);
    if (authorized) {
      loginButton.setText("Logout");
    } else {
      loginButton.setText("Login");
      playerLayoutController.getPlayer().setEmbedCode("none");
    }
  }

  /**
   * Listen to all notifications from the OoyalaPlayer
   */
  @Override
  public void update(Observable arg0, Object argN) {
    final String arg1 = OoyalaNotification.getNameOrUnknown(argN);
    if (arg1 == OoyalaPlayer.TIME_CHANGED_NOTIFICATION_NAME) {
      return;
    }
    Log.d(TAG, "Notification Received: " + arg1 + " - state: " + player.getState());
  }
}

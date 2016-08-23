package com.ooyala.sample.players;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.nielsen.app.sdk.AppSdk;
import com.nielsen.app.sdk.IAppNotifier;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.nielsensdk.NielsenAnalytics;
import com.ooyala.android.skin.OoyalaSkinLayout;
import com.ooyala.android.skin.OoyalaSkinLayoutController;
import com.ooyala.android.skin.configuration.SkinOptions;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.android.util.DebugMode;
import com.ooyala.sample.OptOutActivity;
import com.ooyala.sample.R;
import com.ooyala.android.util.SDCardLogcatOoyalaEventsLogger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This activity illustrates a basic integration with Nielsen Analytics
 *
 * Note that Nielsen Integration requires VisualOn integration, as well
 */
public class NielsenDefaultPlayerActivity extends Activity implements Observer, IAppNotifier, DefaultHardwareBackBtnHandler {
  final static String TAG = NielsenDefaultPlayerActivity.class.getSimpleName();

  String EMBED = null;
  String PCODE = null;
  String DOMAIN = null;

  // Write the sdk events text along with events count to log file in sdcard if the log file already exists
  SDCardLogcatOoyalaEventsLogger playbacklog = new SDCardLogcatOoyalaEventsLogger();

  protected OoyalaPlayerLayoutController playerLayoutController;
  protected OoyalaPlayer player;
  protected OoyalaSkinLayoutController controller;

  private static NielsenAnalytics s_nielsenAnalytics;
  private final static String NIELSEN_SFCODE = "<sfcode-provided-by-Nielsen>";
  private final static String NIELSEN_APPID = "<appid-provided-by-Nielsen>";
  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle(getIntent().getExtras().getString("selection_name"));
    setContentView(R.layout.player_single_button_layout);

    EMBED = getIntent().getExtras().getString("embed_code");
    PCODE = getIntent().getExtras().getString("pcode");
    DOMAIN = getIntent().getExtras().getString("domain");

     //Initialize the player
    OoyalaSkinLayout skinLayout = (OoyalaSkinLayout)findViewById(R.id.ooyalaPlayer);

    // Create the OoyalaPlayer, with some built-in UI disabled
    PlayerDomain domain = new PlayerDomain(DOMAIN);
    Options options = new Options.Builder().setShowPromoImage(false).build();
    player = new OoyalaPlayer(PCODE, domain, options);

    //Create the SkinOptions, and setup React
    SkinOptions skinOptions = new SkinOptions.Builder().build();
    controller = new OoyalaSkinLayoutController(getApplication(), skinLayout, player, skinOptions);

    player.addObserver(this);

    // Create NielsenAnalytics plugin
    s_nielsenAnalytics = new NielsenAnalytics( this, player, this, NIELSEN_APPID, "0.1", "NielsenTestApp", NIELSEN_SFCODE, getCustomConfig(), null );

    // Set up the required Nielsen opt-out UI button
    final Button optInOutButton = (Button)findViewById( R.id.singleButton );
    optInOutButton.setText("Open Nielsen Opt-out");
    optInOutButton.setOnClickListener( new View.OnClickListener() {
      @Override
      public void onClick( View v ) {
        showOptInOutUI();
      }
    } );

    if (player.setEmbedCode(EMBED)) {
      //Uncomment for Auto-Play
      //player.play();
    }
    else {
      Log.e(TAG, "Asset Failure");
    }
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    controller.onKeyDown(keyCode, event);
    return super.onKeyDown(keyCode, event);
  }

  /** Start DefaultHardwareBackBtnHandler **/
  @Override
  public void invokeDefaultOnBackPressed() {
    super.onBackPressed();
  }
  /** End DefaultHardwareBackBtnHandler **/
  @Override
  protected void onStop() {
    super.onStop();
    Log.d(TAG, "Player Activity Stopped");
    if (player != null) {
      player.suspend();
    }
    NielsenDefaultPlayerActivity.decrementRunningActivityCount();
  }

  @Override
  protected void onRestart() {
    super.onRestart();
    Log.d(TAG, "Player Activity Restarted");
    if (player != null) {
      player.resume();
    }
  }
  /** Start Activity methods for Skin **/
  @Override
  protected void onPause() {
    super.onPause();
    if (controller != null) {
      controller.onPause();
    }
    Log.d(TAG, "Player Activity Stopped");
    if (player != null) {
      player.suspend();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (controller != null) {
      controller.onResume( this, this );
    }
    Log.d(TAG, "Player Activity Restarted");
    if (player != null) {
      player.resume();
    }
  }

  @Override
  public void onBackPressed() {
    if (controller != null) {
      controller.onBackPressed();
    } else {
      super.onBackPressed();
    }
  }
  @Override
  protected void onStart() {
    DebugMode.logD(TAG, "onStart");
    super.onStart();
    NielsenDefaultPlayerActivity.incrementRunningActivityCount();
  }

  /**
   * Listen to all notifications from the OoyalaPlayer
   */
  @Override
  public void update(Observable arg0, Object argN) {
    if (arg0 != player) {
      return;
    }

    final String arg1 = OoyalaNotification.getNameOrUnknown(argN);
    if (arg1 == OoyalaPlayer.TIME_CHANGED_NOTIFICATION_NAME) {
      return;
    }

    if (arg1 == OoyalaPlayer.ERROR_NOTIFICATION_NAME) {
      final String msg = "Error Event Received";
      if (player != null && player.getError() != null) {
        Log.e(TAG, msg, player.getError());
      }
      else {
        Log.e(TAG, msg);
      }
      return;
    }

    // Automation Hook: to write Notifications to a temporary file on the device/emulator
    String text="Notification Received: " + arg1 + " - state: " + player.getState();
    // Automation Hook: Write the event text along with event count to log file in sdcard if the log file exists
    playbacklog.writeToSdcardLog(text);

    Log.d(TAG, "Notification Received: " + arg1 + " - state: " + player.getState());
  }

  @Override
  public void onAppSdkEvent(long timestamp, int code, String description) {
    switch( code ) {
      case AppSdk.EVENT_INITIATE:
        Log.d( TAG, "EVENT_INITIATE" );
        break;
      case AppSdk.EVENT_STARTUP:
        Log.d( TAG, "EVENT_STARTUP" );
        break;
      case AppSdk.EVENT_SHUTDOWN:
        Log.d( TAG, "EVENT_SHUTDOWN" );
        break;
    }
  }

  private JSONObject getCustomConfig() {
    final JSONObject json = new JSONObject();
    try {
      json.put( "nol_devDebug", "true" ); // do NOT do this for production apps!
    }
    catch( JSONException e ) {
      Log.e( TAG, "getCustomConfig()", e );
    }
    return json;
  }


//======================== Opt Out UI  =========================//
// The following methods demonstrate one way to expose and respond to Nielsen's required opt-out functionality

  private void showOptInOutUI() {
    final String url = getOptOutUrl();
    if( url == null || url.trim().length() == 0 ) {
      showRestartRequiredMessage();
    }
    else {
      Intent i = new Intent(this, OptOutActivity.class);
      final Bundle pars = new Bundle();
      if (i != null && pars != null) {
        pars.putString(OptOutActivity.OPT_OUT_URL_EXTRAS_KEY, url);
        i.putExtras(pars);
        startActivityForResult(i, OptOutActivity.OPTOUT_REQUEST_CODE);
      }
    }
  }

  private String getOptOutUrl() {
    if( s_nielsenAnalytics != null && s_nielsenAnalytics.isValid() ) {
      return s_nielsenAnalytics.getNielsenAppSdk().userOptOutURLString();
    }
    else {
      return null;
    }
  }

  private void showRestartRequiredMessage() {
    AlertDialog.Builder builder = new AlertDialog.Builder( this );
    builder.setTitle( "No Opt-Out URL" );
    builder.setMessage( "If networking was disabled, please enable networking & restart this app." );
    builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
      }
    });
    builder.show();
  }


  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if( resultCode == RESULT_OK ) {
      if( requestCode == OptOutActivity.OPTOUT_REQUEST_CODE && s_nielsenAnalytics != null ) {
        final String optOutResult = data.getStringExtra( OptOutActivity.OPT_OUT_RESULT_KEY );
        Log.d(TAG, "onActivityResult: Opt Out Result = " + optOutResult);
        s_nielsenAnalytics.getNielsenAppSdk().userOptOut(optOutResult);
      }
    }
  }

//========================= Activity Counting ===================//
// An example of how to show the Opt Out Webview

  public static AtomicInteger s_activityCount = new AtomicInteger();
  public static void incrementRunningActivityCount() {
    s_activityCount.incrementAndGet();
  }
  public static void decrementRunningActivityCount() {
    int v = s_activityCount.decrementAndGet();
    if( v == 0 && s_nielsenAnalytics != null ) {
      new Handler().post(
              new Runnable() {
                @Override
                public void run() {
                  DebugMode.logD( TAG, "onStop: we appear to be 'backgrounded'." );
                  s_nielsenAnalytics.getNielsenAppSdk().stop();
                }
              }
      );
    }
  }

}

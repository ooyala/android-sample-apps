package com.ooyala.sample.common;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.ooyala.android.EmbedTokenGenerator;
import com.ooyala.android.EmbedTokenGeneratorCallback;
import com.ooyala.android.EmbeddedSecureURLGenerator;
import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.skin.OoyalaSkinLayoutController;
import com.ooyala.android.util.SDCardLogcatOoyalaEventsLogger;
import com.ooyala.sample.simple.SimpleCastPlayerActivity;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public abstract class PlayerActivity extends AppCompatActivity implements EmbedTokenGenerator, Observer {
  protected static final String TAG = SimpleCastPlayerActivity.class.getSimpleName();

  protected String embedCode;
  protected String secondEmbedCode;
  protected String pcode;
  protected String domain;

  /*
   * The API Key and Secret should not be saved inside your applciation (even in git!).
   * However, for debugging you can use them to locally generate Ooyala Player Tokens.
   */
  private final String APIKEY = "";
  private final String SECRET = "";
  private final String ACCOUNT_ID = "";

  protected OoyalaPlayer player;
  protected SDCardLogcatOoyalaEventsLogger playbackLog = new SDCardLogcatOoyalaEventsLogger();

  private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
  protected boolean asked = false;
  protected boolean writePermission = false;


  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    parseSharedPreferences();

    if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
    } else {
      writePermission = true;
      asked = true;
    }
  }

  protected void completePlayerSetup() {
    if (asked && writePermission) {
      PlayerDomain playerDomain = new PlayerDomain(domain);
      player = new OoyalaPlayer(pcode, playerDomain, this, getOptions());
      initAndBindController();
      player.addObserver(this);

      if (!TextUtils.isEmpty(embedCode)) {
        play(embedCode);
      } else {
        String currentEmbedCode = getCurrentRemoteEmbedCode();
        if (!TextUtils.isEmpty(currentEmbedCode)) {
          play(currentEmbedCode);
        }
      }
    }
  }

  /**
   * Gets embed code that plays on remote device.
   *
   * @return current embed code that plays on remote device or null
   */
  @Nullable
  abstract protected String getCurrentRemoteEmbedCode();

  protected abstract Options getOptions();

  protected abstract void initAndBindController();

  protected void parseSharedPreferences() {
    SharedPreferences lastChosenParams = getSharedPreferences("LastChosenParams", MODE_PRIVATE);
    if (lastChosenParams != null) {
      embedCode = lastChosenParams.getString("embedcode", "");
      secondEmbedCode = lastChosenParams.getString("secondEmbedCode", null);
      pcode = lastChosenParams.getString("pcode", "");
      domain = lastChosenParams.getString("domain", "");

      removeUsedEmbedCodes(lastChosenParams);
    }
  }

  private void removeUsedEmbedCodes(SharedPreferences lastChosenParams) {
    lastChosenParams
        .edit()
        .putString("embedcode", null)
        .putString("secondEmbedCode", null)
        .apply();
  }

  @Override
  protected void onRestart() {
    super.onRestart();
    Log.d(TAG, "Player Activity Restarted");
    if (null != player) {
      player.resume();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (player != null) {
      player.resume();
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (player != null) {
      player.pause();
    }
  }

  protected void play(String ec) {
    player.setEmbedCode(ec);
    player.play();
  }

  @Override
  protected void onStop() {
    super.onStop();
    if (null != player) {
      player.suspend();
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    if (player != null) {
      player.destroy();
      player = null;
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (requestCode == PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
      asked = true;
      if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
        writePermission = true;
      }
    }
  }

  @Override
  public void update(Observable arg0, Object argN) {
    if (arg0 != player) {
      return;
    }
    final String arg1 = OoyalaNotification.getNameOrUnknown(argN);
    if (arg1 == OoyalaPlayer.TIME_CHANGED_NOTIFICATION_NAME) {
      return;
    }

    if (arg1 == OoyalaPlayer.PLAY_COMPLETED_NOTIFICATION_NAME && secondEmbedCode != null) {
      play(secondEmbedCode);
      secondEmbedCode = null;
    }

    if (arg1.equalsIgnoreCase(OoyalaSkinLayoutController.FULLSCREEN_CHANGED_NOTIFICATION_NAME)) {
      Log.d(TAG, "Fullscreen Notification received : " + arg1 + " - fullScreen: " + ((OoyalaNotification) argN).getData());
    }


    if (arg1 == OoyalaPlayer.ERROR_NOTIFICATION_NAME) {
      final String msg = "Error Event Received";
      if (player != null && player.getError() != null) {
        Log.e(TAG, msg, player.getError());
      } else {
        Log.e(TAG, msg);
      }
    }

    // Automation Hook: to write Notifications to a temporary file on the device/emulator
    String text = getLog(argN);
    playbackLog.writeToSdcardLog(text);
    Log.d(TAG, "Notification Received: " + arg1 + " - state: " + player.getState());
  }

  protected String getLog(Object argN) {
    final String arg1 = OoyalaNotification.getNameOrUnknown(argN);
    return "Notification Received: " + arg1 + " - state: " + player.getState();
  }

  @Override
  public void getTokenForEmbedCodes(List<String> embedCodes, EmbedTokenGeneratorCallback callback) {
    String embedCodesString = "";
    for (String ec : embedCodes) {
      if (ec.equals("")) embedCodesString += ",";
      embedCodesString += ec;
    }
    HashMap<String, String> params = new HashMap<>();

//      Uncommenting this will bypass all syndication rules on your asset
//      This will not work unless you have a working API Key and Secret.
//      This is one reason why you shouldn't keep the Secret in
//      your app/source control
//      params.put("override_syndication_group", "override_all_synd_groups");

    params.put("account_id", ACCOUNT_ID);
    String uri = "/sas/embed_token/" + pcode + "/" + embedCodesString;
    EmbeddedSecureURLGenerator urlGen = new EmbeddedSecureURLGenerator(APIKEY, SECRET);
    URL tokenUrl = urlGen.secureURL("http://player.ooyala.com", uri, params);
    callback.setEmbedToken(tokenUrl.toString());
  }
}

package com.ooyala.sample.players;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.Configuration;
import android.os.Bundle;

import com.ooyala.android.util.DebugMode;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.ui.OptimizedOoyalaPlayerLayoutController;
import com.ooyala.sample.R;
/*
  plays a certain video from channel browser
 */
public class PlayerDetailActivity extends Activity {
  private static final String TAG = "PlayerDetailActivity";

  private OoyalaPlayer player = null;
  private Boolean isSuspended = false;

  @Override
  public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    String embedCode = getIntent().getStringExtra("com.ooyala.embedcode");
    Thread.setDefaultUncaughtExceptionHandler(onUncaughtException);
    try {
      setContentView(R.layout.main);

    } catch (Exception e) {
      e.printStackTrace();
    }

    player = new OoyalaPlayer(ChannelBrowserActivity.PCODE, new PlayerDomain(ChannelBrowserActivity.PLAYERDOMAIN));
    OptimizedOoyalaPlayerLayoutController layoutController = new OptimizedOoyalaPlayerLayoutController((OoyalaPlayerLayout) findViewById(R.id.player), player);

    if (player.setEmbedCode(embedCode)) {
      DebugMode.logD(TAG, "the embed code is set successfully");
      player.play();
    } else {
      DebugMode.logE(TAG, "set embed code " + embedCode + " failed!");
    }
  }

  @Override
  protected void onStop() {
    super.onStop();
    DebugMode.logD(TAG, "OnStop");
    if (player != null && !isSuspended) {
      player.suspend();
      isSuspended = true;
    }
  }

  @Override
  protected void onRestart() {
    super.onRestart();
    DebugMode.logD(TAG, "OnRestart");
    if (player != null) {
      player.resume();
      isSuspended = false;
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    player = null;
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    DebugMode.logD(TAG, "onConfigurationChanged");
    super.onConfigurationChanged(newConfig);
  }

  private Thread.UncaughtExceptionHandler onUncaughtException = new Thread.UncaughtExceptionHandler() {
    public void uncaughtException(Thread thread, Throwable ex) {
      DebugMode.logE(TAG, "Uncaught exception", ex);
      showErrorDialog(ex);
    }
  };

  private void showErrorDialog(Throwable t) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);

    builder.setTitle("Exception!");
    builder.setMessage(t.toString());
    builder.setPositiveButton("OK", null);
    builder.show();
  }
}

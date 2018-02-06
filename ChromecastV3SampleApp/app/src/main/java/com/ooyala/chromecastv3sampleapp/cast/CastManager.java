package com.ooyala.chromecastv3sampleapp.cast;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.ooyala.android.CastManagerInterface;
import com.ooyala.android.CastModeOptions;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.player.PlayerInterface;
import com.ooyala.android.util.DebugMode;

import java.lang.ref.WeakReference;

import static android.content.ContentValues.TAG;


public class CastManager implements CastManagerInterface, CastSessionListener.ConnectStatusListener {
  private CastContext castContext;
  private CastPlayer castPlayer;

  private View castView;
  private WeakReference<OoyalaPlayer> ooyalaPlayer;
  private boolean isConnected = false;
  private boolean isInCastMode = false;

  private static CastManager castManager;

  public static void initialize(Context context) {
    castManager = new CastManager(context);
  }

  public static CastManager getCastManager() {
    return castManager;
  }

  private CastManager(Context context) {
    setupCast(context);
    CastSessionListener castSessionListener = new CastSessionListener(this);
    castContext.getSessionManager().addSessionManagerListener(castSessionListener, CastSession.class);
  }

  //region Working with CastView

  /**
   * Set the view to display while casting
   *
   * @param view to display in the video area while casting. Possibly null.
   * @see #getCastView()
   */
  public void setCastView(View view) {
    DebugMode.assertCondition(view != null, TAG, "cannot set castView to null");
    DebugMode.logD(TAG, "Set cast view to " + view);
    castView = view;
  }

  /**
   * @return the currently associated View. Possibly null.
   * @see #setCastView(android.view.View) the view that is displayed during Cast playback
   */
  public View getCastView() {
    return castView;
  }

  /**
   * Removes castView from the OoyalaPlayer, for use after disconnecting from the receiver.
   */
  private void hideCastView() {
    DebugMode.logD(TAG, "Hide cast view");
    if (ooyalaPlayer != null && ooyalaPlayer.get().getLayout().getChildCount() != 0 && castView != null) {
      ooyalaPlayer.get().getLayout().removeView(castView);
    }
  }

  /**
   * Called after we establish casting to a receiver.
   */
  private void displayCastView() {
    DebugMode.logD(TAG, "CastView = " + castView);
    if (ooyalaPlayer != null && castView != null) {
      if (castView.getParent() != null) {
        ((ViewGroup) castView.getParent()).removeView(castView);
      }
      ooyalaPlayer.get().getLayout().addView(castView);
    }
  }

  //endregion

  //region Implementation of CastManagerInterface

  @Override
  public boolean isConnectedToReceiverApp() {
    return isConnected;
  }

  @Override
  public PlayerInterface getCastPlayer() {
    return null;
  }

  @Override
  public void registerWithOoyalaPlayer(OoyalaPlayer ooyalaPlayer) {
    this.ooyalaPlayer = new WeakReference<>(ooyalaPlayer);
    ooyalaPlayer.registerCastManager(this);
  }

  @Override
  public boolean isInCastMode() {
    return isInCastMode;
  }

  @Override
  public void enterCastMode(CastModeOptions options) {
    new CastManagerInitCastPlayerAsyncTask(this, options).execute();
    isInCastMode = true;
    displayCastView();
  }

  //endregion

  private void setupCast(Context context) {
    castContext = CastContext.getSharedInstance(context);
  }

  void initCast(CastModeOptions options, String token) {
    castPlayer.loadMedia(options, ooyalaPlayer.get(), token);
  }

  private void cleanupAfterReceiverDisconnect() {
    DebugMode.logD(TAG, "Exit Cast Mode");
    isInCastMode = false;
    hideCastView();
    castView = null;
    if (ooyalaPlayer != null) {
      ooyalaPlayer.get().exitCastMode(castPlayer.getPlayheadTime(),
          castPlayer.isPlaying(),
          castPlayer.getEmbedCode());
    }
  }


  //region Implementation of CastSessionListener.ConnectStatusListener :

  @Override
  public void onApplicationConnected(CastSession castSession) {
    castPlayer = new CastPlayer(castSession);

    isConnected = true;
    if (ooyalaPlayer != null && ooyalaPlayer.get().getCurrentItem() != null) {
      ooyalaPlayer.get().switchToCastModeV3(ooyalaPlayer.get().getEmbedCode());
    }
  }

  @Override
  public void onApplicationDisconnect(CastSession session) {
    isConnected = false;
    hideCastView();
    cleanupAfterReceiverDisconnect();
  }

  //endregion
}

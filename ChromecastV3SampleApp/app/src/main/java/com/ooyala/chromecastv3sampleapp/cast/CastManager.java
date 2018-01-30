package com.ooyala.chromecastv3sampleapp.cast;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.images.WebImage;
import com.ooyala.android.CastModeOptions;
import com.ooyala.android.Environment;
import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.item.Video;
import com.ooyala.android.util.DebugMode;
import com.ooyala.chromecastv3sampleapp.CastViewManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Observable;
import java.util.Observer;

import static android.content.ContentValues.TAG;


public class CastManager implements Observer {
  private RemoteMediaClient remoteMediaClient;
  private CastContext castContext;
  private CastSession castSession;
  private SessionManagerListener<CastSession> sessionManagerListener;
  private CastViewManager castViewManager;
  private View castView;
  private Context context;
  private WeakReference<OoyalaPlayer> ooyalaPlayer;


  public CastManager(Context context, OoyalaPlayer player) {
    this.context = context;
    this.ooyalaPlayer = new WeakReference<>(player);
    setupCast(context);
    setupCastListener(player);
    castContext.getSessionManager().addSessionManagerListener(sessionManagerListener, CastSession.class);
    castViewManager = new CastViewManager((Activity) context, this);
  }

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

  private void loadRemoteMedia() {
    if (castSession == null) {
      return;
    }
    remoteMediaClient = castSession.getRemoteMediaClient();
  }

  /**
   * @return the currently associated View. Possibly null.
   * @see #setCastView(android.view.View) the view that is displayed during Cast playback
   */
  public View getCastView() {
    return castView;
  }

  public void loadMedia(Video currentItem, CastModeOptions options, String embedToken) {
    JSONObject playerParams = new JSONObject();
    String itemTitle = null;
    String itemPromoImageUrl = null;
    try {
      if (embedToken != null) {
        playerParams.put("embedToken", embedToken);
      }

      if (options.getCCLanguage() != null) {
        playerParams.put("ccLanguage", "");
      }

      if (options.getAuthToken() != null) {
        playerParams.put("authToken", options.getAuthToken());
      }

      if (options.getDomain() != null) {
        playerParams.put("domain", options.getDomain().toString());
      }

      playerParams.put("ec", options.getEmbedCode());
      playerParams.put("version", null);
      playerParams.put("params", playerParams.toString());
      if (currentItem != null) {
        itemTitle = currentItem.getTitle();
        if (itemTitle != null) {
          playerParams.put("title", itemTitle);
        }
        String itemDescription = currentItem.getDescription();
        if (itemDescription != null) {
          playerParams.put("description", itemDescription);
        }
        itemPromoImageUrl = currentItem.getPromoImageURL(2000, 2000);
        if (itemPromoImageUrl != null) {
          playerParams.put("promo_url", itemPromoImageUrl);
        }
      }
    } catch (JSONException e) {
      return;
    }

    MediaMetadata metadata = new MediaMetadata();
    if (itemTitle != null) {
      metadata.putString(MediaMetadata.KEY_TITLE, itemTitle);
    }
    if (itemPromoImageUrl != null) {
      Uri uri = Uri.parse(itemPromoImageUrl);
      if (uri != null) {
        WebImage image = new WebImage(uri);
        metadata.addImage(image);
      }
    }
    MediaInfo mediaInfo = new MediaInfo.Builder(options.getEmbedCode())
        .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
        .setContentType("video/mp4")
        .setCustomData(playerParams)
        .setMetadata(metadata)
        .build();

    remoteMediaClient.load(mediaInfo, true, options.getPlayheadTimeInMillis());

  }


  private void setupCastListener(final OoyalaPlayer player) {
    sessionManagerListener = new SessionManagerListener<CastSession>() {

      @Override
      public void onSessionEnded(CastSession session, int error) {
        onApplicationDisconnect(session);
      }

      @Override
      public void onSessionResumed(CastSession session, boolean wasSuspended) {
        onApplicationConnected(session);
      }

      @Override
      public void onSessionResumeFailed(CastSession session, int error) {
      }

      @Override
      public void onSessionStarted(CastSession session, String sessionId) {
        onApplicationConnected(session);
      }

      @Override
      public void onSessionStartFailed(CastSession session, int error) {
        onApplicationDisconnect(session);
      }

      @Override
      public void onSessionStarting(CastSession session) {
      }

      @Override
      public void onSessionEnding(CastSession session) {
        onApplicationDisconnect(session);
      }

      private void onApplicationDisconnect(CastSession session) {
        hideCastView();
      }

      @Override
      public void onSessionResuming(CastSession session, String sessionId) {
      }

      @Override
      public void onSessionSuspended(CastSession session, int reason) {
        OoyalaPlayer.setEnvironment(Environment.EnvironmentType.STAGING);
      }

      private void onApplicationConnected(CastSession castSession) {
        displayCastView();
        CastManager.this.castSession = castSession;
        loadRemoteMedia();
        player.switchToCastModeV3(player.getEmbedCode(), new OoyalaPlayer.OnDataPrepared() {
          @Override
          public void prepared(Video currentItem, CastModeOptions castModeOptions, String embedToken) {
            loadMedia(currentItem, castModeOptions, embedToken);
          }
        });
      }
    };
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

  public void enterCastMode(CastModeOptions options) {
//    DebugMode.logD(TAG, "enterCastMode with embedCode = " + options.getEmbedCode() + ", playhead = " + options.getPlayheadTimeInMillis() + " isPlaying = " + options.isPlaying());
//    DebugMode.assertCondition( ooyalaPlayer != null, TAG, "ooyalaPlayer should be not null while entering cast mode" );
//    DebugMode.assertCondition(castPlayer != null, TAG, "castPlayer should be not null while entering cast mode");
//    new CastManagerInitCastPlayerAsyncTask(this, options).execute();
//    displayCastView();
//    isInCastMode = true;
  }

  private void setupCast(Context context) {
    castContext = CastContext.getSharedInstance(context);
    castSession = castContext.getSessionManager().getCurrentCastSession();
  }

  @Override
  public void update(Observable arg0, Object argN) {
    if (ooyalaPlayer != null && arg0 != ooyalaPlayer.get()) {
      return;
    }

    final String arg1 = OoyalaNotification.getNameOrUnknown(argN);
    if (arg1 == OoyalaPlayer.TIME_CHANGED_NOTIFICATION_NAME) {
      return;
    }

    if (arg1 == OoyalaPlayer.CURRENT_ITEM_CHANGED_NOTIFICATION_NAME) {
      castViewManager.configureCastView(ooyalaPlayer.get().getCurrentItem());
    } else if (arg1 == OoyalaPlayer.ERROR_NOTIFICATION_NAME) {
      final String msg = "Error Event Received";
      if (ooyalaPlayer.get() != null && ooyalaPlayer.get().getError() != null) {
        Log.e(TAG, msg, ooyalaPlayer.get().getError());
      } else {
        Log.e(TAG, msg);
      }
    }

    if (arg1 == OoyalaPlayer.STATE_CHANGED_NOTIFICATION_NAME) {
      if (ooyalaPlayer.get().isInCastMode()) {
        OoyalaPlayer.State state = ooyalaPlayer.get().getState();
        castViewManager.updateCastState(context, state);
      }
    }

//    if( arg1 == OoyalaPlayer.PLAY_COMPLETED_NOTIFICATION_NAME && embedCode != null ) {
////      play( embedCode2 );
////      embedCode2 = null;
//    }

    // Automation Hook: to write Notifications to a temporary file on the device/emulator
//    String text="Notification Received: " + arg1 + " - state: " + player.getState();
//    // Automation Hook: Write the event text along with event count to log file in sdcard if the log file exists
//    Playbacklog.writeToSdcardLog(text);
//
//    Log.d(TAG, "Notification Received: " + arg1 + " - state: " + player.getState());
  }

}

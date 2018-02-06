package com.ooyala.chromecastv3sampleapp.cast;


import android.net.Uri;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.images.WebImage;
import com.ooyala.android.CastModeOptions;
import com.ooyala.android.OoyalaException;
import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.item.Video;
import com.ooyala.android.player.PlayerInterface;
import com.ooyala.android.player.PlayerType;
import com.ooyala.android.util.DebugMode;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Observable;

import static com.ooyala.android.OoyalaPlayer.State.PLAYING;

class CastPlayer extends Observable implements PlayerInterface {
  private RemoteMediaClient remoteMediaClient;
  private long playhead = 0;
  private String embedCode = "";

  CastPlayer(CastSession castSession) {
    if (castSession == null) {
      return;
    }
    remoteMediaClient = castSession.getRemoteMediaClient();
    castSession.addCastListener(new Cast.Listener(){

    });
  }

  void loadMedia(CastModeOptions options, OoyalaPlayer ooyalaPlayer, String embedToken) {
    JSONObject playerParams = new JSONObject();
    String itemTitle = null;
    String itemPromoImageUrl = null;
    this.embedCode = options.getEmbedCode();
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

      playerParams.put("ec", embedCode);
      playerParams.put("version", null);
      playerParams.put("params", playerParams.toString());
      if (ooyalaPlayer != null) {
        Video currentItem = ooyalaPlayer.getCurrentItem();
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

    MediaInfo mediaInfo = new MediaInfo.Builder(embedCode)
        .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
        .setContentType("video/mp4")
        .setCustomData(playerParams)
        .setMetadata(metadata)
        .build();

    remoteMediaClient.load(mediaInfo, true, 5000);
    setCurrentTime(5000);

    remoteMediaClient.addProgressListener(new RemoteMediaClient.ProgressListener() {
      @Override
      public void onProgressUpdated(long l, long l1) {
        playhead = l;
        isPlaying = remoteMediaClient.isPlaying();
      }
    }, 1); // Check current playhead every 1 second

    remoteMediaClient.addListener(new RemoteMediaClient.Listener() {
      @Override
      public void onStatusUpdated() {

      }

      @Override
      public void onMetadataUpdated() {

      }

      @Override
      public void onQueueStatusUpdated() {

      }

      @Override
      public void onPreloadStatusUpdated() {

      }

      @Override
      public void onSendingRemoteMediaRequest() {

      }

      @Override
      public void onAdBreakStatusUpdated() {

      }
    });
  }

  /**
   * Change the playhead to the given time.
   *
   * @param curTime position to seek to, in milliseconds.
   */
  private void setCurrentTime(int curTime) {
    playhead = curTime;
    onPlayHeadChanged();
  }

  private void onPlayHeadChanged() {
    setChanged();
    notifyObservers(new OoyalaNotification(OoyalaPlayer.TIME_CHANGED_NOTIFICATION_NAME));
  }

  int getPlayheadTime() {
    return (int) playhead;
  }

  String getEmbedCode() {
    return embedCode;
  }

  private boolean isPlaying = false;

  boolean isPlaying() {
    return isPlaying;
  }

  //region implementation of PlayerInterface

  @Override
  public void pause() {
    remoteMediaClient.pause();
  }

  @Override
  public void play() {
    remoteMediaClient.play();
  }

  @Override
  public void stop() {
    remoteMediaClient.stop();
  }

  @Override
  public int currentTime() {
    return (int) playhead;
  }

  @Override
  public int duration() {
    return (int) remoteMediaClient.getCurrentItem().getPlaybackDuration();
  }

  @Override
  public int buffer() {
    return 0;
  }

  @Override
  public boolean seekable() {
    return isSeekable;
  }

  boolean isSeekable = false;

  @Override
  public void seekToTime(int timeInMillis) {
    remoteMediaClient.seek(timeInMillis);
  }

  @Override
  public OoyalaPlayer.State getState() {
    //INIT, LOADING, READY, PLAYING, PAUSED, COMPLETED, SUSPENDED, ERROR
    //TODO
    return PLAYING;
  }

  @Override
  public int livePlayheadPercentage() {
    //TODO
    return 0;
  }

  @Override
  public void seekToPercentLive(int percent) {
//TODO
  }

  @Override
  public boolean isLiveClosedCaptionsAvailable() {
    return false;
  }

  @Override
  public void setClosedCaptionsLanguage(String language) {

  }

  @Override
  public OoyalaException getError() {
    return null;
  }

  @Override
  public void setVolume(float volume) {
    remoteMediaClient.setStreamVolume(volume);
  }

  @Override
  public PlayerType getPlayerType() {
    return PlayerType.FLAT_PLAYER;
  }

  //endregion
}

package com.ooyala.chromecastv3sampleapp.cast;


import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.RemoteMediaPlayer;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.images.WebImage;
import com.ooyala.android.CastModeOptions;
import com.ooyala.android.OoyalaException;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.item.Video;
import com.ooyala.android.player.PlayerInterface;
import com.ooyala.android.player.PlayerType;

import org.json.JSONException;
import org.json.JSONObject;

import static com.ooyala.android.OoyalaPlayer.State.LOADING;
import static com.ooyala.android.OoyalaPlayer.State.PLAYING;

class CastPlayer implements PlayerInterface {
  private RemoteMediaClient remoteMediaClient;
  private long playhead = 0;
  private boolean isPlaying = false;
  private String embedCode = "";
  private OoyalaPlayer.State state = OoyalaPlayer.State.INIT;

  CastPlayer(CastSession castSession) {
    if (castSession == null) {
      return;
    }
    remoteMediaClient = castSession.getRemoteMediaClient();
  }

  void loadMedia(final CastModeOptions options, OoyalaPlayer ooyalaPlayer, String embedToken) {
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

    MediaMetadata metadata = getMediaMetadata(itemTitle, itemPromoImageUrl);
    MediaInfo mediaInfo = getMediaInfo(playerParams, metadata);
    startLoading(options, mediaInfo);
  }

  @NonNull
  private MediaMetadata getMediaMetadata(String itemTitle, String itemPromoImageUrl) {
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
    return metadata;
  }

  private MediaInfo getMediaInfo(JSONObject playerParams, MediaMetadata metadata) {
    return new MediaInfo.Builder(embedCode)
        .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
        .setContentType("video/mp4")
        .setCustomData(playerParams)
        .setMetadata(metadata)
        .build();
  }

  private void startLoading(final CastModeOptions options, MediaInfo mediaInfo) {
    setState(LOADING);
    remoteMediaClient.load(mediaInfo).setResultCallback(new ResultCallback<RemoteMediaClient.MediaChannelResult>() {
      @Override
      public void onResult(@NonNull RemoteMediaClient.MediaChannelResult mediaChannelResult) {
        //Seek after load video
        remoteMediaClient.seek(options.getPlayheadTimeInMillis());
        setState(PLAYING);
      }
    });

    remoteMediaClient.addProgressListener(new RemoteMediaClient.ProgressListener() {
      @Override
      public void onProgressUpdated(long l, long l1) {
        playhead = l;
        isPlaying = remoteMediaClient.isPlaying();
      }
    }, 1); // Check current playhead every 1 second
  }

  public void setState(OoyalaPlayer.State state) {
    this.state = state;
  }

  int getPlayheadTime() {
    return (int) playhead;
  }

  String getEmbedCode() {
    return embedCode;
  }

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
    remoteMediaClient.seek(timeInMillis, RemoteMediaPlayer.RESUME_STATE_PLAY);
  }

  @Override
  public OoyalaPlayer.State getState() {
    //INIT, LOADING, READY, PLAYING, PAUSED, COMPLETED, SUSPENDED, ERROR
    return state;
  }

  @Override
  public int livePlayheadPercentage() {
    return 0;
  }

  @Override
  public void seekToPercentLive(int percent) {

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

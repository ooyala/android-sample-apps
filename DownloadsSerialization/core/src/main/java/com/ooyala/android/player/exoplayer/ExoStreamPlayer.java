package com.ooyala.android.player.exoplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.format.DateUtils;
import android.util.Base64;
import android.view.View;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.drm.DrmSession;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.drm.FrameworkMediaDrm;
import com.google.android.exoplayer2.drm.HttpMediaDrmCallback;
import com.google.android.exoplayer2.drm.OfflineLicenseHelper;
import com.google.android.exoplayer2.drm.UnsupportedDrmException;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DashUtil;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.dash.manifest.DashManifest;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;
import com.ooyala.android.OoyalaException;
import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.SeekInfo;
import com.ooyala.android.Utils;
import com.ooyala.android.item.Stream;
import com.ooyala.android.notifications.BitrateChangedNotificationInfo;
import com.ooyala.android.offline.DashDownloader;
import com.ooyala.android.player.MovieView;
import com.ooyala.android.player.StreamPlayer;
import com.ooyala.android.util.DebugMode;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * ExoStreamPlayer is based on ExoPlayer 2
 */

public class ExoStreamPlayer extends StreamPlayer implements
  Player.EventListener, ExoPlayerGeneralListener, SeekCompleteObserver.SeekCompleteCallback {

  private static final String TAG = ExoStreamPlayer.class.getSimpleName();
  private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
  private static final long UNKNOWN_TIME = -1L;
  private static final int MSG_DOWNLOAD_FAILED = 1;
  private static final int MSG_DOWNLOAD_COMPLETE = 1;
  private static final String LICENSE_KEYS = "keys";
  private static final String LICENSE_KEY_ARRAY = "LicenseKey";

  private Context context;
  private SimpleExoPlayer player;
  private Stream stream;
  private SimpleExoPlayerView view;
  private Handler mainHandler;
  private DataSource.Factory mediaDataSourceFactory;
  private File offlineVideoFolder;
  private SeekCompleteObserver seekCompleteObserver = new SeekCompleteObserver(this);

  private int timeBeforeSuspend;
  private int lastSeenBitrate;
  private OoyalaPlayer.State stateBeforeSuspend;
  private boolean initPlayStarted;

  // There is no way to determine if 608/708 are in the stream unless we actually see some text
  /**
   * true if the stream at any point emits 608/708 captions
   */
  private boolean liveClosedCaptionsObserved;

  /**
   * true if the setClosedCaptions was set to live CC, false otherwise
   */
  private boolean liveClosedCaptionsEnabled;

  /**
   * Init the player
   *
   * @param parent
   * @param streams
   */
  @Override
  public void init(OoyalaPlayer parent, Set<Stream> streams) {
    context = parent.getLayout().getContext();

    getMainHandler();
    setParent(parent);
    initializeStream(streams);

    timeBeforeSuspend = -1;
    lastSeenBitrate = 0;
    stateBeforeSuspend = OoyalaPlayer.State.INIT;

    if (stream == null) {
      DebugMode.logE(TAG, "ERROR: Invalid Stream (no valid stream available)");
      setError(new OoyalaException(OoyalaException.OoyalaErrorCode.ERROR_PLAYBACK_FAILED, "Invalid Stream"));
      setState(OoyalaPlayer.State.ERROR);
      return;
    }

    // Set up offline video folder
    if (parent.getCurrentItem() != null) {
      offlineVideoFolder = parent.getCurrentItem().getFolder();
    }

    initializePlayer();
    createSurfaceView();
  }

  /**
   * @return instance of {@link MovieView}
   */
  @Override
  public View getView() {
    return view;
  }

  /**
   * Chose the best stream and save it for using
   *
   * @param streams set of streams for initialising
   */
  private void initializeStream(Set<Stream> streams) {
    WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    boolean isWifiEnabled = wifiManager.isWifiEnabled();

    stream = Stream.bestStream(streams, isWifiEnabled);
  }

  /**
   * This method initializes Player and prepare it for working
   */
  private void initializePlayer() {
    // Reset init player started value
    initPlayStarted = false;

    // Create a default track selector
    TrackSelector trackSelector = new DefaultTrackSelector(BANDWIDTH_METER);

    // Init DRM UUID in offline mode if DRM licence is downloaded
    if (isOfflineMode()) {
      byte[] licence = loadLicense(offlineVideoFolder);
      if (licence != null) {
        stream.initWidevineUUID();
      }
    }

    UUID drmUUID = stream.getWidevineUUID();
    String drmLicenseUrl = stream.getWidevineServerPath();
    if (drmUUID == null) {
      if (isOfflineMode()) {
        // Create the offline player without DRM support
        DefaultRenderersFactory rendererFactory = new DefaultRenderersFactory(context);
        player = ExoPlayerFactory.newSimpleInstance(rendererFactory, trackSelector);
      } else {
        // Create the stream player
        player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
      }
    } else {
      // Create the player with DRM support
      try {
        player = buildDRMPlayer(trackSelector, drmUUID, drmLicenseUrl);
      } catch (UnsupportedDrmException e) {
        DebugMode.logE(TAG, "ERROR: Unsupported DRM exception: " + e.toString());
        setError(new OoyalaException(OoyalaException.OoyalaErrorCode.ERROR_PLAYBACK_FAILED,
          "Unsupported DRM exception"));
        setState(OoyalaPlayer.State.ERROR);
        return;
      }
    }

    preparePlayer(player);

    setState(OoyalaPlayer.State.LOADING);
  }

  private SimpleExoPlayer buildDRMPlayer(TrackSelector trackSelector, UUID uuid, String licenseUrl)
    throws UnsupportedDrmException {
    // Create DRM session manager
    DrmSessionManager<FrameworkMediaCrypto> drmSessionManager = buildDrmSessionManager(uuid, licenseUrl);

    // Create the player if the stream supports Digital Rights Management
    DefaultRenderersFactory rendererFactory = new DefaultRenderersFactory(context, drmSessionManager);
    return ExoPlayerFactory.newSimpleInstance(rendererFactory, trackSelector);
  }

  /**
   * Build Digital Rights Management session manager
   *
   * @param uuid       UUID for the Widevine DRM scheme
   * @param licenseUrl The default license URL. Used for key requests that do not specify
   *                   their own license URL
   * @return Digital Rights Management session manager {@link DrmSessionManager}
   * @throws UnsupportedDrmException Thrown when the requested DRM scheme is not supported
   */
  private DrmSessionManager<FrameworkMediaCrypto> buildDrmSessionManager(UUID uuid, String licenseUrl)
      throws UnsupportedDrmException {

    HttpMediaDrmCallback drmCallback = new HttpMediaDrmCallback(licenseUrl, buildHttpDataSourceFactory(true));

    DrmSessionManager<FrameworkMediaCrypto> drmSessionManager = new DefaultDrmSessionManager<>(uuid,
      FrameworkMediaDrm.newInstance(uuid), drmCallback, null, mainHandler, this);

    // Set offline playback mode
    if (isOfflineMode()) {
      ((DefaultDrmSessionManager) drmSessionManager).setMode(DefaultDrmSessionManager.MODE_PLAYBACK,
        loadLicense(offlineVideoFolder));
    }
    return drmSessionManager;
  }

  /**
   * Create and prepare main vr view
   */
  private void createSurfaceView() {
    view = new SimpleExoPlayerView(context);
    view.setPlayer(player);
    view.setUseController(false);

    getParent().addVideoView(view);
  }

  /**
   * @return URI for build media source of ExoPlayer
   */
  private String getURI() {
    return stream.getUrlFormat().equals(Stream.STREAM_URL_FORMAT_B64)
        ? stream.decodedURL().toString().trim() : stream.getUrl().trim();
  }

  /**
   * Remove video view and clean variables
   */
  private void removeSurfaceView() {
    getParent().removeVideoView();
    view = null;
  }

  /**
   * Method to prepare {@link SimpleExoPlayer}
   *
   * @param simpleExoPlayer instance of Player for preparing and adding listener
   */
  private void preparePlayer(SimpleExoPlayer simpleExoPlayer) {
    // Produces DataSource instances through that media data is loaded.
    mediaDataSourceFactory = isOfflineMode() ? buildDefaultDataSourceFactory() : buildDataSourceFactory(true);
    MediaSource mediaSource = buildMediaSource(Uri.parse(getURI()));

    simpleExoPlayer.addListener(this);

    // Set player render listeners
    EventLogger eventLogger = new EventLogger();
    simpleExoPlayer.setAudioDebugListener(eventLogger);
    simpleExoPlayer.setVideoDebugListener(eventLogger);
    simpleExoPlayer.addMetadataOutput(eventLogger);

    simpleExoPlayer.prepare(mediaSource, true, true);
  }

  /**
   * @param uri source for building media sources
   * @return A source of media consisting of one or more {@link MediaPeriod}s.
   * @see DashMediaSource
   * @see HlsMediaSource
   * @see ExtractorMediaSource
   */
  private MediaSource buildMediaSource(Uri uri) {
    switch (stream.getDeliveryType()) {
      case Stream.DELIVERY_TYPE_DASH:
        return new DashMediaSource(
            uri,
            mediaDataSourceFactory,
            new DefaultDashChunkSource.Factory(mediaDataSourceFactory),
            mainHandler,
            this);
      case Stream.DELIVERY_TYPE_HLS:
      case Stream.DELIVERY_TYPE_AKAMAI_HD2_HLS:
      case Stream.DELIVERY_TYPE_AKAMAI_HD2_VOD_HLS:
        return new HlsMediaSource(uri, mediaDataSourceFactory, mainHandler, this);
      default:
        return new ExtractorMediaSource(
            uri, mediaDataSourceFactory, new DefaultExtractorsFactory(), mainHandler, this);
    }
  }

  /**
   * Produces DataSource instances through that media data is loaded.
   *
   * @param useBandwidthMeter should we use {@link DefaultBandwidthMeter}
   * @return new instance of {@link DataSource.Factory}
   */
  private DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {
    return buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
  }

  /**
   * Produces DataSource factory instances through that media data is loaded.
   *
   * @param bandwidthMeter null or {@link DefaultBandwidthMeter}
   * @return new instance of {@link DataSource.Factory}
   */
  private DataSource.Factory buildDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
    return new DefaultDataSourceFactory(
        context, bandwidthMeter, buildHttpDataSourceFactory(bandwidthMeter));
  }

  /**
   * @param useBandwidthMeter should we use {@link DefaultBandwidthMeter}
   * @return new instance of {@link HttpDataSource.Factory}
   */
  private HttpDataSource.Factory buildHttpDataSourceFactory(boolean useBandwidthMeter) {
    return buildHttpDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
  }

  /**
   * @param bandwidthMeter null or {@link DefaultBandwidthMeter}
   * @return new instance of {@link HttpDataSource.Factory}
   */
  private HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
    return new DefaultHttpDataSourceFactory(Util.getUserAgent(context, "ExoStreamPlayer"), bandwidthMeter);
  }

  /**
   * @return new instance of {@link DefaultDataSourceFactory}
   */
  private DefaultDataSourceFactory buildDefaultDataSourceFactory() {
    return new DefaultDataSourceFactory(context, Util.getUserAgent(context, "ExoStreamPlayer"));
  }

  /**
   * Return instance of Handler to pass and manage touch events
   *
   * @return {@link Handler}
   */
  private Handler getMainHandler() {
    if (mainHandler == null) {
      mainHandler = new Handler();
    }
    return mainHandler;
  }

  @Override
  public void reset() {
    if (player != null) {
      destroy();
    }
    initializePlayer();
  }

  @Override
  public void suspend() {
    int millisToResume = -1;
    if (player != null) {
      millisToResume = (int) player.getCurrentPosition();
    }
    suspend(millisToResume, getState());
  }

  /**
   * This is called when plugin should be suspended
   *
   * @param millisToResume time in milliseconds for resuming
   * @param stateToResume  state to resume
   */
  private void suspend(int millisToResume, OoyalaPlayer.State stateToResume) {
    DebugMode.logD(TAG, "suspend with time " + millisToResume + "state" + stateToResume.toString());
    if (getState() == OoyalaPlayer.State.SUSPENDED) {
      DebugMode.logD(TAG, "Suspending an already suspended player");
      return;
    }
    if (player == null) {
      DebugMode.logD(TAG, "Suspending with a null player");
      return;
    }

    if (millisToResume >= 0) {
      timeBeforeSuspend = millisToResume;
    }
    stateBeforeSuspend = stateToResume;
    destroy();
    setState(OoyalaPlayer.State.SUSPENDED);
  }

  @Override
  public void resume() {
    resume(timeBeforeSuspend, stateBeforeSuspend);
  }

  @Override
  public void resume(int millisToResume, OoyalaPlayer.State stateToResume) {
    DebugMode.logD(TAG, "Resuming. time to resume: " + millisToResume + ", state to resume: "
        + stateToResume);
    if (player == null) {
      DebugMode.logE(TAG, "Exoplayer is null, cannot resume");
      return;
    }

    if (millisToResume >= 0) {
      player.seekTo(millisToResume);
    }

    if (stateToResume == OoyalaPlayer.State.PLAYING) {
      player.setPlayWhenReady(true);
    } else {
      setState(stateToResume);
    }
  }

  @Override
  public void destroy() {
    stopPlayheadTimer();

    if (player != null) {
      DebugMode.logD(TAG, "Destroy " + player.toString());
      player.removeListener(this);
      player.release();
      player = null;
    }
    removeSurfaceView();
  }

  @Override
  public void pause() {
    if (player != null) {
      player.setPlayWhenReady(false);
    }
  }

  @Override
  public void play() {
    if (player != null) {
      player.setPlayWhenReady(true);
    }
  }

  @Override
  public void stop() {
    pause();
    seekToTime(0);
  }

  /**
   * @return the playback position in the current window, in milliseconds.
   */
  @Override
  public int currentTime() {
    return player == null ? 0 : (int) player.getCurrentPosition();
  }

  /**
   * @return the duration of the current window in milliseconds, or C.TIME_UNSET if the duration is not known.
   */
  @Override
  public int duration() {
    return (player == null) ? 0 : (int) player.getDuration();
  }

  @Override
  public int buffer() {
    return player == null ? 0 : player.getBufferedPercentage();
  }

  @Override
  public void seekToTime(int timeInMillis) {
    if (player == null) {
      return;
    }
    long seekPosition = player.getDuration() == UNKNOWN_TIME ? 0
        : Math.min(Math.max(0, timeInMillis), duration());
    int seekStartTime = (int) player.getCurrentPosition();
    player.seekTo(seekPosition);
    setChanged();
    notifyObservers(new OoyalaNotification(OoyalaPlayer.SEEK_STARTED_NOTIFICATION_NAME,
      new SeekInfo(seekStartTime, timeInMillis, player.getDuration())));

    seekCompleteObserver.subscribe(player);
  }

  @Override
  public void setClosedCaptionsLanguage(String language) {
    liveClosedCaptionsEnabled = OoyalaPlayer.LIVE_CLOSED_CAPIONS_LANGUAGE.equals(language);
  }

  @Override
  public boolean isLiveClosedCaptionsAvailable() {
    return liveClosedCaptionsObserved;
  }

  /**
   * @return true or false, whether playback will proceed when ready.
   */
  @Override
  public boolean isPlaying() {
    return player != null && player.getPlayWhenReady();
  }

  @Override
  public boolean seekable() {
    return player != null;
  }

  @Override
  public void setVolume(float volume) {
    player.setVolume(volume);
  }

  /*
   * SimpleExoPlayer EventListener
   */

  @Override
  public void onLoadingChanged(boolean isLoading) {
    if (isLoading) {
      DebugMode.logD(TAG, "Load started");
      setChanged();
      notifyObservers(new OoyalaNotification(OoyalaPlayer.BUFFERING_STARTED_NOTIFICATION_NAME));
    } else {
      DebugMode.logD(TAG, "Load completed");
      setChanged();
      notifyObservers(new OoyalaNotification(OoyalaPlayer.BUFFERING_COMPLETED_NOTIFICATION_NAME));
    }
  }

  @Override
  public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
    DebugMode.logD(TAG, "SimpleExoPlayer.OnPlayerStateChanged, playWhenReady " + playWhenReady
        + " state " + playbackState);
    switch (playbackState) {
      case Player.STATE_BUFFERING:
        setState(OoyalaPlayer.State.LOADING);
        break;
      case Player.STATE_ENDED:
        setState(OoyalaPlayer.State.COMPLETED);
        break;
      case Player.STATE_IDLE:
        break;
      case Player.STATE_READY:
        if (playWhenReady) {
          initPlayStarted = true;
          startPlayheadTimer();
          setState(OoyalaPlayer.State.PLAYING);
        } else {
          if (initPlayStarted) {
            stopPlayheadTimer();
            setState(OoyalaPlayer.State.PAUSED);
          } else {
            setState(OoyalaPlayer.State.READY);
          }
        }
        break;
      default:
        break;
    }
  }

  @Override
  public void onRepeatModeChanged(int repeatMode) {
    DebugMode.logD(TAG, "onRepeatModeChanged repeatMode " + repeatMode);
  }

  @Override
  public void onTimelineChanged(Timeline timeline, Object manifest) {
    DebugMode.logD(TAG, "onTimelineChanged timeline " + timeline.toString());
  }

  @Override
  public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    DebugMode.logD(TAG, "onTracksChanged");
  }

  @Override
  public void onPlayerError(ExoPlaybackException error) {
    DebugMode.logD(TAG, "ExoPlaybackException occurred " + error.getMessage());
  }

  @Override
  public void onPositionDiscontinuity() {
    DebugMode.logD(TAG, "onPositionDiscontinuity");
  }

  @Override
  public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
    DebugMode.logD(TAG, "onPlaybackParametersChanged playbackParameters " + playbackParameters.toString());
  }

  // ExtractorMediaSource event listener
  @Override
  public void onLoadError(IOException error) {
    DebugMode.logE(TAG, "Load error " + error.getMessage());
    setError(new OoyalaException(OoyalaException.OoyalaErrorCode.ERROR_DOWNLOAD_FAILURE, "Load error:" + error.getMessage()));
    setState(OoyalaPlayer.State.ERROR);
  }

  // DRM session manager event listener
  @Override
  public void onDrmKeysLoaded() {
    DebugMode.logD(TAG, "DRM keys loaded");
  }

  @Override
  public void onDrmSessionManagerError(Exception error) {
    DebugMode.logE(TAG, String.format("DRM session manager error %s", error.toString()), error);
    setError(new OoyalaException(OoyalaException.OoyalaErrorCode.ERROR_DRM_GENERAL_FAILURE, "DRM failed:" + error.getMessage()));
    setState(OoyalaPlayer.State.ERROR);
  }

  @Override
  public void onDrmKeysRestored() {
    DebugMode.logD(TAG, "DRM keys restored");
  }

  @Override
  public void onDrmKeysRemoved() {
    DebugMode.logD(TAG, "DRM keys removed");
  }

  // Adaptive media source event listener
  @Override
  public void onLoadStarted(DataSpec dataSpec, int dataType, int trackType, Format trackFormat,
                            int trackSelectionReason, Object trackSelectionData,
                            long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs) {
    DebugMode.logD(TAG, "Load started " + EventLogger.getMediaSourceInfo(dataSpec, dataType, trackType,
        trackFormat, mediaStartTimeMs, mediaEndTimeMs, elapsedRealtimeMs));
    setChanged();
    notifyObservers(new OoyalaNotification(OoyalaPlayer.BUFFERING_STARTED_NOTIFICATION_NAME));
  }

  @Override
  public void onLoadCompleted(DataSpec dataSpec, int dataType, int trackType, Format trackFormat,
                              int trackSelectionReason, Object trackSelectionData,
                              long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs,
                              long loadDurationMs, long bytesLoaded) {
    DebugMode.logD(TAG, "Load completed " + EventLogger.getMediaSourceInfo(dataSpec, dataType, trackType,
        trackFormat, mediaStartTimeMs, mediaEndTimeMs, elapsedRealtimeMs)
        + String.format("loadDurationMs: %d, bytesLoaded: %d", loadDurationMs, bytesLoaded));
    setChanged();
    notifyObservers(new OoyalaNotification(OoyalaPlayer.BUFFERING_COMPLETED_NOTIFICATION_NAME));
  }

  @Override
  public void onLoadCanceled(DataSpec dataSpec, int dataType, int trackType, Format trackFormat,
                             int trackSelectionReason, Object trackSelectionData,
                             long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs,
                             long loadDurationMs, long bytesLoaded) {
    DebugMode.logD(TAG, "Load cancelled " + EventLogger.getMediaSourceInfo(dataSpec, dataType, trackType,
        trackFormat, mediaStartTimeMs, mediaEndTimeMs, elapsedRealtimeMs)
        + String.format("loadDurationMs: %d, bytesLoaded: %d", loadDurationMs, bytesLoaded));
  }

  @Override
  public void onLoadError(DataSpec dataSpec, int dataType, int trackType, Format trackFormat,
                          int trackSelectionReason, Object trackSelectionData,
                          long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs,
                          long loadDurationMs, long bytesLoaded, IOException error, boolean wasCanceled) {
    DebugMode.logE(TAG, "Load error " + EventLogger.getMediaSourceInfo(dataSpec, dataType, trackType,
        trackFormat, mediaStartTimeMs, mediaEndTimeMs, elapsedRealtimeMs)
        + String.format("loadDurationMs: %d, bytesLoaded: %d, error: %s",
        loadDurationMs, bytesLoaded, error.getMessage()), error);

    setError(new OoyalaException(OoyalaException.OoyalaErrorCode.ERROR_DOWNLOAD_FAILURE, "Load error:" + error.getMessage()));
    setState(OoyalaPlayer.State.ERROR);
  }

  @Override
  public void onUpstreamDiscarded(int trackType, long mediaStartTimeMs, long mediaEndTimeMs) {
    DebugMode.logD(TAG, "Upstream discarded trackType: " + trackType
        + String.format("mediaStartTime: %d, mediaEndTime: %d", mediaStartTimeMs, mediaEndTimeMs));
  }

  @Override
  public void onDownstreamFormatChanged(int trackType, Format trackFormat, int trackSelectionReason,
                                        Object trackSelectionData, long mediaTimeMs) {
    DebugMode.logD(TAG, "Downstream format changed trackType " + trackType
        + String.format("trackSelectionReason: %d, mediaTime: %d", trackSelectionReason, mediaTimeMs)
        + " bitrate: " + (trackFormat != null ? trackFormat.bitrate : "n/a"));

    if (trackFormat != null && lastSeenBitrate != trackFormat.bitrate) {
      DebugMode.logD(TAG, "New bitrate observed. Was:" + lastSeenBitrate + ", Now:" + trackFormat.bitrate);
      setChanged();
      notifyObservers(new OoyalaNotification(OoyalaPlayer.BITRATE_CHANGED_NOTIFICATION_NAME,
          new BitrateChangedNotificationInfo(lastSeenBitrate, trackFormat.bitrate)));
      lastSeenBitrate = trackFormat.bitrate;
    }
  }

  // Text renderer event listener
  @Override
  public void onCues(List<Cue> cues) {
    // First time we see live closed captions, we keep track and alert OoyalaPlayer
    if (!liveClosedCaptionsObserved) {
      liveClosedCaptionsObserved = true;
      setChanged();
      notifyObservers(new OoyalaNotification(OoyalaPlayer.LIVE_CC_AVAILABILITY_CHANGED_NOTIFICATION_NAME));
    }

    if (liveClosedCaptionsEnabled) {
      for (Cue c : cues) {
        if (c.text != null) {
          HashMap<String, String> data = new HashMap<>();
          data.put(OoyalaPlayer.CLOSED_CAPTION_TEXT, c.text.toString());
          OoyalaNotification notification = new OoyalaNotification(OoyalaPlayer.LIVE_CC_CHANGED_NOTIFICATION_NAME, data);
          setChanged();
          notifyObservers(notification);
        }
      }
    }
  }

  // Seek complete callback interface
  @Override
  public void onSeekCompleteCallback() {
    setChanged();
    notifyObservers(new OoyalaNotification(OoyalaPlayer.SEEK_COMPLETED_NOTIFICATION_NAME,
      new SeekInfo(0, player.getCurrentPosition(), player.getDuration())));
    seekCompleteObserver.unsubscribe();
  }

  @SuppressLint("HandlerLeak")
  private class LicenseHandler extends Handler {

    private File file;
    private DashDownloader.Listener listener;
    private String embedCode;

    public LicenseHandler(Looper looper, File file, DashDownloader.Listener listener, String embedCode) {
      super(looper);
      this.file = file;
      this.listener = listener;
      this.embedCode = embedCode;
    }

    @Override
    public void handleMessage(Message msg) {
      File folder = file.getParentFile();
      if (folder != null && folder.exists()) {
        DebugMode.logD(TAG, "Handle message " + msg.toString());
        storeLicense(msg.getData().getByteArray(LICENSE_KEY_ARRAY), folder, listener, embedCode);
      } else {
        DebugMode.logD(TAG, "License key doesn't store. The folder is null or doesn't exist." + msg.toString());
      }
    }
  }

  public void startLicenseRequest(final Context context, final String mpdUrl, File mpdFile, final String widevineServerUrl,
                                  final DashDownloader.Listener listener, final String embedCode) {
    this.context = context;
    final LicenseHandler licenseHandler = new LicenseHandler(context.getMainLooper(), mpdFile, listener, embedCode);

    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        try {
          HttpDataSource.Factory httpDataSourceFactory = buildHttpDataSourceFactory(BANDWIDTH_METER);
          DataSource dataSource = httpDataSourceFactory.createDataSource();
          DashManifest dashManifest = DashUtil.loadManifest(dataSource, mpdUrl);
          DrmInitData drmInitData = DashUtil.loadDrmInitData(dataSource, dashManifest.getPeriod(0));

          OfflineLicenseHelper offlineLicenseHelper = getOfflineLicenseHelper(widevineServerUrl);
          byte[] offlineLicenseKeySetId = new byte[0];
          if (offlineLicenseHelper != null) {
            offlineLicenseKeySetId = offlineLicenseHelper.downloadLicense(drmInitData);
          }
          Message msg = licenseHandler.obtainMessage();
          Bundle bundle = new Bundle();
          if (offlineLicenseKeySetId.length != 0) {
            bundle.putInt("Response", MSG_DOWNLOAD_COMPLETE);
          } else {
            bundle.putInt("Response", MSG_DOWNLOAD_FAILED);
          }

          bundle.putByteArray(LICENSE_KEY_ARRAY, offlineLicenseKeySetId);
          msg.setData(bundle);
          licenseHandler.sendMessage(msg);

        } catch (InterruptedException | IOException | DrmSession.DrmSessionException e) {
          DebugMode.logE(TAG, "DRM Session exception : " + e.toString());
          setError(new OoyalaException(OoyalaException.OoyalaErrorCode.ERROR_PLAYBACK_FAILED, "DRM Session exception"));
          setState(OoyalaPlayer.State.ERROR);
          listener.onError(embedCode,e);
        }
      }
    };

    new Thread(runnable).start();
  }

  public void storeLicense(byte[] bytes, File folder, DashDownloader.Listener listener, String embedCode) {
    long currentTime = System.currentTimeMillis() / DateUtils.SECOND_IN_MILLIS;
    File licenseFile = new File(folder, DashDownloader.LICENSE_FILE);
    HashMap<String, String> keys = new HashMap<>();
    String encodedBytes = Base64.encodeToString(bytes, Base64.DEFAULT);

    keys.put(LICENSE_KEYS, encodedBytes);
    keys.put(DashDownloader.LICENSE_CURRENT_TIME, String.valueOf(currentTime));

    try {
      Utils.objectToFile(licenseFile, keys);
      if (listener != null) {
        listener.onCompletion(embedCode);
      }
    } catch (IOException ex) {
      if (listener != null) {
        listener.onError(embedCode,ex);
      }
    }
  }

  public byte[] loadLicense(File folder) {
    File licenseFile = new File(folder, DashDownloader.LICENSE_FILE);
    Map<String, String> keys = Utils.mapFromFile(licenseFile);
    if (keys != null) {
      String encryptedKeys = keys.get(LICENSE_KEYS);
      byte[] decryptedKeys = Base64.decode(encryptedKeys, Base64.DEFAULT);
      return decryptedKeys;
    }
    return null;
  }

  private OfflineLicenseHelper getOfflineLicenseHelper(String widevineServerUrl) {
    HttpMediaDrmCallback drmCallback = new HttpMediaDrmCallback(widevineServerUrl,
        buildHttpDataSourceFactory(true));
    try {
      return new OfflineLicenseHelper<>(FrameworkMediaDrm.newInstance(C.WIDEVINE_UUID), drmCallback, null);
    } catch (UnsupportedDrmException e) {
      DebugMode.logE(TAG, "Unsupported DRM exception : " + e.toString());
      setError(new OoyalaException(OoyalaException.OoyalaErrorCode.ERROR_PLAYBACK_FAILED, "Unsupported DRM exception"));
      setState(OoyalaPlayer.State.ERROR);
      return null;
    }
  }

  private boolean isOfflineMode() {
    return offlineVideoFolder != null && offlineVideoFolder.exists();
  }
}

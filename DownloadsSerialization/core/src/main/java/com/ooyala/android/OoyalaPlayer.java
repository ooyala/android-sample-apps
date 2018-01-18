package com.ooyala.android;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.ooyala.android.AdvertisingIdUtils.IAdvertisingIdListener;
import com.ooyala.android.AuthHeartbeat.OnAuthHeartbeatErrorListener;
import com.ooyala.android.Environment.EnvironmentType;
import com.ooyala.android.OoyalaException.OoyalaErrorCode;
import com.ooyala.android.ads.ooyala.OoyalaAdPlayer;
import com.ooyala.android.ads.ooyala.OoyalaAdSpot;
import com.ooyala.android.ads.vast.VASTAdPlayer;
import com.ooyala.android.ads.vast.VASTAdSpot;
import com.ooyala.android.ads.vast.VMAPAdSpot;
import com.ooyala.android.analytics.AnalyticsPluginInterface;
import com.ooyala.android.analytics.AnalyticsPluginManager;
import com.ooyala.android.analytics.AnalyticsPluginManagerInterface;
import com.ooyala.android.analytics.IqConfiguration;
import com.ooyala.android.captions.CaptionUtils;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.configuration.ReadonlyOptionsInterface;
import com.ooyala.android.item.Caption;
import com.ooyala.android.item.Channel;
import com.ooyala.android.item.ChannelSet;
import com.ooyala.android.item.ContentItem;
import com.ooyala.android.item.OoyalaManagedAdSpot;
import com.ooyala.android.item.UnbundledVideo;
import com.ooyala.android.item.Video;
import com.ooyala.android.player.AdMoviePlayer;
import com.ooyala.android.player.DefaultPlayerFactory;
import com.ooyala.android.player.MoviePlayer;
import com.ooyala.android.player.Player;
import com.ooyala.android.player.PlayerFactory;
import com.ooyala.android.player.PlayerInterface;
import com.ooyala.android.player.exoplayer.ExoPlayerFactory;
import com.ooyala.android.plugin.AdPluginInterface;
import com.ooyala.android.ui.AbstractOoyalaPlayerLayoutController;
import com.ooyala.android.ui.LayoutController;
import com.ooyala.android.util.DebugMode;

import org.json.JSONObject;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

/**
 * The OoyalaPlayer is the heart of the playback system.
 * Use it to configure and control asset playback,
 * and to be aware of playback state changes.
 */
public class OoyalaPlayer extends Observable implements Observer,
    OnAuthHeartbeatErrorListener, AdPluginManagerInterface, AnalyticsPluginManagerInterface {
  /**
   * NOTE[jigish] do NOT change the name or location of this variable without
   * changing pub_release.sh
   */
  static final String SDK_VERSION = "v4.29.0_RC8";
  static final String API_VERSION = "1";
  public static final String PREFERENCES_NAME = "com.ooyala.android_preferences";

  public static enum ActionAtEnd {
    CONTINUE, PAUSE, STOP, RESET
  };

  public static enum State {
    INIT, LOADING, READY, PLAYING, PAUSED, COMPLETED, SUSPENDED, ERROR
  };

  public static enum DesiredState {
    DESIRED_PLAY,DESIRED_PAUSE
  };
  public static enum SeekStyle {
    NONE, BASIC, ENHANCED
  };

  static enum InitPlayState {
    NONE, PluginQueried, ContentPlayed
  };
  /**
   * Used by previousVideo and nextVideo. When passed to them, it will cause the
   * video to be played after it is set.
   */
  public static final int DO_PLAY = 0;

  /**
   * Used by previousVideo and nextVideo. When passed to them, it will cause the
   * video to be paused after it is set.
   */
  public static final int DO_PAUSE = 1;

  /**
   * OoyalaNotification name when content tree Player API has been completed.
   * No "data" is passed in the OoyalaNotification.
   * This notification is still usable, but it is preferred to simply listen to OoyalaPlayer.CURRENT_ITEM_CHANGED_NOTIFICATION_NAME.
   */
  public static final String CONTENT_TREE_READY_NOTIFICATION_NAME = "contentTreeReady";

  /**
   * OoyalaNotification name when authorization Player API has been completed.
   * No "data" is passed in the OoyalaNotification.
   * This notification is still usable, but it is preferred to simply listen to OoyalaPlayer.CURRENT_ITEM_CHANGED_NOTIFICATION_NAME.
   */
  public static final String AUTHORIZATION_READY_NOTIFICATION_NAME = "authorizationReady";

  /**
   * OoyalaNotification name when metadata Player API has been completed.
   * No "data" is passed in the OoyalaNotification.
   * This notification is still usable, but it is preferred to simply listen to OoyalaPlayer.CURRENT_ITEM_CHANGED_NOTIFICATION_NAME.
   */
  public static final String METADATA_READY_NOTIFICATION_NAME = "metadataReady";

  /**
   * OoyalaNotification name when all Player API requests have been completed.
   * No "data" is passed in the OoyalaNotification.
   * At this point, you can call OoyalaPlayer.getCurrentItem() to see all up-to-date information.
   * about the item video will be played
   */
  public static final String CURRENT_ITEM_CHANGED_NOTIFICATION_NAME = "currentItemChanged";


  /**
   * OoyalaNotification name when the time has changed during video rendering.
   * No "data" is passed in the OoyalaNotification.
   */
  public static final String TIME_CHANGED_NOTIFICATION_NAME = "timeChanged";
  public static final String STATE_CHANGED_NOTIFICATION_NAME = "stateChanged";
  public static final String DESIRED_STATE_CHANGED_NOTIFICATION_NAME = "desiredStateChanged";
  public static final String BUFFER_CHANGED_NOTIFICATION_NAME = "bufferChanged";
  public static final String CLOSED_CAPTIONS_LANGUAGE_CHANGED_NAME = "closedCaptionsLanguageChanged";

  public static final String ERROR_NOTIFICATION_NAME = "error";
  public static final String PLAY_STARTED_NOTIFICATION_NAME = "playStarted";
  public static final String PLAY_COMPLETED_NOTIFICATION_NAME = "playCompleted";

  /**
   * OoyalaNotification name when a video seek has started.
   * The "data" in the OoyalaNotification will be a SeekInfo object, which contains the time a seek starts and completes.
   * For this notification the SeekInfo.getSeekStart() will always return 0.
   */
  public static final String SEEK_COMPLETED_NOTIFICATION_NAME = "seekCompleted";

  /**
   * OoyalaNotification name when a video seek has started.
   * The "data" in the OoyalaNotification will be a SeekInfo object, which contains the time a seek starts and completes.
   * For this notification the SeekInfo.getSeekStart() will always return 0.
   */
  public static final String SEEK_STARTED_NOTIFICATION_NAME = "seekStarted";
  public static final String CC_STYLING_CHANGED_NOTIFICATION_NAME = "ccStylingChanged";
  public static final String AD_OVERLAY_NOTIFICATION_NAME = "adOverlay";
  public static final String AD_STARTED_NOTIFICATION_NAME = "adStarted";
  public static final String AD_COMPLETED_NOTIFICATION_NAME = "adCompleted";
  public static final String AD_POD_STARTED_NOTIFICATION_NAME = "adPodStarted";
  public static final String AD_POD_COMPLETED_NOTIFICATION_NAME = "adPodCompleted";
  public static final String AD_SKIPPED_NOTIFICATION_NAME = "adSkipped";
  public static final String AD_ERROR_NOTIFICATION_NAME = "adError";

  /**
   * OoyalaNotification name when the player begins downloading a video chunk.
   * No "data" is passed in the OoyalaNotification.
   * This notification is only supported with our ExoPlayer integration.
   */
  public static final String BUFFERING_STARTED_NOTIFICATION_NAME = "bufferingStarted";

  /**
   * OoyalaNotification name when the player completes downloading a video chunk.
   * No "data" is passed in the OoyalaNotification.
   * This notification is only supported with our ExoPlayer integration.
   */
  public static final String BUFFERING_COMPLETED_NOTIFICATION_NAME = "bufferingCompleted";

  /**
   * @deprecated OoyalaNotification name when the player begins Playready DRM Rights Acquisition.
   * No "data" is passed in the OoyalaNotification.
   * This notification is only supported with our deprecated VisualOn/SecurePlayer integration.
   */
  @Deprecated
  public static final String DRM_RIGHTS_ACQUISITION_STARTED_NOTIFICATION_NAME = "drmRightsAcquireStarted";

  /**
   * @deprecated OoyalaNotification name when the player completes Playready DRM Rights Acquisition.
   * No "data" is passed in the OoyalaNotification.
   * This notification is only supported with our deprecated VisualOn/SecurePlayer integration.
   */
  @Deprecated
  public static final String DRM_RIGHTS_ACQUISITION_COMPLETED_NOTIFICATION_NAME = "drmRightsAcquireCompleted";

  /**
   * OoyalaNotification name when the player observes Live Closed Captions within the live stream.
   * No "data" is passed in the OoyalaNotification.
   * This notification is only supported with our ExoPlayer integration.
   */
  public static final String LIVE_CC_AVAILABILITY_CHANGED_NOTIFICATION_NAME = "liveCCAvailabilityChanged";

  /**
   * OoyalaNotification name when the live closed captions text has changed.
   * The "data" in the OoyalaNotification will be a HashMap, which has a key of OoyalaPlayer.CLOSED_CAPTION_TEXT,
   * and a value of the text that needs to be displayed.
   */
  public static final String LIVE_CC_CHANGED_NOTIFICATION_NAME = "liveCCChanged";

  /**
   * OoyalaNotification name when the VOD closed captions text has changed.
   * The "data" is a Captions object which contains text and other information.
   */
  public static final String CC_CHANGED_NOTIFICATION_NAME = "ccChanged";

  /**
   * OoyalaNotification name when the embed code/content ID has changed.
   * No "data" is passed in the OoyalaNotification.
   */
  public static final String EMBED_CODE_SET_NOTIFICATION_NAME = "embedCodeSet";

  /**
   * OoyalaNotification name when the observed Bitrate is changing.
   * The "data" in the OoyalaNotification will be a BitrateChangedNotificationInfo object.
   * This notification is only supported with our ExoPlayer integration.
   */
  public static final String BITRATE_CHANGED_NOTIFICATION_NAME = "bitrateChanged";

  public static final String NOTIFICATION_NAME = "name";
  public static final String CLOSED_CAPTION_TEXT = "caption";

  public enum ContentOrAdType {
    MainContent,
    PreRollAd,
    MidRollAd,
    PostRollAd,
  }

  public static final String LIVE_CLOSED_CAPIONS_LANGUAGE = "Closed Captions";

  /**
   * If set to true, this will allow HLS streams regardless of the Android
   * version. WARNING: Ooyala's internal testing has shown that Android 3.x HLS
   * support is unstable. Android 2.x does not support HLS at all. If set to
   * false, HLS streams will only be allowed on Android 4.x and above
   */
  public static boolean enableHLS = false;

  /**
   * If set to true, this will allow Higher Resolution HLS streams regardless of
   * the Android version. WARNING: Ooyala's internal testing has shown that
   * Android 3.x HLS support is unstable. Android 2.x does not support HLS at
   * all. If set to false, HLS streams will only be allowed on Android 4.x and
   * above. Also this will internally make Ooyala's APIs think that the device
   * is iPad and may have undesired results.
   */
  public static boolean enableHighResHLS = false;

  /**
   * @deprecated If set to true, HLS content will be played using our custom HLS
   * implementation rather than native the Android one. To achieve HLS playback
   * on Android versions before 4, set this to true and also set the enableHLS
   * flag to true. This will have no affect unless the custom playback engine is
   * linked and loaded in addition to the standard Ooyala Android SDK.
   * This was only used for VisualOn integrations
   */
  @Deprecated
  public static boolean enableCustomHLSPlayer = false;

  /**
   * @deprecated If set to true, Smooth and HLS content (both Clear and Playready-encrypted) will be allowed
   * using our custom Playready implementation rather than native the Android one. This will have no
   * affect unless the custom playback engine is linked and loaded in
   * addition to the standard Ooyala Android SDK.
   * This was only used for VisualOn integrations
   */
  @Deprecated
  public static boolean enableCustomPlayreadyPlayer = false;

  /**
   * @deprecated If set to true, DRM enabled players will perform DRM requests in a debug environment if available.
   * This was used for the VisualOn integration
   */
  @Deprecated
  public static boolean enableDebugDRMPlayback = false;

  private static final String TAG = OoyalaPlayer.class.getName();

  private final Handler _handler = new Handler();

  //Helper Classes
  private PlayerAPIClient _playerAPIClient = null;
  private ServerTaskManager serverTaskManager;
  AuthTokenManager authTokenManager;
  protected MoviePlayerSelector _playerSelector;
  private IQAnalyticsPlugin iqAnalyticsPlugin = null;
  private IqConfiguration iqConfiguration;
  private AuthHeartbeat _authHeartbeat;
  OoyalaPlayerContextSwitcher contextSwitcher;
  private OoyalaPlayerObserverHandler observerHandler;
  private OoyalaPlayerPlaybackInteractions playbackInteractions;
  private OoyalaPlayerSessionIDManager sessionIDManager;
  private AnalyticsPluginManager analyticsPluginManager;

  //Video Information
  private Video _currentItem = null;
  private ContentItem _rootItem = null;
  private OoyalaException _error = null;
  private Caption previousCaption;

  //Configurations
  private ActionAtEnd _actionAtEnd;
  private Options _options;
  private EmbedTokenGenerator _embedTokenGenerator = null;
  private PlayerInfo _playerInfo;
  private String _customDRMData = null;

  private String pcode;
  private PlayerDomain domain;

  //State Information
  OoyalaPlayerStateManager stateManager = null;
  private long _suspendTime = System.currentTimeMillis();


  private String _lastAccountId = null;

  //Player Classes
  private final Map<Class<? extends OoyalaManagedAdSpot>, Class<? extends AdMoviePlayer>> _adPlayers;
  private MoviePlayer _player = null;
  private OoyalaManagedAdsPlugin _managedAdsPlugin = null;

  //UI Classes
  public LayoutController _layoutController = null;
  boolean showTVRatingAfterAd;
  private ImageView _promoImageView = null;
  /**
   * Initialize an OoyalaPlayer with the given parameters
   *
   * @param pcode
   *          Your Provider Code
   * @param domain
   *          Your Embed Domain
   */
  public OoyalaPlayer(String pcode, PlayerDomain domain) {
    this(pcode, domain, null, null);
  }

  /**
   * Initialize an OoyalaPlayer with the given parameters
   *
   * @param pcode
   *          Your Provider Code
   * @param domain
   *          Your Embed Domain
   * @param options
   *          Extra settings
   */
  public OoyalaPlayer(String pcode, PlayerDomain domain, Options options) {
    this(pcode, domain, null, options);
  }

  /**
   * Initialize an OoyalaPlayer with the given parameters
   *
   * @param pcode
   *          Your Provider Code, must be non-null.
   * @param domain
   *          Your Embed Domain, must be non-null.
   * @param generator
   *          An embedTokenGenerator used to sign SAS requests, can be null.
   * @param options
   *          Extra settings, can be null in which case default values are used.
   */
  public OoyalaPlayer(String pcode, PlayerDomain domain, EmbedTokenGenerator generator, Options options) {
    _actionAtEnd = ActionAtEnd.CONTINUE;

    _options = options == null ? new Options.Builder().build() : options;
    _options.logOptionsData();

    this.pcode = pcode;
    this.domain = domain;
    _playerAPIClient = new PlayerAPIClient(pcode, domain, generator, _options);
    _embedTokenGenerator = generator;

    // Initialize Ad Players
    _adPlayers = new HashMap<Class<? extends OoyalaManagedAdSpot>, Class<? extends AdMoviePlayer>>();
    registerAdPlayer(OoyalaAdSpot.class, OoyalaAdPlayer.class);
    registerAdPlayer(VASTAdSpot.class, VASTAdPlayer.class);
    registerAdPlayer(VMAPAdSpot.class, VASTAdPlayer.class);

    // Initialize third party plugin managers
    AdPluginManager adManager = new AdPluginManager(this);
    _managedAdsPlugin = new OoyalaManagedAdsPlugin(this);
    if (!_options.getDisableVASTOoyalaAds()) {
      adManager.registerPlugin(_managedAdsPlugin);
    }

    // register player factories;
    _playerSelector = new MoviePlayerSelector();
    if (null == _options || _options.getUseExoPlayer()) {
      _playerSelector.registerPlayerFactory(new ExoPlayerFactory(120));
    } else if(_options.getUseAdobePlayer()) {
      try
      {
        Class adobeMediaPlayerFactory = Class.forName("com.android.ooyala.adobeandroidlibrary.AdobeMediaPlayerFactory");
        Constructor adobeConstructor = adobeMediaPlayerFactory.getConstructor(Integer.TYPE);
        PlayerFactory adobePlayerFactory=   (PlayerFactory)adobeConstructor.newInstance(120);
        _playerSelector.registerPlayerFactory(adobePlayerFactory);
      }
      catch (Exception ex)
      {
        DebugMode.logE(TAG,"Failed.");
        ex.printStackTrace ();
      }

    } else {
      _playerSelector.registerPlayerFactory(new DefaultPlayerFactory());
    }

    _playerInfo = _options.getPlayerInfo();
    if (_playerInfo == null) {
      _playerInfo = new DefaultPlayerInfo(_playerSelector.getSupportedFormats(), null);
    }
    serverTaskManager = new ServerTaskManager(_playerAPIClient, _playerInfo, _options.getConnectionTimeoutInMillisecond());

    stateManager = new OoyalaPlayerStateManager(this);

    contextSwitcher = new OoyalaPlayerContextSwitcher(this, adManager);

    observerHandler = new OoyalaPlayerObserverHandler(this);

    playbackInteractions = new OoyalaPlayerPlaybackInteractions(this);

    sessionIDManager = new OoyalaPlayerSessionIDManager();

    analyticsPluginManager = new AnalyticsPluginManager(this);

    iqAnalyticsPlugin = new IQAnalyticsPlugin(this,_options.getIqConfiguration() );
    analyticsPluginManager.registerPlugin(iqAnalyticsPlugin);

    DebugMode.logI(this.getClass().getName(),
            "Ooyala SDK Version: " + OoyalaPlayer.getVersion());
  }

  /**
   * Reinitializes the player with a new embed code. If embedCode is null, this
   * method has no effect and just returns false.
   *
   * @param embedCode
   * @return true if the embed code was successfully set, false if not.
   */
  public boolean setEmbedCode(String embedCode) {
    return setEmbedCodeWithAdSetCode(embedCode, null);
  }

  /**
   * Reinitializes the player with a new set of embed codes. If embedCodes is
   * null, this method has no effect and just returns false.
   *
   * @param embedCodes
   * @return true if the embed codes were successfully set, false if not.
   */
  public boolean setEmbedCodes(List<String> embedCodes) {
    return setEmbedCodesWithAdSetCode(embedCodes, null);
  }

  /**
   * Reinitializes the player with a new embed code. If embedCode is null, this
   * method has no effect and just returns false. An ad set can be dynamically
   * associated using the adSetCode param.
   *
   * @param embedCode should not be null.
   * @param adSetCode can be null.
   * @return true if the embed code was successfully set, false if not.
   */
  public boolean setEmbedCodeWithAdSetCode(String embedCode, String adSetCode) {
    if (embedCode == null) {
      return false;
    }
    List<String> embeds = new ArrayList<String>();
    embeds.add(embedCode);
    return setEmbedCodesWithAdSetCode(embeds, adSetCode);
  }

  /**
   * Reinitializes the player with a new set of embed codes. If embedCodes is
   * null, this method has no effect and just returns false. An ad set can be
   * dynamically associated using the adSetCode param.
   *
   * @param embedCodes should not be null.
   * @param adSetCode can be null.
   * @return true if the embed codes were successfully set, false if not.
   */
  public boolean setEmbedCodesWithAdSetCode(List<String> embedCodes,
                                            final String adSetCode) {
    sendNotification(EMBED_CODE_SET_NOTIFICATION_NAME);
    if (embedCodes == null || embedCodes.isEmpty()) {
      return false;
    }

    prepareForNewContent();

    serverTaskManager.fetchVideo(embedCodes, adSetCode, new ServerTaskManager.ContentItemCallback() {
      @Override
      public void callback(ContentItem item, OoyalaException error) {
        if (error != null) {
          onError(error, "Error while fetching video");
          return;
        } else if (item == null) {
          onError(new OoyalaException(OoyalaErrorCode.ERROR_CONTENT_TREE_INVALID), "Video could not be found");
          return;
        }
        _rootItem = item;
        setCurrentItem( _rootItem.firstVideo() );
        _handler.post(new Runnable() {
          @Override
          public void run() {
            onCurrentItemChangeComplete();
          }
        });
      }
    });
    return true;
  }

  /**
   * Reinitializes the player with a new external ID. If externalId is null,
   * this method has no effect and just returns false.
   *
   * @param externalId
   * @return true if the external ID was successfully set, false if not.
   */
  public boolean setExternalId(String externalId) {
    if (externalId == null) {
      return false;
    }
    List<String> ids = new ArrayList<String>();
    ids.add(externalId);
    return setExternalIds(ids);
  }

  /**
   * Reinitializes the player with a new set of external IDs. If externalIds is
   * null, this method has no effect and just returns false.
   *
   * @param externalIds
   * @return true if the external IDs were successfully set, false if not.
   */
  public boolean setExternalIds(List<String> externalIds) {
    if (externalIds == null || externalIds.isEmpty()) {
      return false;
    }

    prepareForNewContent();

    serverTaskManager.fetchVideoByExternalId(externalIds, null, new ServerTaskManager.ContentItemCallback() {
      @Override
      public void callback(ContentItem item, OoyalaException error) {
        if (error != null) {
          onError(error, "Error while fetching video");
          return;
        } else if (item == null) {
          onError(new OoyalaException(OoyalaErrorCode.ERROR_CONTENT_TREE_INVALID), "Video could not be found");
          return;
        }
        _rootItem = item;
        setCurrentItem( _rootItem.firstVideo() );
        _handler.post(new Runnable() {
          @Override
          public void run() {
            onCurrentItemChangeComplete();
          }
        });
      }
    });
    return true;
  }



  public boolean setUnbundledVideo( UnbundledVideo unbundledVideo ) {
    if (unbundledVideo == null) {
      return false;
    }

    prepareForNewContent();

    //Convert the Unbundled Video to a Video object OoyalaPlayer recognizes
    Video video = new Video( unbundledVideo );
    if (video == null) {
      cleanupPlayers();
      return false;
    }
    setCurrentItem( video );

    //Unbundled doesn't do authorization so we don't get UserInfo that we can pass to IQ
    //Create an empty one until we support passing it in
    authTokenManager.setUserInfo(new UserInfo(new JSONObject()));

    return changeCurrentItemToUnbundledVideo();
  }

  private void setCurrentItem( Video video ) {
    _currentItem = video;
    previousCaption = null;
  }

  /**
   * Set the current video in a channel if the video is present.
   *
   * @param embedCode
   * @return true if the change was successful, false if not
   */
  public boolean changeCurrentItem(String embedCode) {
    if (_rootItem == null) {
      return false;
    }
    return changeCurrentItem(_rootItem.videoFromEmbedCode(embedCode, _currentItem), null);
  }

  /**
   * Set the current video in a channel if the video is present.
   *
   * @param video should not be null.
   * @param adSetCode can be null.
   * @return true if the change was successful, false if not
   */
  public boolean changeCurrentItem( Video video, String adSetCode ) {
    if (video == null) {
      cleanupPlayers();
      return false;
    }

    //This is not a full reset, but a "switch to another video". DesiredState stays the same
    prepareForNewContentWithoutResettingDesiredState();

    setCurrentItem( video );
    serverTaskManager.fetchVideoAuthAndMetadata(video, new ServerTaskCallback() {
      @Override
      public void callback(boolean success, OoyalaException error) {
        if (error != null) {
          onError(error, "content tree failed");
          return;
        }

        _handler.post(new Runnable() {
          @Override
          public void run() {
            onCurrentItemChangeComplete();
          }
        });
      }
    });

    sendNotification(AUTHORIZATION_READY_NOTIFICATION_NAME);
    return true;
  }


  private boolean changeCurrentItemToUnbundledVideo() {
    sendNotification(CURRENT_ITEM_CHANGED_NOTIFICATION_NAME);
    if (!contextSwitcher.processAdModes(AdMode.ContentChanged, 0)) {
      contextSwitcher.switchToContent(false);
    }
    return true;
  }

  /**
   * Reauthorize the currentItem, which would refresh the auth_token.
   */
  public void reauthorizeCurrentItem(ServerTaskCallback callback) {
    serverTaskManager.reauthorize(_currentItem, callback);
  }


  /**
   * this is getting called after content tree/auth/metadata/playback info are all fetched.
   */
  private void onCurrentItemChangeComplete() {

    if (null == _currentItem) {
      onError(new OoyalaException(OoyalaErrorCode.ERROR_INTERNAL_ANDROID), "The current video instance became null");
      return;
    }

    sendNotification(CONTENT_TREE_READY_NOTIFICATION_NAME);
    sendNotification(AUTHORIZATION_READY_NOTIFICATION_NAME);
    sendNotification(METADATA_READY_NOTIFICATION_NAME);
    sendNotification(CURRENT_ITEM_CHANGED_NOTIFICATION_NAME);

    if (!_currentItem.getAssetPCode().equals(this.pcode)) {
      if (!_options.getBypassPCodeMatching()) {
        onError(OoyalaException.getPCodeMatchError(this.getClass().getSimpleName(), this.pcode, _currentItem.getAssetPCode()), null);
        return;
      }
      else {
        DebugMode.logE(this.getClass().toString(),
            "Provided PCode and Embed Code owner do not match! Provided PCode: " + this.pcode
            + " Embed Code Owner: " + _currentItem.getAssetPCode());
      }
    }

    if (!_currentItem.isAuthorized()) {
      onError(OoyalaException.getAuthError(this.getClass().getSimpleName(), _currentItem), null);
      return;
    }

    startHeartbeatIfNecessary();
    initializeAnalytics();
    contextSwitcher.startVideoWorkflow(_currentItem);
  }

  private void startHeartbeatIfNecessary() {
    if (_currentItem != null && _currentItem.isHeartbeatRequired() && _authHeartbeat == null) {
      _authHeartbeat = new AuthHeartbeat(_playerAPIClient, authTokenManager, _currentItem.getEmbedCode());
      _authHeartbeat.setAuthHeartbeatErrorListener(this);
      _authHeartbeat.start();
    }
  }

  private void stopHeartbeatIfNecessary() {
    if (_authHeartbeat != null) {
      _suspendTime = System.currentTimeMillis();
      _authHeartbeat.stop();
      _authHeartbeat = null;
    }
  }


  @Override
  public void onAuthHeartbeatError(OoyalaException error) {
    cleanupPlayers();
    onError(error, null);
  }

  /**
   * set the analytics tags
   *
   * @param tags
   *          the list of tags to set
   */
  public void setCustomAnalyticsTags(List<String> tags) {
    DebugMode.logE(TAG, "Trying to set Custom Analytics Tags, even though they are not supported anymore");
//    if (iqAnalyticsPlugin != null) {
//      iqAnalyticsPlugin.setTags(tags);
//    }
  }

  private void initializeAnalytics() {
    // If analytics is uninitialized, OR
    // If has account ID that was different than before, OR
    // If no account ID, but last time there _was_ an account id, we need to
    // re-initialize


  }

  /**
   * Reset the Player to the initial state, generally when a new video is requested to play
   */
  private void prepareForNewContent() {
    stateManager.resetState();
    playbackInteractions.resetState();
    _currentItem = null;
    cleanupPlayers();
    getAdPluginManager().resetManager();
    authTokenManager.clearAuthTokenIfExpired();
    serverTaskManager.cancelAll();
    sessionIDManager.regenerateContentSessionId();
  }

  /**
   * This is not a full reset, but a "switch to another video". DesiredState stays the same
   * Used specifically when calling changeCurrentItem - When we switch between videos in a Channel
   * If channels are removed, we can remove this method
   */
  private void prepareForNewContentWithoutResettingDesiredState() {
    stateManager.setState(State.LOADING);
    stateManager.setInitPlayState(InitPlayState.NONE);

    playbackInteractions.resetState();
    _currentItem = null;
    cleanupPlayers();
    getAdPluginManager().resetManager();
    serverTaskManager.cancelAll();
    sessionIDManager.regenerateContentSessionId();
  }

  /**
   * This will release all the resources held by player. It gives more control
   * to developers for cleaning up when they want.
   * Recommendation make "player = null" after you call release
   */
  public void release() {
    cleanupPlayers();
  }

  void cleanupPlayers() {
    stopHeartbeatIfNecessary();
    cleanupPlayer(_player);

    //TEMPORARY - until contentPlayer is solely in context switcher
    contextSwitcher.clearContentPlayer();

    _player = null;
    _layoutController.getLayout().removeAllViews();
    hidePromoImage();
  }

  private void cleanupPlayer(Player p) {
    if (p != null) {
      p.deleteObserver(this);
      p.destroy();
    }
  }

  /**
   * Get the current player, can be either content player or ad player
   *
   * @return the player
   */
  PlayerInterface currentPlayer() {
    return contextSwitcher.currentPlayer();
  }

  private AdPluginManager getAdPluginManager() {
    return contextSwitcher.getAdPluginManager();
  }


  //********** All Playback Interactions **************//

  /**
   * Pause the current video
   */
  public void pause() {
    playbackInteractions.pause();
  }


  public void play() {
    playbackInteractions.play();
  }

  /**
   * Synonym for seek.
   *
   * @param timeInMillis
   *          in milliseconds
   */
  public void setPlayheadTime(int timeInMillis) {
    playbackInteractions.seek(timeInMillis);
  }

  /**
   * Move the playhead to a new location in seconds with millisecond accuracy
   *
   * @param timeInMillis
   *          in milliseconds
   */
  public void seek(int timeInMillis) {
    playbackInteractions.seek(timeInMillis);
  }

  /**
   * Seek to the given percentage
   *
   * @param percent
   *          percent (between 0 and 100) to seek to
   */
  public void seekToPercent(int percent) {
    playbackInteractions.seekToPercent(percent);
  }

  /**
   * Play the current video with an initialTime
   *
   * @param initialTimeInMillis
   *          the time to start the video.
   */
  public void play(int initialTimeInMillis) {
    playbackInteractions.play();
    playbackInteractions.seek(initialTimeInMillis);
  }

  /**
   * Generally used as a "Soft Play" in the OoyalaPlayer.  Whenever a video player is created or resumed,
   * we check our DesiredState for if we should start playback or not.
   */
  void playIfDesired() {
    if (getDesiredState() == DesiredState.DESIRED_PLAY) {
      playbackInteractions.play();
    }
  }

  /**
   * Ignores Ad Players, and always returns the time from ContentPlayer (or queued seek time)
   * @return the time we should start the video in Cast mode
   */
  private int getCurrentPlayheadForCastMode() {
    return playbackInteractions.getCurrentPlayheadForCastMode();
  }

  /**
   * Checks if the content player has observed any in-stream closed captions (for example, CEA-608/708).
   * For some live streams, the player may only observe the captions are available much later after video starts playing back
   * @return true if in-stream closed captions were observed, false otherwise.
   */
  public boolean isLiveClosedCaptionsAvailable() {
    return playbackInteractions.isLiveClosedCaptionsAvailable();
  }

  /**
   * @deprecated For MP4 videos only - Return the bitrate that is most likely playing
   * This API is no longer supported. to get Bitrate information in Exoplayer, see BITRATE_CHANGED_NOTIFICATION_NAME
   * @return get the bitrate of the current item
   */
  @Deprecated
  @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
  public double getBitrate() {
    return -1;
  }

  /**
   * @return true if the current state is State.Playing, false otherwise
   */
  public boolean isPlaying() {
    return getState() == State.PLAYING;
  }

  /**
   * @return true if currently playing ad, false otherwise
   */
  public boolean isAdPlaying() {
    return isShowingAd();
  }

  /**
   * Get the current item's duration
   *
   * @return the duration in milliseconds
   */
  public int getDuration() {
    return playbackInteractions.getDuration();
  }

  /**
   * Get the current item's buffer percentage
   *
   * @return the buffer percentage (between 0 and 100 inclusive)
   */
  public int getBufferPercentage() {
    return playbackInteractions.getBufferPercentage();
  }

  /**
   * Find where the playhead is with millisecond accuracy
   *
   * @return time in milliseconds
   */
  public int getPlayheadTime() {
    return playbackInteractions.getPlayheadTime();
  }

  /**
   * Get the current item's playhead time as a percentage
   *
   * @return the playhead time percentage (between 0 and 100 inclusive)
   */
  public int getPlayheadPercentage() {
    return playbackInteractions.getPlayheadPercentage();
  }

  /**
   * Set the displayed closed captions language
   *
   * @param language
   *          2 letter country code of the language to display or nil to hide
   *          closed captions
   */
  public void setClosedCaptionsLanguage(String language) {
    playbackInteractions.setClosedCaptionsLanguage(language);
  }

  /**
   * Get the currently enabled closed captions language
   *
   * @return 2 letter country code of the language to display or nil to hide
   *          closed captions
   */
  public String getClosedCaptionsLanguage() {
    return playbackInteractions.getClosedCaptionsLanguage();
  }

  /**
   * Set whether ads played by this OoyalaPlayer are seekable (default is false)
   *
   * @param seekable
   *          true if seekable, false if not.
   */
  public void setAdsSeekable(boolean seekable) {
    _managedAdsPlugin.setSeekable(seekable);
  }


  /**
   * @return true if the current player is seekable, false if there is no
   *         current player or it is not seekable
   */
  //TODO: On major rev, change this API name to something more clear than "seekable"
  public boolean seekable() {
    return playbackInteractions.seekable();
  }
  /**
   * Set whether videos played by this OoyalaPlayer are seekable (default is
   * true)
   *
   * @param seekable
   *          true if seekable, false if not.
   */
  //TODO: On major rev, change this API name to something more clear than "seekable"
  public void setSeekable(boolean seekable) {
    playbackInteractions.setSeekable(seekable);
  }

  /**
   * The volume of the OoyalaPlayer, relative to the device's volume setting. set with setVolume
   * @return the volume of the OoyalaPlayer, from 1.0f (default, max), to 0.0f (muted)
   */
  public float getVolume() {
    return playbackInteractions.getVolume();
  }

  /**
   * The volume of the OoyalaPlayer, relative to the device's volume setting.
   * For example, if volume is 1.0 (default), the playback volume would be as loud as the device's Media volume.
   * The volume set here will affect Content, Ooyala, Pulse, and VAST ad playback.  It will not affect other ad managers.
   * This property can be changed at any point after the OoyalaPlayer is initialized.
   * @param volume The volume of the player, from 1.0f (default, max), to 0.0f (muted)
   */
  public void setVolume(float volume) {
    playbackInteractions.setVolume(volume);
  }

  /**
   * A Session ID that is created at the initialization of OoyalaPlayer. Persists for the life of the OoyalaPlayer
   * @return A session ID that corresponds to this player instance
   */
  public String getPlayerSessionId() {
    return sessionIDManager.getPlayerSessionId();
  }

  /**
   * A Session ID that is created on the set of a new piece of content (i.e setEmbedCode). Persists until a new piece of content is set. Can be null if no video was set
   * @return A session ID that corresponds to the current content context.
   */
  public String getContentSessionId() {
    return sessionIDManager.getContentSessionId();
  }

//*******************  All Playback Interactions End *************//

  void restart() {
    if (_currentItem != null) {
      String embedCode = _currentItem.getEmbedCode();
      if (embedCode != null) {
        DebugMode.logD(TAG, "restart with embedcode:" + embedCode);
      }
      setEmbedCode(embedCode);
    }
  }

  /**
   * Suspend the current video (can be resumed later by calling resume). This
   * differs from pause in that it completely deconstructs the view so the
   * layout can be changed.
   */
  public void suspend() {
    if (getCurrentItem() == null) {
      DebugMode.logI(TAG, "Suspend was called without a current item. Doing nothing");
      return;
    }
    contextSwitcher.suspendCurrentPlayer();
    stopHeartbeatIfNecessary();

    stateManager.setState(State.SUSPENDED);
  }

  /**
   * Resume the current video from a suspended state
   */
  public void resume() {
    if (getCurrentItem() == null) {
      DebugMode.logI(TAG, "Resume was called without a current item. Doing nothing");
      return;
    }

    if (needReauthorizeBeforeResume()) {
      DebugMode.logI(TAG, "Need to reauthorize before resume");
      serverTaskManager.reauthorize(_currentItem, new ServerTaskCallback() {
        @Override
        public void callback(boolean success, OoyalaException error) {
          if (!success || error != null) {
            DebugMode.logE(TAG, "reauthorize failed");
          } else {
            _handler.post(new Runnable() {
              @Override
              public void run() {
                resumeAfterAuth();
              }
            });
          }
        }
      });
    } else {
      resumeAfterAuth();
    }
  }

  // Real resume work.
  private void resumeAfterAuth() {
    if (currentPlayer() != null) {
      startHeartbeatIfNecessary();
      contextSwitcher.resumeCurrentPlayer();
    } else if (getCurrentItem().isAuthorized()) {
      startHeartbeatIfNecessary();
      prepareContent(false);
    } else {
      OoyalaException error = new OoyalaException(OoyalaErrorCode.ERROR_PLAYBACK_FAILED,
          "Resuming video from an invalid state");
      onError(error, null);
      return;
    }
  }

  private boolean needReauthorizeBeforeResume() {
    if (_currentItem != null) {
      if (_currentItem.hasTokenExpired()) {
        return true;
      }

      if (_currentItem.isHeartbeatRequired() &&
          (System.currentTimeMillis() >= _suspendTime + authTokenManager.getHeartbeatInterval() * 1000)) {
        return true;
      }
    }
    return false;
  }

  private boolean fetchMoreChildren(PaginatedItemListener listener) {
    Channel parent = _currentItem.getParent();
    if (parent != null) {
      ChannelSet parentOfParent = parent.getParent();
      if (parent.hasMoreChildren()) {
        return _playerAPIClient.fetchMoreChildrenForPaginatedParentItem(parent, listener, _playerInfo);
      } else if (parentOfParent != null && parentOfParent.hasMoreChildren()) {
        return _playerAPIClient.fetchMoreChildrenForPaginatedParentItem(parentOfParent, listener, _playerInfo);
      }
    }
    return false;
  }

  /**
   * Change the current video to the previous video in the Channel or
   * ChannelSet. If there is no previous video, this will seek to the beginning
   * of the video.
   *
   * @param what
   *          OoyalaPlayerControl.DO_PLAY or OoyalaPlayerControl.DO_PAUSE
   *          depending on what to do after the video is set.
   * @return true if there was a previous video, false if not.
   */
  public boolean previousVideo(int what) {
    if (_currentItem.previousVideo() != null) {
      changeCurrentItem(_currentItem.previousVideo(), null);
      if (what == DO_PLAY) {
        play();
      } else if (what == DO_PAUSE) {
        pause();
      }
      return true;
    }
    seek(0);
    return false;
  }

  /**
   * Change the current video to the next video in the Channel or ChannelSet. If
   * there is no next video, nothing will happen. Note that this will trigger a
   * fetch of additional children if the Channel or ChannelSet is paginated. If
   * so, it may take some time before the video is actually set.
   *
   * @param what
   *          OoyalaPlayerControl.DO_PLAY or OoyalaPlayerControl.DO_PAUSE
   *          depending on what to do after the video is set.
   * @return true if there was a next video, false if not.
   */
  public boolean nextVideo(int what) {
    // This is required because android enjoys making things difficult. talk to
    // jigish if you got issues.
    if (_currentItem.nextVideo() != null) {
      changeCurrentItem(_currentItem.nextVideo(), null);
      if (what == DO_PLAY) {
        stateManager.setDesiredState(DesiredState.DESIRED_PLAY);
      } else if (what == DO_PAUSE) {
        stateManager.setDesiredState(DesiredState.DESIRED_PAUSE);
      }
      return true;
    } else if (what == DO_PLAY
            && fetchMoreChildren(new PaginatedItemListener() {
      @Override
      public void onItemsFetched(int firstIndex, int count,
                                 OoyalaException error) {
        _handler.post(new Runnable() {
          @Override
          public void run() {
            changeCurrentItem(_currentItem.nextVideo(), null);
            stateManager.setDesiredState(DesiredState.DESIRED_PLAY);
          }
        });
      }
    })) {
      return true;
    } else if (what == DO_PAUSE
            && fetchMoreChildren(new PaginatedItemListener() {
      @Override
      public void onItemsFetched(int firstIndex, int count,
                                 OoyalaException error) {
        _handler.post(new Runnable() {
          @Override
          public void run() {
            changeCurrentItem(_currentItem.nextVideo(), null);
            stateManager.setDesiredState(DesiredState.DESIRED_PAUSE);
          }
        });
      }
    })) {
      return true;
    }
    return false;
  }

  void onComplete() {
    // castplayer is disconnected after completion, always destroy it and recreate when replay.
    boolean destroyPlayers = isInCastMode();

    switch (_actionAtEnd) {
      case CONTINUE:
        if (nextVideo(DO_PLAY)) {
          return;
        }
        break;
      case PAUSE:
        if (nextVideo(DO_PAUSE)) {
          return;
        }
        break;
      case STOP:
        destroyPlayers = true;
        break;
      case RESET:
        break;
    }

    stateManager.setDesiredState(DesiredState.DESIRED_PAUSE);
    if (destroyPlayers) {
      cleanupPlayers();
    }
    stateManager.setState(State.COMPLETED);
    sendNotification(PLAY_COMPLETED_NOTIFICATION_NAME);
  }


  /**
   * This will reset the state of all the ads to "unplayed" causing any ad that
   * has already played to play again.
   */
  public void resetAds() {
    getAdPluginManager().resetAds();
  }

  /**
   * Skip the currently playing ad. Do nothing if no ad is playing
   */
  public void skipAd() {
    if (isShowingAd()) {
      sendNotification(AD_SKIPPED_NOTIFICATION_NAME);
      getAdPluginManager().skipAd();
    }
  }
  /**
   * Skip the currently playing ad. Do nothing if no ad is playing
   */
  public void clickAd() {
    if (isShowingAd()) {
      //need to be implemented
    }
  }

  /**
   * Process the click through event from UI.
   */
  public void onAdclickThrough() {
    if (isShowingAd() && getAdPluginManager().getActivePlugin() != null) {
      getAdPluginManager().getActivePlugin().processClickThrough();
    }
  }


  /**
   * This is called when an icon is clicked.
   * @param index the index of the icon
   */
  public void onAdIconClicked(int index) {
    if (isShowingAd() && getAdPluginManager().getActivePlugin() != null) {
      getAdPluginManager().getActivePlugin().onAdIconClicked(index);
    }
  }

  /**
   * For internal Ooyala use only.
   * Handle ad overlay click event during content playback.
   * This will open the url in device's default browser.
   * @param clickUrl the click url of the overlay ad.
   */
  public void onAdOverlayClicked(String clickUrl) {
    Utils.openUrlInBrowser(getLayout().getContext(), clickUrl);
  }

  /**
   * Register an Ad player our players and remember it
   *
   * @param adTypeClass
   *          A type of AdSpot that the player is capable of playing
   * @param adPlayerClass
   *          A player that plays the ad
   */
  void registerAdPlayer(Class<? extends OoyalaManagedAdSpot> adTypeClass,
                        Class<? extends AdMoviePlayer> adPlayerClass) {
    _adPlayers.put(adTypeClass, adPlayerClass);
  }

  /**
   * get the ad player class for a certain ad spot
   *
   * @param ad
   *          the adspot
   * @return the adplayer class
   */
  Class<? extends AdMoviePlayer> getAdPlayerClass(OoyalaManagedAdSpot ad) {
    return _adPlayers.get(ad.getClass());
  }

  /**
   * register a ad plugin
   *
   * @param plugin
   *          the plugin to be registered
   * @return true if registration succeeded, false otherwise
   */
  @Override
  public boolean registerPlugin(final AdPluginInterface plugin) {
    return getAdPluginManager().registerPlugin(plugin);
  }

  /**
   * deregister a ad plugin
   *
   * @param plugin
   *          the plugin to be deregistered
   * @return true if deregistration succeeded, false otherwise
   */
  @Override
  public boolean deregisterPlugin(final AdPluginInterface plugin) {
    return getAdPluginManager().deregisterPlugin(plugin);
  }

  @Override
  public boolean registerPlugin(AnalyticsPluginInterface plugin) {
    return analyticsPluginManager.registerPlugin(plugin);
  }

  @Override
  public boolean deregisterPlugin(AnalyticsPluginInterface plugin) {
    return analyticsPluginManager.deregisterPlugin(plugin);
  }

  /**
   * Get the Ooyala Managed Ads Plugin, which maintains VAST and Ooyala Advertisements
   * @return the ManagedAdsPlugin
   */
  public OoyalaManagedAdsPlugin getManagedAdsPlugin() {
    return _managedAdsPlugin;
  }

  /**
   * Insert VAST ads to the managed ad plugin.
   *
   * @param ads the ads to be inserted.
   */
  public void insertAds(List<VASTAdSpot> ads) {
    if (_managedAdsPlugin != null && ads != null) {
      for (VASTAdSpot vast : ads) {
        _managedAdsPlugin.insertAd(vast);
      }
    }
  }
  /**
   * Resgister a castManager
   * @param castManagerInterface
   */
  public void registerCastManager(CastManagerInterface castManagerInterface) {
    contextSwitcher.setCastManager(castManagerInterface);
  }

  /**
   * called by a plugin when it request admode ooyalaplayer
   *
   * @param plugin
   *          the caller plugin
   * @return true if exit succeeded, false otherwise
   */
  @Override
  public boolean requestAdMode(AdPluginInterface plugin) {
    // only allow request ad mode when content is playing
    if (_player == null || _player.getState() != State.PLAYING) {
      return false;
    }
    if (!getAdPluginManager().requestAdMode(plugin)) {
      return false;
    }

    contextSwitcher.switchToAdMode();
    return true;
  }

  /**
   * called by a plugin when it finishes ad play and return the control to
   * ooyalaplayer
   *
   * @param plugin
   *          the caller plugin
   * @return true if exit succeeded, false otherwise
   */
  @Override
  public boolean exitAdMode(final AdPluginInterface plugin) {
    return getAdPluginManager().exitAdMode(plugin);
  }

  public void switchToCastMode(String embedCode) {
    DebugMode.logD(TAG, "Switch to Cast Mode");
    DebugMode.assertCondition(_currentItem != null, TAG, "currentItem should be not null");
    DebugMode.assertCondition(contextSwitcher.getCastManager() != null, TAG, "castManager should be not null");
    // disable analytics for cast mode to avoid double count.
    analyticsPluginManager.disableAnalytics();

    if (isAdPlaying()) {
      getAdPluginManager().forceExitAdMode();
    }

    boolean isPlaying = isPlaying() || getDesiredState() == DesiredState.DESIRED_PLAY;
    int playheadTime = getCurrentPlayheadForCastMode();
    playbackInteractions.queuedSeekTime = 0;  //Clear queued seek time if we start casting
    contextSwitcher.suspendCurrentPlayer();
    CastModeOptions castOptions =
        new CastModeOptions(embedCode, playheadTime, isPlaying, _embedTokenGenerator, getClosedCaptionsLanguage(), authTokenManager.getAuthToken(), getPcode(), getDomain());
    contextSwitcher.getCastManager().enterCastMode(castOptions);
    _layoutController.setFullscreenButtonShowing(false);
    DebugMode.assertCondition(isInCastMode(), TAG, "Should be in cast mode by the end of switchCastMode");
  }

  public void exitCastMode(int exitPlayheadTime, boolean isPlaying, String ec) {
    DebugMode.logD(TAG, "Exit Cast Mode with playhead = " + exitPlayheadTime + ", isPlayer = " + isPlaying + ", embedCode = " + ec);
    DebugMode.assertCondition(ec.equals(this.getEmbedCode()), TAG, "embedCode should be the same as the one in TV playback");
    if (_player == null) {
      if (prepareContent(isPlaying)) {
        _player.seekToTime(exitPlayheadTime);
      } else {
        cleanupPlayers();
        OoyalaException error = new OoyalaException(OoyalaErrorCode.ERROR_PLAYBACK_FAILED, "Player initialization failed");
        onError(error, "Player initialization failed");
      }
    } else {
      DebugMode.logE(TAG, "We are swtiching to content, while the player is in state: " + _player.getState());
      _player.resume(exitPlayheadTime, isPlaying ? State.PLAYING : State.PAUSED);
    }
    _layoutController.setFullscreenButtonShowing(true);

    //We are playing on device again, re-enable analytics
    analyticsPluginManager.enableAnalytics();
  }

  boolean prepareContent(boolean forcePlay) {
    if (_player != null) {
      DebugMode.assertFail(TAG,
          "try to allocate player while player already exist");
      return false;
    }

    MoviePlayer mp = contextSwitcher.createAndInitPlayer(_playerSelector, _currentItem, playbackInteractions.getClosedCaptionsLanguage(), playbackInteractions.seekable());
    if (mp == null) {
      return false;
    }

    _player = mp;

    //TEMPORARY - until contentPlayer is solely in context switcher
    contextSwitcher.setContentPlayer(_player);
    if (forcePlay) {
      play();
    } else {
      playIfDesired();
    }
    return true;
  }

  public boolean isInCastMode() {
    return contextSwitcher.isInCastMode();
  }


  void onError(OoyalaException error, String message) {
    if (error != null) {
      this._error = error;
    }
    if(this._error != null) {
      if (TextUtils.isEmpty(message)) {
        message = _error.getMessage();
      }

      DebugMode.logD(TAG, message, this._error);
    }
    serverTaskManager.cancelAll();
    stateManager.setState(State.ERROR);
    sendNotification(ERROR_NOTIFICATION_NAME);
  }

  @Override
  public void update(Observable observable, Object o) {
    observerHandler.handleObserverUpdate(observable, o);
  }

  void sendClosedCaptionsNotification() {
    if( ! isAdPlaying() ) {
      final Caption caption = CaptionUtils.getCaption(
              getCurrentItem(),
              getClosedCaptionsLanguage(),
              getPlayheadTime()
      );
      if (caption != null && !caption.equals(previousCaption)) {
        sendNotification(
                new OoyalaNotification(
                        CC_CHANGED_NOTIFICATION_NAME,
                        caption
                )
        );
        previousCaption = caption;
      }
    }
  }

  void sendNotification(String notificationName) {
    sendNotification(notificationName, null);
  }

  void sendNotification(String notificationName, Object data) {
    sendNotification(new OoyalaNotification(notificationName, data));
  }

  void sendNotification(OoyalaNotification notification) {
    setChanged();
    notifyObservers(notification);
  }


  void notifyPluginEvent(StateNotifier notifier, String event) {
    sendNotification(event);
  }

  void notifyPluginEvent(StateNotifier notifier, OoyalaNotification notification) {
    sendNotification(notification);
  }

  void notifyPluginStateChange(StateNotifier notifier, State oldState, State newState) {
    if (oldState == newState) {
      DebugMode.logI(TAG, "State change reported, but state has not changed: " + newState);
      return;
    }

    sendNotification(OoyalaPlayer.STATE_CHANGED_NOTIFICATION_NAME);
    if (newState == State.ERROR) {
      sendNotification(OoyalaPlayer.AD_ERROR_NOTIFICATION_NAME);
    }
  }

  public StateNotifier createStateNotifier() {
    return new StateNotifier(this);
  }


  public ID3TagNotifier getID3TagNotifier() {
    return ID3TagNotifier.s_getInstance();
  }

  private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    public DownloadImageTask() {
    }

    @Override
    protected Bitmap doInBackground(String... args) {
      String url = args[0];
      Bitmap bitmap = null;
      try {
        InputStream in = new java.net.URL(url).openStream();
        bitmap = BitmapFactory.decodeStream(in);
      } catch (Exception e) {
        DebugMode.logE("Error", e.getMessage());
        DebugMode.logE(TAG, "Caught!", e);
      }
      return bitmap;
    }

    protected void onPostExecute(Bitmap result) {
      if (_promoImageView != null) {
        _promoImageView.setImageBitmap(result);
        _promoImageView.setAdjustViewBounds(true);
      }
      DebugMode.logD(TAG, "promoimage loaded, state is" + stateManager.getState());
      if (stateManager.getState() == State.LOADING) {
        stateManager.setState(State.READY);
        playIfDesired();
      }
    }
  }


  /**
   * Get the customDRMData for the current player.
   *
   * @return _customDRMData
   */
  public String getCustomDRMData() {
    return _customDRMData;
  }

  /**
   * Set the customDRMData for the current player.
   *
   */
  public void setCustomDRMData(String data) {
    _customDRMData = data;
  }


  /**
   * Get what the player will do at the end of playback.
   *
   * @return the OoyalaPlayer.OoyalaPlayerActionAtEnd to use
   */
  public ActionAtEnd getActionAtEnd() {
    return _actionAtEnd;
  }

  /**
   * Set what the player should do at the end of playback.
   *
   * @param actionAtEnd
   */
  public void setActionAtEnd(ActionAtEnd actionAtEnd) {
    this._actionAtEnd = actionAtEnd;
  }

  public static void setEnvironment(EnvironmentType e) {
    Environment.setEnvironment(e);
  }


  /**
   * @deprecated returns metadata for current video
   * Currently does not return anything. Instead, use getCurrentItem().getMetadata();
   */
  public JSONObject getMetadata() {
    return null;
  }


  /**
   * The current movie.
   *
   * @return movie
   */
  public Video getCurrentItem() {
    return _currentItem;
  }

  /**
   * The embedded item (movie, channel, or channel set).
   *
   * @return movie
   */
  public ContentItem getRootItem() {
    return _rootItem;
  }

  /**
   * Get the current error code, if one exists
   *
   * @return error code
   */
  public OoyalaException getError() {
    return _error;
  }

  /**
   * Get the embedCode for the current player.
   *
   * @return embedCode
   */
  public String getEmbedCode() {
    return _currentItem == null ? null : _currentItem.getEmbedCode();
  }

  /**
   * Get the pcode for this OoyalaPlayer instance
   *
   * @return pcode
   */
  public String getPcode() {
    return pcode;
  }

  /**
   * Get the domain for this OoyalaPlayer instance
   *
   * @return domain
   */
  public PlayerDomain getDomain() {
    return domain;
  }


  /**
   * @return The player info.
   */
  public PlayerInfo getPlayerInfo() {
    return _playerInfo;
  }

  /**
   * For internal use only
   * @return the movie selector
   */
  public MoviePlayerSelector getMoviePlayerSelector() {
    return _playerSelector;
  }

  /**
   * For internal use only
   * @return the handler to schedule running on UI thread
   */
  public Handler getHandler() {
    return _handler;
  }


  /**
   * Get the Options that were provided on creation of the OoyalaPlayer
   * @return
   */
  public ReadonlyOptionsInterface options() {
    return _options;
  }

  /**
   * @return non-null, immutable Options.
   */
  public ReadonlyOptionsInterface getOptions() {
    return _options;
  }

  /**
   * Get current player state. One of playing, paused, buffering, channel, or
   * error
   *
   * @return state
   */
  public State getState() {
    PlayerInterface p = currentPlayer();
    if (p == null) {
      return stateManager.getState();
    } else if (isShowingAd()) {
      return p.getState();
    } else {
      // current player is content player. If promo image is loaded, set state to ready and stop
      // the spinning wheel.
      return stateManager.getState() == State.READY ? State.READY : p.getState();
    }
  }

  public DesiredState getDesiredState() {
    return stateManager.getDesiredState();
  }

  /**
   * @return the OoyalaAPIClient used by this player
   */
  public OoyalaAPIClient getOoyalaAPIClient() {
    return new OoyalaAPIClient(_playerAPIClient);
  }

  PlayerAPIClient getPlayerAPIClient() {
    return this._playerAPIClient;
  }

  /**
   * Get the available closed captions languages
   *
   * @return a Set of Strings containing the available closed captions languages
   */
  public Set<String> getAvailableClosedCaptionsLanguages() {
    Set<String> languages = new HashSet<String>();
    if (_currentItem != null && _currentItem.getClosedCaptions() != null) {

      languages.addAll(_currentItem.getClosedCaptions().getLanguages());
    }

    // Thought about using isLiveClosedCaptionsAvailable() method, but it does not match this
    // behavior and could result in wrong closed captions given from Chromecast
    if (languages.size() <= 0 && currentPlayer() != null && currentPlayer().isLiveClosedCaptionsAvailable()) {
      languages.add(LIVE_CLOSED_CAPIONS_LANGUAGE);
    }

    return languages;
  }



//******************* Auth Token APIs *************//

  /**
   * Get the authToken for the current player.
   *
   * @return authToken The authorization token that represents this device's streaming session
   */
  public String getAuthToken() {
    return authTokenManager.getAuthToken();
  }

  /**
   * Checks the expiration of the authToken, and compares it to the current time.
   * @return true if token is expired, false otherwise
   */
  public boolean isAuthTokenExpired() {
    return authTokenManager.isAuthTokenExpired();
  }

  /**
   * Set the AuthToken used to authorize video playback
   *
   * Changing this manually without an existing auth token can cause an inflated number of concurrent
   * streams for a user
   *
   * @param authToken The authorization token that represents this device's streaming session
   */
  public void setAuthToken(String authToken) {
    if (authTokenManager != null) {
      authTokenManager.setAuthToken(authToken);
    }
  }

  //******************* Auth Token APIs End *************//

  /**
   * @return the kind of content that is on the video display right now.
   */
  public ContentOrAdType getPlayingType() {
    ContentOrAdType t = _getPlayingType();
    //DebugMode.logV( TAG, "getContentOrAdType(): " + t );
    return t;
  }
  private ContentOrAdType _getPlayingType() {
    if( isShowingAd() ) {
      // fyi: don't use getPlayer() here since we want to only check the 'content' player, never the 'ad' one.
      if( _player == null || _player.currentTime() <= 0 ) {
        return ContentOrAdType.PreRollAd;
      }
      else if( _player.getState() == State.COMPLETED ) {
        return ContentOrAdType.PostRollAd;
      }
      else {
        return ContentOrAdType.MidRollAd;
      }
    }
    else {
      return ContentOrAdType.MainContent;
    }
  }


  /**
   * The miliseconds within the video where advertisement cue points exist
   * @return
   */
  @Override
  public Set<Integer> getCuePointsInMilliSeconds() {
    if (_options.getShowCuePoints()) {
      return getAdPluginManager().getCuePointsInMilliSeconds();
    } else {
      return new HashSet<Integer>();
    }
  }

  /**
   * Get the percentages within the video where cue points exist
   * @return a set of integers between 0 and 100 for where cue points exist
   */
  public Set<Integer> getCuePointsInPercentage() {
    Set<Integer> cuePoints = new HashSet<Integer>();
    int duration = getDuration();

    if (!shouldShowCuePoints()) {
      return cuePoints;
    }

    for (Integer i : getAdPluginManager().getCuePointsInMilliSeconds()) {
      if (i <= 0) {
        continue;
      }

      int point = (i >= duration) ? 100 : (i * 100 / duration);
      cuePoints.add(point);
    }
    return cuePoints;
  }

  /**
   * @return true if the OoyalaPlayer is currently showing an ad (in any state).
   *         false if not.
   */
  public boolean isShowingAd() {
    return (getAdPluginManager().inAdMode());
  }


  /**
   * Get the SDK version and RC number of this Ooyala Player SDK
   *
   * @return the SDK version as a string
   */
  public static String getVersion() {
    return SDK_VERSION;
  }

  /**
   * get the seek style
   *
   * @return the seek style of current player
   */
  public SeekStyle getSeekStyle() {
    if (currentPlayer() != null && currentPlayer() instanceof MoviePlayer) {
      return ((MoviePlayer)currentPlayer()).getSeekStyle();
    } else if (currentPlayer() != null) {
      //TODO: the PlayerInterface may need getSeekStyle();
      return SeekStyle.BASIC;
    } else {
      DebugMode.logW(this.getClass().toString(), "We are seeking without a MoviePlayer!");
      return SeekStyle.NONE;
    }
  }

  private boolean shouldShowCuePoints() {
    if (isShowingAd()) {
      return false;
    }

    if (getDuration() <= 0) {
      return false;
    }

    return _options.getShowCuePoints();
  }

  public boolean showingAdWithHiddenControlls() {
    return (isShowingAd() && (options().getShowAdsControls() == false));
  }
  boolean isPlayable(State state) {
    return (state == State.READY || state == State.PLAYING || state == State.PAUSED);
  }

  /**
   * Determine if play is the initial play for the content, required to insert
   * preroll properly.
   *
   * @return true if it is initial play
   */
  boolean needPlayAdsOnInitialContentPlay() {
    if ((stateManager.getInitPlayState() != InitPlayState.NONE) || this.isShowingAd()) {
      return false;
    }
    stateManager.setInitPlayState(InitPlayState.PluginQueried);
    return this.contextSwitcher.processAdModes(AdMode.InitialPlay, 0);
  }

  static public boolean isLiveClosedCaptionsLanguage( String cc ) {
    return OoyalaPlayer.LIVE_CLOSED_CAPIONS_LANGUAGE.equals( cc );
  }


  /**
   * Set the layout controller from which the OoyalaPlayer should fetch the
   * layout to display to
   *
   * @param layoutController
   *          the layoutController to use.
   */
  public void setLayoutController(LayoutController layoutController) {
    _layoutController = layoutController;
    authTokenManager = new AuthTokenManager(getLayout().getContext());
    _playerAPIClient.setAuthTokenManager(authTokenManager);
  }


  /**
   * Get the absolute pixel of the top bar's distance from the top of the
   * device.
   *
   * @return pixels to shift the Learn More button down
   */
  public int getTopBarOffset() {
    // TODO: Fix the way player position it's children
    if(!(_layoutController instanceof AbstractOoyalaPlayerLayoutController)) {
      return 0;
    }
    return ((AbstractOoyalaPlayerLayoutController) _layoutController)
            .getControls().topBarOffset();
  }

  /**
   * For internal Ooyala use only.
   * @param videoView
   */
  public void addVideoView(View videoView ) {
    _layoutController.addVideoView(videoView);
  }

  /**
   * For internal Ooyala use only.
   */
  public void removeVideoView() {
    _layoutController.removeVideoView();
  }

  void maybeReshowTVRating() {
    if( showTVRatingAfterAd && _layoutController != null ) {
      _layoutController.reshowTVRating();
    }
    showTVRatingAfterAd = false;
  }

  void showPromoImage() {
    if (_currentItem != null && _currentItem.getPromoImageURL(0, 0) != null) {
      DebugMode.logD(TAG,
              "loading promoimage , url is " + _currentItem.getPromoImageURL(0, 0));
      hidePromoImage();
      _promoImageView = new ImageView(getLayout().getContext());
      getLayout().addView(_promoImageView);
      new DownloadImageTask().execute(_currentItem.getPromoImageURL(0, 0));
    }
  }

  void hidePromoImage() {
    if (_promoImageView != null) {
      getLayout().removeView(_promoImageView);
      _promoImageView = null;
    }
  }

  /**
   * Returns true if in fullscreen mode, false if not. Fullscreen currently does
   * not work due to limitations in Android.
   *
   * @return fullscreen mode
   */
  public boolean isFullscreen() {
    return _layoutController != null && _layoutController.isFullscreen();
  }

  /**
   * Set fullscreen mode (will only work if fullscreenLayout is set) This will
   * call the setFullscreen method on the associated LayoutController. If you
   * are implementing your own LayoutController here are some things to keep in
   * mind:
   * <ul>
   * <li>If the setFullscreen method of your LayoutController creates a
   * new OoyalaPlayerLayout or switches to a different one, you *must* call
   * OoyalaPlayer.suspend() before doing so and call OoyalaPlayer.resume() after
   * doing so.
   * <li>If the setFullscreen method of your LayoutController uses the
   * same OoyalaPlayerLayout, you do not need to do any special handling.
   * </ul>
   *
   * @param fullscreen
   *          true to switch to fullscreen, false to switch out of fullscreen
   */
  public void setFullscreen(boolean fullscreen) {
    if (isFullscreen() == !fullscreen) { // this is so we don't add/remove cc
      // view if we are not actually
      // changing state.
      _layoutController.setFullscreen(fullscreen);

      // Create Learn More button when going in and out of fullscreen
      if (isShowingAd() && currentPlayer() instanceof AdMoviePlayer) {
        ((AdMoviePlayer) currentPlayer()).updateLearnMoreButton(getLayout(),
                getTopBarOffset());
      }
    }
  }

  /**
   * Get the current OoyalaPlayerLayout
   *
   * @return the current OoyalaPlayerLayout
   */
  public FrameLayout getLayout() {
    if(_layoutController == null) {
      return null;
    }
    return _layoutController.getLayout();
  }

  public void setHook() {
    _playerAPIClient.setHook();
  }

  /**
   * Start obtaining the Advertising Id, which internally is then used in e.g. VAST Ad URL 'device id' macro expansion.
   * This method will: 1st check that the Google Play Services are available, which may fail and return a non-SUCCESS code.
   * If they are available (code SUCCESS) then: 2nd an attempt will be made to load the Advertising Id from those Google Play Services.
   * If the 2nd step fails an OoyalaException will be thrown, wrapping the original exception.
   * Callers of this method should:
   * 1) update AndroidManifest.xml to include meta-data tag per Google Play Services docs.
   * 2) obtain and pass in a valid Android Context;
   * 3) check the return code and decide if the App should prompt the user to install Google Play Services.
   * 4) handle subsequent asynchronous onAdvertisingIdSuccess() and onAdvertisingIdError() callbacks: due to the asynchronous nature of the Google Play Services call used,
   * there can be a long delay either before the Advertising Id is successfully obtained, or a long delay before a failure happens.
   * An invocation of onAdvertisingIdSuccess() means the Ooyala SDK now has an advertising id for using with e.g. 'device id' macros. Nothing further must be done by the App.
   * An invocation of onAdvertisingIdError() means the App might try this whole process again since fetching failed.
   * These callbacks will be invoked on the main thread.
   * @param context must be non-null.
   * @param listener must be non-null.
   * @see <a href="http://developer.android.com/google/play-services/setup.html">http://developer.android.com/google/play-services/setup.html</a>
   * @see <a href="http://developer.android.com/reference/com/google/android/gms/common/GooglePlayServicesUtil.html">http://developer.android.com/reference/com/google/android/gms/common/GooglePlayServicesUtil.html</a>#isGooglePlayServicesAvailable(android.content.Context)
   * @see com.ooyala.android.OoyalaException#getCode()
   * @return status code, can be one of following in ConnectionResult: SUCCESS, SERVICE_MISSING, SERVICE_VERSION_UPDATE_REQUIRED, SERVICE_DISABLED, SERVICE_INVALID, DATE_INVALID.
   */
  public int beginFetchingAdvertisingId(final Context context,
                                        final IAdvertisingIdListener listener) {
    final int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable( context );
    if( status == ConnectionResult.SUCCESS ) {
      final IAdvertisingIdListener listenerWrapper = new IAdvertisingIdListener() {
        @Override
        public void onAdvertisingIdSuccess(String advertisingId) {
          AdvertisingIdUtils.setAdvertisingId(advertisingId);
          listener.onAdvertisingIdSuccess(advertisingId);
        }
        @Override
        public void onAdvertisingIdError(OoyalaException oe) {
          listener.onAdvertisingIdError(oe);
        }
      };
      AdvertisingIdUtils.getAndSetAdvertisingId( context, listenerWrapper );
    }
    return status;
  }

  @Override
  protected void finalize() throws Throwable {
    DebugMode.logV(TAG, "OoyalaPlayer Finalized");
    super.finalize();
  }

  public AnalyticsPluginManager getAnalyticPluginManager() {
    return analyticsPluginManager;
  }
}

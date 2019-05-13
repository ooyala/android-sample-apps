package com.ooyala.sample.utils;

import android.app.Activity;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

/**
 * This is used to store information of a sample activity for use in a Map or List
 *
 */
public class PlayerSelectionOption {

  public static final int ONLINE_PLAYBACK = 0;  // enforce online playback
  public static final int OFFLINE_EMBED_CODE_PLAYBACK = 1; // enforce offline playback
  public static final int OFFLINE_URL_PLAYBACK = 2; // enforce offline playback
  public static final int OFFLINE_ONLINE_PLAYBACK = 3; // will try offline playback if possible, else try online playback
  public static final String UNDEFINED_VALUE = "UNDEFINED_VALUE";

  /**
   * The type of the downloader that was used for downloading media files.
   */
  @Documented
  @Retention(RetentionPolicy.SOURCE)
  @IntDef({ONLINE_PLAYBACK, OFFLINE_EMBED_CODE_PLAYBACK, OFFLINE_URL_PLAYBACK, OFFLINE_ONLINE_PLAYBACK})
  public @interface PlaybackType {
  }

  private String embedCode;
  private Class <? extends Activity> activity;
  private String pcode;
  private String domain;
  private String apiKey;
  private String secretKey;
  private String accountId;
  private String deliveryType = UNDEFINED_VALUE;
  private String url = UNDEFINED_VALUE;
  private @PlaybackType
  int playbackType = OFFLINE_ONLINE_PLAYBACK;

  /**
   * Create a {@link PlayerSelectionOption} to download a media file by embed code
   */
  public PlayerSelectionOption(String embedCode, String pcode, String apiKey, String secretKey,
                               String accountId, String domain, Class<? extends Activity> activity) {
    this.embedCode = embedCode;
    this.activity = activity;
    this.pcode = pcode;
    this.domain = domain;
    this.apiKey = apiKey;
    this.secretKey = secretKey;
    this.accountId = accountId;
  }

  /**
   * Create a {@link PlayerSelectionOption} to download a media file by embed code
   */
  public PlayerSelectionOption(String embedCode, String pcode, String apiKey, String secretKey,
                               String accountId, String domain, @PlaybackType int playbackType,
                               Class<? extends Activity> activity) {
    this(embedCode, pcode, apiKey, secretKey, accountId, domain, activity);
    this.playbackType = playbackType;
  }

  /**
   * Create a {@link PlayerSelectionOption} to download a media file by predefined URL
   *
   * @param embedCode    - the embed code. It is the unique identifier of the media file that
   *                     will be used as a file name for downloading
   * @param deliveryType - the delivery type. The following types have to be used
   *                     {@link com.ooyala.android.item.Stream#DELIVERY_TYPE_DASH},
   *                     {@link com.ooyala.android.item.Stream#DELIVERY_TYPE_HLS}.
   * @param url          - the URL by which the file will be downloaded
   * @param activity     - the {@link Activity}
   */
  public PlayerSelectionOption(String embedCode, String deliveryType, String url,
                               Class<? extends Activity> activity) {
    this(embedCode, UNDEFINED_VALUE, UNDEFINED_VALUE, UNDEFINED_VALUE, UNDEFINED_VALUE,
        UNDEFINED_VALUE, activity);
    this.deliveryType = deliveryType;
    this.url = url;
  }

  /**
   * Create a {@link PlayerSelectionOption} to playback a media file that was downloaded using URL
   */
  public PlayerSelectionOption(String embedCode, String domain, @PlaybackType int playbackType,
                               Class<? extends Activity> activity) {
    this(embedCode, UNDEFINED_VALUE, UNDEFINED_VALUE, UNDEFINED_VALUE, UNDEFINED_VALUE, domain, activity);
    this.playbackType = playbackType;
  }

  /**
   * Get the pcode for this sample
   *
   * @return the pcode
   */
  public String getPcode() {
    return pcode;
  }

  /**
   * Get the domain for this sample
   *
   * @return the domain
   */
  public String getDomain() {
    return domain;
  }

  /**
   * Get the embed code for this sample
   *
   * @return the embed code
   */
  public String getEmbedCode() {
    return this.embedCode;
  }

  /**
   * Get the apikey for this sample
   * @return the apikey
   */
  public String getApiKey() {
    return apiKey;
  }

  /**
   * Get the secretkey for this sample
   *
   * @return the secretKey
   */
  public String getSecretKey() {
    return secretKey;
  }

  /**
   * Get the accountid for this sample
   *
   * @return the accountId
   */
  public String getAccountId() {
    return accountId;
  }


  /**
   * Get the playbackType for this sample
   *
   * @return the playbackType
   */
  public @PlaybackType
  int getPlaybackType() {
    return playbackType;
  }

  /**
   * Get the activity to use for this sample
   *
   * @return the activity to launch
   */
  public Class <? extends Activity> getActivity() {
    return this.activity;
  }

  /**
   * Get the delivery type of the media asset that will be downloaded using a predefined URL
   *
   * @return one of the following delivery types {@link com.ooyala.android.item.Stream#DELIVERY_TYPE_DASH},
   * {@link com.ooyala.android.item.Stream#DELIVERY_TYPE_HLS}
   */
  public String getDeliveryType() {
    return deliveryType;
  }

  /**
   * Get the predefined URL
   *
   * @return the URL by which the file will be downloaded
   */
  public String getUrl() {
    return url;
  }
}

package com.ooyala.android.item;

import android.os.Build;
import android.util.Base64;

import com.ooyala.android.StreamSelector;
import com.ooyala.android.util.DebugMode;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import java.util.UUID;

import static com.google.android.exoplayer2.C.WIDEVINE_UUID;

/**
 * Stream represents a single playable video URL.  This refers to  the Video URL, and any metadata
 * around that particular video.
 *
 * NOTE: When reading Streams that are generated from Ooyala Backlot APIs, A lot of the fields
 * within this class apply solely to Streams with MP4 Delivery Type
 */
public class Stream implements JSONUpdatableItem {
  private static final String TAG = Stream.class.getSimpleName();

  public static final String KEY_VIDEO_BITRATE = "video_bitrate";
  public static final String KEY_AUDIO_BITRATE = "audio_bitrate";
  public static final String KEY_VIDEO_CODEC = "video_codec";
  public static final String KEY_FRAMERATE = "framerate";
  public static final String KEY_DELIVERY_TYPE = "delivery_type";
  public static final String KEY_DATA = "data";
  public static final String KEY_FORMAT = "format";
  public static final String KEY_IS_LIVE_STREAM = "is_live_stream";
  public static final String KEY_ASPECT_RATIO = "aspect_ratio";
  public static final String KEY_PROFILE = "profile";
  public static final String KEY_WIDEVINE_SERVER_PATH = "widevine_server_path";
  public static final String KEY_HEIGHT = "height";
  public static final String KEY_WIDTH = "width";
  public static final String KEY_URL = "url";
  public static final String KEY_TOKEN_EXPIRE = "token_expire";
  public static final String KEY_DRM = "drm";
  public static final String KEY_WIDEVINE = "widevine";
  public static final String KEY_LICENSE_URL = "la_url";

  public static final String PROFILE_BASELINE = "baseline";

  public static final String DELIVERY_TYPE_HLS = "hls";
  public static final String DELIVERY_TYPE_PLAYREADY_HLS = "playready_hls";
  public static final String DELIVERY_TYPE_MP4 = "mp4";
  public static final String DELIVERY_TYPE_M3U8 = "m3u8";
  public static final String DELIVERY_TYPE_DASH = "dash";
  public static final String DELIVERY_TYPE_REMOTE_ASSET = "remote_asset";
  public static final String DELIVERY_TYPE_AKAMAI_HD2_VOD_HLS = "akamai_hd2_vod_hls";
  public static final String DELIVERY_TYPE_AKAMAI_HD2_HLS = "akamai_hd2_hls";
  public static final String DELIVERY_TYPE_SMOOTH = "smooth";

  public static final String STREAM_URL_FORMAT_TEXT = "text";
  public static final String STREAM_URL_FORMAT_B64 = "encoded";

  protected String _deliveryType = null;
  protected String _videoCodec = null;
  protected String _urlFormat = null;
  protected String _framerate = null;
  protected int _videoBitrate = -1;
  protected int _audioBitrate = -1;
  protected int _height = -1;
  protected int _width = -1;
  protected String _url = null;
  protected String _aspectRatio = null;
  protected boolean _isLiveStream = false;
  protected String _profile = null;
  protected String _widevineServerPath = null;  // A path from SAS when this is a widevine encrypted stream
  protected UUID _widevineUUID = null;
  protected Long _tokenExpireDateInMilliSeconds;

  private static class DefaultStreamSelector implements StreamSelector {
    public DefaultStreamSelector() {}

    @Override
    public Stream bestStream(Set<Stream> streams, boolean isWifiEnabled) {
      if (streams == null || streams.size() == 0) { return null; }

      Stream bestBitrateStream = null;
      for (Stream stream : streams) {
        // for remote assets, just pick the first stream
        if (stream.getDeliveryType().equals(DELIVERY_TYPE_REMOTE_ASSET)
            || stream.getDeliveryType().equals(DELIVERY_TYPE_HLS)
            || stream.getDeliveryType().equals(DELIVERY_TYPE_DASH)
            || stream.getDeliveryType().equals(DELIVERY_TYPE_AKAMAI_HD2_HLS)) { return stream; }
        if (Stream.isDeliveryTypePlayable(stream)
            && Stream.isProfilePlayable(stream)
            && (bestBitrateStream == null
                || stream.betterThan(bestBitrateStream, isWifiEnabled))) {
          bestBitrateStream = stream;
        }
      }

      return bestBitrateStream;
    }
  }

  private static StreamSelector _selector = new DefaultStreamSelector();

  /**
   * Set the StreamSelector used to select the Stream to play.
   * @param selector an implemented StreamSelector
   */
  public static void setStreamSelector(StreamSelector selector) {
    _selector = selector;
  }

  /**
   * Reset the StreamSelector to the default
   */
  public static void resetStreamSelector() {
    _selector = new DefaultStreamSelector();
  }

  /**
   * Create an empty Stream object
   */
  public Stream() {}

  /**
   * Create a Stream Object based on a provided JSON Object.  Take a look at Stream's static class variables
   * that are prefixed with "KEY_" to understand the acceptable JSONObject entries
   *
   * @param data a JSONObject with proper entries for a Stream
   */
  public Stream(JSONObject data) {
    update(data);
  }

  /**
   * Create an Stream that simply contains URL and Delivery Type
   * This is the bare minimum for the instantiation of an UnbundledVideo object
   * @param url source for the video stream e.g. "http://techslides.com/demos/sample-videos/small.mp4".
   * @param deliveryType the stream delivery type e.g. DELIVERY_TYPE_MP4.  See Stream's class variables that are prefixed with "DELIVERY_TYPE"
   */
  public Stream( String url, String deliveryType ) {
    this._url = url;
    this._deliveryType = deliveryType;
    this._urlFormat = STREAM_URL_FORMAT_TEXT;
  }

  boolean betterThan(Stream other, boolean isWifiEnabled) {

    // if the bitrates are the same, always choose the bitrate with higher resolution
    if (this.getCombinedBitrate() == other.getCombinedBitrate() && this.getHeight() > other.getHeight()) {
      return true;
    } else if (isWifiEnabled) {
      return this.getCombinedBitrate() > other.getCombinedBitrate();
    } else {
      // if wifi is off, choose the one closest to 400.
      return Math.abs(400 - this.getCombinedBitrate()) < Math.abs(400 - other.getCombinedBitrate());
    }
  }

  ReturnState update(JSONObject data) {
    if (data.isNull(KEY_DELIVERY_TYPE)) {
      System.out.println("ERROR: Fail to update stream with dictionary because no delivery_type exists!");
      return ReturnState.STATE_FAIL;
    }
    if (data.isNull(KEY_URL)) {
      System.out.println("ERROR: Fail to update stream with dictionary because no url element exists!");
      return ReturnState.STATE_FAIL;
    }

    JSONObject urlData = null;
    try {
      urlData = data.getJSONObject(KEY_URL);
    } catch (JSONException exception) {
      System.out.println("ERROR: Fail to update stream with dictionary because url element is invalid.");
      return ReturnState.STATE_FAIL;
    }

    if (urlData.isNull(KEY_DATA)) {
      System.out.println("ERROR: Fail to update stream with dictionary because no url.data exists!");
      return ReturnState.STATE_FAIL;
    }
    if (urlData.isNull(KEY_FORMAT)) {
      System.out.println("ERROR: Fail to update stream with dictionary because no url.format exists!");
      return ReturnState.STATE_FAIL;
    }

    try {
      if (!data.isNull(KEY_WIDEVINE_SERVER_PATH)) {
        _widevineServerPath =  data.getString(KEY_WIDEVINE_SERVER_PATH);

        // TODO: DRM UUID has to be parsed separately and moved on to another part
        _widevineUUID = WIDEVINE_UUID;
      }
      if (!data.isNull(KEY_DRM)) {
        JSONObject drm = data.getJSONObject(KEY_DRM);
        if (!drm.isNull(KEY_WIDEVINE)) {
          JSONObject widevine = drm.getJSONObject(KEY_WIDEVINE);
          if (!widevine.isNull(KEY_LICENSE_URL)) {
            _widevineServerPath = widevine.getString(KEY_LICENSE_URL);

            // TODO: DRM UUID has to be parsed separately and moved on to another part
            _widevineUUID = WIDEVINE_UUID;
          }
        }
      }
      _deliveryType = data.getString(KEY_DELIVERY_TYPE);
      _url = urlData.getString(KEY_DATA);
      _urlFormat = urlData.getString(KEY_FORMAT);
      _videoBitrate = data.isNull(KEY_VIDEO_BITRATE) ? _videoBitrate : data
          .getInt(KEY_VIDEO_BITRATE);
      _audioBitrate = data.isNull(KEY_AUDIO_BITRATE) ? _audioBitrate : data
          .getInt(KEY_AUDIO_BITRATE);
      _videoCodec = data.isNull(KEY_VIDEO_CODEC) ? _videoCodec : data
          .getString(KEY_VIDEO_CODEC);
      _height = data.isNull(KEY_HEIGHT) ? _height : data.getInt(KEY_HEIGHT);
      _width = data.isNull(KEY_WIDTH) ? _width : data.getInt(KEY_WIDTH);
      _framerate = data.isNull(KEY_FRAMERATE) ? _framerate : data
          .getString(KEY_FRAMERATE);
      _aspectRatio = data.isNull(KEY_ASPECT_RATIO) ? _aspectRatio : data
          .getString(KEY_ASPECT_RATIO);
      _isLiveStream = data.isNull(KEY_IS_LIVE_STREAM) ? _isLiveStream : data
          .getBoolean(KEY_IS_LIVE_STREAM);
      _profile = data.isNull(KEY_PROFILE) ? _profile : data.getString(KEY_PROFILE);
      if (!data.isNull(KEY_TOKEN_EXPIRE)) {
        _tokenExpireDateInMilliSeconds = data.getLong(KEY_TOKEN_EXPIRE) * 1000;
      }
    } catch (JSONException jsonException) {
      System.out.println("ERROR: Fail to update stream with dictionary because of invalid JSON: "
          + jsonException);
      return ReturnState.STATE_FAIL;
    }
    return ReturnState.STATE_MATCHED;
  }

  /**
   * Get the Delivery type of this Stream instance
   * @return the String of the Delivery Type. the available options are the Stream's class variables
   * which are prefixed with "DELIVERY_TYPE"
   */
  public String getDeliveryType() {
    return _deliveryType;
  }

  /**
   * Set the Delivery type of this Stream instance
   * @param deliveryType the String of the Delivery Type. the available options are the Stream's
   * class variables which are prefixed with "DELIVERY_TYPE"
   */
  public void setDeliveryType(String deliveryType) {
    this._deliveryType = deliveryType;
  }

  /**
   * Get the URL format for this Stream Instance
   * @return the URL format. the available options are the Stream's class variables which are
   * prefixed with "STREAM_URL_FORMAT"
   */
  public String getUrlFormat() {
    return _urlFormat;
  }

  /**
   * Set the URL format for this Stream instance
   * @param urlFormat the URL format. the available options are the Stream's class variables which
   * are prefixed with "STREAM_URL_FORMAT"
   */
  public void setUrlFormat(String urlFormat) {
    this._urlFormat = urlFormat;
  }

  /**
   * Get the video URL from this Stream instance
   * The returned string could be encoded or unencoded, based on the URL Format field.
   * @return the video url, or encoded string
   */
  public String getUrl() {
    return _url;
  }

  /**
   * Set the video URL of this Stream instance
   * If the video url is encoded, make sure you set the urlFormat
   * @param url the video url, or encoded string
   */
  public void setUrl(String url) {
    this._url = url;
  }

  /**
   * Get the unencoded video URL for the stream, whether it is encoded or not.
   * @return the unencoded video URL
   */
  public URL decodedURL() {
    try {
      if (_urlFormat.equals(STREAM_URL_FORMAT_B64)) { return new URL(new String(Base64.decode(_url,
              Base64.DEFAULT))); }
      return new URL(_url); // Otherwise assume plain text
    } catch (MalformedURLException exception) {
      System.out.println("Malformed URL: " + _url);
      return null;
    }
  }

  /**
   * Get the Widevine Server Path of this content.
   * This field is only populated for Widevine encoded assets
   * @return the Widevine server path, if available
   */
  public String getWidevineServerPath() {
    return _widevineServerPath;
  }

  /**
   * Get the Widevine UUID of this content.
   * This field is only populated for Widevine encoded assets
   * @return the Widevine UUID, if available
   */
  public UUID getWidevineUUID() {
    return _widevineUUID;
  }

  /**
   * Init the Widevine UUID of this content.
   * This field is only populated for Widevine encoded assets
   */
  public void initWidevineUUID() {
    _widevineUUID = WIDEVINE_UUID;
  }

  /**
   * Return true if the Stream has been marked as a live stream.
   * @return true if the stream is marked as a live stream, false otherwise
   */
  public boolean isLiveStream() {
    return _isLiveStream;
  }

  /**
   * Set if the Stream is marked as a live stream
   * @param isLiveStream true if the stream is live, false otherwise
   */
  public void setLiveStream(boolean isLiveStream) {
    this._isLiveStream = isLiveStream;
  }

  /*** MP4-Specific Getters/Setters ***/

  /**
   * Get the video bitrate of this Stream instance
   * NOTE: Only has information for MP4 assets. Other formats may not have this field populated
   * @return the video bitrate (MP4 Only)
   */
  public int getVideoBitrate() {
    return _videoBitrate;
  }

  /**
   * Set the video bitrate of this Stream instance
   * NOTE: Only has information for MP4 assets. Other formats may not have this field populated
   * @param videoBitrate (MP4 Only)
   */
  public void setVideoBitrate(int videoBitrate) {
    this._videoBitrate = videoBitrate;
  }

  /**
   * Get the audio bitrate of this Stream instance
   * NOTE: Only has information for MP4 assets. Other formats may not have this field populated
   * @return the audio bitrate (MP4 Only)
   */
  public int getAudioBitrate() {
    return _audioBitrate;
  }

  /**
   * Set the audio bitrate of this Stream instance
   * NOTE: Only has information for MP4 assets. Other formats may not have this field populated
   * @param audioBitrate the audio bitrate (MP4 Only)
   */
  public void setAudioBitrate(int audioBitrate) {
    this._audioBitrate = audioBitrate;
  }

  /**
   * Get the combined audio + video bitrate for this stream
   * NOTE: Only has information for MP4 assets. Other formats may not have this field populated
   * @return the combined audio and video bitrate
   */
  public int getCombinedBitrate() {
    return (_videoBitrate + _audioBitrate);
  }

  /**
   * Get the height of this Stream instance
   * NOTE: Only has information for MP4 assets. Other formats may not have this field populated
   * @return the height (MP4 Only)
   */
  public int getHeight() {
    return _height;
  }

  /**
   * Set the height of this Stream instance
   * NOTE: Only has information for MP4 assets. Other formats may not have this field populated
   * @param height the height (MP4 Only)
   */
  public void setHeight(int height) {
    this._height = height;
  }

  /**
   * Get the width of this Stream instance
   * NOTE: Only has information for MP4 assets. Other formats may not have this field populated
   * @return the width (MP4 Only)
   */
  public int getWidth() {
    return _width;
  }

  /**
   * Set the width of this Stream instance
   * NOTE: Only has information for MP4 assets. Other formats may not have this field populated
   * @param width the width (MP4 Only)
   */
  public void setWidth(int width) {
    this._width = width;
  }

  /**
   * Get the video codec of this Stream instance
   * NOTE: Only has information for MP4 assets. Other formats may not have this field populated
   * @return the video codec (MP4 Only)
   */
  @Deprecated
  public String getVideoCodec() {
    return _videoCodec;
  }

  /**
   * Set the video codec of this Stream instance
   * @param videoCodec the video codec (MP4 Only)
   */
  @Deprecated
  public void setVideoCodec(String videoCodec) {
    this._videoCodec = videoCodec;
  }

  /**
   * Get the framerate of this Stream instance
   * NOTE: Only has information for MP4 assets. Other formats may not have this field populated
   * @return the framerate (MP4 Only)
   */
  @Deprecated
  public String getFramerate() {
    return _framerate;
  }

  /**
   * Set the framerate of this Stream instance
   * @param framerate (MP4 Only)
   */
  @Deprecated
  public void setFramerate(String framerate) {
    this._framerate = framerate;
  }

  /**
   * Get the aspect ratio of this Stream instance
   * NOTE: May not be populated for any assets
   * @return the aspect ratio, if available
   */
  @Deprecated
  public String getAspectRatio() {
    return _aspectRatio;
  }

  /**
   * Set the aspect ratio of this Stream instance
   * NOTE: May not be populated for any assets
   * @param aspectRatio the aspect ratio
   */
  @Deprecated
  public void setAspectRatio(String aspectRatio) {
    this._aspectRatio = aspectRatio;
  }

  /**
   * Get the encoding profile of this Stream Instance
   * NOTE: Only has information for MP4 assets. Other formats may not have this field populated
   * @return the encoding profile of this Stream
   */
  public String getProfile() {
    return _profile;
  }

  /**
   * Set the encoding profile of this Stream Instance
   * NOTE: Only has information for MP4 assets. Other formats may not have this field populated
   * @param profile the encoding profile of the Stream
   */
  public void setProfile(String profile) {
    this._profile = profile;
  }

  /*** End MP4-Specific Getters/Setters ***/


  /**
   * Determine if the video type is playable on this this Android OS Version
   * Primarily checks if the delivery type is one of the expected delivery types, or if the type is
   * HLS, if the Android OS is above 3.0,
   * @param stream the Stream instance to test against
   * @return true if the Stream instance's delivery type should be playable, false otherwise
   */
  public static boolean isDeliveryTypePlayable(Stream stream) {
    String type = stream.getDeliveryType();
    /**
     * NOTE(jigish) Android 3.0+ supports HLS, but we support it only on 4.0+ to simplify secure HLS
     * implementation
     */

    boolean isHLS = type.equals(DELIVERY_TYPE_HLS) || type.equals (DELIVERY_TYPE_AKAMAI_HD2_VOD_HLS);
    boolean isSmooth = type.equals(DELIVERY_TYPE_SMOOTH);
    return type.equals(DELIVERY_TYPE_MP4) ||
           type.equals(DELIVERY_TYPE_REMOTE_ASSET) ||
           isSmooth ||
           (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && isHLS);
  }

  /**
   * Determine if the encoding profile is playable
   * This check is primarily for MP4 Playback - If the mp4 is not Baseline, we do not play it on
   * Android
   * @param stream the Stream instance to test against
   * @return true if the Stream instance's profile should be playable, false otherwise
   */
  public static boolean isProfilePlayable(Stream stream) {
    if (!DELIVERY_TYPE_MP4.equals(stream.getDeliveryType())) { return true; }
    return stream.getProfile() == null || PROFILE_BASELINE.equals(stream.getProfile());
  }

  /**
   * Determine the best stream from a set of streams.
   * If there are multiple streams, we prioritize remote assets, HLS, and DASH streams.
   * Outside of that, we then priortize Smooth, and finally MP4 streams
   * @param streams a set of Streams to test against
   * @param isWifiEnabled If wifi is enabled, we choose the highest quality of all MP4 bitrates
   * @return the best stream to play back.
   */
  public static Stream bestStream(Set<Stream> streams, boolean isWifiEnabled) {
    return _selector.bestStream(streams, isWifiEnabled);
  }

  /**
   * Check the set of Streams to see if any of the Streams are of a specific delivery type
   * @param streams a set of Streams to test against
   * @param deliveryType the desired Delivery Type
   * @return true if the set of streams contain the desired deliveryType
   */
  public static boolean streamSetContainsDeliveryType(Set<Stream> streams, String deliveryType) {
    return getStreamWithDeliveryType(streams, deliveryType) != null;
  }

  /**
   * Get a Stream that has the desired type
   * @param streams a set of Streams
   * @param desiredType the desired type
   * @return a Stream that satisfy the desired type, null otherwise
   */
  public static Stream getStreamWithDeliveryType(Set<Stream> streams, String desiredType) {
    if (streams == null || desiredType == null) {
      DebugMode.logE(TAG, "input parameters should not be null");
      return null;
    }

    for (Stream stream:streams) {
      String streamType = stream.getDeliveryType();
      if (streamType.equals(desiredType)) {
        return stream;
      } else if (streamType.equals(DELIVERY_TYPE_REMOTE_ASSET) && (stream.decodedURL() != null)) {
        String remoteAsset = stream.decodedURL().toString().toLowerCase();
        if ((desiredType.equals(DELIVERY_TYPE_HLS) || desiredType.equals(DELIVERY_TYPE_M3U8)) && remoteAsset.contains(".m3u8")) {
          return stream;
        }
        if (desiredType.equals(DELIVERY_TYPE_SMOOTH) && remoteAsset.contains(".ism")) {
          return stream;
        }
      }
    }

    return null;
  }

  /**
   * check if the stream url token has expired
   * @param currentTimeMillisecond current time in milliseconds
   * @return true if url token expires, false otherwise
   */
  public boolean hasTokenExpired(long currentTimeMillisecond) {
    if ((_tokenExpireDateInMilliSeconds != null) && (currentTimeMillisecond >= _tokenExpireDateInMilliSeconds)) {
      return true;
    }
    return false;
  }
}

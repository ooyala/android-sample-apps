package com.ooyala.android;

import com.ooyala.android.item.ContentItem;
import com.ooyala.android.item.Video;
import com.ooyala.android.util.DebugMode;

public class OoyalaException extends Exception {
  private static final long serialVersionUID = 1L;

  public enum OoyalaErrorCode {
    /** Authorization failed */
    ERROR_AUTHORIZATION_FAILED(0),
    /** Authorization Response invalid */
    ERROR_AUTHORIZATION_INVALID(1),
    /** Authorization Heartbeat failed.  Check properties. */
    ERROR_AUTHORIZATION_HEARTBEAT_FAILED(2),
    /** Content Tree Response invalid */
    ERROR_CONTENT_TREE_INVALID(3),
    /** The signature of the Authorization Response is invalid */
    ERROR_AUTHORIZATION_SIGNATURE_INVALID(4),
    /** Content Tree Next failed */
    ERROR_CONTENT_TREE_NEXT_FAILED(5),
    /** Playback failed */
    ERROR_PLAYBACK_FAILED(6),
    /** Asset not encoded */
    ERROR_ASSET_NOT_ENCODED(7),
    /** Internal Android error */
    ERROR_INTERNAL_ANDROID(8),
    /** Metadata fetch failed*/
    ERROR_METADATA_FETCH_FAILED(9),
    /** Device check found invalid auth token */
    ERROR_DEVICE_INVALID_AUTH_TOKEN(10),
    /** Device limit has been reached */
    ERROR_DEVICE_LIMIT_REACHED(11),
    /** Device binding failed */
    ERROR_DEVICE_BINDING_FAILED(12),
    /** Device id is too long */
    ERROR_DEVICE_ID_TOO_LONG(13),
    /** General non-Ooyala related DRM failure. stack trace of resulting failure is attached */
    ERROR_DRM_GENERAL_FAILURE(14),
    /** DRM File download failed */
    ERROR_DRM_FILE_DOWNLOAD_FAILED(15),

    /** Errors from DRM Stream Players */
    /** DRM Personalization/Device Identification failed */
    ERROR_DRM_PERSONALIZATION_FAILED(16),
    /** DRM Rights Acquisition server error */
    ERROR_DRM_RIGHTS_SERVER_ERROR(17),
    /** Expected discovery parameters are not provided */
    ERROR_DISCOVERY_INVALID_PARAMETER(18),
    /** Discovery network error */
    ERROR_DISCOVERY_NETWORK_ERROR(19),
    /** Discovery response failure */
    ERROR_DISCOVERY_FAILED_RESPONSE(20),
    /** No available streams */
    ERROR_NO_AVAILABLE_STREAMS(21),
    /** Provided PCode does not match embed code owner */
    ERROR_PCODE_MATCH_FAILED(22),
    /** Failed to download */
    ERROR_DOWNLOAD_FAILURE(23),
    /** Concurrent Streams limit reached */
    ERROR_DEVICE_CONCURRENT_STREAMS(24),

    /** Advertising Id Errors */
    /** Failed to obtain Advertising Id. */
    ERROR_ADVERTISING_ID_FAILURE(25),
    /** Failed to get discovery results. */
    ERROR_DISCOVERY_GET_FAILURE(26),
    /** Failed to post discovery pins. */
    ERROR_DISCOVERY_POST_FAILURE(27),
    /** Unknown error */
    ERROR_UNKNOWN(28);

    private final int errorCode;

    OoyalaErrorCode (int errorCode) {
      this.errorCode = errorCode;
    }

    public int getErrorCode() {
      return errorCode;
    }
  };

   private OoyalaErrorCode _code;

  public OoyalaException(OoyalaErrorCode code, String description, Throwable throwable) {
    super(description, throwable);
    _code = code;
  }

  public OoyalaException(OoyalaErrorCode code, String description) {
    super(description);
    _code = code;
  }

  public OoyalaException(OoyalaErrorCode code) {
    super();
    _code = code;
  }

  public OoyalaException(OoyalaErrorCode code, Throwable throwable) {
    super(throwable);
    _code = code;
  }

  public OoyalaErrorCode getCode() {
    return _code;
  }

  public int getIntErrorCode() {
    return _code.getErrorCode();
  }

  /**
   * Generate the authorization error of a video item.
   *
   * @param currentItem
   * @return a properly described OoyalaException
   */
  static OoyalaException getAuthError(String tag, Video currentItem) {
    // Get description and make the exception
    String description = "Authorization Error: "
            + ContentItem.getAuthError(currentItem.getAuthCode());
    DebugMode.logE(tag, "This video was not authorized: "
            + description);
    return new OoyalaException(
            OoyalaException.OoyalaErrorCode.ERROR_AUTHORIZATION_FAILED, description);
  }

  /** Generate the PCode match error.
   *
   * @param providedPcode
   * @param assetPcode
   * @return a properly described OoyalaException
   */
  static OoyalaException getPCodeMatchError(String tag, String providedPcode, String assetPcode) {
    String description = "Provided PCode and Embed Code owner do not match";
    DebugMode.logE(tag, description + " Provided PCode: " + providedPcode
            + " Embed Code Owner: " + assetPcode);
    return new OoyalaException(
            OoyalaErrorCode.ERROR_PCODE_MATCH_FAILED, description);
  }
}

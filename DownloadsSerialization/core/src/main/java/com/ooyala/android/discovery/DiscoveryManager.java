package com.ooyala.android.discovery;

import com.ooyala.android.EmbeddedSignatureGenerator;
import com.ooyala.android.OoyalaException;
import com.ooyala.android.Utils;
import com.ooyala.android.util.DebugMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zchen on 12/9/15.
 */
public class DiscoveryManager {
  private static final String TAG = DiscoveryManager.class.getName();

  private static final String DISCOVERY_HOST = "http://api.ooyala.com";

  private static final String DISCOVERY_MOMENTUM = "/v2/discover/trending/momentum";
  private static final String DISCOVERY_POPULAR = "/v2/discover/trending/top";
  private static final String DISCOVERY_SIMILAR_ASSETS = "/v2/discover/similar/assets/";
  private static final String DISCOVERY_FEEDBACK_IMPRESSION = "/v2/discover/feedback/impression";
  private static final String DISCOVERY_FEEDBACK_CLICK = "/v2/discover/feedback/play";

  private static final String KEY_PCODE = "pcode";
  private static final String KEY_DEVICE_ID = "device_id";
  private static final String KEY_SIGN_VERSION = "sign_version";
  private static final String KEY_BUCKET_INFO = "bucket_info";
  private static final String KEY_EXPIRES = "expires";
  private static final String KEY_SIGNATURE = "signature";
  private static final String KEY_LIMIT = "limit";
  private static final String KEY_RESULTS = "results";
  private static final String KEY_MESSAGE = "message";

  private static final String VALUE_PLAYER = "player";
  private static final int RESPONSE_LIFE_SECONDS = 5 * 60;

  public interface Callback {
    /**
     * This callback is used for asynchronous contentTree calls
     * @param results an JSONArray for success getResults calls, String message otherwise.
     */
    public void callback(Object results, OoyalaException error);
  }

  private static interface DiscoveryResultsCallback {
    public void callback(String httpResponse, OoyalaException error);
  }


/**
 * get the discovery results
 * @param[in] options the discovery option;
 * @param[in] embedCode the embed code for discovery type similar assets, ignored for other discovery types
 * @param[in] pcode the pcode
 * @param[in] deviceId the device id
 * @param[in] parameters the parameters as key value pair
 *  for a detailed list and examples of valid parameters, please refer to
 *  http://support.ooyala.com/developers/documentation/concepts/content_discovery_summary_of_routes.html
 * @param[in] callback the callback function that handles discovery results.the callback might not be called on the main thread.
 */
  public static void getResults (
      DiscoveryOptions options,
      String embedCode,
      String pcode,
      String deviceId,
      Map<String, String>  parameters,
      final Callback callback) {
    if (callback == null) {
      DebugMode.logE(TAG, "Discovery callback should not be null");
      return;
    }

    if (pcode == null || pcode.length() <= 0 || deviceId == null || deviceId.length() <= 0) {
      OoyalaException e =
          new OoyalaException(OoyalaException.OoyalaErrorCode.ERROR_UNKNOWN, "missing pcode or deviceId");
      callback.callback(null, e);
      return;
    }

    String uri = discoveryUri(options.getType(), embedCode);
    if (uri == null) {
      OoyalaException e =
          new OoyalaException(OoyalaException.OoyalaErrorCode.ERROR_UNKNOWN, "failed to generate discovery uri");
      callback.callback(null, e);
      return;
    }

    Map<String, String> allParams =
        parameters == null ? new HashMap<String, String>() : new HashMap<String, String>(parameters);

    allParams.put(KEY_PCODE, pcode);
    allParams.put(KEY_LIMIT, Long.toString(options.getLimit()));
    allParams.put(KEY_DEVICE_ID, deviceId);
    URL url =  signedUrlForHost(DISCOVERY_HOST, uri, allParams);
    DiscoveryTaskInfo taskInfo =
        new DiscoveryTaskInfo(
            url, false, null, options.getTimoutInMilliSeconds(), options.getTimoutInMilliSeconds());
    DiscoveryTask task = new DiscoveryTask(taskInfo, new DiscoveryResultsCallback() {
      @Override
      public void callback(String results, OoyalaException error) {
        if (error != null) {
          callback.callback(null, error);
        }
        try {
          JSONObject jsonObject = new JSONObject(results);
          if (jsonObject.has(KEY_RESULTS)) {
            JSONArray discoveryItems = jsonObject.getJSONArray(KEY_RESULTS);
            callback.callback(discoveryItems, null);
          } else {
            String errorMessage = "discovery results failure";
            String errorDetail = jsonObject.getString(KEY_MESSAGE);
            callback.callback(null, new OoyalaException(OoyalaException.OoyalaErrorCode.ERROR_DISCOVERY_GET_FAILURE, errorMessage));
          }
        } catch (JSONException e) {
          callback.callback(
              null, new OoyalaException(
                  OoyalaException.OoyalaErrorCode.ERROR_DISCOVERY_GET_FAILURE, "failed to parse json results", e));
        }
      }

    });
    Utils.sharedExecutorService().submit(task);
  }

/**
 * send discovery feedback impression when discovery is shown to user
 * @param[in] options the discovery options
 * @param[in] bucketInfo the bucket info id
 * @param[in] pcode the pcode
 * @param[in] deviceId the device id
 * @param[in] parameters the parameters as key value pair
 *  for a detailed list and examples of valid parameters, please refer to
 *  http://support.ooyala.com/developers/documentation/concepts/content_discovery_summary_of_routes.html
 */
  public static void sendImpression (
      DiscoveryOptions options,
      String bucketInfo,
      String pcode,
      String deviceId,
      Map<String, String> parameters,
      Callback callback) {
    sendFeedback(options, bucketInfo, pcode, deviceId, parameters, callback, DISCOVERY_FEEDBACK_IMPRESSION);
  }

/**
 * send discovery feedback click when user clicks to play an item
 * @param[in] options the discovery options
 * @param[in] bucketInfo the bucket info id
 * @param[in] pcode the pcode
 * @param[in] deviceId the device id
 * @param[in] parameters the parameters as key value pair
 */
  public static void sendClick (
      DiscoveryOptions options,
      String bucketInfo,
      String pcode,
      String deviceId,
      Map<String, String> parameters,
      Callback callback) {
    sendFeedback(options, bucketInfo, pcode, deviceId, parameters, callback, DISCOVERY_FEEDBACK_CLICK);
  }

  private static class DiscoveryTaskInfo {
    private URL url;
    private boolean postMethod; // set true to use HTTP POST, false to use HTTP GET
    private Map<String, String> body; // body for the post method
    private long connectionTimeoutInMillisecond;
    private long readTimeoutInMillisecond;

    DiscoveryTaskInfo(
        URL url,
        boolean postMethod,
        Map<String, String> body,
        long connectionTimeoutInMillisecond,
        long readTimeoutInMillisecond) {
      this.url = url;
      this.postMethod = postMethod;
      this.body = body;
      this.connectionTimeoutInMillisecond = connectionTimeoutInMillisecond;
      this.readTimeoutInMillisecond = readTimeoutInMillisecond;
    }

    public URL getUrl() {
      return url;
    }

    public boolean isPostMethod() {
      return postMethod;
    }

    public Map<String, String> getBody() {
      return body;
    }

    public long getConnectionTimeoutInMillisecond() {
      return connectionTimeoutInMillisecond;
    }

    public long getReadTimeoutInMillisecond() {
      return readTimeoutInMillisecond;
    }
  }

  private static class DiscoveryTask implements Runnable {
    private final DiscoveryTaskInfo taskInfo;
    private final DiscoveryResultsCallback callback;

    DiscoveryTask(DiscoveryTaskInfo info, DiscoveryResultsCallback callback) {
      this.taskInfo = info;
      this.callback = callback;
    }

    @Override
    public void run() {
      String httpResponse = null;

      if (taskInfo.isPostMethod()) {
        String jsonString = Utils.mapToJsonString(taskInfo.getBody());
        httpResponse =
            Utils.postUrl(
            taskInfo.getUrl(),
            jsonString,
            (int)taskInfo.getConnectionTimeoutInMillisecond(),
            (int)taskInfo.getReadTimeoutInMillisecond());

      } else {
        httpResponse =
            Utils.getUrlContent(
                taskInfo.getUrl(),
                (int) taskInfo.getConnectionTimeoutInMillisecond(),
                (int) taskInfo.getReadTimeoutInMillisecond());
      }
      if (callback != null) {
        callback.callback(httpResponse, null);
      }
    }

  }

  // helper methods
  private static String discoveryUri(DiscoveryOptions.Type type, String embedCode) {
    switch (type) {
      case Momentum:
        return DISCOVERY_MOMENTUM;
      case Popular:
        return DISCOVERY_POPULAR;
      case SimilarAssets:
        return embedCode == null ? null : DISCOVERY_SIMILAR_ASSETS + embedCode;
      default:
        DebugMode.logE(TAG, "unknown discovery type" + type.toString());
        return null;
    }
  }

  private static URL signedUrlForHost(String host, String uri, Map<String, String> parameters) {
    String pcode = parameters.get(KEY_PCODE);
    parameters.put(KEY_PCODE, null);
    EmbeddedSignatureGenerator signatureGenerator = new EmbeddedSignatureGenerator(pcode);

    long secondsSince1970 = (new Date()).getTime() / 1000;
    parameters.put(KEY_EXPIRES, Long.toString(secondsSince1970 + RESPONSE_LIFE_SECONDS));
    // Discovery APIs allows client to use pcode to sign the pins if sign version is set to player.
    parameters.put(KEY_SIGN_VERSION, VALUE_PLAYER);
    String stringToSign = genStringToSignFromDict(parameters);
    parameters.put(KEY_SIGNATURE, signatureGenerator.sign(stringToSign));

    parameters.put(KEY_PCODE, pcode);
    URL url = Utils.makeURL(host, uri, parameters);
    return url;
  }

  private static String genStringToSignFromDict(Map<String, String> params) {
    String paramsString = Utils.getParamsString(params, "", false);
    return paramsString;
  }

  private static void sendFeedback (
      DiscoveryOptions options,
      String bucketInfo,
      String pcode,
      String deviceId,
      Map<String, String> parameters,
      final Callback callback,
      String uri) {
    Map<String, String> body =
        parameters == null ? new HashMap<String, String>() : new HashMap<String, String>(parameters);
    body.put(KEY_BUCKET_INFO, bucketInfo);
    body.put(KEY_DEVICE_ID, deviceId);

    Map<String, String> head = new HashMap<String, String>();
    head.put(KEY_PCODE, pcode);
    URL url = DiscoveryManager.signedUrlForHost(DISCOVERY_HOST, uri, head);
    DiscoveryTaskInfo taskInfo =
        new DiscoveryTaskInfo(
            url, true, body, options.getTimoutInMilliSeconds(), options.getTimoutInMilliSeconds());
    DiscoveryTask task = new DiscoveryTask(taskInfo, new DiscoveryResultsCallback() {
      @Override
      public void callback(String httpResponse, OoyalaException error) {
        String results = null;
        OoyalaException e = null;
        if (httpResponse.equals("OK")) {
          results = httpResponse;
        } else {
          e = new OoyalaException(OoyalaException.OoyalaErrorCode.ERROR_DISCOVERY_POST_FAILURE, httpResponse);
        }
        if (callback != null) {
          callback.callback(results, e);
        }
      }
    });
    Utils.sharedExecutorService().submit(task);
  }
}
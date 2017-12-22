package com.ooyala.android;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Base64;

import com.ooyala.android.util.DebugMode;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

public class Utils {
  static final String DEVICE_ANDROID_SDK = "android_sdk";
  /** TODO[jigish] change to android_hls_sdk when SAS is pushed */
  static final String DEVICE_ANDROID_HLS_SDK = "android_3plus_sdk";
  static final String DEVICE_IPAD = "ipad"; // hack for Washington Post - See PB-279

  static final String SEPARATOR_AMPERSAND = "&";
  static final String SEPARATOR_TIME = ":";
  static final String CHARSET = "UTF-8";
  static final int PING_TIMEOUT = 60 * 1000;
  static final int THREAD_POOL_SIZE = 20;

  private static final String TAG = Utils.class.getSimpleName();

  private static final ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
  public static ExecutorService sharedExecutorService() {
    return executor;
  }

  private static class PingTask implements Runnable {
    private URL url;

    PingTask(URL url) {
      this.url = url;
    }

    public void run() {
      String httpResponse = getUrlContent(url, PING_TIMEOUT, PING_TIMEOUT);
      DebugMode.logD(TAG, "ping url:" + url.toString() + " results: " + httpResponse);
    }
  }

  public static String device() {
    // temporarily disable HLS
    if (OoyalaPlayer.enableHighResHLS) { // hack for Washington Post - See PB-279
      return DEVICE_IPAD;
    } else if (OoyalaPlayer.enableHLS || Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
      return DEVICE_ANDROID_HLS_SDK;
    } else return DEVICE_ANDROID_SDK;
  }

  public static URL makeURL(String host, String uri, Map<String, String> params) {
    return makeURL(host, uri, getParamsString(params, SEPARATOR_AMPERSAND, true));
  }

  public static URL makeURL(String host, String uri, String params) {
    try {
      return new URL(host + uri + (params == null || params.length() < 1 ? "" : "?" + params));
    } catch (MalformedURLException e) {
      DebugMode.logE(TAG, "URL format exception:" + e.getMessage(), e);
    }
    return null;
  }

  public static <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
    List<T> list = new ArrayList<T>(c);
    java.util.Collections.sort(list);
    return list;
  }

  public static String getParamsString(Map<String, String> params, String separator, boolean urlEncode) {
    if (params == null || params.isEmpty()) { return ""; }

    StringBuffer result = new StringBuffer();
    boolean first = true;
    for (String key : asSortedList(params.keySet())) {
      if (key == null || params.get(key) == null) {
        continue;
      }

      if (first) {
        first = false;
      } else {
        result.append(separator);
      }

      result.append(key);
      result.append("=");
      if (urlEncode) {
        try {
          result.append(URLEncoder.encode(params.get(key), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
          DebugMode.logE(Utils.class.getName(), "ERROR while trying to encode parameter:" + e.getMessage(), e);
          result.append(params.get(key));
        }
      } else {
        result.append(params.get(key));
      }
    }
    return result.toString();
  }

  public static <T> Set<T> getSubset(Map<String, T> objects, int firstIndex, int count) {
    if (firstIndex < 0 || firstIndex + count > objects.size()) { return null; }

    Iterator<T> iterator = objects.values().iterator();
    for (int i = 0; i < firstIndex && iterator.hasNext(); i++) {
      iterator.next();
    }

    Set<T> result = new HashSet<T>();
    for (int i = 0; i < count && iterator.hasNext(); i++) {
      result.add(iterator.next());
    }
    return result;
  }

  public static String join(Collection<? extends Object> list, String separator) {
    if (list == null) { return null; }

    StringBuffer result = new StringBuffer();

    for (Object o : list) {
      result.append(o.toString());
      result.append(separator);
    }

    if (result.length() > 0) {
      result = result.deleteCharAt(result.length() -1);
    }

    return result.toString();
  }

  public static boolean isNullOrEmpty(String string) {
    return string == null || string.equals("");
  }

  public static Object getJSONValueOrElse( JSONObject json, String key, Object orElse ) {
    Object value;
    if (json == null) {
      value = orElse;
    } else {
      try {
        value = json.get(key);
      } catch (JSONException e) {
        value = orElse;
      }
    }
    return value;
  }

  public static JSONObject objectFromJSON(String json) {
    try {
      return (JSONObject) new JSONTokener(json).nextValue();
    } catch (JSONException exception) {
      System.out.println("JSONException: " + exception);
      return null;
    } catch (ClassCastException exception) {
      System.out.println("ClassCastException: " + exception);
      return null;
    }
  }

  public static void overwriteJSONObject( JSONObject src, JSONObject dst ) throws JSONException {
    if( src != null && dst != null ) {
      final Iterator<?> ccks = src.keys();
      while( ccks.hasNext() ) {
        final String ck = String.valueOf( ccks.next() );
        final Object cv = src.get( ck );
        dst.put( ck, cv );
      }
    }
  }

  public static String encryptString(String rawString) {
    byte[] bytes = rawString.getBytes();
    MessageDigest digest = null;
    try {
      digest = MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException e) {
      DebugMode.logE(TAG, "Encryption exception: " + e.getMessage(), e);
      return null;
    }
    digest.reset();
    String encrypted = Base64.encodeToString(digest.digest(bytes),
        Base64.NO_WRAP);
    return encrypted;
  }

  /**
   * Block the current thread while getting the Ooyala Player Token
   * @param generator an implemented EmbedTokenGenerator to generate an embed code, can be null
   * @param embedCodes the List of embed codes which need a generated embed token
   * @return a string of the Ooyala Player Token, or null if there is no generator or an error
   */
  public static String blockingGetEmbedTokenForEmbedCodes(EmbedTokenGenerator generator, List<String> embedCodes) {
    if (generator != null) {
      DebugMode.logD(TAG, "Requesting an OPT for Chromecast");
      final Semaphore sem = new Semaphore(0);
      final AtomicReference<String> tokenReference = new AtomicReference<String>();
      generator.getTokenForEmbedCodes(embedCodes, new EmbedTokenGeneratorCallback() {

        @Override
        public void setEmbedToken(String token) {
          tokenReference.set(token);
          sem.release();
        }
      });
      try {
        sem.acquire();
      } catch (InterruptedException e) {
        DebugMode.logE(TAG, "Embed Token request was interrupted:" + e.getMessage(), e);
        return null;
      }
      return tokenReference.get();
    }
    else {
      DebugMode.logD(TAG, "No embed token generator to get an OPT");
      return null;
    }
  }

  /**
   * Get string from http get method
   * @param url the url
   * @param connectionTimeoutInMillisecond connectionTimeOut
   * @param readTimeoutInMillisecond readTimeout
   * @return a string of http response
   */
  public static String getUrlContent (
      URL url,
      int connectionTimeoutInMillisecond,
      int readTimeoutInMillisecond) {
    DebugMode.logD(TAG, "Sending Http Request: " + url.toString());
    StringBuffer sb = new StringBuffer();
    BufferedReader rd = null;
    try {
      URLConnection conn = url.openConnection();
      conn.setConnectTimeout(connectionTimeoutInMillisecond);
      conn.setReadTimeout(readTimeoutInMillisecond);
      rd = new BufferedReader(new InputStreamReader(conn.getInputStream()), 8192);
      String line;
      while ((line = rd.readLine()) != null) {
        sb.append(line);
      }
    } catch (SocketTimeoutException e) {
      DebugMode.logE(TAG, "Connection to " + url.toString() + " timed out:" + e.getMessage(), e);
    } catch (IOException e) {
      DebugMode.logE(TAG, "IOException: " + e.getMessage(), e);
    } finally {
      if (rd != null) {
        try {
          rd.close();
        } catch (IOException e) {
          DebugMode.logE(TAG, "IOException: " + e.getMessage(), e);
        }
      }
    }

    return sb.toString();
  }

  /**
   * calling http post method
   * @param url the url
   * @param body the post body
   * @param connectionTimeoutInMillisecond connectionTimeOut
   * @param readTimeoutInMillisecond readTimeout
   * @return the response message
   * */
  public static String postUrl (
      URL url,
      String body,
      int connectionTimeoutInMillisecond,
      int readTimeoutInMillisecond) {
    final String charset = "utf-8";
    byte[] bodyBytes = null;

    try {
      bodyBytes = body.getBytes(charset);
    } catch (UnsupportedEncodingException ex) {
      DebugMode.logE(TAG, "Unsupported Encoding: " + body + " : " + ex.getMessage(), ex);
      return null;
    }
    int responseStatus = 0;
    String responseMessage = "";
    InputStream errorStream;

    try {
      HttpURLConnection conn = preparePost(url, bodyBytes, null, connectionTimeoutInMillisecond, readTimeoutInMillisecond);

      responseStatus = conn.getResponseCode();
      responseMessage = conn.getResponseMessage();
      if (responseStatus != 200) {
        errorStream = conn.getErrorStream();
        if (errorStream != null) {
          String errorString = "";
          String line;
          BufferedReader br = new BufferedReader(new InputStreamReader(errorStream));
          while ((line = br.readLine()) != null) {
            errorString += line;
          }
          errorStream.close();
          DebugMode.logE(TAG,
              "the http response for post method is" + Integer.toString(responseStatus) +
                  " error:" + errorString);
        }
      }
    } catch (SocketTimeoutException e) {
      DebugMode.logE(TAG, "Connection to " + url.toString() + " timed out:" + e.getMessage(), e);
    } catch (IOException e) {
      DebugMode.logE(TAG, "IOException: " + e.getMessage(), e);
    }
    return responseMessage;
  }

  /**
   * calling http post method and returns the response byte array
   * @param urlString the url
   * @param body the post body
   * @param requestProperties the properties for http connections
   * @param connectionTimeoutInMillisecond connectionTimeOut
   * @param readTimeoutInMillisecond readTimeout
   * @return the response message
   * */
  public static byte[] bytesFromPostUrl(
      String urlString,
      byte[] body,
      Map<String, String> requestProperties,
      int connectionTimeoutInMillisecond,
      int readTimeoutInMillisecond) {
    URL url = null;
    try {
      url = new URL(urlString);
    } catch (MalformedURLException ex) {
      DebugMode.logE(TAG, "url is not formatted correctly :" + urlString);
      return null;
    }
    HttpURLConnection urlConnection = null;
    try {

      urlConnection = preparePost(url, body, requestProperties, connectionTimeoutInMillisecond, readTimeoutInMillisecond);
      // Read and return the response body.
      InputStream inputStream = urlConnection.getInputStream();
      try {
        return toByteArray(inputStream);
      } finally {
        inputStream.close();
      }
    } catch (IOException ex) {
      DebugMode.logE(TAG, "failed to post url " + urlString + ": " + ex.getMessage(), ex);
    } finally {
      if (urlConnection != null) {
        urlConnection.disconnect();
      }
    }
    return null;
  }

  private static byte[] toByteArray(InputStream inputStream) throws IOException {
    byte[] buffer = new byte[1024 * 4];
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    int bytesRead;
    while ((bytesRead = inputStream.read(buffer)) != -1) {
      outputStream.write(buffer, 0, bytesRead);
    }
    return outputStream.toByteArray();
  }

  private static HttpURLConnection preparePost(URL url, byte[] body, Map<String, String>properties, int connectionTimeout, int readTimeout)
      throws SocketTimeoutException, IOException {
    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
    conn.setConnectTimeout(connectionTimeout);
    conn.setReadTimeout(readTimeout);
    conn.setDoOutput(body != null);
    conn.setRequestMethod("POST");
    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    conn.setUseCaches(false);
    if (properties != null) {
      for (Map.Entry<String, String> requestProperty : properties.entrySet()) {
        conn.setRequestProperty(requestProperty.getKey(), requestProperty.getValue());
      }
    }
    if (body != null) {
      conn.setRequestProperty("Content-Length", Integer.toString(body.length));
      DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
      wr.write(body);
    }

    return conn;
  }

  /**
   * convert a map to a json string
   * @param map the map to be converted
   * @return the converted string, null if error occurs
   **/
  public static String mapToJsonString(Map<String, String> map) {
    if (map == null) {
      return "";
    }
    JSONObject jsonObject = new JSONObject(map);
    String jsonString = jsonObject.toString();
    return jsonString;
  }

  /**
   * Simply send an HTTP get via shared thread pool, usually used to send tracking feedbacks
   * @param url the url to ping
   **/
  public static void pingUrl(URL url) {
    if (url == null) {
      return;
    }
    PingTask task = new PingTask(url);
    executor.submit(task);
  }

  /**
   * open a url in device browser
   * @param context a context
   * @param url the url string to open
   */
  public static void openUrlInBrowser(Context context, String url) {
    if (url == null || url.length() <= 0) {
      return;
    }

    try {
      url = url.trim(); //strip leading and trailing whitespace
      Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
      context.startActivity(browserIntent);
      DebugMode.logD(TAG, "Opening browser to " + url);
    } catch (Exception e) {
      DebugMode.logE(TAG, "There was some exception on clickthrough!", e);
    }
  }

  public static void bytesToFile(File file, byte[] bytes) throws IOException {
    if (file == null || bytes == null) {
      return;
    }

    OutputStream os = null;
    try {
      os = new FileOutputStream(file);
      os.write(bytes);
    } catch (IOException ex) {
      DebugMode.logE(TAG, "cannot write to file " + file.getAbsolutePath() + ":" + ex.getLocalizedMessage(), ex);
      throw ex;
    } finally {
      try {
        if (os != null) {
          os.close();
        }
      } catch (IOException ex) {
        DebugMode.logE(TAG, "cannot close the file for write " + ex.getLocalizedMessage(), ex);
        throw ex;
      }
    }
  }

  public static byte[] bytesFromFile(File file) {
    byte[] bytes = null;
    if (file != null && file.exists()) {
      InputStream is = null;
      try {
        bytes = new byte[(int)file.length()];
        is = new FileInputStream(file);
        is.read(bytes);
      } catch (IOException ex) {
        DebugMode.logE(TAG, "cannot read file to bytes for " + file.getAbsolutePath() + ":" + ex.getLocalizedMessage(), ex);
        return null;
      } finally {
        try {
          if (is != null) {
            is.close();
          }
        } catch (IOException ex){

        }
      }
    }
    return bytes;
  }

  public static boolean objectToFile(File file, Serializable object) throws IOException {
    if (file == null || object == null) {
      DebugMode.logE(TAG, "write to file failed, either file or object is null");
      return false;
    }

    OutputStream fos = null;
    ObjectOutputStream oos = null;

    try {
      fos = new FileOutputStream(file);
      oos = new ObjectOutputStream(fos);
      oos.writeObject(object);
    } catch (IOException ex) {
      DebugMode.logE(TAG, "cannot write to file " + file.getAbsolutePath() + ":" + ex.getLocalizedMessage(), ex);
      throw ex;
    } finally {
      try {
        if (oos != null) {
          oos.close();
        }
        if (fos != null) {
          fos.close();
        }
      } catch (IOException ex) {
        DebugMode.logE(TAG, "cannot close the file for write " + ex.getLocalizedMessage(), ex);
        throw ex;
      }
    }
    return true;
  }

  public static HashMap<String, String> mapFromFile(File file) {
    HashMap<String, String> map = null;
    if (file != null && file.exists()) {
      InputStream fis = null;
      ObjectInputStream ois = null;
      try {
        fis = new FileInputStream(file);
        ois = new ObjectInputStream(fis);
        map = (HashMap<String,String>)ois.readObject();
      } catch (IOException | ClassNotFoundException ex) {
        DebugMode.logE(TAG, "cannot read file to bytes for " + file.getAbsolutePath() + ":" + ex.getLocalizedMessage(), ex);
      } finally {
        try {
          if (ois != null) {
            ois.close();
          }
          if (fis != null) {
            fis.close();
          }
        } catch (IOException ex){
          DebugMode.logE(TAG, "cannot close the file for read " + ex.getLocalizedMessage(), ex);
        }
      }
    }
    return map;
  }

}

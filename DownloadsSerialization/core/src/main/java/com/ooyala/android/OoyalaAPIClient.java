package com.ooyala.android;

import android.os.AsyncTask;

import com.ooyala.android.configuration.Options;
import com.ooyala.android.item.AuthorizableItem;
import com.ooyala.android.item.ContentItem;
import com.ooyala.android.item.PaginatedParentItem;
import com.ooyala.android.item.Video;
import com.ooyala.android.util.DebugMode;

import org.json.JSONObject;

import java.net.URL;
import java.util.List;
import java.util.Map;

public class OoyalaAPIClient {
  private PlayerAPIClient _playerAPI = null;
  private SecureURLGenerator _secureUrlGenerator = null;

  /**
   * Instantiate an OoyalaAPIClient
   * @param apiKey the API Key to use for secured APIs
   * @param secret the Secret to use for secured APIs
   * @param pcode the Provider Code
   * @param domain the Embed Domain to use
   */
  public OoyalaAPIClient(String apiKey, String secret, String pcode, PlayerDomain domain) {
    this(new EmbeddedSecureURLGenerator(apiKey, secret), pcode, domain, null);
  }

  /**
   * Instantiate an OoyalaAPIClient
   * @param apiKey the API Key to use for secured APIs
   * @param secret the Secret to use for secured APIs
   * @param pcode the Provider Code
   * @param domain the Embed Domain to use
   * @param options the options to use
   */
  public OoyalaAPIClient(String apiKey, String secret, String pcode, PlayerDomain domain, Options options) {
    this(new EmbeddedSecureURLGenerator(apiKey, secret), pcode, domain, options);
  }

  /**
   * Instantiate an OoyalaAPIClient
   * @param apiKey the API Key to use for secured APIs
   * @param signatureGenerator the SignatureGenerator to use for secured APIs
   * @param pcode the Provider Code
   * @param domain the Embed Domain to use
   */
  public OoyalaAPIClient(String apiKey, SignatureGenerator signatureGenerator, String pcode, PlayerDomain domain, Options options) {
    this(new EmbeddedSecureURLGenerator(apiKey, signatureGenerator), pcode, domain, options);
  }

  /**
   * Instantiate an OoyalaAPIClient
   * @param secureURLGenerator the SecureURLGenerator to use for secured APIs
   * @param pcode the Provider Code
   * @param domain the Embed Domain to use
   */
  public OoyalaAPIClient(SecureURLGenerator secureURLGenerator, String pcode, PlayerDomain domain, Options options) {
    this(pcode, domain, options);
    _secureUrlGenerator = secureURLGenerator;
  }

  /**
   * Instantiate an OoyalaAPIClient
   * @param pcode the Provider Code
   * @param domain the Embed Domain to use
   */
  public OoyalaAPIClient(String pcode, PlayerDomain domain, Options options) {
    this(new PlayerAPIClient(pcode, domain, null, options));
  }

  /**
   * Instantiate an OoyalaAPIClient
   * @param pcode the Provider Code
   * @param domain the Embed Domain to use
   */
  public OoyalaAPIClient(String pcode, PlayerDomain domain, EmbedTokenGenerator generator, Options options) {
    this(new PlayerAPIClient(pcode, domain, generator, options));
  }

  /**
   * Instantiate an OoyalaAPIClient
   * @param apiClient the PlayerAPIClient to use
   */
  OoyalaAPIClient(PlayerAPIClient apiClient) {
    _playerAPI = apiClient;
  }

  /**
   * Perform a SAS Authorization on an authorizable item
   * root item is assumed to be a Dynamic Channel and the embed codes are assumed to all be videos. As this
   * method is not asynchronous it should be used within an AsyncTask.
   * @param item the content item to authorize
   * @param playerInfo a PlayerInfo object to determine what can be returned
   * @return success if authorization was successful (not if the item is authorized)
   * @throws OoyalaException
   */
  public boolean authorize(AuthorizableItem item, PlayerInfo playerInfo) throws OoyalaException {
    return _playerAPI.authorize(item, playerInfo);
  }

  /**
   * Perform a SAS Authorization and creates an Video object
   * @return the video object
   * @throws OoyalaException
   */
  public Video authorizeDownload(String embedCode, PlayerInfo playerInfo) throws OoyalaException {
    return _playerAPI.authorizeDownload(embedCode, playerInfo);
  }

  /**
   * Fetch the root ContentItem associated with the given embed codes. If multiple embed codes are given, the
   * root item is assumed to be a Dynamic Channel and the embed codes are assumed to all be videos. As this
   * method is not asynchronous it should be used within an AsyncTask.
   * @param embedCodes the embed codes to fetch
   * @return the root ContentItem representing embedCodes
   * @throws OoyalaException
   */
  public ContentItem contentTree(List<String> embedCodes) throws OoyalaException {
    return _playerAPI.contentTree(embedCodes);
  }

  /**
   * Fetch the root ContentItem associated with the given embed codes and ad set code. If multiple embed
   * codes are given, the root item is assumed to be a Dynamic Channel and the embed codes are assumed to all
   * be videos. The ad set code specifies which ad set be used. As this method is not asynchronous it should
   * be used within an AsyncTask.
   * @param embedCodes the embed codes to fetch
   * @param adSetCode the ad set code to assign, can be null.
   * @return the root ContentItem representing embedCodes
   * @throws OoyalaException
   */
  public ContentItem contentTreeWithAdSet(List<String> embedCodes, String adSetCode) throws OoyalaException {
    return _playerAPI.contentTreeWithAdSet(embedCodes, adSetCode);
  }

  /**
   * Fetch the root ContentItem associated with the given external ids. If multiple external ids are given,
   * the root item is assumed to be a Dynamic Channel and the external ids are assumed to all be videos. As
   * this method is not asynchronous it should be used within an AsyncTask.
   * @param externalIds the external ids to fetch
   * @return the root ContentItem representing externalIds
   * @throws OoyalaException
   */
  public ContentItem contentTreeByExternalIds(List<String> externalIds) throws OoyalaException {
    return _playerAPI.contentTreeByExternalIds(externalIds);
  }

  /**
   * Fetch additional children for the PaginatedParentItem specified using the nextToken specified. As this
   * method is not asynchronous it should be used within an AsyncTask.
   * @param parent the PaginatedParentItem to fetch additional children for
   * @return the PaginatedItemResponse
   */
  public PaginatedItemResponse contentTreeNext(PaginatedParentItem parent) {
    return _playerAPI.contentTreeNext(parent);
  }

  /**
   * Fetch a raw JSONObject from any backlot API (GET requests only). As this method is not asynchronous it
   * should be used within an AsyncTask.
   * @param uri the URI to be fetched from backlot *not* including "/v2". For example, to request
   *          https://api.ooyala.com/v2/assets, uri should be "/assets"
   * @param params Optional parameters to pass to the API
   * @param connectionTimeoutInMilliseconds The length of time to wait before timing out a network connection
   * @param readTimeoutInMilliseconds The length of time to wait before timing out a network read
   * @return the raw JSONObject representing the response
   */
  public JSONObject objectFromBacklotAPI(String uri, Map<String, String> params, int connectionTimeoutInMilliseconds, int readTimeoutInMilliseconds) {
    if (_secureUrlGenerator == null) {
      DebugMode.logD(getClass().getName(), "Backlot APIs are not supported without a SecureURLGenerator or apikey/secret");
      return null;
    }
    URL url = _secureUrlGenerator.secureURL(Environment.BACKLOT_HOST, PlayerAPIClient.BACKLOT_URI_PREFIX + uri, params);
    return OoyalaAPIHelper.objectForAPI(url,connectionTimeoutInMilliseconds, readTimeoutInMilliseconds);
  }

  private class ObjectFromBacklotAPITask extends AsyncTask<Object, Integer, JSONObject> {
    protected ObjectFromBacklotAPICallback _callback = null;
    private int connectionTimeoutInMilliseconds;
    private int readTimeoutInMilliseconds;

    public ObjectFromBacklotAPITask(ObjectFromBacklotAPICallback callback, int connectionTimeoutInMilliseconds, int readTimeoutInMilliseconds) {
      super();
      _callback = callback;
      this.connectionTimeoutInMilliseconds = connectionTimeoutInMilliseconds;
      this.readTimeoutInMilliseconds = readTimeoutInMilliseconds;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected JSONObject doInBackground(Object... allParams) {
      if (allParams == null || allParams.length < 2 || !(allParams[0] instanceof String)
          || !(allParams[0] instanceof Map)) { return null; }
      String uri = (String) (allParams[0]);
      Map<String, String> params = (Map<String, String>) (allParams[1]);
      return objectFromBacklotAPI(uri, params, connectionTimeoutInMilliseconds, readTimeoutInMilliseconds);
    }

    @Override
    protected void onPostExecute(JSONObject result) {
      _callback.callback(result);
    }
  }

  /**
   * Asynchronously fetch a raw JSONObject from any backlot API (GET requests only)
   * @param uri the URI to be fetched from backlot *not* including "/v2". For example, to request
   *          https://api.ooyala.com/v2/assets, uri should be "/assets"
   * @param params Optional parameters to pass to the API
   * @param callback the ObjectFromBacklotAPICallback to execute when the response is received
   * @param connectionTimeoutInMilliseconds The length of time to wait before timing out a network connection
   * @param readTimeoutInMilliseconds The length of time to wait before timing out a network read
   * @return an Object that can be used to cancel the asynchronous fetch using the cancel(Object task) method
   */
  public Object objectFromBacklotAPI(String uri, Map<String, String> params,
      ObjectFromBacklotAPICallback callback, int connectionTimeoutInMilliseconds, int readTimeoutInMilliseconds) {
    ObjectFromBacklotAPITask task = new ObjectFromBacklotAPITask(callback, connectionTimeoutInMilliseconds,  readTimeoutInMilliseconds);
    task.execute(uri, params);
    return task;
  }

  /**
   * @return the SecureURLGenerator this OoyalaAPIClient uses
   */
  SecureURLGenerator getSecureURLGenerator() {
    return _secureUrlGenerator;
  }
}

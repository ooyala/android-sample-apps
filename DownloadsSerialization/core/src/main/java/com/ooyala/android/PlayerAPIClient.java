package com.ooyala.android;

import com.ooyala.android.OoyalaException.OoyalaErrorCode;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.item.AuthorizableItem;
import com.ooyala.android.item.ContentItem;
import com.ooyala.android.item.PaginatedParentItem;
import com.ooyala.android.item.Video;
import com.ooyala.android.util.DebugMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class PlayerAPIClient {
  private static String TAG = PlayerAPIClient.class.getName();

  protected static final String KEY_DOMAIN = "domain";
  protected static final String KEY_AUTHORIZATION_DATA = "authorization_data";
  protected static final String KEY_USER_INFO = "user_info";
  protected static final String KEY_CONTENT_TREE = "content_tree";
  protected static final String KEY_MESSAGE = "message";
  protected static final String KEY_AD_SET_CODE = "adSetCode";
  protected static final String KEY_HEARTBEAT_DATA = "heartbeat_data";
  protected static final String KEY_HEARTBEAT_INTERVAL = "heartbeat_interval";
  protected static final String KEY_AUTH_TOKEN = "auth_token";
  protected static final String KEY_AUTH_TOKEN_EXPIRES = "auth_token_expires";
  protected static final String KEY_CODE = "code";
  protected static final String KEY_AUTHORIZED = "authorized";
  protected static final String KEY_CHILDREN = "children";
  protected static final String KEY_HEIGHT = "height";
  protected static final String KEY_WIDTH = "width";
  protected static final String KEY_METADATA = "metadata";
  protected static final String KEY_DEVICE = "device";
  protected static final String KEY_ERRORS = "errors";
  protected static final String KEY_EMBED_CODE = "embed_code";
  protected static final String KEY_DYNAMIC_FILTERS = "dynamicFilters";

  protected static final String AUTHORIZE_CONTENT_ID_URI = "/sas/player_api/v%s/authorization/content_id/%s/%s";
  protected static final String AUTHORIZE_EMBED_CODE_URI = "/sas/player_api/v%s/authorization/embed_code/%s/%s";
  protected static final String AUTHORIZE_DOWNLOAD_EMBED_CODE_URI = "/sas/player_api/v%s/download_authorization/embed_code/%s/%s";
  protected static final String AUTHORIZE_HEARTBEAT_URI = "/sas/player_api/v%s/auth_heartbeat/pcode/%s/auth_token/%s";
  protected static final String AUTHORIZE_PUBLIC_KEY_B64 = "MCgCIQD1PX86jvLr5bB3b5IFEze7TiWGEaRSHl5Ls7/3AKO5IwIDAQAB";
  protected static final String AUTHORIZE_PUBLIC_KEY_NAME = "sas_public_key";
  protected static final int AUTHORIZE_SIGNATURE_DIGEST_LENGTH = 20;

  protected static final String BACKLOT_URI_PREFIX = "/v2";

  protected static final String CONTENT_TREE_URI = "/player_api/v%s/content_tree/embed_code/%s/%s";
  protected static final String CONTENT_TREE_BY_EXTERNAL_ID_URI = "/player_api/v%s/content_tree/external_id/%s/%s";
  protected static final String CONTENT_TREE_NEXT_URI = "/player_api/v%s/content_tree/next/%s/%s";

  protected static final String METADATA_EMBED_CODE_URI = "/player_api/v%s/metadata/embed_code/%s/%s";

  protected static final String SEPARATOR_URL_IDS = ",";

  protected String _pcode = null;
  protected PlayerDomain _domain = null;
  protected int _width = -1;
  protected int _height = -1;
  protected EmbedTokenGenerator _embedTokenGenerator;
  protected Options _options;
  private boolean _isHook;
  public static final String HOOK = "-hook";
  protected AuthTokenManager authTokenManager = null;
  private int _connectionTimeoutInMillisecond = 0;
  private int _readTimeoutInMillisecond = 0;

  public PlayerAPIClient() {}

  public PlayerAPIClient(String pcode, PlayerDomain domain, EmbedTokenGenerator embedTokenGenerator) {
    this(pcode, domain, embedTokenGenerator, null);
  }

  public PlayerAPIClient(String pcode, PlayerDomain domain, EmbedTokenGenerator embedTokenGenerator, Options options) {
    _pcode = pcode;
    _domain = domain;
    _embedTokenGenerator = embedTokenGenerator;
    if (options != null) {
      _connectionTimeoutInMillisecond = options.getConnectionTimeoutInMillisecond();
      _readTimeoutInMillisecond = options.getReadTimeoutInMillisecond();
      _options = options;
    }
  }

  private JSONObject verifyAuthorizeJSON(JSONObject authResult, List<String> embedCodes) throws OoyalaException {
    if (authResult == null) { throw new OoyalaException(OoyalaErrorCode.ERROR_AUTHORIZATION_INVALID,
        "Authorization response invalid (nil)."); }

    try {
      if (!authResult.isNull(KEY_ERRORS)) {
        JSONObject errors = authResult.getJSONObject(KEY_ERRORS);
        if (!errors.isNull(KEY_CODE) && errors.getInt(KEY_CODE) != 0) { throw new OoyalaException(
            OoyalaErrorCode.ERROR_AUTHORIZATION_INVALID, errors.isNull(KEY_MESSAGE) ? ""
                : errors.getString(KEY_MESSAGE)); }
      }

      if (authResult.isNull(KEY_USER_INFO)) {
        throw new OoyalaException(OoyalaErrorCode.ERROR_AUTHORIZATION_INVALID,
            "User info data does not exist.");
      }

      if (authResult.isNull(KEY_AUTHORIZATION_DATA)) {
        throw new OoyalaException(OoyalaErrorCode.ERROR_AUTHORIZATION_INVALID,
            "Authorization data does not exist.");
      } else {
        JSONObject authData = authResult.getJSONObject(KEY_AUTHORIZATION_DATA);
        for (String embedCode : embedCodes) {
          if (authData.isNull(embedCode)
              || authData.getJSONObject(embedCode).isNull(KEY_AUTHORIZED)) { throw new OoyalaException(
              OoyalaErrorCode.ERROR_AUTHORIZATION_INVALID, "Authorization invalid for embed code: "
                  + embedCode); }
        }

        // TODO(mikhail): currently we do not check signature. fix this once we properly implement signatures
        // server side.

        return authData;
      }
    } catch (JSONException exception) {
      System.out.println("JSONException: " + exception);
      throw new OoyalaException(OoyalaErrorCode.ERROR_AUTHORIZATION_INVALID,
          "Authorization response invalid (exception).");
    }
  }

  private JSONObject verifyAuthorizeHeartbeatJSON(JSONObject result) throws OoyalaException {
    if (result == null) { throw new OoyalaException(OoyalaErrorCode.ERROR_AUTHORIZATION_HEARTBEAT_FAILED,
        "response invalid (nil)."); }

    if (result.isNull(KEY_MESSAGE)) {
      throw new OoyalaException(OoyalaErrorCode.ERROR_AUTHORIZATION_HEARTBEAT_FAILED,
          "response invalid (nil).");
    }
    try {
      if(!result.getString(KEY_MESSAGE).equals("OK")) {
        throw new OoyalaException(OoyalaErrorCode.ERROR_AUTHORIZATION_HEARTBEAT_FAILED,
            "response code (" + result.getString(KEY_MESSAGE) + ").");
      }
      if (authTokenManager != null && !result.isNull(KEY_AUTH_TOKEN)) {
        authTokenManager.setAuthToken(result.getString(KEY_AUTH_TOKEN));
      }
      if (authTokenManager != null && !result.isNull(KEY_AUTH_TOKEN_EXPIRES)) {
        authTokenManager.setAuthTokenExpires(result.getLong(KEY_AUTH_TOKEN_EXPIRES));
      }
    } catch (JSONException e) {
      throw new OoyalaException(OoyalaErrorCode.ERROR_AUTHORIZATION_HEARTBEAT_FAILED,
          "response invalid (error).");
    }
    return result;
  }

  private JSONObject verifyContentTreeObject(JSONObject contentTree, List<String> keys)
      throws OoyalaException {
    JSONObject contentTreeData = getContentTreeData(contentTree); // let any thrown exceptions propagate up
    if (contentTreeData != null && keys != null) {
      for (String key : keys) {
        if (contentTreeData.isNull(key)) { throw new OoyalaException(
            OoyalaErrorCode.ERROR_CONTENT_TREE_INVALID, "Content Tree response invalid (no key for: " + key
                + ")."); }
      }
    }
    return contentTreeData;
  }

  // embedCodes should be an empty list that will be populated with embedCodes corresponding to the
  // externalIds.
  private JSONObject verifyContentTreeObject(JSONObject contentTree, List<String> externalIds,
      List<String> embedCodes) throws OoyalaException {
    JSONObject contentTreeData = getContentTreeData(contentTree); // let any thrown exceptions propagate up
    if (contentTreeData != null && externalIds != null) {
      JSONArray embeds = contentTreeData.names();
      if ((embeds == null || embeds.length() == 0) && externalIds.size() > 0) { throw new OoyalaException(
          OoyalaErrorCode.ERROR_CONTENT_TREE_INVALID,
          "Content Tree response did not contain any values.  Expected: " + externalIds.size()); }
      try {
        for (int i = 0; i < embeds.length(); i++) {
          embedCodes.add(embeds.getString(i));
        }
      } catch (JSONException exception) {
        System.out.println("JSONException: " + exception);
        throw new OoyalaException(OoyalaErrorCode.ERROR_CONTENT_TREE_INVALID,
            "Content tree response invalid (exception casting embedCode to String)");
      }
      // Size comparison is done after filling in embedCodes on purpose.
      if (embedCodes.size() != externalIds.size()) { throw new OoyalaException(
          OoyalaErrorCode.ERROR_CONTENT_TREE_INVALID,
          "Content Tree response did not contain values for all external IDs. Found " + embedCodes.size()
              + " of " + externalIds.size()); }
      for (String embedCode : embedCodes) {
        if (contentTreeData.isNull(embedCode)) { throw new OoyalaException(
            OoyalaErrorCode.ERROR_CONTENT_TREE_INVALID, "Content Tree response invalid (no key for: "
                + embedCode + ")."); }
      }
    }
    return contentTreeData;
  }

  private Map<String, String> authorizeParams(List<String> embedCodes) {
    final Map<String, String> params = new HashMap<String, String>();
    params.put(KEY_DEVICE, Utils.device() + (_isHook ? HOOK : ""));
    params.put(KEY_DOMAIN, _domain.toString());

    if (authTokenManager != null && authTokenManager.getAuthToken().length() > 0) {
      params.put(KEY_AUTH_TOKEN, authTokenManager.getAuthToken());
    }

    if (_embedTokenGenerator != null) {
      params.put("embedToken", Utils.blockingGetEmbedTokenForEmbedCodes(_embedTokenGenerator, embedCodes));
    }
    return params;
  }

  public Video authorizeDownload(String embedCode, PlayerInfo playerInfo) throws OoyalaException {
    if (embedCode == null) {
      DebugMode.logE(TAG, "cannot authorize download: embedcode is null");
      return null;
    }

    List<String> embedCodes = new ArrayList<>();
    embedCodes.add(embedCode);

    // download only exists for v2
    JSONObject authData = authorizeEmbedCodes(embedCodes, playerInfo, AUTHORIZE_DOWNLOAD_EMBED_CODE_URI, "2");
    if (authData == null) {
      return null;
    }
    return new Video(authData, embedCode);
  }

  public boolean authorize(AuthorizableItem item, PlayerInfo playerInfo) throws OoyalaException {
    List<String> embedCodes = item.embedCodesToAuthorize();
    if (item == null) {
      return false;
    }

    JSONObject authData = authorizeEmbedCodes(embedCodes, playerInfo);
    if (authData == null) {
      return false;
    }
    item.update(authData);
    return true;
  }

  public JSONObject authorizeEmbedCodes(List<String> embedCodes, PlayerInfo playerInfo)
    throws OoyalaException {
    return authorizeEmbedCodes(embedCodes, playerInfo, AUTHORIZE_EMBED_CODE_URI, OoyalaPlayer.API_VERSION);
  }

  /**
   * handle authorize and download autorize
   * @param embedCodes - the embed codes
   * @param playerInfo - the player info
   * @param route - the api route
   * @param apiVersion - the api version
   * @return
   * @throws OoyalaException
   */
  private JSONObject authorizeEmbedCodes(List<String> embedCodes, PlayerInfo playerInfo, String route, String apiVersion)
      throws OoyalaException {

    String uri = String.format(route, apiVersion, _pcode, Utils.join(embedCodes, SEPARATOR_URL_IDS));
    Map<String, String> params = authorizeParams(embedCodes);
    params.put("device", playerInfo.getDevice() + (_isHook ? HOOK : ""));

    if (playerInfo != null) {
      if (playerInfo.getSupportedFormats() != null)
        params.put("supportedFormats", Utils.join(playerInfo.getSupportedFormats(), ","));

      if (playerInfo.getSupportedProfiles() != null)
        params.put("profiles", Utils.join(playerInfo.getSupportedProfiles(), ","));

      if (playerInfo.getMaxHeight() > 0)
        params.put("maxHeight", Integer.toString(playerInfo.getMaxHeight()));

      if (playerInfo.getMaxWidth() > 0)
        params.put("maxWidth", Integer.toString(playerInfo.getMaxWidth()));

      if (playerInfo.getMaxBitrate() > 0) {
        params.put("br", Integer.toString(playerInfo.getMaxBitrate()));
      }
    }

    if (_options != null && _options.getDynamicFilters() !=null && !_options.getDynamicFilters().isEmpty()) {
      List<String> dynamicFilters = new ArrayList<>();
      for(String filter : _options.getDynamicFilters()) {
        String trimmedString = filter.trim();
        try {
          trimmedString = URLEncoder.encode(trimmedString, Utils.CHARSET);
        }
        catch(Exception e) {}
        dynamicFilters.add(trimmedString);
      }
      params.put(KEY_DYNAMIC_FILTERS, Utils.join(dynamicFilters, SEPARATOR_URL_IDS));
    }
    
    JSONObject json = OoyalaAPIHelper.objectForAPI(Environment.AUTHORIZE_HOST, uri, params,
            _connectionTimeoutInMillisecond, _readTimeoutInMillisecond);
    if (json == null) {
      throw new OoyalaException(OoyalaErrorCode.ERROR_AUTHORIZATION_FAILED,
              "Authorization connection timed out.");
    }
    JSONObject authData = null;
    try {
      authData = verifyAuthorizeJSON(json, embedCodes);
      //parse out and save auth token and heartbeat data
      if (!json.isNull(KEY_AUTH_TOKEN) && authTokenManager != null) {
        authTokenManager.setAuthToken(json.getString(KEY_AUTH_TOKEN));
      }
      if (!json.isNull(KEY_AUTH_TOKEN_EXPIRES) && authTokenManager != null) {
        authTokenManager.setAuthTokenExpires(json.getLong(KEY_AUTH_TOKEN_EXPIRES));
      }

      if (!json.isNull(KEY_HEARTBEAT_DATA) && authTokenManager != null) {
        JSONObject heartbeatData = json.getJSONObject(KEY_HEARTBEAT_DATA);
        if (!heartbeatData.isNull(KEY_HEARTBEAT_INTERVAL)) {
          authTokenManager.setHeartbeatInterval(heartbeatData.getInt(KEY_HEARTBEAT_INTERVAL));
        }
      }

      if (!json.isNull(KEY_USER_INFO) && authTokenManager != null) {
        authTokenManager.setUserInfo(new UserInfo(json.getJSONObject(KEY_USER_INFO)));
      }
    } catch (OoyalaException e) {
      System.out.println("Unable to authorize: " + e);
      throw e;
    } catch (JSONException exception) {
      System.out.println("JSONException: " + exception);
      throw new OoyalaException(OoyalaErrorCode.ERROR_AUTHORIZATION_INVALID,
          "Authorization response invalid (exception).");
    }

    return authData;
  }

  public boolean authorizeHeartbeat(String embedCode) throws OoyalaException {
    String uri = String.format(AUTHORIZE_HEARTBEAT_URI, OoyalaPlayer.API_VERSION, _pcode, authTokenManager.getAuthToken());
    Map<String, String> params = new HashMap<String, String>();
    params.put( KEY_EMBED_CODE, embedCode );
    JSONObject json = OoyalaAPIHelper.objectForAPI(Environment.AUTHORIZE_HOST, uri, params,
            _connectionTimeoutInMillisecond, _readTimeoutInMillisecond);
    try {
      return verifyAuthorizeHeartbeatJSON(json) != null;  // any returned result is valid
    } catch (OoyalaException e) {
      System.out.println("Unable to authorize: " + e);
      throw e;
    }
  }

  private Map<String, String> contentTreeParams(Map<String, String> additionalParams) {
    Map<String, String> params = new HashMap<String, String>();
    if (additionalParams != null) {
      params.putAll(additionalParams);
    }
    params.put(KEY_DEVICE, Utils.device() + (_isHook ? HOOK : ""));
    if (_height > 0 && _width > 0) {
      params.put(KEY_WIDTH, Integer.toString(_width));
      params.put(KEY_HEIGHT, Integer.toString(_height));
    }
    return params;
  }

  private JSONObject getContentTreeData(JSONObject contentTree) throws OoyalaException {
    if (contentTree == null) { throw new OoyalaException(OoyalaErrorCode.ERROR_CONTENT_TREE_INVALID,
        "Content Tree response invalid (nil)."); }

    try {
      if (!contentTree.isNull(KEY_ERRORS)) {
        JSONObject errors = contentTree.getJSONObject(KEY_ERRORS);
        if (!errors.isNull(KEY_CODE) && errors.getInt(KEY_CODE) != 0) { throw new OoyalaException(
            OoyalaErrorCode.ERROR_CONTENT_TREE_INVALID, errors.isNull(KEY_MESSAGE) ? ""
                : errors.getString(KEY_MESSAGE)); }
      }

      // TODO(mikhail): currently we do not check signature. fix this once we properly implement signatures
      // server side.

      if (contentTree.isNull(KEY_CONTENT_TREE)) {
        throw new OoyalaException(OoyalaErrorCode.ERROR_CONTENT_TREE_INVALID,
            "Content tree data does not exist.");
      } else {
        return contentTree.getJSONObject(KEY_CONTENT_TREE);
      }
    } catch (JSONException exception) {
      System.out.println("JSONException: " + exception);
      throw new OoyalaException(OoyalaErrorCode.ERROR_CONTENT_TREE_INVALID,
          "Content tree response invalid (exception).");
    }
  }

  public ContentItem contentTree(List<String> embedCodes) throws OoyalaException {
    return contentTreeWithAdSet(embedCodes, null);
  }

  /**
   * @param embedCodes should be non-null and non-empty.
   * @param adSetCode can be null.
   * @return ContentItem instance.
   * @throws OoyalaException
   */
  public ContentItem contentTreeWithAdSet(List<String> embedCodes, String adSetCode) throws OoyalaException {
    Map<String, String> params = null;
    if (adSetCode != null) {
      params = new HashMap<String, String>(1);
      params.put(KEY_AD_SET_CODE, adSetCode);
    }

    String uri = String.format(CONTENT_TREE_URI, OoyalaPlayer.API_VERSION, _pcode,
        Utils.join(embedCodes, SEPARATOR_URL_IDS));
    JSONObject obj = OoyalaAPIHelper.objectForAPI(Environment.CONTENT_TREE_HOST, uri, contentTreeParams(params),
            _connectionTimeoutInMillisecond, _readTimeoutInMillisecond);
    JSONObject contentTree = null;
    try {
      contentTree = verifyContentTreeObject(obj, embedCodes);
    } catch (OoyalaException e) {
      System.out.println("Unable to create objects: " + e);
      throw e;
    }
    ContentItem item = ContentItem.create(contentTree, embedCodes);
    if (item == null) { throw new OoyalaException(OoyalaErrorCode.ERROR_CONTENT_TREE_INVALID,
        "Unknown Content Type"); }
    return item;
  }

  public ContentItem contentTreeByExternalIds(List<String> externalIds) throws OoyalaException {
    String uri = String.format(CONTENT_TREE_BY_EXTERNAL_ID_URI, OoyalaPlayer.API_VERSION, _pcode,
        Utils.join(externalIds, SEPARATOR_URL_IDS));
    JSONObject obj = OoyalaAPIHelper.objectForAPI(Environment.CONTENT_TREE_HOST, uri, contentTreeParams(null),
            _connectionTimeoutInMillisecond, _readTimeoutInMillisecond);
    if (obj == null) { return null; }
    List<String> embedCodes = new ArrayList<String>(); // will be filled in by verifyContentTreeObject call
                                                       // below
    JSONObject contentTree = null;
    try {
      contentTree = verifyContentTreeObject(obj, externalIds, embedCodes);
    } catch (OoyalaException e) {
      System.out.println("Unable to create externalId objects: " + e);
      throw e;
    }

    ContentItem item = ContentItem.create(contentTree, embedCodes);
    if (item == null) { throw new OoyalaException(OoyalaErrorCode.ERROR_CONTENT_TREE_INVALID,
        "Unknown Content Type"); }
    return item;
  }


  public PaginatedItemResponse contentTreeNext(PaginatedParentItem parent) {
    if (!parent.hasMoreChildren()) { return null; }
    String uri = String.format(CONTENT_TREE_NEXT_URI, OoyalaPlayer.API_VERSION, _pcode,
        parent.getNextChildren());
    JSONObject obj = OoyalaAPIHelper.objectForAPI(Environment.CONTENT_TREE_HOST, uri, contentTreeParams(null),
            _connectionTimeoutInMillisecond, _readTimeoutInMillisecond);
    if (obj == null) { return null; }
    JSONObject contentTree = null;
    List<String> keys = new ArrayList<String>();
    keys.add(parent.getNextChildren());
    try {
      contentTree = verifyContentTreeObject(obj, keys);
    } catch (Exception e) {
      System.out.println("Unable to create next objects: " + e);
      return null;
    }

    /**
     * NOTE: We have to convert the content token keyed dictionary to one that is embed code keyed in order
     * for it to work with update. We could just create a new update in each class, but this seemed better
     * because that would have a lot of duplicate code.
     */
    if (contentTree.isNull(parent.getNextChildren())) {
      System.out.println("Could not find token in content_tree_next response.");
      return null;
    }
    try {
      JSONObject tokenDict = contentTree.getJSONObject(parent.getNextChildren());
      JSONObject parentDict = new JSONObject();
      parentDict.put(parent.getEmbedCode(), tokenDict);

      int startIdx = parent.childrenCount();
      parent.update(parentDict);
      return new PaginatedItemResponse(startIdx, tokenDict.isNull(KEY_CHILDREN) ? 0 : tokenDict
          .getJSONArray(KEY_CHILDREN).length());
    } catch (JSONException e) {
      System.out.println("Unable to create next objects due to JSON Exception: " + e);
      return null;
    }
  }

  private class MetadataFetchTaskParam {
    public final ContentItem item;
    public final String adSetCode;
    /**
     * @param item should be non-null.
     * @param adSetCode can be null.
     */
    private MetadataFetchTaskParam( final ContentItem item, final String adSetCode ) {
      this.item = item;
      this.adSetCode = adSetCode;
    }
  }


  /**
   * @param embedCodes should be non-null and non-empty.
   * @param adSetCode can be null.
   * @return true when completed.
   * @throws OoyalaException
   */
  public JSONObject fetchMetadataForEmbedCodesWithAdSet( List<String> embedCodes, String adSetCode) throws OoyalaException {
    JSONObject result = null;
    Map<String, String> params = null;
    if (adSetCode != null) {
      params = new HashMap<String, String>(1);
      params.put(KEY_AD_SET_CODE, adSetCode);
    }

    // fetch metadata
    String uri = String.format(METADATA_EMBED_CODE_URI, OoyalaPlayer.API_VERSION, _pcode,
        Utils.join(embedCodes, SEPARATOR_URL_IDS));
    JSONObject root = OoyalaAPIHelper.objectForAPI(Environment.METADATA_HOST, uri, contentTreeParams(params),
            _connectionTimeoutInMillisecond, _readTimeoutInMillisecond);

    // validate the result
    if (root == null) {
      throw new OoyalaException(OoyalaErrorCode.ERROR_METADATA_FETCH_FAILED, "Empty metadata response");
    }

    try {
      int errorCode = root.getJSONObject("errors").getInt("code");
      if(errorCode != 0) {
        throw new OoyalaException(OoyalaErrorCode.ERROR_METADATA_FETCH_FAILED, "Non-zero metadata response code");
      }
      result = root.getJSONObject(KEY_METADATA);
    } catch (JSONException je) {
      throw new OoyalaException(OoyalaErrorCode.ERROR_METADATA_FETCH_FAILED, "Failed to parse metadata");
    }

    // return the JSON data
    return result;
  }

  /**
   * @param item should be non-null.
   * @param adSetCode can be null.
   * @return true when completed.
   * @throws OoyalaException
   */
  public boolean fetchMetadata( ContentItem item, String adSetCode ) throws OoyalaException {
    if (item == null) {
      return false;
    }

    JSONObject meta = fetchMetadataForEmbedCodesWithAdSet( item.embedCodesToAuthorize(), adSetCode);
    if (meta == null) {
      return false;
    }
    item.update(meta);
    return true;
  }

  private boolean _isFetchingMoreChildren = false;
  public boolean fetchMoreChildrenForPaginatedParentItem(PaginatedParentItem parent, PaginatedItemListener listener, PlayerInfo info) {
    // The two lines below aren't within a synchronized block because we assume
    // single thread
    // of execution except for the threads we explicitly spawn below, but those
    // set
    // _isFetchingMoreChildren = false at the very end of their execution.
    if (!parent.hasMoreChildren() || _isFetchingMoreChildren) { return false; }
    _isFetchingMoreChildren = true;

    Thread thread = new Thread(new NextChildrenRunner(parent, listener, info));
    thread.start();
    return true;
  }

  private class NextChildrenRunner implements Runnable {
    private PaginatedItemListener _listener;
    private PaginatedParentItem _parent;
    private PlayerInfo _info;

    public NextChildrenRunner(PaginatedParentItem parent, PaginatedItemListener listener, PlayerInfo info) {
      _parent = parent;
      _listener = listener;
      _info = info;
    }

    @Override
    public void run() {
      PaginatedItemResponse response = contentTreeNext(_parent);
      if (response == null) {
        _listener.onItemsFetched(-1, 0, new OoyalaException(OoyalaErrorCode.ERROR_CONTENT_TREE_NEXT_FAILED,
            "Null response"));
        _isFetchingMoreChildren = false;
        return;
      }

      if (response.firstIndex < 0) {
        _listener.onItemsFetched(response.firstIndex, response.count, new OoyalaException(
            OoyalaErrorCode.ERROR_CONTENT_TREE_NEXT_FAILED, "No additional children found"));
        _isFetchingMoreChildren = false;
        return;
      }

      List<String> childEmbedCodesToAuthorize = ContentItem.getEmbedCodes(_parent.getAllAvailableChildren().subList(
          response.firstIndex, response.firstIndex + response.count));
      try {
        // TODO: parallel these
        JSONObject metaData = null;
        JSONObject authData = authorizeEmbedCodes(childEmbedCodesToAuthorize, _info);
        if (authData != null) {
          metaData = fetchMetadataForEmbedCodesWithAdSet(childEmbedCodesToAuthorize, null);
        }

        if (metaData != null && authData != null) {
          _parent.update(authData);
          _parent.update(metaData);
          _listener.onItemsFetched(response.firstIndex, response.count, null);
        } else {
          _listener.onItemsFetched(response.firstIndex, response.count, new OoyalaException(
              OoyalaErrorCode.ERROR_AUTHORIZATION_FAILED, "Additional child authorization failed"));
        }
      } catch (OoyalaException e) {
        _listener.onItemsFetched(response.firstIndex, response.count, e);
      }
      _isFetchingMoreChildren = false;
      return;
    }
  }

  public void setHook() {
    _isHook = true;
  }

  /**
   * Set the instance of AuthTokenManager that handles storage of AuthToken, and other information
   * returned from Authorization
   * @param manager
   */
  public void setAuthTokenManager(AuthTokenManager manager) {
    this.authTokenManager = manager;
  }
}

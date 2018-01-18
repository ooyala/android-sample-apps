package com.ooyala.android.item;

import com.ooyala.android.FCCTVRating;
import com.ooyala.android.util.DebugMode;
import com.ooyala.android.util.OrderedMapValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Stores the info and metadata for the specified content item.
 *
 */
public abstract class ContentItem implements AuthorizableItem, OrderedMapValue<String>, JSONUpdatableItem {

  //Item
  protected static final String KEY_EMBED_CODE = "embed_code";
  protected static final String KEY_EXTERNAL_ID = "external_id";
  protected static final String KEY_CONTENT_TOKEN = "content_token";
  protected static final String KEY_TITLE = "title";
  protected static final String KEY_DESCRIPTION = "description";
  protected static final String KEY_PROMO_IMAGE = "promo_image";
  protected static final String KEY_HOSTED_AT_URL = "hostedAtURL";
  protected static final String KEY_THUMBNAIL_IMAGE = "thumbnail_image";
  protected static final String KEY_ASSET_PCODE = "asset_pcode";
  protected static final String KEY_CONTENT_TYPE = "content_type";
  protected static final String KEY_ADS = "ads";
  protected static final String KEY_NEXT_CHILDREN = "next_children";
  protected static final String KEY_DURATION = "duration";
  protected static final String KEY_CLOSED_CAPTIONS = "closed_captions";
  protected static final String KEY_CLOSED_CAPTIONS_VTT = "closed_captions_vtt";
  protected static final String KEY_REQUIRE_HEARTBEAT = "require_heartbeat";
  protected static final String KEY_CODE = "code"; //Item, OoyalaAd, PAPI
  protected static final String KEY_AUTHORIZED = "authorized";//AuthorizableItem, PAPI
  protected static final String KEY_CHILDREN = "children";  //Channel, PAPI
  protected static final String KEY_STREAMS = "streams";  //OoyalaAdSpot, Video
  protected static final String KEY_METADATA = "metadata";  //Content Item, PAPI
  protected static final String KEY_METADATA_BASE = "base";
  protected static final String KEY_METADATA_MODULES = "modules";
  protected static final String KEY_METADATA_MODULE_TYPE = "type";
  protected static final String KEY_METADATA_ALL_ADS = "all_ads";
  protected static final String KEY_METADATA_ALL_ADS_POSITION = "position";
  protected static final String KEY_METADATA_TVRATING_RATING = "tvrating";
  protected static final String KEY_METADATA_TVRATING_SUBRATINGS = "tvsubratings";
  protected static final String KEY_METADATA_TVRATING_CLICKTHROUGH_URL = "tvratingsurl";
  public static final String KEY_METADATA_HA_ENABLED = "ha_enabled";
  public static final String KEY_METADATA_HA_COUNT = "ha_count";

  protected static final String CONTENT_TYPE_CHANNEL_SET = "MultiChannel";
  protected static final String CONTENT_TYPE_CHANNEL = "Channel";
  protected static final String CONTENT_TYPE_VIDEO = "Video";
  protected static final String CONTENT_TYPE_LIVE_STREAM = "LiveStream";


  protected String _embedCode = null;
  protected String _externalId = null;
  protected String _contentToken = null;
  protected String _title = null;
  protected String _description = null;
  protected String _promoImageURL = null;
  protected String _assetPCode = null;
  protected String _hostedAtURL = null;
  protected boolean _authorized = false;
  protected int _authCode = AuthCode.NOT_REQUESTED;
  protected boolean _heartbeatRequired;
  protected Map<String, String> _metadata;
  protected Map<String, ModuleData> _moduleData;
  protected JSONObject _metadataJSON;
  protected JSONArray _metadataAllAdsJSONArray;
  //protected JSONObject _metadataAllAds;
  protected FCCTVRating _tvRating;

  ContentItem() {}

  ContentItem(String embedCode, String title, String description) {
    this(embedCode, null, title, description);
  }

  ContentItem(String embedCode, String contentToken, String title, String description) {
    _embedCode = embedCode;
    _contentToken = contentToken;
    _title = title;
    _description = description;
  }

  ContentItem(JSONObject data, String embedCode) {
    _embedCode = embedCode;
    update(data);
  }

  /**
   * Get the embedCode for this content item.
   * @return embedCode of this content item
   */
  public String getEmbedCode() {
    return _embedCode;
  }

  /**
   * Get the externalId for this content item.
   * @return externalId of this content item
   */
  public String getExternalId() {
    return _externalId;
  }

  /**
   * Get the contentToken for this content item.
   * @return contentToken of this content item
   */
  String getContentToken() {
    return _contentToken;
  }

  /**
   * Get the title for this content item
   * @return title of this content item
   */
  public String getTitle() {
    return _title;
  }

  /**
   * Get the description for this content item
   * @return description of this content item
   */
  public String getDescription() {
    return _description;
  }

  /**
   * Get the Asset PCode for this content item
   * @return Asset PCode for this content item
   */
  public String getAssetPCode() {
    return _assetPCode;
  }

  /**
   * Get the inner metadata JSONObject for this content item
   * @return inner metadata JSONObject for this content item
   */
  public JSONObject getMetadataJSON() {
    return _metadataJSON;
  }

  /**
   * Get the all_ads JSONArray in the metadata for this content item
   * @return all_ads JSONArray in the metadata for this content item
   */
  public JSONArray getMetadataAllAds() {
    return _metadataAllAdsJSONArray;
  }

  /**
   * Subclasses must override this.
   */
  public abstract Video firstVideo();

  /**
   * Subclasses must override this.
   */
  public abstract int getDuration();

  /**
   * @return possibly null.
   */
  public FCCTVRating getTVRating() {
    return _tvRating;
  }

  @Override
  /** For internal use only.
   * Update the AuthorizableItem using the specified data (subclasses should override and call this)
   * @param data the data to use to update this AuthorizableItem
   * @return a ReturnState based on if the data matched or not (or parsing failed)
   */
  public ReturnState update(JSONObject data) {
    if (data == null) { return ReturnState.STATE_FAIL; }

    if (_embedCode == null || data.isNull(_embedCode)) { return ReturnState.STATE_UNMATCHED; }

    try {
      JSONObject myData = data.getJSONObject(_embedCode);
      //DebugMode.logD(this.getClass().toString(), "************** myData **************\n" + myData.toString());
      if (!myData.isNull(KEY_AUTHORIZED)) {
        _authorized = myData.getBoolean(KEY_AUTHORIZED);
        if (!myData.isNull(KEY_CODE)) {
          int authCode = myData.getInt(KEY_CODE);
          _authCode = authCode;
        }
        if (!myData.isNull(KEY_REQUIRE_HEARTBEAT)) {
          _heartbeatRequired = myData.getBoolean(KEY_REQUIRE_HEARTBEAT);
        }
        return ReturnState.STATE_MATCHED;
      }

      if (_embedCode != null && !myData.isNull(KEY_EMBED_CODE)
          && !_embedCode.equals(myData.getString(KEY_EMBED_CODE))) { return ReturnState.STATE_FAIL; }

      if (!myData.isNull(KEY_EMBED_CODE)) {
        _embedCode = myData.getString(KEY_EMBED_CODE);
      }
      if (!myData.isNull(KEY_EXTERNAL_ID)) {
        _externalId = myData.getString(KEY_EXTERNAL_ID);
      }
      if (!myData.isNull(KEY_CONTENT_TOKEN)) {
        _contentToken = myData.getString(KEY_CONTENT_TOKEN);
      }
      if (!myData.isNull(KEY_TITLE)) {
        _title = myData.getString(KEY_TITLE);
      }
      if (!myData.isNull(KEY_DESCRIPTION)) {
        _description = myData.getString(KEY_DESCRIPTION);
      }
      if (!myData.isNull(KEY_PROMO_IMAGE)) {
        _promoImageURL = myData.getString(KEY_PROMO_IMAGE);
      }
      if (!myData.isNull(KEY_ASSET_PCODE)) {
        _assetPCode = myData.getString(KEY_ASSET_PCODE);
      }
      if (!myData.isNull(KEY_HOSTED_AT_URL)) {
        _hostedAtURL = myData.getString(KEY_HOSTED_AT_URL);
      }
      if (myData.has(KEY_METADATA_BASE)) {
        _metadata = ItemUtils.mapFromJSONObject(myData.getJSONObject(KEY_METADATA_BASE));
      }
      if (myData.has(KEY_METADATA_MODULES)) {
        // TODO change HashMap<String, ModuleData> to HashMap<String, JSONObject>
        _moduleData = new HashMap<String, ModuleData>();
        JSONObject modules = myData.getJSONObject(KEY_METADATA_MODULES);

        Iterator<?> itr = modules.keys();
        while (itr.hasNext()) {
          String key = (String)itr.next();
          JSONObject module = modules.getJSONObject(key);
          String type = module.optString(KEY_METADATA_MODULE_TYPE);
          Map<String, String> metadata = ItemUtils.mapFromJSONObject(module.getJSONObject(KEY_METADATA));
          _moduleData.put(key, new ModuleData(key, type, metadata));

          if (type.equals("google-ima-ads-manager")) {
            JSONArray arr = parseIMAMetadata(module);
            if (arr != null) {
              _metadataAllAdsJSONArray = arr;
            }
          }
        }
      }
      if( _metadata != null && _metadata.containsKey(KEY_METADATA_TVRATING_RATING) ) {
        _tvRating = new FCCTVRating(
            _metadata.get(KEY_METADATA_TVRATING_RATING),
            _metadata.get(KEY_METADATA_TVRATING_SUBRATINGS),
            _metadata.get(KEY_METADATA_TVRATING_CLICKTHROUGH_URL)
            );
      }
    } catch (JSONException exception) {
      System.out.println("JSONException: " + exception);
      return ReturnState.STATE_FAIL;
    }
    return ReturnState.STATE_MATCHED;
  }

  /**
   * Extract the IMA all_ads array from the metadata field of the module
   * @param module the JSONObject to extract data from
   * @return the JSONArray representing the all_ads field
   */
  private JSONArray parseIMAMetadata(JSONObject module) {
    try {
      JSONObject metadataJSON = module.getJSONObject(KEY_METADATA);
      JSONArray metadataAllAdsJSONArray = metadataJSON.getJSONArray(KEY_METADATA_ALL_ADS);
      DebugMode.logD(this.getClass().toString(), "All ads JSON array\n" + metadataAllAdsJSONArray.toString());

      int JSONArrLen = metadataAllAdsJSONArray.length();
      DebugMode.logD(this.getClass().toString(), "All ads JSON array length\n" + JSONArrLen);

      return metadataAllAdsJSONArray;
    }
    catch (JSONException exception) {
      DebugMode.logE(this.getClass().toString(), "JSONException: " + exception);
      return null;
    }
  }
  
  public static ContentItem create(JSONObject data, List<String> embedCodes) {
    if (data == null || embedCodes == null || embedCodes.size() == 0) { return null; }
    if (embedCodes.size() == 1) { return create(data, embedCodes.get(0)); }
    return new DynamicChannel(data, embedCodes);
  }

  public static ContentItem create(JSONObject data, String embedCode) {
    if (data == null || embedCode == null || data.isNull(embedCode)) { return null; }
    String contentType = null;
    try {
      JSONObject myData = data.getJSONObject(embedCode);
      if (myData.isNull(KEY_CONTENT_TYPE)) { return null; }
      contentType = myData.getString(KEY_CONTENT_TYPE);
    } catch (JSONException exception) {
      System.out.println("Create failed due to JSONException: " + exception);
      return null;
    }

    if (contentType == null) {
      return null;
    } else if (contentType.equals(CONTENT_TYPE_VIDEO)
        || contentType.equals(CONTENT_TYPE_LIVE_STREAM)) {
      return new Video(data, embedCode);
    } else if (contentType.equals(CONTENT_TYPE_CHANNEL)) {
      return new Channel(data, embedCode);
    } else if (contentType.equals(CONTENT_TYPE_CHANNEL_SET)) {
      return new ChannelSet(data, embedCode);
    } else {
      System.out.println("Unknown content_type: " + contentType);
      return null;
    }
  }

  @Override
  /** For internal use only.
   * The embed codes to authorize for the AuthorizableItem
   * @return the embed codes to authorize as a List
   */
  public List<String> embedCodesToAuthorize() {
    List<String> embedCodes = new ArrayList<String>();
    embedCodes.add(_embedCode);
    return embedCodes;
  }

  public abstract Video videoFromEmbedCode(String embedCode, Video currentItem);

  /**
   * Returns a promo image URL for this content item that will be at least the specified dimensions
   * @param width
   * @param height
   * @return the image url
   */
  public String getPromoImageURL(int width, int height) {
    return _promoImageURL;
  }

  /**
   * Returns a URL that video is hosted at for this content item
   * @return the url
   */
  public String getHostedAtUrl() {
    return _hostedAtURL;
  }

  public static List<String> getEmbedCodes(List<? extends ContentItem> items) {
    if (items == null) { return null; }
    List<String> result = new ArrayList<String>();
    for (ContentItem item : items) {
      result.add(item.getEmbedCode());
    }
    return result;
  }

  @Override
  public boolean isAuthorized() {
    return _authorized;
  }

  @Override
  public int getAuthCode() {
    return _authCode;
  }

  @Override
  public String getKey() {
    return _embedCode;
  }

  @Override
  public boolean isHeartbeatRequired() {
    return _heartbeatRequired;
  }

  public Map<String, String> getMetadata() {
    return _metadata;
  }

  public Map<String, ModuleData> getModuleData() {
    return _moduleData;
  }

  /**
   * Generate the authorization error of a video item.
   * @param authCode
   * @return a properly described OoyalaException
   */
  public static String getAuthError(int authCode) {
    // Actually authorized
    if(authCode == 0) {
      return "Video is authorized!";
    }

    // Out of bounds of authCodes
    if (authCode < 0 || authCode >= AuthorizableItem.authCodeDescription.length) {
      return "Invalid Authorization Error Code";
    }

    // Get description and make the exception
    return AuthorizableItem.authCodeDescription[authCode];
  }
}

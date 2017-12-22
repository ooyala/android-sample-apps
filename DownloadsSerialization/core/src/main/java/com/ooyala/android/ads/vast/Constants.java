package com.ooyala.android.ads.vast;

/**
 * Created by zchen on 2/29/16.
 */
public class Constants {
  public static final double MINIMUM_SUPPORTED_VAST_VERSION = 2.0;
  public static final double MAXIMUM_SUPPORTED_VAST_VERSION = 3.0;
  public static final double MINIMUM_SUPPORTED_VMAP_VERSION = 1.0;
  public static final double MAXIMUM_SUPPORTED_VMAP_VERSION = 1.0;

  public static final String ELEMENT_VAST = "VAST";
  public static final String ELEMENT_AD = "Ad";
  public static final String ELEMENT_IN_LINE = "InLine";
  public static final String ELEMENT_WRAPPER = "Wrapper";
  public static final String ELEMENT_AD_SYSTEM = "AdSystem";
  public static final String ELEMENT_AD_TITLE = "AdTitle";
  public static final String ELEMENT_DESCRIPTION = "Description";
  public static final String ELEMENT_SURVEY = "Survey";
  public static final String ELEMENT_ERROR = "Error";
  public static final String ELEMENT_IMPRESSION = "Impression";
  public static final String ELEMENT_CREATIVES = "Creatives";
  public static final String ELEMENT_CREATIVE = "Creative";
  public static final String ELEMENT_LINEAR = "Linear";
  public static final String ELEMENT_NONLIEAR = "NonLinear";
  public static final String ELEMENT_NON_LINEAR_ADS = "NonLinearAds";
  public static final String ELEMENT_COMPANION_ADS = "CompanionAds";
  public static final String ELEMENT_COMPANION = "Companion";
  public static final String ELEMENT_EXTENSIONS = "Extensions";
  public static final String ELEMENT_DURATION = "Duration";
  public static final String ELEMENT_TRACKING_EVENTS = "TrackingEvents";
  public static final String ELEMENT_TRACKING = "Tracking";
  public static final String ELEMENT_AD_PARAMETERS = "AdParameters";
  public static final String ELEMENT_VIDEO_CLICKS = "VideoClicks";
  public static final String ELEMENT_CLICK_THROUGH = "ClickThrough";
  public static final String ELEMENT_CLICK_TRACKING = "ClickTracking";
  public static final String ELEMENT_CUSTOM_CLICK = "CustomClick";
  public static final String ELEMENT_MEDIA_FILES = "MediaFiles";
  public static final String ELEMENT_MEDIA_FILE = "MediaFile";
  public static final String ELEMENT_VAST_AD_TAG_URI = "VASTAdTagURI";
  public static final String ELEMENT_ICONS = "Icons";
  public static final String ELEMENT_ICON = "Icon";
  public static final String ELEMENT_ICON_CLICKS = "IconClicks";
  public static final String ELEMENT_ICON_CLICK_THROUGH = "IconClickThrough";
  public static final String ELEMENT_ICON_CLICK_TRACKING = "IconClickTracking";
  public static final String ELEMENT_ICON_VIEW_TRACKING = "IconViewTracking";
  public static final String ELEMENT_STATIC_RESOURCE = "StaticResource";
  public static final String ELEMENT_IFRAME_RESOURCE = "IFrameResource";
  public static final String ELEMENT_HTML_RESOURCE = "HTMLResource";

  // nonlinear element
  public static final String ELEMENT_CREATIVE_EXTENSIONS = "CreativeExtensions";
  public static final String ELEMENT_CREATIVE_EXTENSION = "CreativeExtension";
  public static final String ELEMENT_NONLINEAR_CLICK_TRACKING = "NonLinearClickTracking";
  public static final String ELEMENT_NONLINEAR_CLICK_THROUGH =  "NonLinearClickThrough";
  public static final String ELEMENT_ALT_TEXT = "AltText";
  public static final String ELEMENT_COMPANION_CLICK_THROUGH = "CompanionClickThrough";

  // vmap elements
  public static final String ELEMENT_VMAP = "vmap:VMAP";
  public static final String ELEMENT_ADBREAK = "vmap:AdBreak";
  public static final String ELEMENT_ADSOURCE = "vmap:AdSource";
  public static final String ELEMENT_ADTAGURI = "vmap:AdTagURI";
  public static final String ELEMENT_VASTADDATA = "vmap:VASTAdData";
  public static final String ELEMENT_CUSTOMDATA = "vmap:CustomAdData";

  public static final String ATTRIBUTE_VERSION = "version";
  public static final String ATTRIBUTE_ID = "id";
  public static final String ATTRIBUTE_ADID = "AdID";
  public static final String ATTRIBUTE_SEQUENCE = "sequence";
  public static final String ATTRIBUTE_EVENT = "event";
  public static final String ATTRIBUTE_DELIVERY = "delivery";
  public static final String ATTRIBUTE_TYPE = "type";
  public static final String ATTRIBUTE_BITRATE = "bitrate";
  public static final String ATTRIBUTE_WIDTH = "width";
  public static final String ATTRIBUTE_HEIGHT = "height";
  public static final String ATTRIBUTE_SCALABLE = "scalable";
  public static final String ATTRIBUTE_MAINTAIN_ASPECT_RATIO = "maintainAspectRatio";
  public static final String ATTRIBUTE_API_FRAMEWORK = "apiFramework";
  public static final String ATTRIBUTE_SKIPOFFSET = "skipoffset";
  public static final String ATTRIBUTE_PROGRAM = "program";
  public static final String ATTRIBUTE_XPOSITION = "xPosition";
  public static final String ATTRIBUTE_YPOSITION = "yPosition";
  public static final String ATTRIBUTE_OFFSET = "offset";
  public static final String ATTRIBUTE_DURATION = "duration";
  public static final String ATTRIBUTE_CREATIVE_TYPE = "creativeType";
  public static final String ATTRIBUTE_REQUIRED = "required";
  public static final String ATTRIBUTE_EXPANDED_WIDTH = "expandedWidth";
  public static final String ATTRIBUTE_EXPANDED_HEIGHT = "expandedHeight";
  public static final String ATTRIBUTE_MIN_SUGGESTED_DURATION = "minSuggestedDuration";
  public static final String ATTRIBUTE_ASSET_WIDTH = "assetWidth";
  public static final String ATTRIBUTE_ASSET_HEIGHT = "assetHeight";
  public static final String ATTRIBUTE_AD_SLOT_ID = "adSlotId";

  // vmap attributes
  public static final String ATTRIBUTE_TIMEOFFSET = "timeOffset";
  public static final String ATTRIBUTE_BREAKTYPE = "breakType";
  public static final String ATTRIBUTE_BREAKID = "breakId";
  public static final String ATTRIBUTE_REPEAT_AFTER = "repeatAfter";
  public static final String ATTRIBUTE_ALLOW_MULTIPLE_ADS = "allowMultipleAds";
  public static final String ATTRIBUTE_FOLLOW_REDIRECTS = "followRedirects";
  public static final String ATTRIBUTE_TEMPLATE_TYPE = "templateType";

  public static final String MIME_TYPE_MP4 = "video/mp4";
  public static final String MIME_TYPE_M3U8 = "application/x-mpegURL";

  public static final String KEY_SIGNATURE = "signature";
  public static final String KEY_URL = "url";
  public static final String KEY_DURATION = "duration";

  // Error codes
  public static final int ERROR_XML_PARSING = 100; // XML parsing error.
  public static final int ERROR_VAST_SCHEMA = 101; // VAST schema validation error.
  public static final int ERROR_VAST_VERSION_NOT_SUPPORTED = 102; // VAST version of response not supported.
  public static final int ERROR_TRAFFIC = 200; // Trafficking error. Video player received an Ad type that it was not expecting and/or cannot display.
  public static final int ERROR_UNEXPECTED_LINEAR = 201; // Video player expecting different linearity.
  public static final int ERROR_UNEXPECTED_DURATION = 202; // Video player expecting different duration.
  public static final int ERROR_UNEXPECTED_SIZE = 203; // Video player expecting different size.
  public static final int ERROR_WRAPPER_GENERAL = 300; // General Wrapper error.
  public static final int ERROR_WRAPPER_TIMEOUT = 301; // Timeout of VAST URI provided in Wrapper element.
  public static final int ERROR_WRAPPER_LIMIT_REACHED = 302; // Wrapper limit reached,as defined by the video  player. Too many Wrapper responses have been received with no InLine response.
  public static final int ERROR_WRAPPER_NO_VAST_RESPONSE = 303; //No  Ads  VAST  response  after  one  or  more  Wrappers.
  public static final int ERROR_LINEAR_GENERAL = 400; // General Linear error. Video player is unable to display the Linear Ad.
  public static final int ERROR_LINEAR_FILE_NOT_FOUND = 401; // File not found. Unable to find Linear/MediaFile from URI.
  public static final int ERROR_LINEAR_TIMEOUT_MEDIAFILE = 402; // Timeout  of  MediaFile  URI.
  public static final int ERROR_LINEAR_SUPPORTED_MEDIA_NOT_FOUND = 403; // Couldn’t find MediaFile that is supported by this video player, based on the attributes of the MediaFile  element.
  public static final int ERROR_LINEAR_CANNOT_DISPLAY_MEDIA = 405; // Problem displaying MediaFile. Video player found a MediaFile with supported type butcouldn’t display it.
  public static final int ERROR_NONLINEAR_GENERAL = 500; // General  NonLinearAds  error.
  public static final int ERROR_NONLINEAR_UNABLE_TO_DISPLAY = 501; // Unable to display NonLinear Ad because creative dimensions do not align with creative display area (i.e. creative dimension too large).
  public static final int ERROR_NONLINEAR_UNABLE_TO_FETCT = 502; // Unable to fetch NonLinearAds/NonLinear resource.
  public static final int ERROR_NONLINEAR_SUPPORTED_RESOURCE_NOT_FOUND = 503; // Couldn’t find NonLinear resource with supported type.
  public static final int ERROR_COMPANION_GENERAL = 600; // General CompanionAds error.
  public static final int ERROR_COMPANION_UNABLE_TO_DISPLAY = 601; // Unable to display Companion because creative dimensions do not fit within Companion intdisplay area (i.e., no available space).
  public static final int ERROR_COMPANION_UNABLE_TO_DISPLAY_REQUIRED = 602; // Unable to display Required Companion.
  public static final int ERROR_COMPANION_UNABLE_TO_FETCT = 603; // Unable to fetch CompanionAds/Companion resource.
  public static final int ERROR_COMPANION_SUPPORTED_TYPE_NOT_FOUND = 604; // Couldn’t find Companion resource with supported type.
  public static final int ERROR_UNDEFINED = 900; // Undefined Error.
}

package com.ooyala.android.ads.vast;

import com.ooyala.android.util.DebugMode;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A class that represents VAST Companion
 */
class Companion {
  private static final String TAG = Companion.class.getSimpleName();

  private String id;
  private int width;
  private int height;
  private int assetWidth;
  private int assetHeight;
  private int expandedWidth;
  private int expandedHeight;
  private String apiFramework;
  private String adSlotId;

  private Resource resource;
  private Map<String, Set<String>> trackingEvents = new HashMap<String, Set<String>>();
  private String clickThrough;
  private Element creativeExtensions;
  private String parameters;
  private String altText;

  public Companion(Element e) {
    parse(e);
  }

  public void parse(Element e) {
    if (e == null || !Constants.ELEMENT_COMPANION.equals(e.getTagName())) {
      DebugMode.logE(TAG, "invalid element");
      return;
    }

    id = e.getAttribute(Constants.ATTRIBUTE_ID);
    apiFramework = e.getAttribute(Constants.ATTRIBUTE_API_FRAMEWORK);

    width = VASTUtils.getIntAttribute(e, Constants.ATTRIBUTE_WIDTH, 0);
    height = VASTUtils.getIntAttribute(e, Constants.ATTRIBUTE_HEIGHT, 0);
    assetWidth = VASTUtils.getIntAttribute(e, Constants.ATTRIBUTE_ASSET_WIDTH, 0);
    assetHeight = VASTUtils.getIntAttribute(e, Constants.ATTRIBUTE_ASSET_HEIGHT, 0);
    expandedWidth = VASTUtils.getIntAttribute(e, Constants.ATTRIBUTE_EXPANDED_WIDTH, 0);
    expandedHeight = VASTUtils.getIntAttribute(e, Constants.ATTRIBUTE_EXPANDED_HEIGHT, 0);
    adSlotId = e.getAttribute(Constants.ATTRIBUTE_AD_SLOT_ID);

    for (Node node = e.getFirstChild(); node != null; node = node.getNextSibling()) {
      if (node instanceof Element) {
        Element el = (Element)node;
        String tag = el.getTagName();
        if (Constants.ELEMENT_CREATIVE_EXTENSIONS.equals(tag)) {
          creativeExtensions = el;
        } else if (Constants.ELEMENT_AD_PARAMETERS.equals(tag)) {
          parameters = el.getTextContent();
        } else if (Constants.ELEMENT_COMPANION_CLICK_THROUGH.equals(tag)) {
          clickThrough = el.getTextContent().trim();
        } else if (Constants.ELEMENT_TRACKING_EVENTS.equals(tag)) {
          VASTUtils.parseTrackingEvents(el, trackingEvents);
        } else if (Constants.ELEMENT_STATIC_RESOURCE.equals(tag)) {
          String mimeType = el.getAttribute(Constants.ATTRIBUTE_CREATIVE_TYPE);
          resource = new Resource(Resource.Type.Static, mimeType, el.getTextContent().trim());
        } else if (Constants.ELEMENT_IFRAME_RESOURCE.equals(tag)) {
          resource = new Resource(Resource.Type.IFrame, null, el.getTextContent().trim());
        } else if (Constants.ELEMENT_HTML_RESOURCE.equals(tag)) {
          resource = new Resource(Resource.Type.HTML, null, el.getTextContent().trim());
        } else if (Constants.ELEMENT_ALT_TEXT.equals(tag)) {
          altText = el.getTextContent();
        }
      }
    }
  }

  /**
   * @return id
   */
  public String getId() {
    return id;
  }

  /**
   * @return width
   */
  public int getWidth() {
    return width;
  }

  /**
   * @return height
   */
  public int getHeight() {
    return height;
  }

  /**
   * @return assetWidth
   */
  public int getAssetWidth() {
    return assetWidth;
  }

  /**
   * @return assetHeight
   */
  public int getAssetHeight() {
    return assetHeight;
  }

  /**
   * @return expandedWidth
   */
  public int getExpandedWidth() {
    return expandedWidth;
  }

  /**
   * @return expandedHeight
   */
  public int getExpandedHeight() {
    return expandedHeight;
  }

  /**
   * @return api framework
   */
  public String getApiFramework() {
    return apiFramework;
  }

  /**
   * @return adSlotId
   */
  public String getAdSlotId() {
    return adSlotId;
  }

  /**
   * @return resource for the companion
   */
  public Resource getResource() {
    return resource;
  }

  /**
   * @return parameters
   */
  public String getParameters() {
    return parameters;
  }

  /**
   * @return raw creative extensions
   */
  public Element getCreativeExtensions() {
    return creativeExtensions;
  }

  /**
   * @return click through
   */
  public String getClickThrough() {
    return clickThrough;
  }

  /**
   * @return tracking events
   */
  public Map<String, Set<String>> getTrackingEvents() {
    return trackingEvents;
  }
}

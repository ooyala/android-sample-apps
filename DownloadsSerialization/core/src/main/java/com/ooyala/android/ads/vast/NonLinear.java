package com.ooyala.android.ads.vast;

import com.ooyala.android.util.DebugMode;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.HashSet;
import java.util.Set;

/**
 * A class that represents NonLinear element
 */
public class NonLinear {
  private static final String TAG = NonLinear.class.getSimpleName();

  private String id;
  private int width;
  private int height;
  private int expandedWidth;
  private int expandedHeight;
  private boolean scalable;
  private boolean maintainAspectRatio;
  // in milliseconds
  private int minSuggestedDuration;
  private String apiFramework;
  private Resource resource;
  private Set<String> clickTrackings = new HashSet<>();
  private String clickThrough;
  private Element creativeExtensions;
  private String parameters;

  public NonLinear(Element e) {
    parse(e);
  }

  private void parse(Element e) {
    if (e == null || !Constants.ELEMENT_NONLIEAR.equals(e.getTagName())) {
      DebugMode.logE(TAG, "invalid element");
      return;
    }

    id = e.getAttribute(Constants.ATTRIBUTE_ID);
    apiFramework = e.getAttribute(Constants.ATTRIBUTE_API_FRAMEWORK);

    width = VASTUtils.getIntAttribute(e, Constants.ATTRIBUTE_WIDTH, 0);
    height = VASTUtils.getIntAttribute(e, Constants.ATTRIBUTE_HEIGHT, 0);
    expandedWidth = VASTUtils.getIntAttribute(e, Constants.ATTRIBUTE_EXPANDED_WIDTH, 0);
    expandedHeight = VASTUtils.getIntAttribute(e, Constants.ATTRIBUTE_EXPANDED_HEIGHT, 0);
    scalable = VASTUtils.getBooleanAttribute(e, Constants.ATTRIBUTE_SCALABLE, false);
    maintainAspectRatio = VASTUtils.getBooleanAttribute(e, Constants.ATTRIBUTE_MAINTAIN_ASPECT_RATIO, false);
    minSuggestedDuration = (int)(VASTUtils.secondsFromTimeString(e.getAttribute(Constants.ATTRIBUTE_MIN_SUGGESTED_DURATION), 0.0) * 1000);

    for (Node node = e.getFirstChild(); node != null; node = node.getNextSibling()) {
      if (node instanceof Element) {
        Element el = (Element)node;
        String tag = el.getTagName();
        if (Constants.ELEMENT_CREATIVE_EXTENSIONS.equals(tag)) {
          creativeExtensions = el;
        } else if (Constants.ELEMENT_AD_PARAMETERS.equals(tag)) {
          parameters = el.getTextContent();
        } else if (Constants.ELEMENT_NONLINEAR_CLICK_THROUGH.equals(tag)) {
          clickThrough = el.getTextContent().trim();
        } else if (Constants.ELEMENT_NONLINEAR_CLICK_TRACKING.equals(tag)) {
          clickTrackings.add(el.getTextContent().trim());
        } else if (Constants.ELEMENT_STATIC_RESOURCE.equals(tag)) {
          String mimeType = el.getAttribute(Constants.ATTRIBUTE_CREATIVE_TYPE);
          resource = new Resource(Resource.Type.Static, mimeType, el.getTextContent().trim());
        } else if (Constants.ELEMENT_IFRAME_RESOURCE.equals(tag)) {
          resource = new Resource(Resource.Type.IFrame, null, el.getTextContent().trim());
        } else if (Constants.ELEMENT_HTML_RESOURCE.equals(tag)) {
          resource = new Resource(Resource.Type.HTML, null, el.getTextContent().trim());
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
   * @return scalable
   */
  public boolean getScalable() {
    return scalable;
  }

  /**
   * @return get maintain aspect ratio
   */
  public boolean getMaintainAspectRatio() {
    return maintainAspectRatio;
  }

  /**
   * @return min supported duration in milliseconds
   */
  public int getMinSupportedDuration() {
    return minSuggestedDuration;
  }

  /**
   * @return api framework
   */
  public String getApiFramework() {
    return apiFramework;
  }

  /**
   * @return resource for the nonlinear
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
   * @return click trackings
   */
  public Set<String> getClickTrackings() {
    return clickTrackings;
  }
}

package com.ooyala.android.ads.vast;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by zchen on 2/25/16.
 */
public class Icon {
  private static final String TAG = Icon.class.getSimpleName();


  private String program;
  private int width;
  private int height;
  private int xPosition;
  private int yPosition;
  private double offset;
  private double duration;
  private Resource resource;
  private String apiFramework;

  private Set<String> clickTrackings = new HashSet<String>();
  private Set<String> viewTrackings = new HashSet<String>();
  String clickThrough;

  Icon(Element data) {
    if (data == null || !data.getTagName().equals(Constants.ELEMENT_ICON)) {
      return;
    }
    parseXml(data);
  }

  private void parseXml(Element xml) {
    program = xml.getAttribute(Constants.ATTRIBUTE_PROGRAM);
    width = VASTUtils.getIntAttribute(xml, Constants.ATTRIBUTE_WIDTH, 0);
    height = VASTUtils.getIntAttribute(xml, Constants.ATTRIBUTE_HEIGHT, 0);

    String xString = xml.getAttribute(Constants.ATTRIBUTE_XPOSITION);
    if ("left".equals(xString)) {
      xPosition = 0;
    } else if ("right".equals(xString)) {
      xPosition = Integer.MAX_VALUE;
    } else {
      xPosition = VASTUtils.getIntAttribute(xml, Constants.ATTRIBUTE_XPOSITION, 0);
    }

    String yString = xml.getAttribute(Constants.ATTRIBUTE_YPOSITION);
    if ("top".equals(yString)) {
      yPosition = 0;
    } else if ("bottom".equals(yString)) {
      yPosition = Integer.MAX_VALUE;
    } else {
      yPosition = VASTUtils.getIntAttribute(xml, Constants.ATTRIBUTE_YPOSITION, 0);
    }

    duration = VASTUtils.secondsFromTimeString(xml.getAttribute(Constants.ATTRIBUTE_DURATION), 0);
    offset = VASTUtils.secondsFromTimeString(xml.getAttribute(Constants.ATTRIBUTE_OFFSET), 0);
    apiFramework = xml.getAttribute(Constants.ATTRIBUTE_API_FRAMEWORK);

    Node child = xml.getFirstChild();
    while (child != null) {
      if (child instanceof Element) {
        Element e = (Element)child;
        String tag = e.getTagName();
        if (tag.equals(Constants.ELEMENT_STATIC_RESOURCE)) {
          String mimeType = e.getAttribute(Constants.ATTRIBUTE_CREATIVE_TYPE);
          resource = new Resource(Resource.Type.Static, mimeType, e.getTextContent().trim());
        } else if (tag.equals(Constants.ELEMENT_IFRAME_RESOURCE)) {
          resource = new Resource(Resource.Type.IFrame, null, e.getTextContent().trim());
        } else if (tag.equals(Constants.ELEMENT_HTML_RESOURCE)) {
          resource = new Resource(Resource.Type.HTML, null, e.getTextContent().trim());
        } else if (tag.equals(Constants.ELEMENT_ICON_VIEW_TRACKING)) {
          String viewTracking = e.getTextContent().trim();
          if (viewTracking != null && viewTracking.length() > 0) {
            viewTrackings.add(viewTracking);
          }
        } else if (tag.equals(Constants.ELEMENT_ICON_CLICKS)) {
          parseClicks(e);
        }
      }
      child = child.getNextSibling();
    }
  }

  private void parseClicks(Element xml) {
    Node child = xml.getFirstChild();
    while (child != null) {
      if (child instanceof Element) {
        Element e = (Element)child;
        String tag = e.getTagName();
        if (tag.equals(Constants.ELEMENT_ICON_CLICK_THROUGH)) {
          clickThrough = e.getTextContent().trim();
        } else if (tag.equals(Constants.ELEMENT_ICON_CLICK_TRACKING)) {
          String tracking = e.getTextContent().trim();
          if (tracking != null && tracking.length() > 0) {
            clickTrackings.add(tracking);
          }
        }
      }
      child = child.getNextSibling();
    }
  }

  /**
   * @return program
   */
  public String getProgram() {
    return program;
  }

  /**
   * @return width in pixels
   */
  public int getWidth() {
    return width;
  }

  /**
   * @return height in pixels
   */
  public int getHeight() {
    return height;
  }

  /**
   * @return xPosition in pixels
   */
  public int getXPosition() {
    return xPosition;
  }

  /**
   * @return yPosition in pixels
   */
  public int getYPosition() {
    return yPosition;
  }

  /**
   * @return duration in seconds
   */
  public double getDuration() {
    return duration;
  }

  /**
   * @return time offset in seconds
   */
  public double getOffset() {
    return offset;
  }

  /**
   * @return resource
   */
  public Resource getResource() {
    return resource;
  }

  /**
   * @return api framework
   */
  public String getApiFramework() {
    return apiFramework;
  }

  /**
   * @return a list of click tracking urls
   */
  public Set<String> getClickTrackings() {
    return clickTrackings;
  }

  /**
   * @return a list of viewing urls
   */
  public Set<String> getViewTrackings() {
    return viewTrackings;
  }

  /**
   * @return click through url
   */
  public String getClickThrough() {
    return clickThrough;
  }
}

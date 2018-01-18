package com.ooyala.android.ads.vast;

import com.ooyala.android.util.DebugMode;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * This is a static helper class to deserialize a VMAP XML document into VMAP ad spots.
 */
public class VASTHelper {
  private static final String TAG = VASTHelper.class.getSimpleName();
  /**
   * Parse a VMAP XML element according to the spec and generates a list of VASTAdSpots
   * @param e the root element of the VMAP XML
   * @param spots a list of vast ad spots as output
   * @param duration the content duration, used to compute percentage time offset
   * @return true if pass succeeds, false if failed
   */
  public static boolean parse(Element e, List<VASTAdSpot> spots, int duration) {
    if (e == null || spots == null) {
      DebugMode.logE(TAG, "some of the arguments are null");
      return false;
    }

    if (!Constants.ELEMENT_VMAP.equals(e.getTagName())) {
      DebugMode.logE(TAG, "xml type is incorrect, tag is:" + e.getTagName());
    }

    String version = e.getAttribute(Constants.ATTRIBUTE_VERSION);
    if (version == null) {
      return false;
    }
    double versionValue = 0;
    try {
      versionValue = Double.parseDouble(version);
    } catch (NumberFormatException ex) {
      return false;
    }

    if (versionValue < Constants.MINIMUM_SUPPORTED_VMAP_VERSION ||
        versionValue > Constants.MAXIMUM_SUPPORTED_VMAP_VERSION) {
      DebugMode.logE(TAG, "unsupported vast version" + versionValue);
      return false;
    }

    for (Node node = e.getFirstChild(); node != null; node = node.getNextSibling()) {
      if (node instanceof Element) {
        Element ad = (Element) node;
        String tagName = ad.getTagName();
        if (!Constants.ELEMENT_ADBREAK.equals(tagName)) {
          continue;
        }

        VMAPAdSpot adSpot = parseAdBreak(ad, duration);
        if (adSpot != null) {
          spots.add(adSpot);
        }
      }
    }

    return true;
  }

  /**
   * Parse a VMAP AdBreak element according to the spec and generates a list of VASTAdSpots
   * @param e the root element of the AdBreak
   * @param duration the content duration, used to compute percentage time offset
   * @return a VMAPAdSpot, null if parse failed.
   */
  private static VMAPAdSpot parseAdBreak(Element e, int duration) {
    String timeOffsetString = e.getAttribute(Constants.ATTRIBUTE_TIMEOFFSET);
    String repeatAfterString = e.getAttribute(Constants.ATTRIBUTE_REPEAT_AFTER);
    String breakId = e.getAttribute(Constants.ATTRIBUTE_BREAKID);
    String breakType = e.getAttribute(Constants.ATTRIBUTE_BREAKTYPE);
    if (timeOffsetString == null) {
      DebugMode.logE(TAG, "cannot find timeoffset");
      return null;
    }

    TimeOffset timeOffset = TimeOffset.parseOffset(timeOffsetString);
    if (timeOffset == null) {
      DebugMode.logE(TAG, "invalid timeOffset:" + timeOffsetString);
      return null;
    }

    double repeatAfter = -1;
    if (repeatAfterString != null && repeatAfterString.length() > 0) {
      repeatAfter = VASTUtils.secondsFromTimeString(repeatAfterString, -1);
    }

    Element adSource = VASTHelper.getFirstElementByName(e, Constants.ELEMENT_ADSOURCE);
    if (adSource == null) {
      return null;
    }

    String adSourceId= adSource.getAttribute(Constants.ATTRIBUTE_ID);
    String allowMultiAdsString = adSource.getAttribute(Constants.ATTRIBUTE_ALLOW_MULTIPLE_ADS);
    String followRedirectsString = adSource.getAttribute(Constants.ATTRIBUTE_FOLLOW_REDIRECTS);
    boolean allowMultiAds = true;
    boolean followRedirects = true;
    if (allowMultiAdsString != null) {
      allowMultiAds = Boolean.parseBoolean(allowMultiAdsString);
    }
    if (followRedirectsString != null) {
      followRedirects = Boolean.parseBoolean(followRedirectsString);
    }

    for (Node n = adSource.getFirstChild(); n != null; n = n.getNextSibling()) {
      if (!(n instanceof Element)) {
        continue;
      }
      Element vast = (Element)n;
      String tag = vast.getTagName();
      if (Constants.ELEMENT_VASTADDATA.equals(tag)) {
        Element v = VASTHelper.getFirstElementByName(vast, Constants.ELEMENT_VAST);
        return new VMAPAdSpot(timeOffset, duration, repeatAfter, breakType, breakId, adSourceId, allowMultiAds, followRedirects, v);
      } else if (Constants.ELEMENT_ADTAGURI.equals(tag)) {
        String uri = vast.getTextContent().trim();
        try {
          URL url = new URL(uri);
          return new VMAPAdSpot(timeOffset, duration, repeatAfter, breakType, breakId, adSourceId, allowMultiAds, followRedirects, url);
        } catch (MalformedURLException ex) {
          DebugMode.logE(TAG, "invalid uri:" + ex.getMessage(), ex);
          return null;
        }
      } else if (Constants.ELEMENT_CUSTOMDATA.equals(tag)) {
        // not implemented
        return null;
      } else {
        DebugMode.logE(TAG, "invalid AdSource element:" + tag);
        return null;
      }
    }
    return null;
  }

  public static Element getFirstElementByName(Element e, String name) {
    if (e == null || name == null) {
      DebugMode.logE(TAG, "cannot get child from null element or name");
      return null;
    }
    for (Node node = e.getFirstChild(); node != null; node = node.getNextSibling()) {
      if (node instanceof Element && name.equals(((Element) node).getTagName())) {
       return (Element)node;
      }
    }
    return null;
  }
}

package com.ooyala.android.ads.vast;

import com.ooyala.android.util.DebugMode;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A class that represents NonLinearAds element
 */
public class NonLinearAds {
  private static final String TAG = NonLinearAds.class.getSimpleName();
  private Map<String, Set<String>> trackingEvents = new HashMap<String, Set<String>>();
  private List<NonLinear> nonLinears = new ArrayList<>();

  public NonLinearAds(Element e) {
    parse(e);
  }

  private void parse(Element e) {
    if (e == null || !Constants.ELEMENT_NON_LINEAR_ADS.equals(e.getTagName())) {
      DebugMode.logE(TAG, "invalid nonlinearAds element");
      return;
    }

    for (Node node = e.getFirstChild(); node != null; node = node.getNextSibling()) {
      if (node instanceof Element) {
        Element el = (Element)node;
        if (Constants.ELEMENT_TRACKING_EVENTS.equals(el.getTagName())) {
          VASTUtils.parseTrackingEvents(el, trackingEvents);
        } else if (Constants.ELEMENT_NONLIEAR.equals(el.getTagName())) {
          NonLinear nonLinear = new NonLinear(el);
          nonLinears.add(nonLinear);
        }
      }
    }
  }

  /**
   *
   * @return tracking events
   */
  public Map<String, Set<String>> getTrackingEvents() {
    return trackingEvents;
  }

  /**
   *
   * @return nonlinears
   */
  public List<NonLinear> getNonLinears() {
    return nonLinears;
  }
}

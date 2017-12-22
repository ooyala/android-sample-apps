package com.ooyala.android.ads.vast;

import com.ooyala.android.util.DebugMode;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that represents VAST CompanionAds element
 */
public class CompanionAds {
  private static final String TAG = CompanionAds.class.getSimpleName();

  public enum RequiredType {
    All,
    Any,
    None
  };

  private RequiredType required;
  private List<Companion> companions = new ArrayList<>();

  public CompanionAds(Element e) {
    parse(e);
  }

  private void parse(Element e) {
    if (!Constants.ELEMENT_COMPANION_ADS.equals(e.getTagName())) {
      DebugMode.logE(TAG, "Invalid tag");
      return;
    }

    String requiredString = e.getAttribute(Constants.ATTRIBUTE_REQUIRED);
    for (Node node = e.getFirstChild(); node != null; node = node.getNextSibling()) {
      if (node instanceof Element) {
        Element el = (Element)node;
        if (Constants.ELEMENT_COMPANION.equals(el.getTagName())) {
          Companion companion = new Companion(el);
          companions.add(companion);
        }
      }
    }
  }

  private RequiredType parseRequired(String s) {
    if ("none".equals(s)) {
      return RequiredType.None;
    } else if ("any".equals(s)) {
      return RequiredType.Any;
    } else {
      return RequiredType.All;
    }
  }

  public RequiredType getRequired() {
    return required;
  }

  public List<Companion> getCompanions() {
    return companions;
  }
}

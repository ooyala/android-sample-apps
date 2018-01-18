package com.ooyala.android.ads.vast;

import com.ooyala.android.util.DebugMode;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

class Creative implements Comparable<Creative> {
  private static final String TAG = Creative.class.getSimpleName();
  /** The preferred order in which multiple Creatives should be displayed, optional */
  private int sequence;
  /** The creative id */
  private String id;
  /** AdID for the creative (formerly ISCI) */
  private String adID;

  /** The Linear Ad associated with this item */
  private Linear linear;
  /** The Non-Linear Ads associated with this item in the sequence (XML Element) */
  private NonLinearAds nonLinearAds;
  /** The Companion Ads associated with this item in the sequence (XML Element) */
  private CompanionAds companionAds;

  public Creative(Element e) {
    parse(e);
  }

  /**
   * Whether or not this VASTSequenceItem has a linear ad
   * @return true if there exists a linear ad, false if there does not;
   */
  public boolean hasLinear() {
    return linear != null;
  }

  public boolean hasCompanionAds() {
    return companionAds != null;
  }

  public boolean hasNonlinearAds() {
    return nonLinearAds != null;
  }

  private void parse(Element e) {
    if (!Constants.ELEMENT_CREATIVE.equals(e.getTagName())) {
      DebugMode.logE(TAG, "invalid tag");
    }
    id = e.getAttribute(Constants.ATTRIBUTE_ID);
    adID = e.getAttribute(Constants.ATTRIBUTE_ADID);
    sequence = VASTUtils.getIntAttribute(e, Constants.ATTRIBUTE_SEQUENCE, 0);
    for (Node node = e.getFirstChild(); node != null; node = node.getNextSibling()) {
      if (node instanceof Element) {
        Element el = (Element)node;
        String tag = el.getTagName();
        if (Constants.ELEMENT_LINEAR.equals(tag)) {
          // only parse the first linear element
          if (linear == null) {
            linear = new Linear(el);
          }
        } else if (Constants.ELEMENT_COMPANION_ADS.equals(tag)) {
          companionAds = new CompanionAds(el);
        } else if (Constants.ELEMENT_NON_LINEAR_ADS.equals(tag)) {
          nonLinearAds = new NonLinearAds(el);
        }
      }
    }
  }

  /**
   * This method is used to sort Lists of VASTSequenceItems. There is no need to call this method.
   */
  @Override
  public int compareTo(Creative arg0) {
    if (sequence < arg0.getSequence()) {
      return -1;
    } else if (sequence > arg0.getSequence()) {
      return 1;
    }
    return 0;
  }

  /**
   * @return sequence
   */
  public int getSequence() {
    return sequence;
  }

  /**
   *
   * @return id
   */
  public String getId() {
    return id;
  }

  /**
   *
   * @return AdID
   */
  public String getAdID() {
    return adID;
  }

  /**
   * Get the VASTLinearAd in this VASTSequenceItem.
   * @return the VASTLinearAd
   */
  public Linear getLinear() {
    return linear;
  }

  /**
   * @return the nonLinearAds.
   */
  public NonLinearAds getNonLinearAds() {
    return nonLinearAds;
  }

  /**
   * @return the compaionAds.
   */
  public CompanionAds getCompanionAds() {
    return companionAds;
  }

}

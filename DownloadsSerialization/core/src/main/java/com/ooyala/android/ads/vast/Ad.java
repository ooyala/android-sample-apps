package com.ooyala.android.ads.vast;

import com.ooyala.android.util.DebugMode;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

/**
 * A representation of VAST advertisement, and the data that can be stored in it
 */
class Ad implements Comparable<Ad> {
  private static final String TAG = Ad.class.getSimpleName();
  private static final int WRAPPER_LIMIT = 5;

  /** the ID of the Ad */
  private String _adID;

  private int _adSequence;
  /** the System */
  protected String _system;
  /** the System Version */
  protected String _systemVersion;
  /** the title of the Ad */
  protected String _title;
  /** the description of the Ad */
  protected String _description;
  /** the survey URLs of the Ad */
  protected List<String> _surveyURLs = new ArrayList<String>();
  /** the error URLs of the Ad */
  protected List<String> _errorURLs = new ArrayList<String>();
  /** the impression URLs of the Ad */
  protected List<String> _impressionURLs = new ArrayList<String>();
  /** the ordered sequence of the Ad (List of VASTSequenceItem) */
  protected List<Creative> linearCreatives = new ArrayList<>();
  protected List<Creative> nonLinearCreatives = new ArrayList<>();
  protected List<Creative> companionCreatives = new ArrayList<>();

  /** the extensions of the Ad */
  protected Element _extensions;
  /** the number of linear creatives without sequence numbers */
  private int _numOfLinear = 0;
  /** the errors */
  protected Set<Integer> errors = new HashSet<Integer>();

  private int wrapperDepth;
  // track if the inline element is found.
  private boolean inlineFound;

  /**
   * Create a VASTAd using the specified xml (subclasses should override this)
   * @param data the Element containing the xml to use to initialize this VASTAd
   */
  public Ad(Element data) {
    update(data);
    checkErrors();
  }

  /**
   * Update the VASTAd using the specified xml (subclasses should override this)
   * Assumptions:
   *  1) There can be multiple wrappers (we set no limit on the redirects).
   *  2) If the wrapper has multiple linear creatives with sequence numbers AND the child has multiple linear creatives
   *     with corresponding sequence numbers, we put the right tracking events to the correct linear creatives of the child.
   *  3) If the wrapper has multiple linear creatives but the child's linear creatives do not have sequence numbers we assume that
   *     the order that the creatives are given is the order that we want to match with the wrapper's sequence numbers
   * @param xml the VAST AD element to use to update this VASTAd
   * @return YES if the XML was properly formatter, NO if not
   */
  private boolean update(Element xml) {
    if (xml == null) {
      errors.add(Constants.ERROR_XML_PARSING);
      DebugMode.logE(TAG, "input element is null");
      return false;
    }

    if (xml == null || !xml.getTagName().equals(Constants.ELEMENT_AD)) {
      errors.add(Constants.ERROR_VAST_SCHEMA);
      DebugMode.logE(TAG, "invalid VAST tag:" + xml.getTagName());
      return false;
    }

    if (wrapperDepth == 0) {
      _adID = xml.getAttribute(Constants.ATTRIBUTE_ID);
      _adSequence = VASTUtils.getIntAttribute(xml, Constants.ATTRIBUTE_SEQUENCE, 0);
    }

    for (Node node = xml.getFirstChild(); node != null; node = node.getNextSibling()) {
      if (node instanceof Element) {
        Element e = (Element) node;
        String tag = e.getTagName();
        if (Constants.ELEMENT_IN_LINE.equals(tag)) {
          inlineFound = true;
        } else if (Constants.ELEMENT_WRAPPER.equals(tag)) {
          processVastAdTags(e.getElementsByTagName(Constants.ELEMENT_VAST_AD_TAG_URI));
        }
        parseElement(e, Constants.ELEMENT_IN_LINE.equals(tag));
        return inlineFound;
      }
    }
    return inlineFound;
  }

  /**
   * Parse an inline or wrapper element
   */
  private void parseElement(Element e, boolean isInline) {
    for (Node node = e.getFirstChild(); node != null; node = node.getNextSibling()) {
      if (node instanceof Element) {
        String text = node.getTextContent().trim();
        Element element = (Element) node;
        String tag = element.getTagName();
        if (Constants.ELEMENT_AD_SYSTEM.equals(tag)) {
          _system = text;
          _systemVersion = element.getAttribute(Constants.ATTRIBUTE_VERSION);
        } else if (Constants.ELEMENT_AD_TITLE.equals(tag)) {
          _title = text;
        } else if (Constants.ELEMENT_DESCRIPTION.equals(tag)) {
          _description = text;
        } else if (Constants.ELEMENT_SURVEY.equals(tag)) {
          _surveyURLs.add(text);
        } else if (Constants.ELEMENT_ERROR.equals(tag)) {
          _errorURLs.add(text);
        } else if (Constants.ELEMENT_IMPRESSION.equals(tag)) {
          _impressionURLs.add(text);
        } else if (Constants.ELEMENT_EXTENSIONS.equals(tag)) {
          _extensions = element;
        } else if (Constants.ELEMENT_CREATIVES.equals(tag)) {
          parseCreatives(element, isInline);
        }
      }
    }
  }

  private void parseCreatives(Element e, boolean isInline) {
    for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
      if (n instanceof Element) {
        Element element = (Element)n;
        if (!Constants.ELEMENT_CREATIVE.equals(element.getTagName())) {
          errors.add(Constants.ERROR_VAST_SCHEMA);
          return;
        }
        Creative c = new Creative(element);
        if (c.hasLinear() && c.hasNonlinearAds()) {
          DebugMode.logE(TAG, "Creative has both linear and nonlinear- id:" + c.getId());
        }
        if (isInline) {
          if (c.hasLinear()) {
            linearCreatives.add(c);
          } else if (c.hasCompanionAds()) {
            companionCreatives.add(c);
          } else if (c.hasNonlinearAds()) {
            nonLinearCreatives.add(c);
          }
        } else {
          // wrapper elements, try to update existing creative;
          if (c.hasLinear()) {
            mergeLinear(c);
          } else if (c.hasNonlinearAds()) {
            nonLinearCreatives.add(c);
          } else if (c.hasCompanionAds()) {
            companionCreatives.add(c);
          }
        }
      }
    }
  }

  private void mergeLinear(Creative linearCreative) {
    for (Creative c : linearCreatives) {
      if (c.getSequence() == linearCreative.getSequence()) {
        c.getLinear().merge(linearCreative.getLinear());
        return;
      }
    }
    // did not find any creative that has the same sequence, just merge with first
    if (linearCreatives.size() > 0) {
      linearCreatives.get(0).getLinear().merge(linearCreative.getLinear());
    } else {
      DebugMode.logE(TAG, "no linear creative is found for wrapper merge");
    }
  }

  /**
   * Fetch the id of this VASTAd. This doesn't really mean anything.
   * @return the id of this VASTAd.
   */
  public String getAdID() {
    return _adID;
  }

  /**
   * Fetch the sequence of this VASTAd. VAST 3.0
   * @return the sequence value, 0 otherwise.
   */
  public int getAdSequence() {
    return _adSequence;
  }

  /**
   * Fetch the VASTAd's System. This is a String defining which ad provider this VASTAd uses.
   * @return the VASTAd's System.
   */
  public String getSystem() {
    return _system;
  }

  /**
   * Fetch the VASTAd's System's Version.
   * @return the version of the VASTAd's System.
   */
  public String getSystemVersion() {
    return _systemVersion;
  }

  /**
   * Fetch the title of this VASTAd.
   * @return the title of this VASTAd.
   */
  public String getTitle() {
    return _title;
  }

  /**
   * Fetch the description of this VASTAd.
   * @return the description of this VASTAd.
   */
  public String getDescription() {
    return _description;
  }

  /**
   * Fetch the list of Survey URLs associated with this VASTAd.
   * @return the list of Survey URLs associated with this VASTAd.
   */
  public List<String> getSurveyURLs() {
    return _surveyURLs;
  }

  /**
   * Fetch the list of URLs to ping when this VASTAd throws an error.
   * @return the list of URls to ping when this VASTAd throws an error.
   */
  public List<String> getErrorURLs() {
    return _errorURLs;
  }

  /**
   * @return the errors.
   */
  public Set<Integer> getErrors() {
    return errors;
  }
  /**
   * Fetch the list of URLs to ping when this VASTAd plays.
   * @return the list of URLs to ping when this VASTAd plays.
   */
  public List<String> getImpressionURLs() {
    return _impressionURLs;
  }

  /**
   *
   * @return the linear creative
   */
  public Creative getLinearCreative() {
    return  (linearCreatives.size() > 0) ? linearCreatives.get(0) : null;
  }

  public List<Creative> getNonLinearCreatives() {
    return nonLinearCreatives;
  }
  
  public List<Creative> getCompanionCreatives() {
    return companionCreatives;
  }

  /**
   * Fetch the raw XML Element object for the VAST Extensions associated with this VASTAd.
   * @return the Element object containing the VAST Extensions.
   */
  public Element getExtensions() {
    return _extensions;
  }

  @Override
  public int compareTo(Ad t) {
    return this.getAdSequence() - t.getAdSequence();
  }

  private void processVastAdTags(NodeList tags) {
    if (wrapperDepth++ >= WRAPPER_LIMIT) {
      DebugMode.logE(TAG, "reached wrapper limit");
      errors.add(Constants.ERROR_WRAPPER_LIMIT_REACHED);
      return;
    }

    if (tags.getLength() < 1) {
      DebugMode.logE(TAG, "no adtag was found");
      errors.add(Constants.ERROR_WRAPPER_GENERAL);
      return;
    }

    Node node = tags.item(0);
    if (!(node instanceof Element) || node.getTextContent() == null) {
      DebugMode.logE(TAG, "invalid node:" + node.toString());
      errors.add(Constants.ERROR_XML_PARSING);
      return;
    }

    String adTagUrl = tags.item(0).getTextContent().trim();
    if (adTagUrl == null || adTagUrl.length() <= 0) {
      DebugMode.logE(TAG, "wrapper adtag was empty");
      errors.add(Constants.ERROR_WRAPPER_GENERAL);
      return;
    }

    URL url = null;
    try {
      url = new URL(adTagUrl);
    } catch (MalformedURLException ex){
      DebugMode.logE(TAG, "adtag format is incorrect:"+adTagUrl);
      errors.add(Constants.ERROR_WRAPPER_GENERAL);
      return;
    }

    Element element = null;
    try {
      element = VASTUtils.getXmlDocument(url, 60 * 1000, 60 * 1000);
    } catch (IOException ex) {
      errors.add(Constants.ERROR_WRAPPER_TIMEOUT);
      return;
    } catch (ParserConfigurationException ex) {
      errors.add(Constants.ERROR_WRAPPER_GENERAL);
      return;
    } catch (SAXException ex) {
      errors.add(Constants.ERROR_XML_PARSING);
      return;
    }

    if (element == null) {
      errors.add(Constants.ERROR_WRAPPER_NO_VAST_RESPONSE);
      return;
    }
    updateWrapperResponse(element);
  }

  private void updateWrapperResponse(Element vast) {
    String tag = vast.getTagName();
    if (!Constants.ELEMENT_VAST.equals(tag)) {
      errors.add(Constants.ERROR_WRAPPER_NO_VAST_RESPONSE);
      return;
    }

    String vastVersion = vast.getAttribute(Constants.ATTRIBUTE_VERSION);
    double version = 0;
    try {
      version = Double.parseDouble(vastVersion);
    } catch (NumberFormatException e) {
      errors.add(Constants.ERROR_VAST_VERSION_NOT_SUPPORTED);
      return;
    }

    if (version < Constants.MINIMUM_SUPPORTED_VAST_VERSION ||
        version > Constants.MAXIMUM_SUPPORTED_VAST_VERSION) {
      DebugMode.logE(TAG, "unsupported vast version" + vastVersion);
      errors.add(Constants.ERROR_VAST_VERSION_NOT_SUPPORTED);
      return;
    }

    for (Node node = vast.getFirstChild(); node != null; node = node.getNextSibling()) {
      if (node instanceof Element) {
        Element ad = (Element) node;
        String tagName = ad.getTagName();
        if (Constants.ELEMENT_ERROR.equals(tagName)) {
          // It is possible vAST only contains an error, we need to ping the error link here
          errors.add(Constants.ERROR_WRAPPER_NO_VAST_RESPONSE);
          _errorURLs.add(ad.getTextContent().trim());
          return;
        } else if (Constants.ELEMENT_AD.equals(tagName)) {
          update(ad);
        }
      }
    }
  }

  private void checkErrors() {
    for (Creative c : linearCreatives) {
      if (c.getLinear() != null) {
        if (c.getLinear().getStreams().size() <= 0) {
          errors.add(Constants.ERROR_LINEAR_FILE_NOT_FOUND);
        } else if (c.getLinear().getStream() == null) {
          errors.add(Constants.ERROR_LINEAR_SUPPORTED_MEDIA_NOT_FOUND);
        }
      }
    }
  }
}

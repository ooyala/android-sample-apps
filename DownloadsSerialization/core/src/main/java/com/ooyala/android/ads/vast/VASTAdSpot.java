package com.ooyala.android.ads.vast;

import com.ooyala.android.AdOverlayInfo;
import com.ooyala.android.OoyalaAPIClient;
import com.ooyala.android.PlayerInfo;
import com.ooyala.android.item.OoyalaManagedAdSpot;
import com.ooyala.android.util.DebugMode;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

/**
 * A model of an VAST Ad spot, which can be played during video playback
 */
public class VASTAdSpot extends OoyalaManagedAdSpot {
  private static final String TAG = VASTAdSpot.class.getSimpleName();

  static final String KEY_EXPIRES = "expires";  //embedded, Vast, PAPI
  static final String KEY_SIGNATURE = "signature"; // embedded, VAST
  static final String KEY_URL = "url";  // CC, Stream, VAST

  /** The signature for the vast request */
  protected String _signature;
  /** The expires for the vast request */
  protected long _expires;
  /** The url for the vast request */
  protected URL _vastURL;
  /** The actual ads (List of VASTAd) */
  protected List<Ad> _poddedAds = new ArrayList<Ad>();
  protected List<Ad> _standAloneAds = new ArrayList<Ad>();
  protected List<VASTAdSpot> _vmapAdSpots;
  protected int _contentDuration;
  protected boolean _infoFetched;

  protected Set<Integer> errors = new HashSet<Integer>();
  protected List<String> errorUrls = new ArrayList<String>();

  /**
   * for testing purpose only
   * package private on purpose
   * @param e the xml
   */
  VASTAdSpot(Element e) {
    this(0, 0, e);
  }

  /**
   * Initialize a VASTAdSpot using the specified data
   * @param timeOffset the time offset at which the VASTAdSpot should play
   * @param clickURL the clickthrough URL
   * @param trackingURLs the tracking URLs that should be pinged when this ad plays
   * @param vastURL the VAST URL containing the VAST compliant XML for this ad spot
   */
  public VASTAdSpot(int timeOffset, int duration, URL clickURL, List<URL> trackingURLs, URL vastURL) {
    super(timeOffset, clickURL, trackingURLs);
    _contentDuration = duration;
    _vastURL = VASTUtils.urlFromAdUrlString(vastURL.toString());
  }

  /**
   * Initialize a VASTAdSpot using the specified data (subclasses should override this)
   * @param data the NSDictionary containing the data to use to initialize this VASTAdSpot
   */
  public VASTAdSpot(JSONObject data) {
    update(data);
  }

  /**
   * initialize a VASTAdSpot via XML document
   * @param timeOffset the time offset of the ad spot
   * @param duration the content duration
   * @param e the VAST document element
   */
  public VASTAdSpot(int timeOffset, int duration, Element e) {
    super(timeOffset, null, null);
    _contentDuration = duration;
    parse(e);
  }

  /**
   * Update the VASTAdSpot using the specified data (subclasses should override and call this)
   * @param data the NSDictionary containing the data to use to update this VASTAdSpot
   * @return ReturnState.STATE_FAIL if the parsing failed, ReturnState.STATE_MATCHED if it was successful
   */
  @Override

  public ReturnState update(JSONObject data) {
    switch (super.update(data)) {
      case STATE_FAIL:
        return ReturnState.STATE_FAIL;
      case STATE_UNMATCHED:
        return ReturnState.STATE_UNMATCHED;
      default:
        break;
    }
    if (!data.isNull(Constants.KEY_DURATION)) {
      try {
        _contentDuration = data.getInt(Constants.KEY_DURATION);
      } catch (JSONException e) {
        DebugMode.logE(TAG, "unable to get content duration", e);
      }
    }
    if (data.isNull(Constants.KEY_SIGNATURE)) {
      DebugMode.logE(TAG, "ERROR: Fail to update Ad with dictionary because no signature exists!");
      return ReturnState.STATE_FAIL;
    }
    if (data.isNull(KEY_EXPIRES)) {
      DebugMode.logE(TAG, "ERROR: Fail to update Ad with dictionary because no expires exists!");
      return ReturnState.STATE_FAIL;
    }
    if (data.isNull(KEY_URL)) {
      DebugMode.logE(TAG, "ERROR: Fail to update Ad with dictionary because no url exists!");
      return ReturnState.STATE_FAIL;
    }
    try {
      _signature = data.getString(Constants.KEY_SIGNATURE);
      _expires = data.getInt(KEY_EXPIRES);
      _vastURL = VASTUtils.urlFromAdUrlString(data.getString(Constants.KEY_URL));
      if (_vastURL == null) {
        return ReturnState.STATE_FAIL;
      }
    } catch (JSONException exception) {
      DebugMode.logD(this.getClass().getName(), "JSONException: " + exception);
      return ReturnState.STATE_FAIL;
    }

    return ReturnState.STATE_MATCHED;
  }

  /**
   * Fetch the additional required info for the ad NOTE: As of right now, we only support VAST 2.0 Linear Ads.
   * Information about Non-Linear and Companion Ads are stored in the dictionaries nonLinear and companion
   * respectively.
   * @return false if errors occurred, true if successful
   */
  @Override
  public boolean fetchPlaybackInfo(OoyalaAPIClient api, PlayerInfo info) {
    if (_vastURL == null) {
      errors.add(Constants.ERROR_VAST_SCHEMA);
      return false;
    }
    if (_infoFetched) {
      return true;
    }
    try {
      // TODO: use timeout from options.
      Element e = VASTUtils.getXmlDocument(_vastURL, 60*1000, 60*1000);
      return parse(e);
    } catch (IOException ex) {
      errors.add(Constants.ERROR_WRAPPER_TIMEOUT);
      return false;
    } catch (ParserConfigurationException ex) {
      errors.add(Constants.ERROR_WRAPPER_GENERAL);
      return false;
    } catch (SAXException ex) {
      errors.add(Constants.ERROR_XML_PARSING);
      return false;
    }
  }

  protected boolean parse(Element vast) {
    String tag = vast.getTagName();
    if (Constants.ELEMENT_VMAP.equals(tag)) {
      _vmapAdSpots = new ArrayList<VASTAdSpot>();
      _infoFetched = VASTHelper.parse(vast, _vmapAdSpots, _contentDuration);
      return _infoFetched;
    } else if (!Constants.ELEMENT_VAST.equals(tag)) {
      return false;
    }

    String vastVersion = vast.getAttribute(Constants.ATTRIBUTE_VERSION);
    double version = 0;
    try {
      version = Double.parseDouble(vastVersion);
    } catch (NumberFormatException e) {
      return false;
    }

    if (version < Constants.MINIMUM_SUPPORTED_VAST_VERSION ||
        version > Constants.MAXIMUM_SUPPORTED_VAST_VERSION) {
      DebugMode.logE(TAG, "unsupported vast version" + vastVersion);
      errors.add(Constants.ERROR_VAST_VERSION_NOT_SUPPORTED);
      return false;
    }

    for (Node node = vast.getFirstChild(); node != null; node = node.getNextSibling()) {
      if (node instanceof Element) {
        Element ad = (Element) node;
        String tagName = ad.getTagName();
        if (Constants.ELEMENT_ERROR.equals(tagName)) {
          // It is possible vAST only contains an error, we need to ping the error link here
          errors.add(Constants.ERROR_WRAPPER_NO_VAST_RESPONSE);
          errorUrls.add(ad.getTextContent().trim());
          return false;
        } else if (!Constants.ELEMENT_AD.equals(tagName)) {
          continue;
        }

        Ad vastAd = new Ad(ad);
        if (vastAd != null) {
          if (vastAd.getAdSequence() > 0) {
            _poddedAds.add(vastAd);
          } else {
            _standAloneAds.add(vastAd);
          }
        }
      }
    }

    if (_poddedAds.size() > 0) {
      // sort podded ads
      Collections.sort(_poddedAds);
    }
    _infoFetched = true;
    return _infoFetched;
  }

  public List<Ad> getAds() {
    if (_poddedAds.size() > 0) {
      return _poddedAds;
    }
    return _standAloneAds;
  }

  public URL getVASTURL() {
    return _vastURL;
  }

  public List<VASTAdSpot> getVMAPAdSpots() {
    return _vmapAdSpots;
  }

  public boolean isInfoFetched() {
    return _infoFetched;
  }

  /**
   * @return error codes.
   */
  public Set<Integer> getErrors() {
    return errors;
  }

  /**
   * @return error urls.
   */
  public List<String> getErrorUrls() {
    return errorUrls;
  }

  public List<Ad> getLinearAds() {
    List<Ad> linearAds = new ArrayList<>();
    for (Ad ad : getAds()) {
      if (ad.getLinearCreative() != null) {
        linearAds.add(ad);
      }
    }
    return linearAds;
  }

  @Override
  public boolean needsPauseContent() {
    if (!_infoFetched) {
      return true;
    }

    for (Ad ad : getAds()) {
      if (ad.getLinearCreative() != null && ad.getLinearCreative().getLinear() != null) {
        return true;
      }
    }
    return false;
  }

  public AdOverlayInfo getAdOverlayInfo() {
    for (Ad ad : getAds()) {
      if (ad.getNonLinearCreatives() == null || ad.getNonLinearCreatives().size() <= 0) {
        continue;
      }

      Creative nonLinearCreative = ad.getNonLinearCreatives().get(0);
      NonLinearAds nonLinearAds = nonLinearCreative.getNonLinearAds();
      if (nonLinearAds == null || nonLinearAds.getNonLinears() == null || nonLinearAds.getNonLinears().size() <= 0) {
        continue;
      }
      NonLinear nonLinear = nonLinearAds.getNonLinears().get(0);
      if (nonLinear != null) {
        return new AdOverlayInfo(nonLinear);
      }
    }
    return null;
  }
}

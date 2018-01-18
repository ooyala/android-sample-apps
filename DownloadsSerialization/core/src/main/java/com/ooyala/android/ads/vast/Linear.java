package com.ooyala.android.ads.vast;

import com.ooyala.android.item.PlayableItem;
import com.ooyala.android.item.Stream;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Linear implements PlayableItem {
  private static final String TAG = Linear.class.getSimpleName();

  /*
   * package private on purpose
   */

  /** The duration of the ad in seconds */
  private double _duration;
  /** The tracking events in a HashMap of event name to Set of String */
  private HashMap<String, Set<String>> _trackingEvents = new HashMap<String, Set<String>>();
  /** The additional ad parameters */
  private String _parameters;
  /** The click through url */
  private String _clickThroughURL;
  /** The click tracking urls in an Set of String */
  private Set<String> _clickTrackingURLs = new HashSet<String>();
  /** The custom click urls in an Set of String */
  private Set<String> _customClickURLs = new HashSet<String>();
  /** The streams in an HashSet of Stream */
  private Set<Stream> _streams = new HashSet<Stream>();
  /** The skipoffset */
  private TimeOffset _skipOffset;

  private Set<Integer> errors = new HashSet<Integer>();

  /** icons */
  private List<Icon>  _icons = new ArrayList<Icon>();

  /**
   * Initialize a VASTLinearAd using the specified xml (subclasses should override this)
   * @param data the Element containing the xml to use to initialize this VASTLinearAd
   */
  Linear(Element data) {
    if (!data.getTagName().equals(Constants.ELEMENT_LINEAR)) { return; }
    String skipoffset = data.getAttribute(Constants.ATTRIBUTE_SKIPOFFSET);
    if (skipoffset != null && skipoffset.length() > 0) {
      _skipOffset = TimeOffset.parseOffset(skipoffset);
    }
    Node child = data.getFirstChild();
    while (child != null) {
      if (!(child instanceof Element)) {
        child = child.getNextSibling();
        continue;
      }
      if (!VASTUtils.isNullOrEmpty(child.getTextContent())
          && ((Element) child).getTagName().equals(Constants.ELEMENT_DURATION)) {
        _duration = VASTUtils.secondsFromTimeString(child.getTextContent(), 0);
      } else if (!VASTUtils.isNullOrEmpty(child.getTextContent())
          && ((Element) child).getTagName().equals(Constants.ELEMENT_AD_PARAMETERS)) {
        _parameters = child.getTextContent();
      } else if (((Element) child).getTagName().equals(Constants.ELEMENT_TRACKING_EVENTS)) {
        VASTUtils.parseTrackingEvents((Element)child, _trackingEvents);
      } else if (((Element) child).getTagName().equals(Constants.ELEMENT_VIDEO_CLICKS)) {
        Node clickChild = child.getFirstChild();
        while (clickChild != null) {
          if (!(clickChild instanceof Element) || VASTUtils.isNullOrEmpty(clickChild.getTextContent())) {
            clickChild = clickChild.getNextSibling();
            continue;
          }
          if (((Element) clickChild).getTagName().equals(Constants.ELEMENT_CLICK_THROUGH)) {
            _clickThroughURL = clickChild.getTextContent().trim();
          } else if (((Element) clickChild).getTagName().equals(Constants.ELEMENT_CLICK_TRACKING)) {
            _clickTrackingURLs.add(clickChild.getTextContent().trim());
          } else if (((Element) clickChild).getTagName().equals(Constants.ELEMENT_CUSTOM_CLICK)) {
            _customClickURLs.add(clickChild.getTextContent().trim());
          }
          clickChild = clickChild.getNextSibling();
        }
      } else if (((Element) child).getTagName().equals(Constants.ELEMENT_MEDIA_FILES)) {
        Node fileChild = child.getFirstChild();
        while (fileChild != null) {
          if (!(fileChild instanceof Element)) {
            fileChild = fileChild.getNextSibling();
            continue;
          }
          VASTStream stream = new VASTStream((Element) fileChild);
          _streams.add(stream);
          fileChild = fileChild.getNextSibling();
        }
      } else if (((Element) child).getTagName().equals(Constants.ELEMENT_ICONS)) {
        Node fileChild = child.getFirstChild();
        while (fileChild != null) {
          if (fileChild instanceof Element) {
            Icon icon = new Icon((Element) fileChild);
            _icons.add(icon);
          }
          fileChild = fileChild.getNextSibling();
        }
      }
      child = child.getNextSibling();
    }
  }

  /**
   * Fetch the duration of this VASTLinearAd in seconds.
   * @return the duration in seconds.
   */
  public double getDuration() {
    return _duration;
  }

  /**
   * Fetch a HashMap containing the tracking events.
   * @return a HashMap containing the tracking events. The key is the event name and the value is a Set of
   *         Strings.
   */
  public HashMap<String, Set<String>> getTrackingEvents() {
    return _trackingEvents;
  }

  /**
   * Fetch the additional ad parameters.
   * @return the additional ad parameters.
   */
  public String getParameters() {
    return _parameters;
  }

  /**
   * Fetch the URL go to when the user clicks this ad.
   * @return the URL go to when the user clicks this ad.
   */
  public String getClickThroughURL() {
    return _clickThroughURL;
  }

  /**
   * Fetch the Set of URLs to ping when the user clicks this ad.
   * @return The Set of URLs to ping when the user clicks this ad.
   */
  public Set<String> getClickTrackingURLs() {
    return _clickTrackingURLs;
  }

  /**
   * Fetch the custom click URLs as a Set of Strings
   * @return the custom click URLs as a Set of Strings
   */
  public Set<String> getCustomClickURLs() {
    return _customClickURLs;
  }

  /**
   * Fetch all the streams associated with this VASTLinearAd.
   * @return a Set of Stream objects
   */
  @Override
  public Set<Stream> getStreams() {
    return _streams;
  }

  /**
   * Fetch the best stream for this ad
   * @return a Set of Stream objects
   */
  public Stream getStream() {
    return Stream.bestStream(_streams, false);
  }

  /**
   * Fetch icons
   * @return a list of icons
   */
  public List<Icon> getIcons() {
    return _icons;
  }

  /**
   * Update the HashMap containing the tracking events by adding to it
   * @param trackingEvents new set of tracking events to add
   */
  public void updateTrackingEvents(HashMap<String, Set<String>> trackingEvents) {
    for (String event : trackingEvents.keySet()) {
      Set<String> newUrls = trackingEvents.get(event);
      Set<String> urls = _trackingEvents.get(event);

      if (urls != null) {
        urls.addAll(newUrls);
      } else {
        urls = new HashSet<String>();
        urls.addAll(newUrls);
        _trackingEvents.put(event, urls);
      }
    }
  }

  /**
   * Update the set containing click tracking urls by adding to it
   * @param newClickTrackingURLs new set of click tracking urls to add
   */
  public void updateClickTrackingURLs(Set<String> newClickTrackingURLs) {
    if (newClickTrackingURLs != null) {
      _clickTrackingURLs.addAll(newClickTrackingURLs);
    }
  }

  /**
   * Fetch if the ad is skippable
   * @return true if skippable, false otherwise
   */
  public boolean getSkippable() {
    return _skipOffset != null && _skipOffset.getType() != TimeOffset.Type.Position;
  }

  /**
   * Fetch if the vast ad skip offset
   * @return the offset in seconds, negative if the ad is not skippable.
   */
  public double getSkipOffset() {
    if (_skipOffset == null) {
      return -1;
    }
    switch (_skipOffset.getType()) {
      case Seconds:
        return _skipOffset.getSeconds();
      case Percentage:
        return _skipOffset.getPercentage() * _duration;
      default:
        return -1;
    }
  }

  public Set<Integer> getErrors() {
    return errors;
  }

  void merge(Linear linear) {
    if (linear == null) {
      return;
    }
    if (linear.getStreams() != null) {
      this._streams.addAll(linear.getStreams());
    }
    if (linear.getTrackingEvents() != null) {
      this._trackingEvents.putAll(linear.getTrackingEvents());
    }
    if (linear.getCustomClickURLs() != null) {
      this._customClickURLs.addAll(linear.getCustomClickURLs());
    }
  }
}

package com.ooyala.android.ads.vast;

import android.text.TextUtils;

import com.ooyala.android.AdvertisingIdUtils;
import com.ooyala.android.util.DebugMode;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

class VASTUtils {
  private static final String TAG = "VASTUtils";
  private static final String SEPARATOR_TIME = ":";
  private static final List<String> TIMESTAMP_MACROS_TO_REPLACE = Arrays.asList("%5BPlace_Random_Number_Here%5D",
      "[Place_Random_Number_Here]", "%3Cnow%3E", "%3Crand-num%3E", "[TIMESTAMP]", "%5BTIMESTAMP%5E", "[timestamp]", "%5Btimestamp%5E");
  private static final List<String> DEVICEID_MACROS_TO_REPLACE = Arrays.asList("%5BLR_DEVICEID%5D", "[LR_DEVICEID]");
  private static final String ERRORCODE_MACRO_TO_REPLACE = "[ERRORCODE]";

  public static boolean isNullOrEmpty(String string) {
    return string == null || string.equals("");
  }

  /*
   * parse string to time
   * legal format HH:MM:SS.mmm
   * @param time the time string
   * @param defaultValue the default value to be used.
   * @return time value in seconds, negative if failed;
   */
  public static double secondsFromTimeString(String time, double defaultValue) {
    if (time == null) {
      return defaultValue;
    }
    double seconds = 0;
    String[] hms = time.split(SEPARATOR_TIME);
    for (int i = 0; i < hms.length; ++i) {
      try {
        double value = Double.parseDouble(hms[i]);
        seconds = seconds * 60 + value;
      } catch (NumberFormatException e) {
        DebugMode.logE(TAG, "invalid time string: " + time);
        return defaultValue;
      }
    }
    return seconds;
  }

  /**
   * get boolean attribute from an XML element
   * @param e the xml element
   * @param attributeName the attribute name
   * @param defaultValue the default value used if the attribute does not present or is invalid
   * @return
   */
  public static boolean getBooleanAttribute(Element e, String attributeName, boolean defaultValue) {
    String attributeString = e.getAttribute(attributeName);
    if (attributeString == null) {
      DebugMode.logD(TAG, "Attribute " + attributeName + " does not exist");
      return defaultValue;
    }

    try {
      return Boolean.parseBoolean(attributeString);
    } catch (NumberFormatException ex) {
      DebugMode.logE(TAG, "Invalid Attribute " + attributeName + " :" + attributeString);
      return defaultValue;
    }
  }


  /**
   * get int attribute from an XML element
   * @param e the xml element
   * @param attributeName the attribute name
   * @return the integer value, default is used if attribute does not present or is invalid
   */
  public static int getIntAttribute(Element e, String attributeName, int defaultValue) {
    String attributeString = e.getAttribute(attributeName);
    if (attributeString == null) {
      DebugMode.logD(TAG, "Attribute " + attributeName + " does not exist");
      return defaultValue;
    }

    try {
      return Integer.parseInt(attributeString);
    } catch (NumberFormatException ex) {
      DebugMode.logE(TAG, "Invalid Attribute " + attributeName + " :" + attributeString);
      return defaultValue;
    }
  }

  public static URL urlFromAdUrlString(String urlStr) {
    urlStr = replaceTimestampMacros( urlStr );
    urlStr = replaceAdIdMacros( urlStr );

    URL url = null;
    try {
      url = new URL( urlStr );
    } catch (MalformedURLException e) {
      DebugMode.logE(TAG, "Malformed VAST URL: " + url);
    }
    return url;
  }

  private static String replaceTimestampMacros( String url ) {
    final String timestamp = "" + (System.currentTimeMillis() / 1000);
    for (String replace : TIMESTAMP_MACROS_TO_REPLACE) {
      url = url.replace(replace, timestamp);
    }
    return url;
  }

  private static String replaceAdIdMacros( String url ) {
    final String advertisingId = AdvertisingIdUtils.getAdvertisingId();
    if( advertisingId != null ) {
      for (String replace : DEVICEID_MACROS_TO_REPLACE) {
        url = url.replace(replace, advertisingId);
      }
    }
    return url;
  }

  public static URL formatErrorUrl(String errorUrl, Set<Integer> errorCodes) {
    if (errorUrl == null || errorCodes == null) {
      DebugMode.logE(TAG, "inputs cannot be null");
      return null;
    }

    String urlString = errorUrl.replace(ERRORCODE_MACRO_TO_REPLACE, TextUtils.join(",", errorCodes));
    URL url = null;
    try {
      url = new URL(urlString);
    } catch (MalformedURLException e) {
      DebugMode.logE(TAG, "Malformed VAST URL: " + url);
    }
    return url;
  }

  /**
   * Get an XML element from a url string.
   * @param url the url string
   * @param connectionTimeout connection timeout in milliseconds
   * @param readTimeout read timeout in milliseconds
   * @return the root element
   * @throws IOException
   * @throws ParserConfigurationException
   * @throws SAXException
   */
  public static Element getXmlDocument(URL url, int connectionTimeout, int readTimeout)
      throws IOException, ParserConfigurationException, SAXException {
    Element element = null;
    if (url == null) {
      DebugMode.logE(TAG, "url is null");
      return null;
    }

    URLConnection connection = null;
    try {
      connection = url.openConnection();
      connection.setConnectTimeout(connectionTimeout);
      connection.setReadTimeout(readTimeout);
    } catch (IOException ex) {
      DebugMode.logE(TAG, "Connection failed:" + ex.getMessage(), ex);
      throw ex;
    }

    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document doc = db.parse(connection.getInputStream());
      element = doc.getDocumentElement();
    } catch (ParserConfigurationException ex) {
      DebugMode.logE(TAG, "parser configuration failed:" + ex.getMessage(), ex);
      throw ex;
    } catch (SAXException ex) {
      DebugMode.logE(TAG, "parsing xml failed:" + ex.getMessage(), ex);
      throw ex;
    }

    return element;
  }

  /**
   * parse the tracking events element
   * @param element the tracking events element
   * @param map the output tracking events map
   */
  public static void parseTrackingEvents(Element element, Map<String, Set<String> > map) {
    if (map == null) {
      DebugMode.logE(TAG, "map is NULL!!!");
      return;
    }
    if (!Constants.ELEMENT_TRACKING_EVENTS.equals(element.getTagName())) {
      DebugMode.logE(TAG, "invalid tracking events element:" + element.getTagName());
      return;
    }

    for (Node node = element.getFirstChild(); node != null; node = node.getNextSibling()) {
      if (node instanceof Element) {
        Element tracking = (Element)node;
        String text = node.getTextContent();
        if (!Constants.ELEMENT_TRACKING.equals(tracking.getTagName()) ||
            VASTUtils.isNullOrEmpty(text)) {
          DebugMode.logE(TAG, "invalid tracking element");
          continue;
        }
        String event = ((Element) node).getAttribute(Constants.ATTRIBUTE_EVENT);
        Set<String> urls = map.get(event);
        if (urls != null) {
          urls.add(text.trim());
        } else {
          urls = new HashSet<String>();
          urls.add(text.trim());
          map.put(event, urls);
        }
      }
    }
  }
}

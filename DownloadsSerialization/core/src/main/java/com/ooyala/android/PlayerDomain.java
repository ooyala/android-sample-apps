package com.ooyala.android;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Represents the domain under which Ooyala analytics will be recorded.
 */
public class PlayerDomain {
  private static final String[] schemes = {"http://","https://"};
  private URL _domainUrl = null;

  public static boolean isValid(final String domain) {
    for (String scheme : PlayerDomain.schemes) {
      if (domain.startsWith(scheme)) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * Construct a PlayerDomain
   * @param domainString a parseable URL in the form of a string, starting with either http:// or https://.
   */
  public PlayerDomain(String domainString) {
    if (!PlayerDomain.isValid(domainString)) {
      throw new RuntimeException("Invalid Domain String: " + domainString);
    }
    
    try {
      _domainUrl = new URL(domainString);
    } catch (MalformedURLException e) {
      throw new RuntimeException ("Domain is malformed:" + domainString);
    }
  }

  public URL url() {
    return _domainUrl;
  }

  public String toString() {
    return _domainUrl.toString();
  }
}

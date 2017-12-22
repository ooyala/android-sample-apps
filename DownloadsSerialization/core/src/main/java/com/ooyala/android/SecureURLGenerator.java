package com.ooyala.android;

import java.net.URL;
import java.util.Map;

public interface SecureURLGenerator {
  /**
   * Generate the secure URL
   * <p>
   * This method should use one of the following security method to create a complete NSURL:
   * <ul>
   * <li>Create a signature from the parameters (including API Key and Domain, which are not guarenteed to be
   * in params) and a secret
   * <li>Create a URL by appending the params (making sure to URL encode the signature)
   * 
   * </ul>@param host the hostname for the URL
   * @param uri the URI for the URL
   * @param params the URI params for the URL (not including any security params that the security method
   *          would use)
   * @return a secure NSURL created from the parameters using one of the supported security methods
   */
  public URL secureURL(String host, String uri, Map<String, String> params);
}

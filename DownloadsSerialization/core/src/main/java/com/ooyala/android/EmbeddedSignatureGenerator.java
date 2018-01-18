package com.ooyala.android;

import java.security.MessageDigest;

import android.util.Base64;

public class EmbeddedSignatureGenerator implements SignatureGenerator {
  private String _secret = null;

  public EmbeddedSignatureGenerator(String secret) {
    _secret = secret;
  }

  /**
   * Generate the APIv2/SAS style signature
   * This method should do the following:
   * <ul>
   * <li> Prepend the secret key to data
   * <li> Hash the resulting string using the SHA256 algorithm
   * <li> Base64 encode the resulting hash
   * <li> Convert the Base64 encoded hash to a String
   * <li> Truncate the String to 43 characters
   * <li> Strip any '=' characters from the end of the truncated String
   * <li> Return the resulting String
   * </ul>
   * @param data the String to create the signature from (not prepended with the secret key)
   * @return String containing the signature
   */
  public String sign(String data) {
    try {
      String prepended = _secret + data;
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] sha256 = md.digest(prepended.getBytes());
      String base64 = Base64.encodeToString(sha256, Base64.DEFAULT);
      if (base64.length() > 43) {
        base64 = base64.substring(0, 43);
      }
      return base64.replaceAll("=", "");
    } catch (Exception e) {
      System.out.println("Exception generating signature: " + e);
    }
    return null;
  }
}

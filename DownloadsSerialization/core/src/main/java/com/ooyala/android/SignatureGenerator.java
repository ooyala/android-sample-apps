package com.ooyala.android;

public interface SignatureGenerator {
  /**
   * Generate the APIv2/SAS style signature.
   * <p>
   * This method should do the following:
   * <ul>
   * <li>Prepend the secret key to data
   * <li>Hash the resulting string using the SHA256 algorithm
   * <li>Base64 encode the resulting hash
   * <li>Convert the Base64 encoded hash to an NSString
   * <li>Truncate the NSString to 43 characters
   * <li>Strip any '=' characters from the end of the truncated NSString
   * <li>Return the resulting NSString
   * </ul>
   * @param data the NSString to create the signature from (not prepended with the secret key)
   * @return an NSString containing the signature
   */
  public String sign(String data);
}

package com.ooyala.android;

public interface EmbedTokenGeneratorCallback {
  /**
   * This callback is used for asynchronous EmbedTokenGenerator calls
   * @param token embed token for playback authentication
   */
  public void setEmbedToken(String token);
}

package com.ooyala.android;

/**
 * A class which specifies and allows setting of the environment for Ooyala APIs in the OoyalaPlayer
 */
public class Environment {
  /**
   * An enumeration of the various Ooyala Environments that can be used for running the OoyalaPlayer
   */
  public static enum EnvironmentType {
    PRODUCTION, NEXTSTAGING, STAGING, LOCAL
  };

  public static String JS_ANALYTICS_HOST = "http://player.ooyala.com";
  public static String AUTHORIZE_HOST = "http://player.ooyala.com";
  public static String CONTENT_TREE_HOST = "http://player.ooyala.com";
  public static String DRM_HOST = "http://player.ooyala.com";
  public static String BACKLOT_HOST = "http://cdn.api.ooyala.com";
  public static String METADATA_HOST = "http://player.ooyala.com";

  /**
   * Set the environment to use when calling Ooyala APIs
   * @param e
   */
  static void setEnvironment(EnvironmentType e) {
    if (e == EnvironmentType.PRODUCTION) {
      JS_ANALYTICS_HOST = "http://player.ooyala.com";
      AUTHORIZE_HOST = "http://player.ooyala.com";
      CONTENT_TREE_HOST = "http://player.ooyala.com";
      DRM_HOST = "http://player.ooyala.com";
      BACKLOT_HOST = "http://cdn.api.ooyala.com";
      METADATA_HOST = "http://player.ooyala.com";
    } else if (e == EnvironmentType.STAGING) {
      JS_ANALYTICS_HOST = "http://player-staging.ooyala.com";
      AUTHORIZE_HOST = "http://player-staging.ooyala.com";
      CONTENT_TREE_HOST = "http://player-staging.ooyala.com";
      DRM_HOST = "http://player-staging.ooyala.com";
      BACKLOT_HOST = "http://api-staging.ooyala.com";
      METADATA_HOST = "http://player-staging.ooyala.com";
    } else if (e == EnvironmentType.NEXTSTAGING) {
      JS_ANALYTICS_HOST = "http://player-next-staging.ooyala.com";
      AUTHORIZE_HOST = "http://player-next-staging.ooyala.com";
      CONTENT_TREE_HOST = "http://player-next-staging.ooyala.com";
      DRM_HOST = "http://player-next-staging.ooyala.com";
      BACKLOT_HOST = "http://api-next-staging.ooyala.com";
      METADATA_HOST = "http://player-next-staging.ooyala.com";
    } else if (e == EnvironmentType.LOCAL) {
      JS_ANALYTICS_HOST = "http://dev.corp.ooyala.com:3000";
      AUTHORIZE_HOST = "http://dev.corp.ooyala.com:4567";
      CONTENT_TREE_HOST = "http://dev.corp.ooyala.com:3000";
      DRM_HOST = "http://dev.corp.ooyala.com:4567";
      BACKLOT_HOST = "http://api-staging.ooyala.com";
      METADATA_HOST = "http://dev.corp.ooyala.com:3000";
    } else {
      JS_ANALYTICS_HOST = "http://player.ooyala.com";
      AUTHORIZE_HOST = "http://player.ooyala.com";
      CONTENT_TREE_HOST = "http://player.ooyala.com";
      DRM_HOST = "http://player.ooyala.com";
      BACKLOT_HOST = "http://cdn.api.ooyala.com";
      METADATA_HOST = "http://player.ooyala.com";
    }
  }

}
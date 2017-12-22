package com.ooyala.android;

import java.util.UUID;

/**
 * Manages the creation of Player and Content Session IDs
 * NOTE: This could also be the class that notifies of changes in Session IDs
 */
class OoyalaPlayerSessionIDManager {

  private String playerSessionId;
  private String contentSessionId;

  OoyalaPlayerSessionIDManager() {
    this.playerSessionId = UUID.randomUUID().toString();
    this.contentSessionId = null;
  }

  /**
   * A Session ID that is created at the initialization of OoyalaPlayer. Persists for the life of the OoyalaPlayer
   * @return
   */
  String getPlayerSessionId() {
    return this.playerSessionId;
  }

  /**
   * A Session ID that is created on the set of a new piece of content (i.e setEmbedCode). Persists until a new piece of content is set
   * @return
   */
  String getContentSessionId() {
    return this.contentSessionId;
  }

  /**
   * Used when a new piece of content is set, and a new Content Session ID must be created
   */
  void regenerateContentSessionId() {
      this.contentSessionId = UUID.randomUUID().toString();
  }

}

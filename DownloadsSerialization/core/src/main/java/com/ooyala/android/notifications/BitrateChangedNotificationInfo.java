package com.ooyala.android.notifications;

/**
 * A class that is passed as the "data" in OoyalaNotifications which have
 * OoyalaPlayer.BITRATE_CHANGED_NOTIFICATION_NAME as the name.
 * This notification is only supported with our ExoPlayer integration
 */
public class BitrateChangedNotificationInfo {
  private final int oldBitrate;
  private final int newBitrate;

  /**
   * Create the notification info object passed with the BITRATE_CHANGED_NOTIFICATION_NAME
   * @param oldBitrate The previously observed bitrate
   * @param newBitrate The bitrate that is about to be rendered
   */
  public BitrateChangedNotificationInfo(int oldBitrate, int newBitrate) {
    this.oldBitrate = oldBitrate;
    this.newBitrate = newBitrate;
  }

  /**
   * Get the previously observed bitrate.
   * @return The previously observed bitrate.  Returns 0 if this is the first BitrateChanged notification for this video playback
   */
  public int getOldBitrate() {
    return oldBitrate;
  }

  /**
   * Get the new bitrate that is being reported in this notification
   * @return The bitrate that is about to be rendered
   */
  public int getNewBitrate() {
    return newBitrate;
  }
}

package com.ooyala.android;
/**
 * A callback interface for some Asynchronous callback tasks
 * Generally used only internally for network requests
 */
public interface ServerTaskCallback {
  /**
   * called when the task is completed, caller should check both item and error
   * @param success success if the task completed successfully
   * @param error the error, could be null
   */
  void callback(boolean success, OoyalaException error);

}

package com.ooyala.android;

import com.ooyala.android.item.AuthorizableItem;
import com.ooyala.android.util.DebugMode;

class ServerTaskReauthorize implements Runnable {
  private static final String TAG = ServerTaskReauthorize.class.getSimpleName();

  private final AuthorizableItem item;
  private final PlayerAPIClient apiClient;
  private final PlayerInfo playerInfo;
  private final ServerTaskCallback callback;

  public ServerTaskReauthorize(PlayerAPIClient apiClient, PlayerInfo playerInfo, AuthorizableItem item, ServerTaskCallback callback) {
    this.item = item;
    this.apiClient = apiClient;
    this.playerInfo = playerInfo;
    this.callback = callback;
  }

  @Override
  public void run() {
    OoyalaException error = null;
    Boolean result = false;
    try {
      result = apiClient.authorize(item, playerInfo);
    } catch (OoyalaException ex) {
      DebugMode.logE(TAG, "reauthorize item failed" + ex.getMessage(), ex);
      error = ex;
    }

    if (callback != null) {
      callback.callback(result, error);
    }
  }
}

package com.ooyala.android;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * A base class for server substasks
 */
abstract class ServerSubTask<T> implements Callable<T> {
  protected final PlayerAPIClient apiClient;
  protected final PlayerInfo playerInfo;
  protected final List<String> embedCodes;
  protected final String adSetCode;

  protected ServerSubTask(PlayerAPIClient apiClient, PlayerInfo playerInfo, List<String> embedCodes, String adSetCode) {
    this.apiClient = apiClient;
    this.playerInfo = playerInfo;
    this.embedCodes = embedCodes;
    this.adSetCode = adSetCode;
  }
}

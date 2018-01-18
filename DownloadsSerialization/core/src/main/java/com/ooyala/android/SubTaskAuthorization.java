package com.ooyala.android;

import org.json.JSONObject;

import java.util.List;

/**
 * A class that implements fetching authorization data subtask.
 */
class SubTaskAuthorization extends ServerSubTask<JSONObject> {
  public SubTaskAuthorization(PlayerAPIClient apiClient, PlayerInfo playerInfo, List<String> embedCodes, String adSetCode) {
    super(apiClient, playerInfo, embedCodes, adSetCode);
  }

  @Override
  public JSONObject call() throws OoyalaException{
    return apiClient.authorizeEmbedCodes(embedCodes, playerInfo);
  }
}

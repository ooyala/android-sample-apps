package com.ooyala.android;

import org.json.JSONObject;

import java.util.List;

/**
 * A class that implements fetching metadata subtask.
 */
class SubTaskMetaData extends ServerSubTask<JSONObject> {
  public SubTaskMetaData(PlayerAPIClient apiClient, PlayerInfo playerInfo, List<String> embedCodes, String adSetCode) {
    super(apiClient, playerInfo, embedCodes, adSetCode);
  }

  @Override
  public JSONObject call() throws OoyalaException {
    return apiClient.fetchMetadataForEmbedCodesWithAdSet(embedCodes, adSetCode);
  }
}


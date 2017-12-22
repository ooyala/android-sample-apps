package com.ooyala.android;

import com.ooyala.android.item.ContentItem;

import java.util.List;

/**
 * A class that implements Content Tree subtask.
 */
public class SubTaskContentTree extends ServerSubTask<ContentItem> {
  public SubTaskContentTree(PlayerAPIClient apiClient, PlayerInfo playerInfo, List<String> embedCodes, String adSetCode) {
    super(apiClient, playerInfo, embedCodes, adSetCode);
  }

  @Override
  public ContentItem call() throws OoyalaException{
    return apiClient.contentTreeWithAdSet(embedCodes, adSetCode);
  }
}

package com.ooyala.android;

import com.ooyala.android.item.ContentItem;

import java.util.List;

/**
 * A class that implements Content Tree by external IDs subtask.
 */
public class SubTaskContentTreeByExternalIds extends ServerSubTask<ContentItem> {
  public SubTaskContentTreeByExternalIds(
      PlayerAPIClient apiClient, PlayerInfo playerInfo, List<String> embedCodes, String adSetCode) {
    super(apiClient, playerInfo, embedCodes, adSetCode);
  }

  @Override
  public ContentItem call() throws OoyalaException{
    ContentItem result = apiClient.contentTreeByExternalIds(embedCodes);
    return result;
  }
}

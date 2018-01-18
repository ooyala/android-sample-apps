package com.ooyala.android;

import com.ooyala.android.item.Video;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that implements auth and metadata task
 */
public class ServerTaskAuthAndMetadata extends ServerTaskVideo {
  public ServerTaskAuthAndMetadata(
      PlayerAPIClient apiClient, PlayerInfo playerInfo, Video video, int connectionTimeoutInMilliseconds, ServerTaskManager.ContentItemCallback callback) {
    super(apiClient, playerInfo, new ArrayList<String>(), null, connectionTimeoutInMilliseconds, callback);
    this.embedCodes.add(video.getEmbedCode());
    this.item = video;
  }

  @Override
  protected List<ServerSubTask> prepareSubTasks() {
    List<ServerSubTask> subTasks = new ArrayList<>();
    subTasks.add(new SubTaskAuthorization(apiClient, playerInfo, embedCodes, adSetCode));
    subTasks.add(new SubTaskMetaData(apiClient, playerInfo, embedCodes, adSetCode));
    return subTasks;
  }
}

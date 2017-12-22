package com.ooyala.android;

import com.ooyala.android.item.ContentItem;
import com.ooyala.android.item.DynamicChannel;
import com.ooyala.android.util.DebugMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A class that implements video fetching by external Ids
 */
class ServerTaskVideoByExternalIds extends ServerTaskVideo {
  private static final String TAG = ServerTaskVideoByExternalIds.class.getSimpleName();

  public ServerTaskVideoByExternalIds(
      PlayerAPIClient apiClient, PlayerInfo playerInfo, List<String> embedCodes, String adSetCode, int connectionTimeoutInMilliseconds, ServerTaskManager.ContentItemCallback callback) {
    super(apiClient, playerInfo, embedCodes, adSetCode, connectionTimeoutInMilliseconds, callback);
  }

  /**
   * Initializes tasks with their required info.
   * @return List of tasks to perform.
     */
  @Override
  protected List<ServerSubTask> prepareSubTasks() {
    List<ServerSubTask> subTasks = new ArrayList<>();
    subTasks.add(new SubTaskAuthorization(apiClient, playerInfo, embedCodes, adSetCode));
    subTasks.add(new SubTaskMetaData(apiClient, playerInfo, embedCodes, adSetCode));
    return subTasks;
  }

  /**
   * This run() will only fetch the content tree, the parent will perform both
   * metadata and authorization requests.
   * This behavior is required because for external ids we need to get the embed code
   * before performing the metadata and authorization requests. The embed code can
   * be found in the content tree response.
   */
  @Override
  public void run() {
    OoyalaException error = null;

    // Send content tree call first and wait for the response
    Future<ContentItem> contentTreeFuture = Utils.sharedExecutorService()
            .submit(new SubTaskContentTreeByExternalIds(apiClient, playerInfo, embedCodes, adSetCode));
    try {
      if (item == null) {
        item = contentTreeFuture.get(connectionTimeoutInMilliseconds, TimeUnit.MILLISECONDS);
        if (item == null) {
          DebugMode.logD(TAG, "content tree is invalid");
          error = new OoyalaException(OoyalaException.OoyalaErrorCode.ERROR_CONTENT_TREE_INVALID);
        }
      }
    } catch (ExecutionException | InterruptedException | TimeoutException ex) {
      error = new OoyalaException(OoyalaException.OoyalaErrorCode.ERROR_CONTENT_TREE_INVALID, " Not able to retrieve content tree", ex);
    }

    // Substitute external ids with real embed codes to be used for Metadata and Authorization requests
    if (null != item) {
      if (null != item.getEmbedCode()) { // only one embed code
        this.embedCodes.clear();
        this.embedCodes.add(item.getEmbedCode());
      } else if (item instanceof DynamicChannel) { // multiple embed codes
        this.embedCodes.clear();
        this.embedCodes.addAll(((DynamicChannel) item).getEmbedCodes());
      }
    }

    if (null != error && null != callback) { // there's an error, stop here
      callback.callback(item, error);
    } else { // continue with metadata and authorization
      super.run();
    }
  }

}

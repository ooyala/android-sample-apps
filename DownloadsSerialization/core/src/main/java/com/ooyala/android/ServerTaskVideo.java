package com.ooyala.android;

import com.ooyala.android.item.ContentItem;
import com.ooyala.android.item.Video;
import com.ooyala.android.util.DebugMode;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A class that implements video fetching
 */
class ServerTaskVideo implements Runnable {
  private static final String TAG = ServerTaskVideo.class.getSimpleName();

  protected final PlayerAPIClient apiClient;
  protected final PlayerInfo playerInfo;
  protected final String adSetCode;
  protected final ServerTaskManager.ContentItemCallback callback;
  protected final List<String> embedCodes;
  protected ContentItem item;
  protected final int connectionTimeoutInMilliseconds;

  public ServerTaskVideo(
      PlayerAPIClient apiClient, PlayerInfo playerInfo, List<String> embedCodes, String adSetCode, int connectionTimeoutInMilliseconds, ServerTaskManager.ContentItemCallback callback) {
    this.apiClient = apiClient;
    this.playerInfo = playerInfo;
    this.embedCodes = embedCodes;
    this.adSetCode = adSetCode;
    this.callback = callback;
    this.item = null;
    this.connectionTimeoutInMilliseconds = connectionTimeoutInMilliseconds;
  }

  protected List<ServerSubTask> prepareSubTasks() {
    List<ServerSubTask> subTasks = new ArrayList<>();
    subTasks.add(new SubTaskContentTree(apiClient, playerInfo, embedCodes, adSetCode));
    subTasks.add(new SubTaskAuthorization(apiClient, playerInfo, embedCodes, adSetCode));
    subTasks.add(new SubTaskMetaData(apiClient, playerInfo, embedCodes, adSetCode));
    return subTasks;
  }

  @Override
  public void run() {
    OoyalaException error = null;
    List<Future> pendingTasks = new ArrayList<>(3);

    for (ServerSubTask subTask : prepareSubTasks()) {
      pendingTasks.add(Utils.sharedExecutorService().submit(subTask));
    }

    int subTaskCount = pendingTasks.size();
    int i = 0;
    do {
      try {
        if (item == null) {
          // need to fetch content tree first
          Future<ContentItem> contentTreeFuture = pendingTasks.get(i++);
          item = contentTreeFuture.get(connectionTimeoutInMilliseconds, TimeUnit.MILLISECONDS);
          if (item == null) {
            DebugMode.logD(TAG, "content tree is invalid");
            error = new OoyalaException(OoyalaException.OoyalaErrorCode.ERROR_CONTENT_TREE_INVALID);
            break;
          }
        }

        // fetch authorization data
        Boolean success = processJsonTask(pendingTasks.get(i++), item);
        if (!success) {
          DebugMode.logD(TAG, "authorization failed");
          error = new OoyalaException(OoyalaException.OoyalaErrorCode.ERROR_AUTHORIZATION_FAILED);
          break;
        }

        // fetch metadata
        success = processJsonTask(pendingTasks.get(i++), item);
        if (!success) {
          DebugMode.logD(TAG, "fetch metadata failed");
          error = new OoyalaException(OoyalaException.OoyalaErrorCode.ERROR_METADATA_FETCH_FAILED);
          break;
        }
      } catch (ExecutionException | InterruptedException | TimeoutException ex) {
        error = new OoyalaException(OoyalaException.OoyalaErrorCode.ERROR_CONTENT_TREE_INVALID, " Not able to retrieve content tree", ex);
      } finally {
        for (; i < subTaskCount; ++i) {
          pendingTasks.get(i).cancel(true);
        }
      }
    } while (false);

    // fetch playback info, if necessary
    if (item != null) {
      Video video = item.firstVideo();
      if (video != null && video.needsFetchInfo()) {
        pendingTasks.clear();
        List<Callable<Boolean>> playbackInfoSubTasks = ServerTaskPlaybackInfo.prepareSubTasks(video, apiClient, playerInfo);
        for (Callable<Boolean> subTask : playbackInfoSubTasks) {
          pendingTasks.add(Utils.sharedExecutorService().submit(subTask));
        }

        for (Future<Boolean> future : pendingTasks) {
          try {
            Boolean success = future.get(connectionTimeoutInMilliseconds, TimeUnit.MILLISECONDS);
            if (!success) {
              DebugMode.logE(TAG, "playback info fetch subtask failed");
            }
          } catch (ExecutionException | InterruptedException | TimeoutException ex) {
            DebugMode.logE(TAG, ex.getMessage(), ex);
            continue;
          }
        }
      }
    }

    // notify finish
    if (callback != null) {
      callback.callback(item, error);
    }
  }

  protected boolean processJsonTask(Future<JSONObject> jsonFuture, ContentItem item)
      throws ExecutionException, InterruptedException, TimeoutException {
    JSONObject json = jsonFuture.get(connectionTimeoutInMilliseconds, TimeUnit.MILLISECONDS);
    if (json == null) {
      return false;
    } else {
      item.update(json);
      return true;
    }
  }
}

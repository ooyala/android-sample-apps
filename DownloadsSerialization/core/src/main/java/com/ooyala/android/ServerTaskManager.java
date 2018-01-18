package com.ooyala.android;

import com.ooyala.android.item.ContentItem;
import com.ooyala.android.item.Video;
import com.ooyala.android.util.DebugMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * A Class that handles parallel server tasks
 */
class ServerTaskManager {
  private static final String TAG = ServerTaskManager.class.getSimpleName();

  private Map<Integer, Future> tasks = new HashMap<>();
  private final PlayerAPIClient apiClient;
  private final PlayerInfo playerInfo;
  private final int connectionTimeoutInMilliseconds;

  public ServerTaskManager(PlayerAPIClient apiClient, PlayerInfo playerInfo, int connectionTimeoutInMilliseconds) {
    this.apiClient = apiClient;
    this.playerInfo = playerInfo;
    this.connectionTimeoutInMilliseconds = connectionTimeoutInMilliseconds;
  }

  /**
   * a callback interface for content item fetch tasks
   */
  public interface ContentItemCallback {
    /**
     * called when the task is completed, caller should check both item and error
     * @param item the fetched items, null if failed
     * @param error the error, could be null
     */
    void callback(ContentItem item, OoyalaException error);
  }

  /**
   * start fetching a content tree/auth/metadata/playback info in parallel
   * @param embedCodes the embedCodes
   * @param adSetCode the adSetCode
   * @param callback a callback to notify the caller for results
   */
  public void fetchVideo(List<String> embedCodes, String adSetCode, final ContentItemCallback callback) {
    final int signature = embedCodes.hashCode();
    ServerTaskVideo task = new ServerTaskVideo(apiClient, playerInfo, embedCodes, adSetCode, connectionTimeoutInMilliseconds, new ContentItemCallback() {
      @Override
      public void callback(ContentItem item, OoyalaException error) {
        tasks.remove(signature);
        if (callback != null) {
          callback.callback(item, error);
        }
      }
    });
    startTask(signature, task);
  }

  /**
   * start fetching content tree/auth/metadata/playback info by external Ids in parallel
   * @param embedCodes the embedCodes
   * @param adSetCode the adSetCode
   * @param callback a callback to notify the caller for results
   */
  public void fetchVideoByExternalId(List<String> embedCodes, String adSetCode, final ContentItemCallback callback) {
    final int signature = embedCodes.hashCode();
    ServerTaskVideoByExternalIds task = new ServerTaskVideoByExternalIds(apiClient, playerInfo, embedCodes, adSetCode, connectionTimeoutInMilliseconds, new ContentItemCallback() {
      @Override
      public void callback(ContentItem item, OoyalaException error) {
        tasks.remove(signature);
        if (callback != null) {
          callback.callback(item, error);
        }
      }
    });
    startTask(signature, task);
  }

  /**
   * given a video object, fetch auth and metadata
   * @param video the video
   * @param callback a callback to notify the caller for results
   */
  public void fetchVideoAuthAndMetadata(final Video video, final ServerTaskCallback callback) {
    if (video == null) {
      DebugMode.logE(TAG, "fetch info is not required");
      if (callback != null) {
        callback.callback(true, null);
      }
      return;
    }

    final int signature = video.hashCode();
    ServerTaskAuthAndMetadata task = new ServerTaskAuthAndMetadata(apiClient, playerInfo, video, connectionTimeoutInMilliseconds, new ContentItemCallback() {
      @Override
      public void callback(ContentItem item, OoyalaException error) {
        tasks.remove(signature);
        if (callback != null) {
          callback.callback(true, error);
        }

      }
    });
    startTask(signature, task);
  }

  /**
   * given a video object, fetch ads and closed captions
   * @param video the video
   * @param callback a callback to notify the caller for results
   */
  public void fetchPlaybackInfo(Video video, final ServerTaskCallback callback) {
    if (video == null || !video.needsFetchInfo()) {
      DebugMode.logE(TAG, "fetch info is not required");
      if (callback != null) {
        callback.callback(true, null);
      }
      return;
    }

    final int signature = video.hashCode();
    ServerTaskPlaybackInfo task = new ServerTaskPlaybackInfo(apiClient, playerInfo, video, connectionTimeoutInMilliseconds, new ServerTaskCallback(){
      @Override
      public void callback(boolean success, OoyalaException error) {
        tasks.remove(signature);
        if (callback != null) {
          callback.callback(success, error);
        }
      }
    });
    startTask(signature, task);
  }

  /**
   * reauthorize a video
   * @param video the video
   * @param callback a callback to notify the caller for results
   */
  public void reauthorize(Video video, final ServerTaskCallback callback) {
    if (video == null) {
      DebugMode.logE(TAG, "cannot reauthorize a null item");
      if (callback != null) {
        callback.callback(false, null);
      }
      return;
    }

    final int signature = video.hashCode();
    ServerTaskReauthorize task = new ServerTaskReauthorize(apiClient, playerInfo, video, new ServerTaskCallback(){
      @Override
      public void callback(boolean success, OoyalaException error) {
        tasks.remove(signature);
        if (callback != null) {
          callback.callback(success, error);
        }
      }
    });
    startTask(signature, task);
  }

  /**
   * cancel all pending tasks
   */
  public void cancelAll() {
    for (Future f : tasks.values()) {
      f.cancel(true);
    }
    tasks.clear();
  }

  private void startTask(int key, Runnable task) {
    if (tasks.containsKey(key)) {
      tasks.get(key).cancel(true);
    }
    tasks.put(key, Utils.sharedExecutorService().submit(task));
  }
}

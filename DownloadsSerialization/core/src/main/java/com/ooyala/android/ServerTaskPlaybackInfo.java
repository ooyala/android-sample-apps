package com.ooyala.android;

import com.ooyala.android.item.ClosedCaptions;
import com.ooyala.android.item.OoyalaManagedAdSpot;
import com.ooyala.android.item.Video;
import com.ooyala.android.util.DebugMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A class that implement fetching playback info
 */
class ServerTaskPlaybackInfo implements Runnable {
  private static final String TAG = ServerTaskPlaybackInfo.class.getSimpleName();

  private final PlayerAPIClient apiClient;
  private final PlayerInfo playerInfo;
  private final Video item;
  private final ServerTaskCallback callback;
  private final int connectionTimeoutInMilliseconds;

  public ServerTaskPlaybackInfo(PlayerAPIClient apiClient, PlayerInfo playerInfo, Video item, int connectionTimeoutInMilliseconds, ServerTaskCallback callback) {
    this.apiClient = apiClient;
    this.playerInfo = playerInfo;
    this.item = item;
    this.callback = callback;
    this.connectionTimeoutInMilliseconds  = connectionTimeoutInMilliseconds;
  }

  public static List<Callable<Boolean>> prepareSubTasks(Video item, PlayerAPIClient apiClient, PlayerInfo playerInfo) {
    List<Callable<Boolean>> subTasks = new ArrayList<>();
    OoyalaAPIClient api = new OoyalaAPIClient(apiClient);
    if (item.hasAds()) {
      for (OoyalaManagedAdSpot ad : item.getAds()) {
        subTasks.add(new SubTaskAdInfo(ad, api, playerInfo));
      }
    }

    if (item.hasClosedCaptions()) {
      ClosedCaptions cc = item.getClosedCaptions();
      subTasks.add(new SubTaskClosedCaptions(cc));
    }

    return subTasks;
  }

  @Override
  public void run() {
    OoyalaException error = null;
    List<Future<Boolean>> pendingTasks = new ArrayList<>();
    for (Callable<Boolean> subTask : prepareSubTasks(item, apiClient, playerInfo)) {
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

    if (callback != null) {
      callback.callback(true, error);
    }
  }
}

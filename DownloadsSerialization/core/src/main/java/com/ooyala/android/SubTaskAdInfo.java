package com.ooyala.android;

import com.ooyala.android.item.OoyalaManagedAdSpot;

import java.util.concurrent.Callable;

/**
 * A class that impelemnts fetching ad info
 */
class SubTaskAdInfo implements Callable<Boolean>  {
  private final OoyalaManagedAdSpot ad;
  private final OoyalaAPIClient api;
  private final PlayerInfo playerInfo;

  public SubTaskAdInfo(OoyalaManagedAdSpot ad, OoyalaAPIClient api, PlayerInfo playerInfo) {
    this.ad = ad;
    this.api = api;
    this.playerInfo = playerInfo;
  }

  @Override
  public Boolean call() {
    return ad.fetchPlaybackInfo(api, playerInfo);
  }
}

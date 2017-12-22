package com.ooyala.android.plugin;

import com.ooyala.android.AdsLearnMoreInterface;
import com.ooyala.android.player.PlayerInterface;

import java.util.Set;

/**
 * The interface that must be implemented in order to plug into OoyalaPlayer to
 * play ads.
 * 
 * 
 */
public interface AdPluginInterface extends LifeCycleInterface, AdsLearnMoreInterface {
  /**
   * This is called when content changed
   * 
   * @return a token string if plugin wants take control, null otherwise
   */
  public boolean onContentChanged();

  /**
   * This is called before start playing content so plugin can play preroll
   * 
   * @return a token string if plugin wants take control, null otherwise
   */
  public boolean onInitialPlay();

  /**
   * This is called when playhead is updated so plugin can play midroll
   * 
   * @return a token string if plugin wants take control, null otherwise
   */
  public boolean onPlayheadUpdate(int playhead);

  /**
   * This is called before finishing playing content so plugin can play postroll
   * 
   * @return a token string if plugin wants take control, null otherwise
   */
  public boolean onContentFinished(); // put your postrolls here

  /**
   * This is called when a cue point is reached so plugin can play midroll
   * 
   * @return a token string if plugin wants take control, null otherwise
   */
  public boolean onCuePoint(int cuePointIndex);

  /**
   * This is called when an error occured when playing back content
   * 
   * @return a token string if plugin wants take control, null otherwise
   */
  public boolean onContentError(int errorCode);

  /**
   * This is called when control is handed over to the plugin
   */
  public void onAdModeEntered();

  /**
   * This is called ooyala UI pass down UI related events.
   * 
   * @return an object that implements PlayerInterface if plugin needs to
   *         process ui events, null if these events should be ignored.
   */
  public PlayerInterface getPlayerInterface();

  /**
   * This is called to reset all ads to unplayed.
   */
  public void resetAds();

  /**
   * This is called to skip the current ad.
   * 
   */
  public void skipAd();

  /**
   * This is called when an icon is clicked.
   * @param index the icon index
   */
  public void onAdIconClicked(int index);

  /**
   * This returns the cue points.
   * 
   */
  public Set<Integer> getCuePointsInMilliSeconds();
}

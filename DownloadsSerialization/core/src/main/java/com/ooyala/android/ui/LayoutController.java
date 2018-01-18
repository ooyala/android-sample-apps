package com.ooyala.android.ui;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.ooyala.android.OoyalaPlayerLayout;

public interface LayoutController {
  /**
   * Get the current active layout to display the video on.
   * @return the current active layout.
   */
  public FrameLayout getLayout();

  /**
   * Set the fullscreen state
   * @param fullscreen true for fullscreen, false for inline
   */
  public void setFullscreen(boolean fullscreen);

  /**
   * Get the fullscreen state
   * @return true for fullscreen, false for inline
   */
  public boolean isFullscreen();


  /**
   * Show the Closed Captions selector menu
   */
  public void showClosedCaptionsMenu();

  /**
   * Handle the touch events from OoyalaPlayerLayout
   * @param event the event
   * @param source the layout that created the event
   * @return
   */
  public boolean onTouchEvent(MotionEvent event, OoyalaPlayerLayout source);

  /**
   * Handle the keydown events from OoyalaPlayerLayout
   * @param keyCode the keycode
   * @param event the event
   * @return
   */
  public boolean onKeyUp(int keyCode, KeyEvent event);

  /**
   * Insert the video view into the view hierarchy. Will
   * automatically include other views such as TV Ratings.
   * @param videoView
   */
  public void addVideoView( View videoView );

  /**
   * Remove any previously added video view, and related
   * items such as the TV Ratings view.
   */
  public void removeVideoView();

  /**
   * Reshow TV rating (if the current FCCTVRatingConfiguration allows it).
   */
  public void reshowTVRating();

  /**
   * setFullscreenButtonShowing will enable and disable visibility of the fullscreen button
   */
  public void setFullscreenButtonShowing(boolean showing);
}

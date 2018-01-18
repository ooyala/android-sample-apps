package com.ooyala.android;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.ooyala.android.ui.LayoutController;

public class OoyalaPlayerLayout extends FrameLayout {
  private LayoutController _controller = null;

  protected FrameLayout _playerFrame = null;

  /**
   * Initialize the OoyalaPlayerLayout with the given Context
   * @param context the Context to use
   */
  public OoyalaPlayerLayout(Context context) {
    super(context);
    setupPlayerFrame();
  }

  /**
   * Initialize the OoyalaPlayerLayout with the given Context and AttributeSet
   * @param context the Context to use
   * @param attrs the AttributeSet to use
   */
  public OoyalaPlayerLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
    setupPlayerFrame();
  }

  /**
   * Initialize the OoyalaPlayerLayout with the given Context, AttributeSet, and style
   * @param context the Context to use
   * @param attrs the AttributeSet to use
   * @param defStyle the style of the Layout
   */
  public OoyalaPlayerLayout(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    setupPlayerFrame();
  }

  private void setupPlayerFrame() {
    _playerFrame = new FrameLayout(getContext());
    FrameLayout.LayoutParams frameLP = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
        FrameLayout.LayoutParams.MATCH_PARENT);
    addView(_playerFrame, frameLP);
  }

  /**
   * Returns the FrameLayout surrounding the player.
   * @return the parent FrameLayout of the player.
   */
  public FrameLayout getPlayerFrame() {
    return _playerFrame;
  }

  /**
   * Returns the LayoutController corresponding to this OoyalaPlayerLayout.
   * @return the LayoutController corresponding to this OoyalaPlayerLayout.
   */
  public LayoutController getLayoutController() {
    return _controller;
  }

  /**
   * Set the LayoutController corresponding to this OoyalaPlayerLayout.
   * @param controller the LayoutController to connect to this Layout.
   */
  public void setLayoutController(LayoutController controller) {
    _controller = controller;
  }

  /**
   * Touch event handler for this OoyalaPlayerLayout. This method should not be called directly.
   */
  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (_controller == null) { return false; }
    return _controller.onTouchEvent(event, this);
  }
}

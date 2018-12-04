package com.ooyala.sample.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.ui.OoyalaPlayerControls;

import java.util.Observable;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * This is an example of creating an Overlay.
 *
 * This class implements OoyalaPlayerControls, which can be added as an Inline or Fullscreen
 *   Overlay to the OoyalaPlayerViewController
 *
 * Using this method may be "heavy" for many users.  You can simply insert views into the PlayerLayout
 *   if that solves your use case
 */
public class CustomOverlay extends AppCompatTextView implements OoyalaPlayerControls {

  OoyalaPlayerLayout layout;

  public CustomOverlay(Context context, OoyalaPlayerLayout layout) {
    super(context);
    this.setBackgroundColor(Color.GREEN);
    this.setTextColor(Color.BLACK);
    this.setText("This is an overlay");
    this.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL);
    FrameLayout.LayoutParams spinnerLP = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
            100, Gravity.TOP | Gravity.CENTER_HORIZONTAL);
    this.layout = layout;
    this.layout.addView(this, spinnerLP);
  }

  @Override
  public void setParentLayout(OoyalaPlayerLayout ooyalaPlayerLayout) {
    this.layout.removeView(this);
    this.layout = ooyalaPlayerLayout;;
    FrameLayout.LayoutParams spinnerLP = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
            100, Gravity.TOP | Gravity.CENTER_HORIZONTAL);
    this.layout.addView(this, spinnerLP);
  }

  @Override
  public void setOoyalaPlayer(OoyalaPlayer ooyalaPlayer) {

  }

  @Override
  public void show() {
    //You can use this to hide and show alongside the controls
    setVisibility(View.VISIBLE);
  }

  @Override
  public void hide() {
    //You can use this to hide and show alongside the controls
    setVisibility(View.INVISIBLE);
  }

  @Override
  public boolean isShowing() {
    return this.getVisibility() == View.VISIBLE;
  }

  @Override
  public int bottomBarOffset() {
    return 0;
  }

  @Override
  public int topBarOffset() {
    return 0;
  }

  @Override
  public void setFullscreenButtonShowing(boolean b) {

  }

  @Override
  public void setVisible(boolean b) {

  }

  @Override
  public void refresh() {

  }

  @Override
  public void update(Observable observable, Object o) {

  }
}

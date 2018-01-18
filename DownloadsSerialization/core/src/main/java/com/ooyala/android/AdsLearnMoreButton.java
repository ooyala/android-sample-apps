package com.ooyala.android;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AdsLearnMoreButton extends RelativeLayout {

  private TextView _learnMore;

  public AdsLearnMoreButton(Context context) {
    super(context);
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public AdsLearnMoreButton(Context context,
      final AdsLearnMoreInterface callback, int topMargin) {
    super(context);

    //Set up the Learn More button's properties
    _learnMore = new TextView(context);
    String learnMoreText = LocalizationSupport.localizedStringFor("Learn More");
    _learnMore.setText(learnMoreText);
    _learnMore.setTextSize(15);
    _learnMore.setTextColor(Color.BLACK);
    _learnMore.setBackgroundColor(Color.GRAY);
    _learnMore.setPadding(20, 20, 20, 20);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      _learnMore.setAlpha((float) 0.7);
    }

    _learnMore.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        callback.processClickThrough();
      }
    });

    LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
    this.addView(_learnMore, params);
    setTopMargin(topMargin);
  }

  /**
   * Sets the topMargin for the Learn More button
   * @param topMargin the margin to push down the Learn More button in fullscreen
   */
  public void setTopMargin(int topMargin) {
    LayoutParams params = (LayoutParams) _learnMore.getLayoutParams();
    params.setMargins(0, topMargin, 0, 0);
  }

  /**
   * Completely destroys the Learn More button.
   * If you need to create a Learn More button again, you need to call the constructor.
   */
  public void destroy() {
    this.removeView(_learnMore);
    _learnMore = null;
  }
}
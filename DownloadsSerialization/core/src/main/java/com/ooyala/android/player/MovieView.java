package com.ooyala.android.player;

import android.content.Context;
import android.util.AttributeSet;

import com.ooyala.android.util.DebugMode;

public class MovieView extends ControlSharingSurfaceView {
  private float _aspectRatio = -1;

  public MovieView(boolean preventVideoViewSharing, Context context) {
    super(preventVideoViewSharing, context);
  }

  public MovieView(boolean preventVideoViewSharing, Context context, AttributeSet attrs) {
    super(preventVideoViewSharing, context, attrs);
  }

  public MovieView(boolean preventVideoViewSharing, Context context, AttributeSet attrs, int defStyle) {
    super(preventVideoViewSharing, context, attrs, defStyle);
  }

  public void setAspectRatio(float aspectRatio) {
    _aspectRatio = aspectRatio;
    requestLayout();
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    if (_aspectRatio <= 0) {
      super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    } else {
      int pWidth = MeasureSpec.getSize(widthMeasureSpec);
      int pHeight = MeasureSpec.getSize(heightMeasureSpec);
      int newWidth = 0;
      int newHeight = 0;
      if (pWidth == 0 || pHeight == 0) {
        DebugMode.logE(this.getClass().getName(), "ERROR: cannot set MovieView size");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        return;
      }
      float availableAspectRatio = ((float) pWidth) / ((float) pHeight);
      if (availableAspectRatio > _aspectRatio) {
        // bounded by the available height
        newWidth = (int) (_aspectRatio * ((float) pHeight));
        newHeight = pHeight;
      } else if (availableAspectRatio < _aspectRatio) {
        // bounded by the available width
        newWidth = pWidth;
        newHeight = (int) (((float) pWidth) / _aspectRatio);
      } else {
        // no bound, aspect ratios are the same.
        newWidth = pWidth;
        newHeight = pHeight;
      }
      setMeasuredDimension(newWidth, newHeight);
    }
  }
}

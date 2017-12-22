package com.ooyala.android.player;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SurfaceView;

import com.ooyala.android.util.DebugMode;

import java.lang.annotation.Target;

public class ControlSharingSurfaceView extends SurfaceView {
  private final static String TAG = ControlSharingSurfaceView.class.getSimpleName();
  public ControlSharingSurfaceView(boolean preventVideoViewSharing, Context context) {
    this(preventVideoViewSharing, context, null);
  }

  public ControlSharingSurfaceView(boolean preventVideoViewSharing, Context context, AttributeSet attrs) {
    this(preventVideoViewSharing, context, attrs, 0);
  }

  @TargetApi(17)
  public ControlSharingSurfaceView(boolean preventVideoViewSharing, Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    DebugMode.logD(TAG, "<init>: setSecure=" + preventVideoViewSharing + ", sdk=" + Build.VERSION.SDK_INT);
    if (Build.VERSION.SDK_INT >= 17) {
      setSecure(preventVideoViewSharing);
    }
  }
}

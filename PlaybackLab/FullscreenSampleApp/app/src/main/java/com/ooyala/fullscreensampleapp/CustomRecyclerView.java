package com.ooyala.fullscreensampleapp;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class CustomRecyclerView extends RecyclerView {

  float downXValue, downYValue;
  static private final String TAG = "CustomRecyclerView";

  public CustomRecyclerView(Context context) {
    super(context);
  }

  public CustomRecyclerView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public CustomRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent event) {
    Log.d(TAG, "Dispatch MotionEvent : " + event.toString());
    switch (event.getAction()) {

      case MotionEvent.ACTION_DOWN: {
        // store the X value when the user's finger was pressed down
        downXValue = event.getX();
        downYValue = event.getY();
        Log.d(TAG, "= " + downYValue);
        break;
      }

      case MotionEvent.ACTION_MOVE: {
        // Get the X value when the user released his/her finger
        float currentX = event.getX();
        float currentY = event.getY();
        // check if horizontal or vertical movement was bigger
        // if it is horizontal return true so that child view is gets called
        // if vertical call super so that recyclerview will handle it
        if (Math.abs(downXValue - currentX) > Math.abs(downYValue
            - currentY)) {
          Log.v(TAG, "x");
          // going backwards: pushing stuff to the right
          if (downXValue < currentX) {
            Log.d(TAG, "right");
            return true;
          }
          // going forwards: pushing stuff to the left
          if (downXValue > currentX) {
            Log.d(TAG, "left");
            return true;
          }
        } else {
          Log.v(TAG, "y ");
          if (downYValue < currentY) {
            Log.d(TAG, "down");
            return super.dispatchTouchEvent(event);
          }
          if (downYValue > currentY) {
            Log.d(TAG, "up");
            return super.dispatchTouchEvent(event);
          }
        }
        break;
      }
    }

    return super.dispatchTouchEvent(event);

  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent event) {
    Log.d(TAG, "Intercept MotionEvent : " + event.toString());
    return super.onInterceptTouchEvent(event);
  }

  @Override
  public boolean onTouchEvent(MotionEvent e) {
    Log.d(TAG, "onTouchEvent MotionEvent : " + e.toString());
    return super.onTouchEvent(e);
  }
}

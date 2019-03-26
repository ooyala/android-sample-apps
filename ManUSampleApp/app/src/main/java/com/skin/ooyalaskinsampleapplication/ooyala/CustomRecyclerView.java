package com.skin.ooyalaskinsampleapplication.ooyala;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.skin.ooyalaskinsampleapplication.DispatchEvent;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by pratishtha.d on 16-10-2017.
 */

public class CustomRecyclerView extends RecyclerView {

    Context context;
    DispatchEvent dispatchEvent;

    public CustomRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRecyclerView(Context context) {
        super(context);
        this.context = context;
    }
//    public CustomRecyclerView(Context context, @Nullable Att) {
//        super(context);
//        this.context = context;
//    }

    public void setInterface(DispatchEvent dispatchEvent) {
        this.dispatchEvent = dispatchEvent;
    }

    float downXValue, downYValue;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {


            case MotionEvent.ACTION_DOWN: {
                // store the X value when the user's finger was pressed down
                downXValue = event.getX();
                downYValue = event.getY();
                Log.d("ANDROID", "= " + downYValue);
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
                    Log.v("ANDROID", "x");
                    // going backwards: pushing stuff to the right
                    if (downXValue < currentX) {
                        Log.d("ANDROID", "right");
                        dispatchEvent.handleView();

                        return super.dispatchTouchEvent(event);
                    }
                    // going forwards: pushing stuff to the left
                    if (downXValue > currentX) {
                        Log.d("ANDROID", "left");
                        dispatchEvent.handleView();

                        return super.dispatchTouchEvent(event);
                    }
                } else {


                }
                break;
            }
        }


        return super.dispatchTouchEvent(event);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        Log.d("ANDROID", "Intercept MotionEvent : " + event.toString());
        return super.onInterceptTouchEvent(event);
    }


    @Override
    public boolean onTouchEvent(MotionEvent e) {
        Log.d("ANDROID", "onTouchEvent MotionEvent : " + e.toString());
        return super.onTouchEvent(e);
    }

}

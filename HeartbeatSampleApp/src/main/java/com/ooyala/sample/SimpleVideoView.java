package com.ooyala.sample;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;


public class SimpleVideoView extends VideoView {

    private EventListener mEventListener;
    private int mVideoWidth;
    private int mVideoHeight;

    public SimpleVideoView(Context context) {
        super(context);
    }

    public SimpleVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SimpleVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setEventListener(EventListener listener) {
        mEventListener = listener;
    }

    @Override
    public void start() {
        super.start();
        if(mEventListener != null) {
            mEventListener.onVideoPlay();
        }
    }

    @Override
    public void pause() {
        super.pause();
        if(mEventListener != null) {
            mEventListener.onVideoPause();
        }
    }

    @Override
    public void seekTo(int msec) {
        super.seekTo(msec);
        if(mEventListener != null) {
            mEventListener.onSeekTo(msec);
        }
    }

    public interface EventListener {
        void onVideoPlay();
        void onVideoPause();
        void onSeekTo(int msec);
    }
}

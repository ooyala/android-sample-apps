package com.ooyala.sample.utils;

import android.content.Context;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ooyala.android.OoyalaException;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.item.Stream;
import com.ooyala.android.player.StreamPlayer;
import com.ooyala.android.util.DebugMode;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;


/**
 *  This is a new player which extends from StreamPlayer.
 *  Extending from StreamPlayer is necessary to get it work with Ooyala SDK.
 *
 */
public class SampleVideoStreamPlayer extends StreamPlayer {

    private static final String TAG = SampleVideoStreamPlayer.class.getSimpleName();

    private int DURATION;
    private final int REFRESH_RATE = 250;

    private Timer timer;
    private int playhead = 0;
    private Handler timerHandler;
    private TextView textView;
    private LinearLayout linearLayout;

    private Stream stream;


    @Override
    public void init(OoyalaPlayer parent, Set<Stream> streams) {
        WifiManager wifiManager = (WifiManager)parent.getLayout().getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        boolean isWifiEnabled = wifiManager.isWifiEnabled();

        stream = Stream.bestStream(streams, isWifiEnabled);
        if (stream == null) {
            DebugMode.logE(TAG, "ERROR: Invalid Stream (no valid stream available)");
            this._error = new OoyalaException(OoyalaException.OoyalaErrorCode.ERROR_PLAYBACK_FAILED, "Invalid Stream");
            setState(OoyalaPlayer.State.ERROR);
            return;
        }

        if (parent == null) {
            this._error = new OoyalaException(OoyalaException.OoyalaErrorCode.ERROR_PLAYBACK_FAILED, "Invalid Parent");
            setState(OoyalaPlayer.State.ERROR);
            return;
        }
        DURATION = parent.getCurrentItem().getDuration() != 0 ? parent.getCurrentItem().getDuration() : 15000 ;
        setParent(parent);
        initPlayer();
    }

    public void initPlayer () {
        // create layout/view which will contain playerview
        linearLayout = new LinearLayout(_parent.getLayout().getContext());
        linearLayout.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER));
        linearLayout.setBackgroundColor(Color.BLACK);
        textView = new TextView(_parent.getLayout().getContext());
        linearLayout.addView(textView);
        // Add newly created playerView to parent which is Ooyala player
        _parent.getLayout().addView(linearLayout);
        timerHandler = new Handler() {
            public void handleMessage(Message msg) {
                refresh();
            }
        };

        // if auto-play is enabled the state needs to be PLAYING
        if (_parent.getDesiredState() == OoyalaPlayer.DesiredState.DESIRED_PLAY) {
          setState(OoyalaPlayer.State.PLAYING);
        } else {
          setState(OoyalaPlayer.State.READY);
        }

    }

    @Override
    public void play() {
        if (timer == null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    timerHandler.sendEmptyMessage(0);
                }
            }, REFRESH_RATE, REFRESH_RATE);
        }
        setState(OoyalaPlayer.State.PLAYING);
    }

    @Override
    public int buffer() {
        return 100;
    }

    @Override
    public int duration() {
        return DURATION;
    }

    @Override
    public boolean seekable() {
        return true;
    }

    @Override
    public int currentTime() {
        return playhead;
    }

    private void refresh() {
        // This method simulates video playback
        playhead += REFRESH_RATE;
        String text = " Sample Video Player Plugin " + String.valueOf((DURATION - playhead) / 1000)
                + "\n\n\n" + stream.decodedURL().toString();
        textView.setText(text);
        if (playhead >= DURATION && timer != null) {
            timer.cancel();
            timer = null;
            setState(OoyalaPlayer.State.COMPLETED);
        }
    }
}

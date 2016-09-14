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
 * Created by achaudhari on 9/12/16.
 */
public class SampleVideoStreamPlayer extends StreamPlayer {

    private static final String TAG = SampleVideoStreamPlayer.class.getSimpleName();

    private final int DURATION = 15000;
    private final int REFRESH_RATE = 250;

    private Timer _timer;
    private int _playhead = 0;
    private Handler _timerHandler;
    private TextView textView;
    private LinearLayout _linearLayout;

    private Stream stream;


    @Override
    public void init(OoyalaPlayer parent, Set<Stream> streams) {
        WifiManager wifiManager = (WifiManager)parent.getLayout().getContext().getSystemService(Context.WIFI_SERVICE);
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

        setParent(parent);
        initPlayer();
    }

    public void initPlayer () {
        _linearLayout = new LinearLayout(_parent.getLayout().getContext());
        _linearLayout.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER));
        _linearLayout.setBackgroundColor(Color.BLACK);
        textView = new TextView(_parent.getLayout().getContext());
        _linearLayout.addView(textView);
        _parent.getLayout().addView(_linearLayout);
        _timerHandler = new Handler() {
            public void handleMessage(Message msg) {
                refresh();
            }
        };
        setState(OoyalaPlayer.State.READY);

    }

    @Override
    protected void startPlayheadTimer() {
        // TODO Auto-generated method stub
    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub
    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub
    }

    @Override
    public void play() {
        if (_timer == null) {
            _timer = new Timer();
            _timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    _timerHandler.sendEmptyMessage(0);
                }
            }, REFRESH_RATE, REFRESH_RATE);
        }
        setState(OoyalaPlayer.State.PLAYING);
    }

    @Override
    public void stop() {
        // TODO Auto-generated method stub
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
        return _playhead;
    }

    @Override
    public void suspend() {
        // TODO Auto-generated method stub
    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub
    }

    @Override
    public void seekToTime(int timeInMillis) {

    }

    private void refresh() {
        _playhead += REFRESH_RATE;
        String text = " Sample Video Player Plugin " + String.valueOf((DURATION - _playhead) / 1000)
                + "\n\n\n" + stream.decodedURL().toString();
        textView.setText(text);
        if (_playhead >= DURATION && _timer != null) {
            _timer.cancel();
            _timer = null;
            setState(OoyalaPlayer.State.COMPLETED);
        }
    }
}

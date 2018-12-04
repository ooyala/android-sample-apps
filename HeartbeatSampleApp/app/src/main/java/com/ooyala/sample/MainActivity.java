package com.ooyala.sample;

import android.app.ActionBar;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private final int FREQUENCY_MS = 10000;
    private final int FREQUENCY_S = FREQUENCY_MS / 1000;
    private final String PLAY_TIME = "PLAY_TIME";
    private final String WAS_PLAYING = "WAS_PLAYING";
    private final String LAST_HEARTBEAT = "LAST_HEARTBEAT";
    private final String  PROTOCOL_HTTP = "http://";
    private final String  PROTOCOL_HTTPS = "https://";

    private long playTime = 0;
    private boolean wasPlaying = false;
    private Handler mHandler = null;
    private HandlerThread mHandlerThread = null;
    private SimpleExoPlayer player;
    private SimpleExoPlayerView playerView;
    private boolean isHandlerRunning = false;
    private long lastHeartbeatEpoch = 0;
    private String protocol = PROTOCOL_HTTP;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            lastHeartbeatEpoch = System.currentTimeMillis();
            mHandler.postDelayed(this, FREQUENCY_MS);
            postDataToHeartbeat();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        protocol = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ? PROTOCOL_HTTPS : PROTOCOL_HTTP;
        playerView = findViewById(R.id.player_view);

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            playTime = savedInstanceState.getLong(PLAY_TIME);
            wasPlaying = savedInstanceState.getBoolean(WAS_PLAYING);
            lastHeartbeatEpoch = savedInstanceState.getLong(LAST_HEARTBEAT);
        }
        startVideoView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        connect();
        fullScreenOnLandscape();
    }

    @Override
    protected void onPause() {
        disconnect();
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putLong(PLAY_TIME, player.getCurrentPosition());
        savedInstanceState.putBoolean(WAS_PLAYING, wasPlaying);
        savedInstanceState.putLong(LAST_HEARTBEAT, lastHeartbeatEpoch);
        // Pause playback when locking the device
        if (player != null) {
            player.setPlayWhenReady(false);
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    private void connect() {
        if (player == null || !player.getPlayWhenReady() || player.getPlaybackState() != Player.STATE_READY) {
            return;
        }
        startRecurringHandler();
    }

    private void disconnect() {
        if (mHandler == null) {
            return;
        }
        mHandler.removeCallbacks(runnable);
        mHandler = null;
        mHandlerThread = null;
        isHandlerRunning = false;
    }

    public void startRecurringHandler() {
        if (isHandlerRunning) {
            return;
        }
        mHandlerThread = new HandlerThread("HeartbeatHandler");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
        long nextHeartbeat = FREQUENCY_MS - (System.currentTimeMillis() - lastHeartbeatEpoch);
        mHandler.postDelayed(runnable, nextHeartbeat);
        isHandlerRunning = true;
    }

    public void postDataToHeartbeat() {
        if (player == null) {
            return;
        }

        String embedCode = "l5bm11ZjE6VFJyNE2iE6EKpCBVSRroAF";
        String uuid = "HeartbeatSampleTest";
        String url = protocol + "ssai.ooyala.com/v1/vod_playback_pos/" + embedCode + "?ssai_guid=" + uuid;

        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                Map<String, Integer> params = new HashMap<>();
                int positionSeconds = (int) player.getCurrentPosition() / 1000;
                params.put("playheadpos", positionSeconds);
                params.put("pingfrequency", FREQUENCY_S);
                return new JSONObject(params).toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        NetworkSingleton.getInstance(getApplicationContext()).addToRequestQueue(sr);
    }

    private void fullScreenOnLandscape() {
        View decorView = getWindow().getDecorView();
        int uiOptions;

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Hide the status bar.
            uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }
        }
        else {
            uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        }
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void startVideoView() {
        String baseUrl = protocol + "ssai.ooyala.com/vhls/l5bm11ZjE6VFJyNE2iE6EKpCBVSRroAF/ZsdGgyOnugo44o442aALkge_dVVK/eyJvIjoiaHR0cDovL3BsYXllci5vb3lhbGEuY29tL2hscy9wbGF5ZXIvYWxsL2w1Ym0xMVpqRTZWRkp5TkUyaUU2RUtwQ0JWU1Jyb0FGLm0zdTg_dGFyZ2V0Qml0cmF0ZT0yMDAwJnNlY3VyZV9pb3NfdG9rZW49UldwQ1YzZ3Zhazl0TDJOQ05rdFlOMjV2V1hScmNqTXpZMFoxU0hKTFNra3daV2xyZEZGVU5ITkNaMlJxWmxoQlQyMXRUVkJGVldSd2NsQlFDbnB1VDI5Vkt6UkVSekV3UjBGdWIySm1jRmR3Y0ZoVFpucEJQVDBLIiwiZSI6IjE1NDA5MjQ5MjEiLCJzIjoiYkJRa0FsVjVEak0wTFEwMXJZRXdkajBWR2UtRkdVd1NnZ2o1SnpzXy1Icz0ifQ==/manifest.m3u8?ssai_guid";
        String SSAI_GUID = "HeartbeatSampleTest";
        Uri uri = Uri.parse(baseUrl + SSAI_GUID);

        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);

        player = ExoPlayerFactory.newSimpleInstance(getApplicationContext(), trackSelector);
        playerView.setPlayer(player);

        String userAgent = Util.getUserAgent(this, "HeartbeatSampleApp");
        HttpDataSource.Factory dataSource = new DefaultHttpDataSourceFactory(userAgent, bandwidthMeter);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, bandwidthMeter, dataSource);
        MediaSource videoSource = new HlsMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);

        player.addListener(new PlayerEventListener());
        player.prepare(videoSource);

        // Check whether we're recreating a previously destroyed instance
        if (playTime != 0) {
            player.seekTo(playTime);
            player.setPlayWhenReady(wasPlaying);
        }
    }

    private class PlayerEventListener extends Player.DefaultEventListener {

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            super.onPlayerStateChanged(playWhenReady, playbackState);
            if (playWhenReady) {
                if (playbackState == Player.STATE_READY) {
                    // Asset is playing
                    wasPlaying = true;
                    connect();
                }
                else if (playbackState == Player.STATE_ENDED) {
                    // Asset has reached the end
                    postDataToHeartbeat();
                    disconnect();
                }
            }
            else {
                // Asset was paused, last heartbeat is irrelevant
                lastHeartbeatEpoch = 0;
                wasPlaying = false;
                disconnect();
            }
        }
    }
}



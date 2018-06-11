package com.ooyala.sample;

import android.app.ActionBar;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
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

    private long playTime = 0;
    private boolean wasPlaying = false;
    private Handler mHandler = null;
    private HandlerThread mHandlerThread = null;
    private SimpleExoPlayer player;
    private SimpleExoPlayerView playerView;
    private boolean isHandlerRunning = false;
    private long lastHeartbeatEpoch = 0;

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

        String embedCode = "ltZ3l5YjE6lUAvBdflvcDQ-zti8q8Urd";
        String uuid = "HeartbeatSampleTest";
        String url ="http://ssai.ooyala.com/v1/vod_playback_pos/" + embedCode + "?ssai_guid=" + uuid;

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
        String baseUrl = "http://ssai.ooyala.com/vhls/ltZ3l5YjE6lUAvBdflvcDQ-zti8q8Urd/RpOWUyOq86gFq-STNqpgzhzIcXHV/eyJvIjoiaHR0cDovL3BsYXllci5vb3lhbGEuY29tL3BsYXllci9hbGwvbHRaM2w1WWpFNmxVQXZCZGZsdmNEUS16dGk4cThVcmQubTN1OD90YXJnZXRCaXRyYXRlPTEyMDAmc2VjdXJlX2lvc190b2tlbj1hMWRCYWtoSVFreHNMMFJqT0ZsUFlVZ3hNRFZLTVdSNWEwbDBaSFE0VW5SVFEzWnZVM041UVdsNGRXcFFVRFpXV0VOVVUycFVUazFQTHpWb0NuVnZWM2RoVUVGR2NIUTJjbGhTVmtOYVJIaFRXRkYxUkVaM1BUMEsiLCJlIjoiMTQ5OTQ0Mjg5MiIsInMiOiJHQk9wZFhGNGNzZzhfTzJ3MVlyU2VFejVlQzhQY0h5c054LU5FRDk3cmxzPSJ9/manifest.m3u8?ssai_guid=";
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



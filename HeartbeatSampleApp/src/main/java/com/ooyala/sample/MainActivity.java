package com.ooyala.sample;

import android.app.ActionBar;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.MediaController;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, SimpleVideoView.EventListener {


    private final int FREQUENCY_MS = 10000;
    private final int FREQUENCY_S = FREQUENCY_MS / 1000;
    private final String PLAY_TIME = "PLAY_TIME";

    private int playTime = 0;
    private Handler mHandler = null;
    private HandlerThread mHandlerThread = null;
    private SimpleVideoView videoView;
    private boolean isHandlerRunning = false;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mHandler.postDelayed(this, FREQUENCY_MS);
            postDataToHeartbeat();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = findViewById(R.id.videoView);

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            playTime = savedInstanceState.getInt(PLAY_TIME);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fullScreenOnLandscape();
        startVideoView();
    }

    @Override
    protected void onPause() {
        disconnect();
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(PLAY_TIME, videoView.getCurrentPosition());
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    public void onPrepared(MediaPlayer mp) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        postDataToHeartbeat();
        disconnect();
    }

    @Override
    public void onVideoPlay() {
        connect();
    }

    @Override
    public void onVideoPause() {
        disconnect();
    }

    @Override
    public void onSeekTo(int msec) {

    }

    private void connect() {
        if(!isHandlerRunning) {
            postDataToHeartbeat();
        }
        startRecurringHandler();
    }

    private void disconnect() {
        if(mHandler == null) {
            return;
        }
        mHandler.removeCallbacks(runnable);
        mHandler = null;
        mHandlerThread = null;
        isHandlerRunning = false;
    }

    public void startRecurringHandler(){
        if(isHandlerRunning) {
            return;
        }
        mHandlerThread = new HandlerThread("HeartbeatHandler");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
        mHandler.postDelayed(runnable, FREQUENCY_MS);
        isHandlerRunning = true;
    }


    public void postDataToHeartbeat() {
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
                int positionSeconds = videoView.getCurrentPosition() / 1000;
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
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        String baseUrl = "http://ssai.ooyala.com/vhls/ltZ3l5YjE6lUAvBdflvcDQ-zti8q8Urd/RpOWUyOq86gFq-STNqpgzhzIcXHV/eyJvIjoiaHR0cDovL3BsYXllci5vb3lhbGEuY29tL3BsYXllci9hbGwvbHRaM2w1WWpFNmxVQXZCZGZsdmNEUS16dGk4cThVcmQubTN1OD90YXJnZXRCaXRyYXRlPTEyMDAmc2VjdXJlX2lvc190b2tlbj1hMWRCYWtoSVFreHNMMFJqT0ZsUFlVZ3hNRFZLTVdSNWEwbDBaSFE0VW5SVFEzWnZVM041UVdsNGRXcFFVRFpXV0VOVVUycFVUazFQTHpWb0NuVnZWM2RoVUVGR2NIUTJjbGhTVmtOYVJIaFRXRkYxUkVaM1BUMEsiLCJlIjoiMTQ5OTQ0Mjg5MiIsInMiOiJHQk9wZFhGNGNzZzhfTzJ3MVlyU2VFejVlQzhQY0h5c054LU5FRDk3cmxzPSJ9/manifest.m3u8?ssai_guid=";
        String SSAI_GUID = "HeartbeatSampleTest";
        Uri uri = Uri.parse(baseUrl + SSAI_GUID);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.setOnCompletionListener(this);
        videoView.setOnPreparedListener(this);
        videoView.setEventListener(this);

        // Check whether we're recreating a previously destroyed instance
        if (playTime != 0) {
            videoView.seekTo(playTime);
            videoView.start();
        }
        else {
            // Show a frame
            videoView.seekTo(60);
        }
    }
}



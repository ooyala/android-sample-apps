package com.ooyala.ddmm;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;

import com.ooyala.android.DefaultPlayerInfo;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.item.Stream;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.telstra.android.media.capabilities.CapabilityUtils;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PlayerActivity extends AppCompatActivity {
    private static final String TAG = "PlayerActivity";

    OoyalaPlayerLayoutController playerLayoutController;
    OoyalaPlayer player;

    protected String pcode = "c0cTkxOqALQviQIGAHWY5hP0q9gU";
    protected String embedCode = "Y1ZHB1ZDqfhCPjYYRbCEOz0GR8IsVRm1";
    protected String domain = "http://www.ooyala.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupOoyalaPlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.suspend();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null) {
            player.resume();
        }
    }

    protected void setupOoyalaPlayer() {
        String myCapabilities = CapabilityUtils.capabilitesToJson(CapabilityUtils.getCapabilities(this));
        String capabilities64 = null;

        try {
            byte[] capabilitiesData = myCapabilities.getBytes("UTF-8");
            capabilities64 = Base64.encodeToString(capabilitiesData, Base64.NO_WRAP);
        } catch (UnsupportedEncodingException e) {
            Log.w(TAG, "could not convert capabilities String to Base 64");
        }

        Map<String, String> myQueryParams = new HashMap<>();
        if (capabilities64 != null) {
            myQueryParams.put("ddmm_device_param", capabilities64);
        }

        MyPlayerInfo playerInfo = new MyPlayerInfo();
        playerInfo.setAdditionalParams(myQueryParams);

        Options options = new Options.Builder().setPlayerInfo(playerInfo).build();
        player = new OoyalaPlayer(pcode, new PlayerDomain(domain), options);

        OoyalaPlayerLayout playerLayout = findViewById(R.id.player_layout);
        playerLayoutController = new OoyalaPlayerLayoutController(playerLayout, player);

        if (player.setEmbedCode(embedCode)) {
            player.play();
        }
    }

    class MyPlayerInfo extends DefaultPlayerInfo {
        Map<String, String> additionalParams;
        Set<String> supportedFormats = new HashSet<String>();

        public MyPlayerInfo() {
            super(null, null);
            supportedFormats.add(Stream.DELIVERY_TYPE_DASH);
            supportedFormats.add(Stream.DELIVERY_TYPE_HLS);
            supportedFormats.add(Stream.DELIVERY_TYPE_AKAMAI_HD2_VOD_HLS);
            supportedFormats.add(Stream.DELIVERY_TYPE_AKAMAI_HD2_HLS);
            supportedFormats.add(Stream.DELIVERY_TYPE_M3U8);
            supportedFormats.add(Stream.DELIVERY_TYPE_MP4);
        }

        @Override
        public Map<String, String> getAdditionalParams() {
            return additionalParams;
        }

        public void setAdditionalParams(Map<String, String> additionalParams) {
            this.additionalParams = additionalParams;
        }

        @Override
        public Set<String> getSupportedFormats() {
            return supportedFormats;
        }
    }
}

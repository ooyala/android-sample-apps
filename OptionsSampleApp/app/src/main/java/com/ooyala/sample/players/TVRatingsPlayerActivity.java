package com.ooyala.sample.players;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ToggleButton;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.FCCTVRatingConfiguration;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.ui.OptimizedOoyalaPlayerLayoutController;
import com.ooyala.sample.R;


public class TVRatingsPlayerActivity extends AbstractHookActivity implements OnClickListener {

    private final int TVRATING_DURATION = 5;
    private Button setButton;
    private ToggleButton verticalAlignToggle;
    private ToggleButton horizontalAlignToggle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_toggle_button_layout);
        completePlayerSetup(asked);
    }

    @Override
    void completePlayerSetup(boolean asked){
        if(asked) {
            setButton = (Button) findViewById(R.id.setButton);
            setButton.setText("Create Video");
            setButton.setOnClickListener(this);

            verticalAlignToggle = (ToggleButton) findViewById(R.id.toggleButton1);
            verticalAlignToggle.setTextOn("Align Top");
            verticalAlignToggle.setTextOff("Align Bottom");
            verticalAlignToggle.setChecked(false);

            horizontalAlignToggle = (ToggleButton) findViewById(R.id.toggleButton2);
            horizontalAlignToggle.setTextOn("Align Left");
            horizontalAlignToggle.setTextOff("Align Right");
            horizontalAlignToggle.setChecked(false);
        }
    }

    private FCCTVRatingConfiguration.Position getTVRatingPosition() {
        if (verticalAlignToggle.isChecked() && horizontalAlignToggle.isChecked()) {
            return FCCTVRatingConfiguration.Position.TopLeft;
        } else if (verticalAlignToggle.isChecked() && !horizontalAlignToggle.isChecked()) {
            return FCCTVRatingConfiguration.Position.TopRight;
        } else if (!verticalAlignToggle.isChecked() && horizontalAlignToggle.isChecked()) {
            return FCCTVRatingConfiguration.Position.BottomLeft;
        } else {// if (!verticalAlignToggle.isChecked() && !horizontalAlignToggle.isChecked()) {
            return FCCTVRatingConfiguration.Position.BottomRight;
        }
    }

    @Override
    public void onClick(View v) {
        // remove the previous player to only play the current player
        if (null != player) {
            player.suspend();
            player.removeVideoView();
        }
        OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);

        FCCTVRatingConfiguration fccConfig = new FCCTVRatingConfiguration.Builder().setPosition(getTVRatingPosition()).setDurationSeconds(TVRATING_DURATION).build();
        Options options = new Options.Builder().setTVRatingConfiguration(fccConfig).setUseExoPlayer(true).build();

        player = new OoyalaPlayer(PCODE, new PlayerDomain(DOMAIN), options);
        optimizedOoyalaPlayerLayoutController = new OptimizedOoyalaPlayerLayoutController(
            playerLayout, player);
        player.addObserver(this);
        player.setEmbedCode(EMBED_CODE);
    }
}

package com.ooyala.sample.players;

import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ToggleButton;

import com.ooyala.android.util.DebugMode;
import com.ooyala.android.LocalizationSupport;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.ui.OptimizedOoyalaPlayerLayoutController;
import com.ooyala.sample.R;
import com.ooyala.android.util.SDCardLogcatOoyalaEventsLogger;

public class PreloadWithInitTimePlayerActivity extends AbstractHookActivity implements OnClickListener {

	private Button setButton;
	private ToggleButton preloadButton;
	private ToggleButton showPromoImageButton;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player_toggle_button_layout);
		String localeString = getResources().getConfiguration().locale.toString();
		Log.d(TAG, "locale is " + localeString);
		LocalizationSupport.useLocalizedStrings(LocalizationSupport
				.loadLocalizedStrings(localeString));
		completePlayerSetup(asked);
	}

	@Override
	void completePlayerSetup(boolean asked) {
		if (asked) {
			setButton = (Button) findViewById(R.id.setButton);
			setButton.setText("Create Video");
			setButton.setOnClickListener(this);

			preloadButton = (ToggleButton) findViewById(R.id.toggleButton1);
			preloadButton.setTextOn("Preload On");
			preloadButton.setTextOff("Preload Off");
			preloadButton.setChecked(true);

			showPromoImageButton = (ToggleButton) findViewById(R.id.toggleButton2);
			showPromoImageButton.setTextOn("Show PromoImage On");
			showPromoImageButton.setTextOff("Show PromoImage Off");
			showPromoImageButton.setChecked(true);
		}
	}

	@Override
	public void onClick(View v) {
		if (null != player) {
			player.suspend();
			player.removeVideoView();
		}

		OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);
		boolean showPromoImage = this.showPromoImageButton.isChecked();
		boolean preload = this.preloadButton.isChecked();
		DebugMode.logD(TAG, "showPromoImage: " + showPromoImage
				+ " preload: " + preload);
		Options options = new Options.Builder().setPreloadContent(preload).setShowPromoImage(showPromoImage).setUseExoPlayer(true).build();

		player = new OoyalaPlayer(PCODE, new PlayerDomain(DOMAIN), options);
		optimizedOoyalaPlayerLayoutController = new OptimizedOoyalaPlayerLayoutController(playerLayout, player);
		player.addObserver(this);

		player.setEmbedCode(EMBED_CODE);
		player.seek(20000);
	}
}

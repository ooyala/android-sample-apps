package com.ooyala.sample.players;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ToggleButton;

import com.ooyala.android.util.DebugMode;
import com.ooyala.android.LocalizationSupport;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.ui.OptimizedOoyalaPlayerLayoutController;
import com.ooyala.sample.R;

public class PreloadWithInitTimePlayerActivity extends AbstractHookActivity implements OnClickListener {

	private final String TAG = this.getClass().toString();
	private Button setButton;
	private ToggleButton preloadButton;
	private ToggleButton showPromoImageButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		String localeString = getResources().getConfiguration().locale.toString();
		Log.d(TAG, "locale is " + localeString);
		LocalizationSupport.useLocalizedStrings(LocalizationSupport
				.loadLocalizedStrings(localeString));

		super.onCreate(savedInstanceState);
		setContentView(R.layout.player_toggle_button_layout);
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

		player = new OoyalaPlayer(pcode, new PlayerDomain(domain), options);
		ooyalaPlayerLayoutController = new OptimizedOoyalaPlayerLayoutController(playerLayout, player);
		player.addObserver(this);

		player.setEmbedCode(embedCode);
		player.play(20000);
	}
}

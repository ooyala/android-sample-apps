package com.ooyala.sample.players;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.ooyala.android.util.DebugMode;
import com.ooyala.android.LocalizationSupport;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.freewheelsdk.OoyalaFreewheelManager;
import com.ooyala.android.ui.OptimizedOoyalaPlayerLayoutController;
import com.ooyala.sample.R;

import java.util.HashMap;
import java.util.Map;

public class TimeoutOptionsPlayerActivity extends AbstractHookActivity implements OnClickListener {

	private Button setButton;
	private EditText connectionTimeout;
	private EditText readTimeout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		String localeString = getResources().getConfiguration().locale.toString();
		Log.d(TAG, "locale is " + localeString);
		LocalizationSupport.useLocalizedStrings(LocalizationSupport
				.loadLocalizedStrings(localeString));

		super.onCreate(savedInstanceState);
		setContentView(R.layout.player_double_textedit_layout);
		completePlayerSetup(asked);
	}

	@Override
	void completePlayerSetup(boolean asked) {
		if (asked) {
			setButton = (Button) findViewById(R.id.setButton);
			setButton.setText("Create Video");
			setButton.setOnClickListener(this);

			connectionTimeout = (EditText) findViewById(R.id.edit1);
			connectionTimeout.setHint("connection timeout in milliseconds");

			readTimeout = (EditText) findViewById(R.id.edit2);
			readTimeout.setHint("read timeout in milliseconds");
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

		//If the connection timeout is specified, add it to the builder
		Options.Builder builder = new Options.Builder();
		if (!this.connectionTimeout.getText().toString().equals("")) {
			try {
				int connectionTimeoutMs = Integer.valueOf(this.connectionTimeout.getText().toString());
				builder.setConnectionTimeout(connectionTimeoutMs);
			} catch (Exception e) {
				DebugMode.logE(TAG, "The value provided was not a number. Cannot continue");
				return;
			}
		}

		//If read timeout is specified, add it to the builder
		if (!this.readTimeout.getText().toString().equals("")) {
			try {
				int readTimeoutMs = Integer.valueOf(this.readTimeout.getText().toString());
				builder.setReadTimeout(readTimeoutMs);
			} catch (Exception e) {
				DebugMode.logE(TAG, "The value provided was not a number. Cannot continue");
				return;
			}
		}

		//Build the options with the potentially updated builder
		Options options = builder.setUseExoPlayer(true).build();
		player = new OoyalaPlayer(PCODE, new PlayerDomain(DOMAIN), options);
		optimizedOoyalaPlayerLayoutController = new OptimizedOoyalaPlayerLayoutController(playerLayout, player);

		player.addObserver(this);

		OoyalaFreewheelManager freewheelManager = new OoyalaFreewheelManager(this,
				optimizedOoyalaPlayerLayoutController);
		Map<String, String> freewheelParameters = new HashMap<String, String>();
		freewheelParameters.put("fw_android_ad_server", "http://g1.v.fwmrm.net/");
		freewheelParameters
				.put("fw_android_player_profile", "90750:ooyala_android");
		freewheelParameters.put("fw_android_site_section_id",
				"ooyala_android_internalapp");
		freewheelParameters.put("fw_android_video_asset_id", EMBED_CODE);

		freewheelManager.overrideFreewheelParameters(freewheelParameters);
		player.setEmbedCode(EMBED_CODE);
	}
}
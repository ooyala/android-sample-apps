package com.ooyala.sample.players;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.sample.R;
import com.ooyala.sample.utils.AdobePassLoginController;
import com.ooyala.sample.utils.OnAuthorizationChangedListener;

public class AdobePassSampleAppAcitivity extends AbstractHookActivity implements OnAuthorizationChangedListener {

	private AdobePassLoginController adobePassController;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player_double_button_layout);
		setTitle(getIntent().getExtras().getString("selection_name"));
		completePlayerSetup(asked);
	}

	@Override
	void completePlayerSetup(boolean asked) {
		if (asked) {
			//Initialize the player
			adobePassController = new AdobePassLoginController(this, "ooyala",
					getResources().openRawResource(R.raw.adobepass), "adobepass", this);
			adobePassController.checkAuth();
			OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);
			Options options = new Options.Builder().setUseExoPlayer(true).build();
			player = new OoyalaPlayer(pcode, new PlayerDomain(domain), options);
			playerLayoutController = new OoyalaPlayerLayoutController(playerLayout, player);
			player.addObserver(this);

			Button loginButton = (Button) findViewById(R.id.doubleLeftButton);
			loginButton.setText("Login");
			loginButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Button loginButton = (Button) v;
					if (loginButton.getText().equals("Login")) {
						adobePassController.login();
					} else {
						adobePassController.logout();
					}
				}
			});
			Button setEmbedCodeButton = (Button) findViewById(R.id.doubleRightButton);
			setEmbedCodeButton.setText("SetEmbedCode");
			setEmbedCodeButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					playerLayoutController.getPlayer().setEmbedCode(embedCode);
					playerLayoutController.getPlayer().play();
				}
			});
		}
	}

	@Override
	public void authChanged(Boolean authorized) {
		Button loginButton = (Button) findViewById(R.id.doubleLeftButton);
		if (authorized) {
			loginButton.setText("Logout");
		} else {
			loginButton.setText("Login");
			playerLayoutController.getPlayer().setEmbedCode("none");
		}
	}
}
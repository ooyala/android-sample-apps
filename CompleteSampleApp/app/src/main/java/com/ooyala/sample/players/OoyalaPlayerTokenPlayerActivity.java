package com.ooyala.sample.players;

import android.os.Bundle;
import android.util.Log;

import com.ooyala.android.EmbedTokenGenerator;
import com.ooyala.android.EmbedTokenGeneratorCallback;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.sample.R;
import com.ooyala.sample.utils.EmbeddedSecureURLGenerator;

import java.net.URL;
import java.util.HashMap;
import java.util.List;

/**
 * This activity illustrates how you use Ooyala Player Token.
 * Ooyala Player Token can also be used in conjunction with the following security mechanisms
 * 1) Device Management,
 * 2) Concurrent Streams,
 * 3) Entitlements, and
 * 4) Stream Takedown
 *
 * This activity will NOT Playback any video.  You will need to:
 *  1) provide your own embed code, restricted with Ooyala Player Token
 *  2) provide your own PCODE, which owns the embed code
 *  3) have your API Key and Secret, which correlate to a user from the provider
 *
 * To play OPT-enabled videos, you must implement the EmbedTokenGenerator interface
 */
public class OoyalaPlayerTokenPlayerActivity extends AbstractHookActivity implements EmbedTokenGenerator {

	private final String ACCOUNT_ID = "accountID";
	/*
	 * The API Key and Secret should not be saved inside your applciation (even in git!).
	 * However, for debugging you can use them to locally generate Ooyala Player Tokens.
	 */
	private final String APIKEY = "Use this for testing, don't keep your secret in the application";
	private final String SECRET = "Use this for testing, don't keep your secret in the application";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getIntent().getExtras().getString("selection_name"));
		setContentView(R.layout.player_simple_layout);
		completePlayerSetup(asked);
	}

	@Override
	void completePlayerSetup(boolean asked) {
		if (asked) {
			//Initialize the player
			OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);

			Options options = new Options.Builder().setUseExoPlayer(true).build();
			//Need to pass `this` as the embedTokenGenerator
			player = new OoyalaPlayer(pcode, new PlayerDomain(domain), this, options);
			playerLayoutController = new OoyalaPlayerLayoutController(playerLayout, player);
			player.addObserver(this);

			if (player.setEmbedCode(embedCode)) {
				player.play();
			}
		}
	}

	/*
	 * Get the Ooyala Player Token to play the embed code.
	 * This should contact your servers to generate the OPT server-side.
	 * For debugging, you can use Ooyala's EmbeddedSecureURLGenerator to create local embed tokens
	 */
	@Override
	public void getTokenForEmbedCodes(List<String> embedCodes,
																		EmbedTokenGeneratorCallback callback) {
		String embedCodesString = "";
		for (String ec : embedCodes) {
			if(ec.equals("")) embedCodesString += ",";
			embedCodesString += ec;
		}

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("account_id", ACCOUNT_ID);

		// Uncommenting this will bypass all syndication rules on your asset
		// This will not work unless you have a working API Key and Secret.
		// This is one reason why you shouldn't keep the Secret in your app/source control
		// params.put("override_syndication_group", "override_all_synd_groups");

		String uri = "/sas/embed_token/" + pcode + "/" + embedCodesString;

		EmbeddedSecureURLGenerator urlGen = new EmbeddedSecureURLGenerator(APIKEY, SECRET);

		URL tokenUrl  = urlGen.secureURL("http://player.ooyala.com", uri, params);

		callback.setEmbedToken(tokenUrl.toString());
	}
}

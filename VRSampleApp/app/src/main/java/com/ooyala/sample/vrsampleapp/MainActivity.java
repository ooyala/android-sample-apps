package com.ooyala.sample.vrsampleapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.ooyala.android.EmbedTokenGenerator;
import com.ooyala.android.EmbedTokenGeneratorCallback;
import com.ooyala.android.EmbeddedSecureURLGenerator;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.FCCTVRatingConfiguration;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.skin.OoyalaSkinLayout;
import com.ooyala.android.skin.OoyalaSkinLayoutController;
import com.ooyala.android.skin.configuration.SkinOptions;
import com.ooyala.android.util.DebugMode;
import com.ooyala.sample.vrsampleapp.lists.AdListActivity;
import com.ooyala.sample.vrsampleapp.utils.PlayerSelectionOption;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements EmbedTokenGenerator, Observer {

	private static final String TAG = "VRSampleApp";

	private final String APIKEY = "";
	private final String SECRET = "";

	private final String PCODE = "4d772c1ee9044294b7e2c5feb1a07d27";
	private final String EMBEDCODE = "15Ym5tYzE6HVEfJkUZy2a4-cEW-NxGdC";

	private final String ACCOUNT_ID = "pbk-373@ooyala.com";
	private final String PLAYERDOMAIN = "http://www.ooyala.com";

	private OoyalaSkinLayout skinLayout;
	private OoyalaPlayer player;

	private static Map<String, PlayerSelectionOption> selectionMap;
	ArrayAdapter<String> selectionAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		skinLayout = (OoyalaSkinLayout) findViewById(R.id.player_skin_layout);
		PlayerDomain domain = null;
		try {
			domain = new PlayerDomain(PLAYERDOMAIN);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			DebugMode.logE(TAG, "Caught!", e);
		}

		final FCCTVRatingConfiguration tvRatingConfiguration = new FCCTVRatingConfiguration.Builder().setDurationSeconds(5).build();

		player = new OoyalaPlayer(PCODE, domain, this, new Options.Builder()
				.setTVRatingConfiguration( tvRatingConfiguration )
				.setBypassPCodeMatching(true)
				.setUseExoPlayer(true)
				.build());

		SkinOptions options = new SkinOptions.Builder().build();
		final OoyalaSkinLayoutController playerController = new OoyalaSkinLayoutController(getApplication(), skinLayout, player, options);
		playerController.addObserver(this);
		player.addObserver(this);
		player.setEmbedCode(EMBEDCODE);
	}

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

		String uri = "/sas/embed_token/" + PCODE + "/" + embedCodesString;
		EmbeddedSecureURLGenerator urlGen = new EmbeddedSecureURLGenerator(APIKEY, SECRET);

		URL tokenUrl  = urlGen.secureURL("http://player.ooyala.com", uri, params);

		callback.setEmbedToken(tokenUrl.toString());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_show_ad: {
				Intent intent = new Intent(this, AdListActivity.class);
				startActivity(intent);
				break;
			}
		}
		return false;
	}

	@Override
	public void update(Observable o, Object arg) {
	}
}

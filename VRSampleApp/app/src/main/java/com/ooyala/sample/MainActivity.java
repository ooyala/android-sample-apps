package com.ooyala.sample;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.KeyEvent;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.FCCTVRatingConfiguration;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.skin.OoyalaSkinLayout;
import com.ooyala.android.skin.OoyalaSkinLayoutController;
import com.ooyala.android.skin.configuration.SkinOptions;
import com.ooyala.sample.lists.AdListActivity;

public class MainActivity extends AbstractHookActivity {
	private static final String TAG = "VRSampleApp";
    private OoyalaSkinLayoutController playerController;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);
    completePlayerSetup(asked);
  }

	@Override
	void completePlayerSetup(boolean asked) {
    if (asked) {
      final FCCTVRatingConfiguration tvRatingConfiguration = new FCCTVRatingConfiguration.Builder().setDurationSeconds(5).build();
      final Options options = new Options.Builder()
          .setTVRatingConfiguration(tvRatingConfiguration)
          .setBypassPCodeMatching(true)
          .setUseExoPlayer(true)
          .build();

      player = new OoyalaPlayer(pcode, new PlayerDomain(domain), options);
      player.addObserver(this);

      OoyalaSkinLayout skinLayout = (OoyalaSkinLayout) findViewById(R.id.player_skin_layout);
      SkinOptions skinOptions = new SkinOptions.Builder().build();
      playerController = new OoyalaSkinLayoutController(getApplication(), skinLayout, player, skinOptions);
      playerController.addObserver(this);

      player.setEmbedCode(embedCode);
    }
	}

  @Override
  void initPlayerData() {
    embedCode = "15Ym5tYzE6HVEfJkUZy2a4-cEW-NxGdC";
    pcode = "4d772c1ee9044294b7e2c5feb1a07d27";
    domain = "http://www.ooyala.com";
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return playerController.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return playerController.onKeyUp(keyCode, event);
    }
}

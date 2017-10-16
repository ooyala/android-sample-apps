package com.ooyala.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.KeyEvent;

import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.FCCTVRatingConfiguration;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.skin.OoyalaSkinLayout;
import com.ooyala.android.skin.OoyalaSkinLayoutController;
import com.ooyala.android.skin.configuration.SkinOptions;
import com.ooyala.sample.lists.AdListActivity;

import java.util.Observable;

public class MainActivity extends AbstractHookActivity {
	private static final String TAG = "VRSampleApp";
    private OoyalaSkinLayoutController playerController;
  Toolbar toolbar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);
    toolbar = (Toolbar) findViewById(R.id.toolbar);
    completePlayerSetup(asked);
    if (toolbar != null) {
      setSupportActionBar(toolbar);
      toolbar.bringToFront();
    }
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
    embedCode = "Q0Z3d3YzE6R-MpJACU0TU83n8_ZPy7pA";
    pcode = "3e961eabae664aad9e3c788b638da096";
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
  public void update(Observable o, Object arg) {
    super.update(o, arg);
    changeToolbarVisibilityInFullscreenMode(arg);
  }

  private void changeToolbarVisibilityInFullscreenMode(Object arg) {
    String notificationName = OoyalaNotification.getNameOrUnknown(arg);
    if (notificationName.equals(OoyalaSkinLayoutController.FULLSCREEN_CHANGED_NOTIFICATION_NAME) && toolbar != null) {
      if (((OoyalaNotification) arg).getData().equals(Boolean.TRUE)) {
        toolbar.setVisibility(View.GONE);
      } else {
        toolbar.setVisibility(View.VISIBLE);
        toolbar.bringToFront();
      }
    }
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

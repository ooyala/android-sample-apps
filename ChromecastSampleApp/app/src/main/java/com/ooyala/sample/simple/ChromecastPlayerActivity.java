package com.ooyala.sample.simple;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;

import com.google.android.gms.cast.framework.CastButtonFactory;
import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.ui.OoyalaPlayerControls;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.cast.CastManager;
import com.ooyala.cast.mediainfo.VideoData;
import com.ooyala.sample.R;
import com.ooyala.sample.common.CastActivity;

import java.util.Observable;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

public class ChromecastPlayerActivity extends CastActivity {

  private CastViewManager castViewManager;
  private OoyalaPlayerLayoutController layoutController;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.player_activity);
    setupActionBar();

    completePlayerSetup();
    castViewManager = new CastViewManager(this, castManager);

  }

  @Override
  protected void onStart() {
    super.onStart();
    if (layoutController != null) {
      final OoyalaPlayerControls controls = layoutController.getControls();
      if (controls != null) {
        controls.refresh();
      }
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    boolean createOptionsMenu = super.onCreateOptionsMenu(menu);
    getMenuInflater().inflate(R.menu.browse, menu);
    CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), menu,
        R.id.media_route_menu_item);
    return createOptionsMenu;
  }

  private void setupActionBar() {
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
  }

  @Override
  public void update(Observable arg0, Object argN) {
    super.update(arg0, argN);
  }

  private void completePlayerSetup() {
    if (asked && writePermission) {
      PlayerDomain playerDomain = new PlayerDomain(domain);
      Options options = new Options.Builder().setUseExoPlayer(true).build();
      OoyalaPlayerLayout ooyalaPlayerLayout = findViewById(R.id.ooyalaPlayer);
      player = new OoyalaPlayer(pcode, playerDomain, this, options);
      layoutController = new OoyalaPlayerLayoutController(ooyalaPlayerLayout, player);
      castManager.registerWithOoyalaPlayer(player);
      player.addObserver(this);
      play(embedCode);
      castManager.registerWithOoyalaPlayer(player);
    }
  }

  @Override
  protected void updateCastView(OoyalaNotification notification) {
    if (notification != null && notification.getData() != null && notification.getData() instanceof VideoData) {
      VideoData data = (VideoData) notification.getData();
      castViewManager.configureCastView(data.getTitle(), data.getDescription(), data.getUrl());
    } else if (player != null && player.getCurrentItem() != null) {
      castViewManager.configureCastView(
          player.getCurrentItem().getTitle(),
          player.getCurrentItem().getDescription(),
          player.getCurrentItem().getPromoImageURL(0, 0)
      );
    }
  }

  @Override
  protected void updateCastViewState(OoyalaPlayer.State state) {
    castViewManager.updateCastState(this, state);
  }
}


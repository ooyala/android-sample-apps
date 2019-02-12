package com.ooyala.sample.skin;

import android.os.Bundle;
import android.view.Menu;

import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.skin.OoyalaSkinLayout;
import com.ooyala.android.skin.OoyalaSkinLayoutController;
import com.ooyala.android.skin.configuration.SkinOptions;
import com.ooyala.sample.R;
import com.ooyala.sample.common.CastActivity;

import org.json.JSONObject;

import java.util.Observable;
import java.util.Observer;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

public class SkinCastPlayerActivity extends CastActivity implements Observer, DefaultHardwareBackBtnHandler {
  protected OoyalaSkinLayoutController playerLayoutController;
  protected OoyalaSkinLayout skinLayout;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    skinLayout = findViewById(R.id.ooyalaSkin);
    setupActionBar();

    completePlayerSetup();
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
  protected void initAndBindController() {
    JSONObject overrides = createSkinOverrides();
    SkinOptions skinOptions = new SkinOptions.Builder().setSkinOverrides(overrides).build();
    playerLayoutController = new OoyalaSkinLayoutController(getApplication(), skinLayout, player, skinOptions);
    //Add observer to listen to fullscreen open and close events
    playerLayoutController.addObserver(this);
  }

  protected Options getOptions() {
    Options.Builder optionBuilder = new Options.Builder().setShowNativeLearnMoreButton(false).setShowPromoImage(false).setUseExoPlayer(true);
    return optionBuilder.build();
  }

  private JSONObject createSkinOverrides() {
    return new JSONObject();
  }

  @Override
  public void invokeDefaultOnBackPressed() {
    super.onBackPressed();
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (null != playerLayoutController) {
      playerLayoutController.onPause();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (null != playerLayoutController) {
      playerLayoutController.onResume(this, this);
    }
  }

  @Override
  public void onBackPressed() {
    if (null != player) {
      player.suspend();
    }
    if (null != playerLayoutController) {
      playerLayoutController.onBackPressed();
    } else {
      super.onBackPressed();
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (null != playerLayoutController) {
      playerLayoutController.onDestroy();
    }
  }

  @Override
  public void update(Observable arg0, Object argN) {
    super.update(arg0, argN);
  }

  @Override
  protected void updateCastView(OoyalaNotification notification) {

  }

  @Override
  protected void updateCastViewState(OoyalaPlayer.State state) {

  }
}

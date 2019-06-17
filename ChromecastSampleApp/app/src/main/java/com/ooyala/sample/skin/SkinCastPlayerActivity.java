package com.ooyala.sample.skin;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.skin.OoyalaSkinLayout;
import com.ooyala.android.skin.OoyalaSkinLayoutController;
import com.ooyala.android.skin.configuration.SkinOptions;
import com.ooyala.sample.R;
import org.json.JSONObject;
import com.ooyala.sample.common.CastActivity;

import java.util.Observer;

public class SkinCastPlayerActivity extends CastActivity implements Observer, DefaultHardwareBackBtnHandler {
  protected OoyalaSkinLayoutController playerLayoutController;
  protected OoyalaSkinLayout skinLayout;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    skinLayout = findViewById(R.id.ooyalaSkin);

    completePlayerSetup();
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

    if (playerLayoutController != null) {
      playerLayoutController.onDestroy();
      playerLayoutController = null;
    }

    if (skinLayout != null) {
      skinLayout.release();
      skinLayout = null;
    }
  }
}

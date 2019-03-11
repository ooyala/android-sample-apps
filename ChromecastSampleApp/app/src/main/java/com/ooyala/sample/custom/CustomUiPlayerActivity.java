package com.ooyala.sample.custom;

import android.os.Bundle;
import android.view.View;

import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.item.CastMediaRoute;
import com.ooyala.android.ui.OptimizedOoyalaPlayerLayoutController;
import com.ooyala.sample.R;
import com.ooyala.sample.common.CastActivity;

import java.util.Observable;
import java.util.Set;

import androidx.annotation.Nullable;

public class CustomUiPlayerActivity extends CastActivity {
  protected OptimizedOoyalaPlayerLayoutController playerLayoutController;
  protected OoyalaPlayerLayout playerLayout;
  Set<CastMediaRoute> mediaRoutes;
  private CustomPlayerControls customPlayerControls;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.custom_ui_layout);
    playerLayout = findViewById(R.id.layoutPlayerOoyala);

    completePlayerSetup();
  }

  @Override
  protected void initAndBindController() {
    playerLayoutController = new OptimizedOoyalaPlayerLayoutController(playerLayout, player);
    customPlayerControls = new CustomPlayerControls(player, playerLayout, getCastClickListener());
    playerLayoutController.setInlineControls(customPlayerControls);

    player.addObserver(this);
  }

  private View.OnClickListener getCastClickListener() {
    return v -> {
      if (player.isInCastMode()) {
          player.disconnectCast();
      } else {
        if (!mediaRoutes.isEmpty()) {
          customPlayerControls.showList(mediaRoutes);
        }
      }
    };
  }

  @Override
  protected Options getOptions() {
    return new Options.Builder().setUseExoPlayer(true).build();
  }

  @Override
  public void update(Observable arg0, Object argN) {
    super.update(arg0, argN);

    final String arg1 = OoyalaNotification.getNameOrUnknown(argN);

    OoyalaNotification notification = null;
    if (argN instanceof OoyalaNotification) {
      notification = (OoyalaNotification) argN;
    }

    if (OoyalaPlayer.CAST_DEVICES_AVAILABLE_NOTIFICATION_NAME.equals(arg1)) {
      mediaRoutes = (Set<CastMediaRoute>) notification.getData();
      if(mediaRoutes.isEmpty()) {
        customPlayerControls.hideCastButton();
      } else {
        customPlayerControls.showCastButton();
      }
    }
  }
}

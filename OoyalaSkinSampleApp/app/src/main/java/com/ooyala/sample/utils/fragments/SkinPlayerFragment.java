package com.ooyala.sample.utils.fragments;

import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.RelativeLayout;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.skin.OoyalaSkinLayout;
import com.ooyala.android.skin.OoyalaSkinLayoutController;
import com.ooyala.android.skin.configuration.SkinOptions;
import com.ooyala.sample.R;

import java.util.Observable;
import java.util.Observer;

import static com.ooyala.sample.players.ResizablePlayerActivity.UNSET_ID;

public class SkinPlayerFragment extends Fragment implements Observer, DefaultHardwareBackBtnHandler {
  private static final String EMBED = "JiOTdrdzqAujYa5qvnOxszbrTEuU5HMt";
  final String TAG = this.getClass().toString();

  final String PCODE = "c0cTkxOqALQviQIGAHWY5hP0q9gU";
  final String DOMAIN = "http://ooyala.com";

  private OoyalaPlayer player;
  private OoyalaSkinLayout skinLayout;
  OoyalaSkinLayoutController controller;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.configurable_skin_fragment, container, false);

    // Get the SkinLayout from our layout xml
    skinLayout = view.findViewById(R.id.ooyalaSkin);

    // Create the OoyalaPlayer, with some built-in UI disabled
    PlayerDomain domain = new PlayerDomain(DOMAIN);
    Options options = new Options.Builder().setShowAdsControls(false).setShowPromoImage(false).setUseExoPlayer(true).build();
    player = new OoyalaPlayer(PCODE, domain, options);

    //Create the SkinOptions, and setup React
    SkinOptions skinOptions = new SkinOptions.Builder().build();
    controller = new OoyalaSkinLayoutController(getActivity().getApplication(), skinLayout, player, skinOptions);
    //Add observer to listen to fullscreen open and close events
    controller.addObserver(this);

    if (player.setEmbedCode(EMBED)) {
      //Uncomment for autoplay
      //player.play();
    } else {
      Log.e(TAG, "Asset Failure");
    }
    return view;
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.skin_menu, menu);
  }

  public void resizePlayer(int type, int toolbarHeight, boolean fullscreen) {
    Display display = getActivity().getWindowManager().getDefaultDisplay();
    Point size = new Point();
    display.getSize(size);
    int width = size.x;
    int height = size.y;

    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);

    switch (type) {
      case R.id.action_wide:
        updateParamsWide(params, height);
        break;
      case R.id.action_tall:
        updateParamsTall(params, width, height, toolbarHeight, fullscreen);
        break;
      case R.id.action_fill:
        updateParamsFill(params, height, toolbarHeight, fullscreen);
        break;
      case UNSET_ID:
        updateParamsDefault(params, fullscreen);
        break;
    }

    skinLayout.setLayoutParams(params);
  }

  private void updateParamsWide(RelativeLayout.LayoutParams params, int height){
    params.height = height / 2;
    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
  }

  private void updateParamsTall(RelativeLayout.LayoutParams params, int width, int height, int toolbarHeight, boolean fullscreen) {
    params.width = width / 2;
    if (fullscreen) {
      params.height = ViewGroup.LayoutParams.MATCH_PARENT;
      params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
    } else {
      params.height = height - toolbarHeight;
      params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
      params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
    }
  }

  private void updateParamsFill(RelativeLayout.LayoutParams params, int height, int toolbarHeight, boolean fullscreen) {
    params.width = ViewGroup.LayoutParams.MATCH_PARENT;
    if (fullscreen) {
      params.height = ViewGroup.LayoutParams.MATCH_PARENT;
    } else {
      params.height = height - toolbarHeight;
      params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
    }
    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
  }

  private void updateParamsDefault(RelativeLayout.LayoutParams params, boolean fullscreen) {
    if (fullscreen) {
      params.width = ViewGroup.LayoutParams.MATCH_PARENT;
      params.height = ViewGroup.LayoutParams.MATCH_PARENT;
    } else {
      params.width = skinLayout.getSourceWidth();
      params.height = skinLayout.getSourceHeight();
    }
    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
  }

  @Override
  public void onPause() {
    super.onPause();
    if (controller != null) {
      controller.onPause();
    }
    Log.d(TAG, "Player Activity Stopped");
    if (player != null) {
      player.suspend();
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    if (controller != null) {
      controller.onResume(getActivity(), this);
    }
    Log.d(TAG, "Player Fragment Restarted");
    if (player != null) {
      player.resume();
    }
  }

  @Override
  public void invokeDefaultOnBackPressed() {
    getActivity().onBackPressed();
  }

  @Override
  public void update(Observable arg0, Object argN) {
    final String arg1 = OoyalaNotification.getNameOrUnknown(argN);

    if (arg1 == OoyalaSkinLayoutController.FULLSCREEN_CHANGED_NOTIFICATION_NAME) {
      Log.d(TAG, "Fullscreen Notification received : " + arg1 + " - fullScreen: " + ((OoyalaNotification) argN).getData());
    }
  }
}

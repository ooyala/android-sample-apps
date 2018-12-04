package com.ooyala.sample.screen;

import android.content.res.Configuration;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.FCCTVRatingConfiguration;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.skin.OoyalaSkinLayout;
import com.ooyala.android.skin.OoyalaSkinLayoutController;
import com.ooyala.android.skin.configuration.SkinOptions;
import com.ooyala.android.util.SDCardLogcatOoyalaEventsLogger;
import com.ooyala.android.vrsdk.player.VRPlayerFactory;
import com.ooyala.sample.R;
import com.ooyala.sample.utils.VideoData;

import java.util.Observable;
import java.util.Observer;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class VideoFragment extends Fragment implements Observer, DefaultHardwareBackBtnHandler {

  public static final String TAG = VideoFragment.class.getCanonicalName();
  private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

  private OoyalaSkinLayoutController playerController;

  private final SDCardLogcatOoyalaEventsLogger logger = new SDCardLogcatOoyalaEventsLogger();
  private boolean writeStoragePermissionGranted = false;

  private String embedCode;
  private String pCode;
  private String domain;

  protected OoyalaPlayer player;

  public void setArguments(VideoData data) {
    final Bundle args = new Bundle();
    args.putString("embedCode", data.getEmbedCode());
    args.putString("pCode", data.getpCode());
    args.putString("domain", data.getDomain());
    this.setArguments(args);
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View inflated = inflater.inflate(R.layout.video_fragment, container, false);
    Bundle arguments = getArguments();
    if (arguments != null) {
      this.embedCode = arguments.getString("embedCode");
      this.pCode = arguments.getString("pCode");
      this.domain = arguments.getString("domain");
    }
    return inflated;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    if (ContextCompat.checkSelfPermission(getContext(), WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
      requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
    } else {
      initPlayer();
      writeStoragePermissionGranted = true;
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    if (player != null) {
      player.resume();
    }
    if (playerController != null) {
      playerController.onResume(getActivity(), this);
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (playerController != null) {
      playerController.onDestroy();
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    if (player != null) {
      player.suspend();
    }
    if (playerController != null) {
      playerController.onPause();
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (requestCode == PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
      if (grantResults.length != 0 && grantResults[0] == PERMISSION_GRANTED) {
        writeStoragePermissionGranted = true;
      }
      initPlayer();
    }
  }

  @Override
  public void update(Observable o, Object arg) {
    final String notification = OoyalaNotification.getNameOrUnknown(arg);
    if (!notification.equals(OoyalaPlayer.TIME_CHANGED_NOTIFICATION_NAME)) {
      final String text = "Notification Received: " + notification + " - state: " + player.getState();
      Log.d(TAG, text);
      if (writeStoragePermissionGranted) {
        Log.d(TAG, "Writing log to SD card");
        logger.writeToSdcardLog(text);
      }
    }
    changeToolbarVisibilityInFullscreenMode(arg);
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    if (player != null) {
      player.configurationChanged(newConfig);
    }
  }

  public void applyADSManager(OoyalaSkinLayout skinLayout) {

  }

  private void changeToolbarVisibilityInFullscreenMode(Object arg) {
    String notificationName = OoyalaNotification.getNameOrUnknown(arg);
    if (notificationName.equals(OoyalaSkinLayoutController.FULLSCREEN_CHANGED_NOTIFICATION_NAME)) {
      AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
      if (appCompatActivity != null && appCompatActivity.getSupportActionBar() != null) {
        if (((OoyalaNotification) arg).getData().equals(Boolean.TRUE)) {
          appCompatActivity.getSupportActionBar().hide();
        } else {
          appCompatActivity.getSupportActionBar().show();
        }
      }
    }
  }

  private void initPlayer() {
    final FCCTVRatingConfiguration tvRatingConfiguration = new FCCTVRatingConfiguration.Builder().setDurationSeconds(5).build();
    final Options options = new Options.Builder()
        .setTVRatingConfiguration(tvRatingConfiguration)
        .setBypassPCodeMatching(true)
        .setUseExoPlayer(true)
        .setShowNativeLearnMoreButton(false)
        .setShowPromoImage(false)
        .build();

    player = new OoyalaPlayer(pCode, new PlayerDomain(domain), options);
    player.registerFactory(new VRPlayerFactory());
    player.addObserver(this);

    OoyalaSkinLayout skinLayout = (OoyalaSkinLayout) getView().findViewById(R.id.playerSkinLayout);
    final SkinOptions skinOptions = new SkinOptions.Builder().build();

    playerController = new OoyalaSkinLayoutController(getActivity().getApplication(), skinLayout, player, skinOptions);
    playerController.addObserver(this);

    player.setEmbedCode(embedCode);

    applyADSManager(skinLayout);
  }

  @Override
  public void invokeDefaultOnBackPressed() {
    getActivity().onBackPressed();
  }
}

package com.ooyala.sample.custom;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.item.CastMediaRoute;
import com.ooyala.android.ui.AbstractDefaultOoyalaPlayerControls;
import com.ooyala.android.ui.CuePointsSeekBar;
import com.ooyala.sample.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.TimeZone;

import androidx.core.util.Consumer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CustomPlayerControls extends AbstractDefaultOoyalaPlayerControls implements Observer {
  private float volume = 0;

  private boolean isSeekBarReady = false;
  private FrameLayout playerControlsLayout;
  private ImageView playImageView;
  private ImageView castButton;
  private ProgressBar loadingIndicator;
  private CuePointsSeekBar seekBar;
  private SeekBar volumeSeekBar;
  private ImageView volumeImageView;
  private TextView durationTextView;
  private TextView errorTextView;
  private TextView castConnected;
  private View controlsHolder;
  private RecyclerView routReciclerView;
  private View.OnClickListener onCastClickListener;

  CustomPlayerControls(OoyalaPlayer player, OoyalaPlayerLayout layout, View.OnClickListener onCastClickListener) {
    this.onCastClickListener = onCastClickListener;
    setParentLayout(layout);
    setOoyalaPlayer(player);
    player.addObserver(this);
    setupControls();
  }

  @Override
  public int bottomBarOffset() {
    return (PREFERRED_BUTTON_HEIGHT_DP * 2 + MARGIN_SIZE_DP * 4);
  }

  @Override
  protected void setupControls() {
    if (_layout == null) {
      return;
    }
    LayoutInflater inflater = LayoutInflater.from(_layout.getContext());
    _baseLayout = (FrameLayout) inflater.inflate(R.layout.player_controls, null, false);
    _layout.addView(_baseLayout);

    setUpControls();

    this._baseLayout.setVisibility(View.INVISIBLE);
    this._baseLayout.bringToFront();

    initPlayButton();
    initVolume();
    initProgressSeekBar();
    playerControlsLayout.setVisibility(View.VISIBLE);
  }

  private void setUpControls() {
    playerControlsLayout = _baseLayout.findViewById(R.id.player_controls);
    seekBar = _baseLayout.findViewById(R.id.playerControlsProgress);
    playImageView = _baseLayout.findViewById(R.id.play_image_view);
    volumeImageView = _baseLayout.findViewById(R.id.volume_image_view);
    errorTextView = _baseLayout.findViewById(R.id.error_text_view);
    volumeSeekBar = _baseLayout.findViewById(R.id.volume_seek_bar);
    durationTextView = _baseLayout.findViewById(R.id.duration_text_view);
    controlsHolder = _baseLayout.findViewById(R.id.controls_holder);
    routReciclerView = _baseLayout.findViewById(R.id.rout_list);
    loadingIndicator = _baseLayout.findViewById(R.id.loading_indicator);
    castConnected = _baseLayout.findViewById(R.id.cast_connected_text_view);
    castButton = _baseLayout.findViewById(R.id.cast_button);
    castButton.setOnClickListener(onCastClickListener);
  }


  private void initPlayButton() {
    playImageView.setColorFilter(Color.WHITE);
    playImageView.setOnClickListener(v -> {
      if (_player.isPlaying()) {
        _player.pause();
        playImageView.setImageDrawable(playImageView.getResources().getDrawable(R.drawable.play));
      } else {
        _player.play();
        playImageView.setImageDrawable(playImageView.getResources().getDrawable(R.drawable.pause));
      }
    });
  }

  private void initProgressSeekBar() {
    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
          _player.setPlayheadTime(progress);
        }
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {
        if (isSeekBarReady) {
          _player.pause();
        }
      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
        if (isSeekBarReady) {
          _player.play();
        }
      }
    });
  }

  private void initVolume() {
    volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
          _player.setVolume((float) progress / 100);
          updateVolumeImage(progress);
        }
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {

      }
    });

    volumeImageView.setOnClickListener(v -> {
      if (_player.getVolume() == 0f) {
        _player.setVolume(volume);
        volumeSeekBar.setProgress((int) (volume * 100));
        volumeImageView.setImageDrawable(volumeImageView.getResources().getDrawable(R.drawable.volume));
      } else {
        volume = _player.getVolume();
        _player.setVolume(0f);
        volumeSeekBar.setProgress(0);
        volumeImageView.setImageDrawable(volumeImageView.getResources().getDrawable(R.drawable.mute));
      }
    });
  }

  @Override
  protected void updateButtonStates() {

  }

  @Override
  public void setFullscreenButtonShowing(boolean b) {

  }

  @Override
  public void update(Observable arg0, Object argN) {
    final String arg1 = OoyalaNotification.getNameOrUnknown(argN);

    if (arg1.equals(OoyalaPlayer.TIME_CHANGED_NOTIFICATION_NAME)) {
      if (_player != null) {
        int playheadTime = _player.getPlayheadTime();
        loadingIndicator.setVisibility(View.GONE);
        seekBar.setProgress(playheadTime);
        String text = convertTime(playheadTime) + " - " + convertTime(_player.getDuration());
        durationTextView.setText(text);
      }
    }

    if (arg1.equals(OoyalaPlayer.CAST_CONNECTED_NOTIFICATION_NAME)) {
      castConnected.setVisibility(View.VISIBLE);
      castButton.setColorFilter(Color.BLUE);
      seekBar.setMax(_player.getDuration());
    }

    if (arg1.equals(OoyalaPlayer.CAST_DISCONNECTED_NOTIFICATION_NAME)) {
      castButton.setColorFilter(Color.WHITE);
      castConnected.setVisibility(View.GONE);
    }

    if (arg1.equals(OoyalaPlayer.STATE_CHANGED_NOTIFICATION_NAME)) {
      OoyalaPlayer.State currentState = _player.getState();
      updateButtonStates();
      if (currentState == OoyalaPlayer.State.LOADING || currentState == OoyalaPlayer.State.INIT) {
        loadingIndicator.setVisibility(View.VISIBLE);
      }

      if (currentState == OoyalaPlayer.State.PLAYING) {
        loadingIndicator.setVisibility(View.GONE);
        playImageView.setImageDrawable(playImageView.getResources().getDrawable(R.drawable.pause));
      }

      if (currentState == OoyalaPlayer.State.PAUSED) {
        loadingIndicator.setVisibility(View.GONE);
        playImageView.setImageDrawable(playImageView.getResources().getDrawable(R.drawable.play));
      }

      if (currentState == OoyalaPlayer.State.READY) {
        loadingIndicator.setVisibility(View.GONE);
        seekBar.setMax(_player.getDuration());
        seekBar.setProgress(0);
        isSeekBarReady = true;
      }

      if (currentState == OoyalaPlayer.State.READY || currentState == OoyalaPlayer.State.PLAYING || currentState == OoyalaPlayer.State.PAUSED) {
        _isPlayerReady = true;
      }

      if (currentState == OoyalaPlayer.State.SUSPENDED || currentState == OoyalaPlayer.State.ERROR) {
        isSeekBarReady = false;
        _isPlayerReady = false;
      }

      if (currentState == OoyalaPlayer.State.COMPLETED || currentState == OoyalaPlayer.State.ERROR) {
        loadingIndicator.setVisibility(View.GONE);
        isSeekBarReady = false;
      }
    }
    if (arg1.equals(OoyalaPlayer.ERROR_NOTIFICATION_NAME)) {
      _baseLayout.setVisibility(View.VISIBLE);
      controlsHolder.setVisibility(View.GONE);
      errorTextView.setVisibility(View.VISIBLE);
      errorTextView.setText(_player.getError().getMessage());
    }
  }

  private String convertTime(int timeSTamp) {
    if (timeSTamp == -1) {
      timeSTamp = 0;
    }
    Date date = new Date(timeSTamp);
    DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
    formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    return formatter.format(date);
  }

  private void updateVolumeImage(int progress) {
    if (progress != 0) {
      volumeImageView.setImageDrawable(volumeImageView.getResources().getDrawable(R.drawable.volume));
    } else {
      volumeImageView.setImageDrawable(volumeImageView.getResources().getDrawable(R.drawable.mute));
    }
  }

  void setVolume(int currentVolume) {
    volumeSeekBar.setProgress(currentVolume * 10);
    _player.setVolume(currentVolume);
    updateVolumeImage(currentVolume * 10);
  }

  void showList(Set<CastMediaRoute> mediaRoutes) {
    if (mediaRoutes.isEmpty()) {
      castButton.setVisibility(View.GONE);
      return;
    }
    _player.pause();
    RoutsAdapter adapter = new RoutsAdapter(new LinkedList<>(mediaRoutes), getCastMediaRouteConsumer());
    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(_baseLayout.getContext());
    routReciclerView.setLayoutManager(mLayoutManager);
    routReciclerView.setAdapter(adapter);
    routReciclerView.setVisibility(View.VISIBLE);
  }

  private Consumer<CastMediaRoute> getCastMediaRouteConsumer() {
    return castMediaRoute -> {
      routReciclerView.setVisibility(View.GONE);
      _player.connectDevice(castMediaRoute);
      loadingIndicator.setVisibility(View.VISIBLE);
    };
  }

  void hideCastButton() {
    castButton.setVisibility(View.GONE);
  }

  void showCastButton() {
    castButton.setVisibility(View.VISIBLE);
  }
}
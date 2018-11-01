package com.ooyala.sample;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.cast.CastManager;
import com.ooyala.cast.UpdateImageViewRunnable;
import com.ooyala.android.item.Video;

class CastViewManager {
  private View castView;
  private TextView stateTextView;

  CastViewManager(Activity activity, CastManager manager) {
    castView = activity.getLayoutInflater().inflate(R.layout.cast_video_view, null);
    manager.setCastView(castView);
    stateTextView = castView.findViewById(R.id.castStateTextView);
  }

  void configureCastView(String title, String description, String imageUrl) {
    final ImageView castBackgroundImage = castView.findViewById(R.id.castBackgroundImage);

    // Update the ImageView on a separate thread
    new Thread(new UpdateImageViewRunnable(castBackgroundImage, imageUrl)).start();

    TextView videoTitle = castView.findViewById(R.id.videoTitle);
    videoTitle.setText(title);

    TextView videoDescription = castView.findViewById(R.id.videoDescription);
    videoDescription.setText(description);
  }

  void updateCastState(Context c, OoyalaPlayer.State state) {
    String castDeviceName = CastManager.getCastManager().getDeviceName();
    if (state == OoyalaPlayer.State.LOADING) {
      stateTextView.setText(c.getString(R.string.loading));
    } else if (state == OoyalaPlayer.State.PLAYING || state == OoyalaPlayer.State.PAUSED) {
      String statusString = String.format(c.getString(R.string.castingTo), castDeviceName);
      stateTextView.setText(statusString);
    } else {
      stateTextView.setText("");
    }
  }
}


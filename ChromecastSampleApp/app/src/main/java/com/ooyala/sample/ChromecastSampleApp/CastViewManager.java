package com.ooyala.sample.ChromecastSampleApp;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayer.State;
import com.ooyala.android.castsdk.CastManager;
import com.ooyala.android.item.Video;

/**
 * Created by zchen on 1/13/16.
 */
public class CastViewManager {
  private View castView;
  private TextView stateTextView;

  public CastViewManager(Activity activity, CastManager manager) {
    castView = activity.getLayoutInflater().inflate(R.layout.cast_video_view, null);
    manager.setCastView(castView);
    stateTextView = (TextView)castView.findViewById(R.id.castStateTextView);
  }

  public void configureCastView(Video video) {
    final ImageView castBackgroundImage = (ImageView) castView.findViewById(R.id.castBackgroundImage);

    // Update the ImageView on a separate thread

    new Thread(new UpdateImageViewRunnable(castBackgroundImage, video.getPromoImageURL(0, 0))).start();

    TextView videoTitle = (TextView) castView.findViewById(R.id.videoTitle);
    videoTitle.setText(video.getTitle());

    TextView videoDescription = (TextView) castView.findViewById(R.id.videoDescription);
    videoDescription.setText(video.getDescription());
  }

  public void updateCastState(Context c, State state) {
    String castDeviceName = CastManager.getVideoCastManager().getDeviceName();
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

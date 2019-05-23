package com.ooyala.sample.players;


import android.os.Bundle;
import android.util.Log;

import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.item.Stream;
import com.ooyala.android.item.UnbundledVideo;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.sample.R;

import java.util.Observable;

public class UnbundledPlayerActivity extends AbstractHookActivity {

  private Stream stream;

  public static String getName() {
    return "Unbundled";
  }

  @Override
  void completePlayerSetup(boolean asked) {
    if (asked) {
      //Initialize the player
      player = new OoyalaPlayer(pcode, new PlayerDomain(domain), createPlayerOptions());
      playerLayout = findViewById(R.id.ooyalaPlayer);
      playerLayoutController = new OoyalaPlayerLayoutController(playerLayout, player);
      player.addObserver(this);

      final String url = getIntent().getExtras().getString("embed_code");
      stream = new Stream(url, Stream.DELIVERY_TYPE_MP4);
      UnbundledVideo u = new UnbundledVideo(stream);
      final boolean success = player.setUnbundledVideo(u);
      if (success) {
        //Uncomment for Auto-Play
        //player.play();
      } else {
        Log.e(TAG, "Asset Failure");
      }
    }
  }

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.player_simple_layout);
    completePlayerSetup(asked);
  }

  @Override
  public void update(Observable arg0, Object argN) {
    super.update(arg0, argN);
    final String arg1 = OoyalaNotification.getNameOrUnknown(argN);

    // STREAM_PARAMS_UPDATED_NOTIFICATION_NAME is called when the available or selected tracks change.
    if (arg1.equalsIgnoreCase(OoyalaPlayer.STREAM_PARAMS_UPDATED_NOTIFICATION_NAME)) {
      int videoBitrate = stream.getVideoBitrate();
      int audioBitrate = stream.getAudioBitrate();
      int combinedBitrate = stream.getCombinedBitrate();
      int width = stream.getWidth();
      int height = stream.getHeight();
      String url = stream.getUrl();
      String streamParams = String.format("Stream params: video bitrate: %d, audio bitrate: %d, " +
        "combined bitrate: %d, width: %d, height: %d, URL: %s", videoBitrate, audioBitrate,
        combinedBitrate, width, height, url);
      Log.d(TAG, streamParams);
    }
  }
}

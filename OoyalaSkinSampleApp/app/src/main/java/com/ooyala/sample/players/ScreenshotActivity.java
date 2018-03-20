package com.ooyala.sample.players;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.player.exoplayer.PlayerBitmapListener;
import com.ooyala.android.skin.OoyalaSkinLayout;
import com.ooyala.android.skin.OoyalaSkinLayoutController;
import com.ooyala.sample.R;

/**
 * This activity illustrates how you can create a screenshot
 */
public class ScreenshotActivity extends AbstractHookActivity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.player_screenshot_layout);
    completePlayerSetup(asked);

    // create a bitmap
    Button screenshotButton = findViewById(R.id.screenshotButton);
    final ImageView screenshotView = findViewById(R.id.screenshotView);

    screenshotButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (player != null) {
          player.createBitmapScreenshot(new PlayerBitmapListener() {
            @Override
            public void onBitmapReady(Bitmap bitmap) {
              screenshotView.setImageBitmap(bitmap);
            }
          });
        }
      }
    });
  }

  @Override
  void completePlayerSetup(boolean asked) {
    if (asked) {
      // Get the SkinLayout from our layout xml
      skinLayout = (OoyalaSkinLayout) findViewById(R.id.ooyalaSkin);

      // Create the OoyalaPlayer, with some built-in UI disabled
      PlayerDomain playerDomain = new PlayerDomain(domain);
      Options options = new Options.Builder()
        .setShowNativeLearnMoreButton(false)
        .setShowPromoImage(false)
        .setUseExoPlayer(true)
        .build();

      player = new OoyalaPlayer(pcode, playerDomain, options);
      playerLayoutController = new OoyalaSkinLayoutController(getApplication(), skinLayout, player);

      //Add observer to listen to fullscreen open and close events
      playerLayoutController.addObserver(this);
      player.addObserver(this);

      if (player.setEmbedCode(embedCode)) {
      } else {
        Log.e(TAG, "Asset Failure");
      }
    }
  }
}

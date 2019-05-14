package com.ooyala.sample.players;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ooyala.android.OoyalaException;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.sample.R;
import com.ooyala.android.skin.OoyalaSkinLayout;
import com.ooyala.android.skin.OoyalaSkinLayoutController;
import com.ooyala.android.skin.configuration.SkinOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * This activity illustrates how you can play basic playback video using the Skin SDK
 * you can also play Ooyala and VAST advertisements programmatically
 * through the SDK
 */
public class AssetActivity extends AbstractHookActivity {

  public static String getName() {
    return "Asset Activity";
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.player_set_asset_layout);

    completePlayerSetup(asked);

    initButtonListeners();
  }

  private void initButtonListeners() {
    Button setFirstAssetButton = findViewById(R.id.set_first_asset);
    setFirstAssetButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        try {
          player.setAsset(loadJSONFromAsset("asset1.json"));
        } catch (JSONException e) {
          e.printStackTrace();
        } catch (OoyalaException e) {
          e.printStackTrace();
        }
      }
    });

    Button setSecondAssetButton = findViewById(R.id.set_second_asset);
    setSecondAssetButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        try {
          player.setAsset(loadJSONFromAsset("asset2.json"));
        } catch (JSONException e ) {
          e.printStackTrace();
        } catch (OoyalaException e) {
          e.printStackTrace();
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
      Options options = new Options.Builder().setShowNativeLearnMoreButton(false).setShowPromoImage(false).setUseExoPlayer(true).build();
      player = new OoyalaPlayer(pcode, new PlayerDomain("http://www.ooyala.com"), options);
      //Create the SkinOptions, and setup React
      JSONObject overrides = createSkinOverrides();
      SkinOptions skinOptions = new SkinOptions.Builder().setSkinOverrides(overrides).build();
      playerLayoutController = new OoyalaSkinLayoutController(getApplication(), skinLayout, player, skinOptions);
      //Add observer to listen to fullscreen open and close events
      playerLayoutController.addObserver(this);
      player.addObserver(this);

      try {
        player.setAsset(loadJSONFromAsset("asset1.json"));
      } catch (OoyalaException e) {
        e.printStackTrace();
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Create skin overrides to show up in the skin.
   * Default commented. Uncomment to show changes to the start screen.
   * @return the overrides to apply to the skin.json in the assets folder
   */
  private JSONObject createSkinOverrides() {
    JSONObject overrides = new JSONObject();
    return overrides;
  }
  
}
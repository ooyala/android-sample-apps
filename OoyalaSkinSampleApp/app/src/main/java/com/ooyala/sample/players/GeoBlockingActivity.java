package com.ooyala.sample.players;


import android.os.Bundle;
import android.util.Log;

import com.ooyala.android.EmbedTokenGenerator;
import com.ooyala.android.EmbedTokenGeneratorCallback;
import com.ooyala.android.EmbeddedSecureURLGenerator;
import com.ooyala.android.Environment;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.skin.OoyalaSkinLayout;
import com.ooyala.android.skin.OoyalaSkinLayoutController;
import com.ooyala.android.skin.configuration.SkinOptions;
import com.ooyala.sample.R;
import com.ooyala.sample.utils.CustomPlayerInfo;

import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class GeoBlockingActivity extends AbstractHookActivity implements EmbedTokenGenerator {
  private  String ACCOUNT_ID = "player-qa";

  /*
   * The API Key and Secret should not be saved inside your applciation (even in git!).
   * However, for debugging you can use them to locally generate Ooyala Player Tokens.
   */
  private  String APIKEY = "FhN2MyOlEAUn2NZ0OM94L7XNcL0f.yPx4J";
  private  String SECRET = "6TCPX_HENTkNUlaO34U4qcoi2SkCoXQwCDgQOcaW";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.player_skin_simple_layout);
    completePlayerSetup(asked);
  }

  @Override
  void completePlayerSetup(boolean asked) {
    if (asked) {
      // Get the SkinLayout from our layout xml
      skinLayout = (OoyalaSkinLayout) findViewById(R.id.ooyalaSkin);
      // Create the OoyalaPlayer, with some built-in UI disabled
      PlayerDomain playerDomain = new PlayerDomain(domain);
      Options.Builder optionsBuilder = new Options.Builder().setShowNativeLearnMoreButton(false).setShowPromoImage(false).setUseExoPlayer(true);
      if(!selectedFormat.equalsIgnoreCase("default")) {
        optionsBuilder.setPlayerInfo(new CustomPlayerInfo(selectedFormat)).build();
      }
      Options options = optionsBuilder.build();

        //Uncomment next line if you work on STAGING
        //OoyalaPlayer.setEnvironment(Environment.EnvironmentType.STAGING);

        if(getIntent().getExtras().getString("className").contains("AddAssetActivity")) {
            ACCOUNT_ID = accountId;
            APIKEY = apiKey;
            SECRET = secret;
        }

      //Uncomment next line if you work on STAGING
      //OoyalaPlayer.setEnvironment(Environment.EnvironmentType.STAGING);

      if(isStaging) {
        Log.i(TAG, "Environment Set to Staging:");
        OoyalaPlayer.setEnvironment(Environment.EnvironmentType.STAGING, Environment.PROTOCOL_HTTPS);
      } else {
        Log.i(TAG, "Environment Set to Production:");
        OoyalaPlayer.setEnvironment(Environment.EnvironmentType.PRODUCTION, Environment.PROTOCOL_HTTPS);
      }
      player = new OoyalaPlayer(pcode, playerDomain,this, options);

      //Create the SkinOptions, and setup React
      JSONObject overrides = createSkinOverrides();
      SkinOptions skinOptions = new SkinOptions.Builder().setSkinOverrides(overrides).build();
      playerLayoutController = new OoyalaSkinLayoutController(getApplication(), skinLayout, player, skinOptions);
      //Add observer to listen to fullscreen open and close events
      playerLayoutController.addObserver(this);
      player.addObserver(this);

      if (player.setEmbedCode(embedCode)) {
        if(autoPlay) {
          player.play();
        }
      } else {
        Log.e(TAG, "Asset Failure");
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

  /*
   * Get the Ooyala Player Token to play the embed code.
   * This should contact your servers to generate the OPT server-side.
   * For debugging, you can use Ooyala's EmbeddedSecureURLGenerator to create local embed tokens
   */
  @Override
  public void getTokenForEmbedCodes(List<String> embedCodes,
                                    EmbedTokenGeneratorCallback callback) {
    String embedCodesString = "";
    for (String ec : embedCodes) {
      if(ec.equals("")) embedCodesString += ",";
      embedCodesString += ec;
    }

    HashMap<String, String> params = new HashMap<String, String>();
    params.put("account_id", ACCOUNT_ID);


    String uri = "/sas/embed_token/" + pcode + "/" + embedCodesString;

    EmbeddedSecureURLGenerator urlGen = new EmbeddedSecureURLGenerator(APIKEY, SECRET);

    URL tokenUrl  = urlGen.secureURL("http://player.ooyala.com", uri, params);

    callback.setEmbedToken(tokenUrl.toString());
  }

  public static String getName() {
    return GeoBlockingActivity.class.getSimpleName();
  }
}

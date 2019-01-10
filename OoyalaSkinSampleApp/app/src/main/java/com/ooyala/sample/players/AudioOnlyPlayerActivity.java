package com.ooyala.sample.players;

import android.os.Bundle;
import android.util.Log;

import com.ooyala.android.EmbedTokenGenerator;
import com.ooyala.android.EmbedTokenGeneratorCallback;
import com.ooyala.android.EmbeddedSecureURLGenerator;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.skin.OoyalaSkinLayoutController;
import com.ooyala.android.skin.configuration.SkinOptions;
import com.ooyala.sample.R;

import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class AudioOnlyPlayerActivity extends AbstractHookActivity implements EmbedTokenGenerator {

  // An account ID, if you are using Concurrent Streams or Entitlements
  private final String ACCOUNT_ID = "ACCOUNT_ID";
  private final String APIKEY = "Use this for testing, don't keep your secret in the application";
  private final String SECRET = "Use this for testing, don't keep your secret in the application";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.player_skin_audio_layout);
    completePlayerSetup(asked);
  }

  @Override
  void completePlayerSetup(boolean asked) {
    if (asked) {
      skinLayout = findViewById(R.id.ooyalaSkin);
      player = new OoyalaPlayer(pcode, new PlayerDomain(domain), this, createPlayerOptions());
      playerLayoutController = new OoyalaSkinLayoutController(getApplication(), skinLayout, player, createSkinOptions());
      playerLayoutController.addObserver(this);
      player.addObserver(this);

      if (player.setEmbedCode(embedCode)) {
        if (autoPlay)
          player.play();
      } else {
        Log.e(TAG, "Asset Failure");
      }
    }
  }

  private SkinOptions createSkinOptions() {
    return new SkinOptions.Builder().setSkinOverrides(createSkinOverrides()).build();
  }

  private Options createPlayerOptions() {
    return new Options.Builder()
        .setShowNativeLearnMoreButton(false)
        .setShowPromoImage(false)
        .setUseExoPlayer(true)
        .setAudioOnly(true) // This is crucial
        .build();
  }

  private JSONObject createSkinOverrides() {
    try {
      return new JSONObject();
    } catch (Exception e) {
      throw new RuntimeException("Exception while creating skin overrides", e);
    }
  }

  @Override
  public void getTokenForEmbedCodes(List<String> embedCodes, EmbedTokenGeneratorCallback callback) {
    String embedCodesString = "";
    for (String ec : embedCodes) {
      if (ec.equals("")) embedCodesString += ",";
      embedCodesString += ec;
    }

    HashMap<String, String> params = new HashMap<String, String>();
    params.put("account_id", ACCOUNT_ID);

     /* Uncommenting this will bypass all syndication rules on your asset
       This will not work unless you have a working API Key and Secret.
       This is one reason why you shouldn't keep the Secret in your app/source control */
//     params.put("override_syndication_group", "override_all_synd_groups");

    String uri = "/sas/embed_token/" + pcode + "/" + embedCodesString;

    EmbeddedSecureURLGenerator urlGen = new EmbeddedSecureURLGenerator(APIKEY, SECRET);

    URL tokenUrl = urlGen.secureURL("http://player.ooyala.com", uri, params);

    callback.setEmbedToken(tokenUrl.toString());
  }
}

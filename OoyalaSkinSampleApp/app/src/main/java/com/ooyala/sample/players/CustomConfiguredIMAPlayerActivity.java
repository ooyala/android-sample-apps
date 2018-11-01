package com.ooyala.sample.players;

import android.os.Bundle;
import android.util.Log;

import com.google.ads.interactivemedia.v3.api.AdDisplayContainer;
import com.google.ads.interactivemedia.v3.api.AdErrorEvent;
import com.google.ads.interactivemedia.v3.api.AdEvent;
import com.google.ads.interactivemedia.v3.api.AdsManagerLoadedEvent;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.imasdk.IMAAdErrorListener;
import com.ooyala.android.imasdk.IMAAdEventListener;
import com.ooyala.android.imasdk.IMAAdsLoadedListener;
import com.ooyala.android.imasdk.IMAContainerUpdatedListener;
import com.ooyala.android.imasdk.OoyalaIMAManager;
import com.ooyala.android.skin.OoyalaSkinLayout;
import com.ooyala.android.skin.OoyalaSkinLayoutController;
import com.ooyala.android.skin.configuration.SkinOptions;
import com.ooyala.sample.R;


/**
 * This activity illustrates how to override IMA parameters in application code
 *
 * Supported methods:
 * imaManager.setAdUrlOverride(String)
 * imaManager.setAdTagParameters(Map<String, String>)
 */
public class CustomConfiguredIMAPlayerActivity extends AbstractHookActivity
        implements IMAAdErrorListener, IMAAdEventListener, IMAAdsLoadedListener, IMAContainerUpdatedListener {
  final String TAG = this.getClass().toString();
  public final static String getName() {
    return "Custom Configured IMA Player";
  }

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.player_simple_frame_layout);
    completePlayerSetup(asked);
  }

  @Override
  void completePlayerSetup(boolean asked) {
    //Initialize the player
    skinLayout = (OoyalaSkinLayout) findViewById(R.id.ooyalaPlayer);
    // Create the OoyalaPlayer, with some built-in UI disabled
    PlayerDomain playerDomain = new PlayerDomain(domain);
    Options options = new Options.Builder().setShowPromoImage(false).setUseExoPlayer(true).build();
    player = new OoyalaPlayer(pcode, playerDomain, options);

    //Create the SkinOptions, and setup React
    SkinOptions skinOptions = new SkinOptions.Builder().build();
    playerLayoutController = new OoyalaSkinLayoutController(getApplication(), skinLayout, player, skinOptions);
    //Add observer to listen to fullscreen open and close events
    playerLayoutController.addObserver(this);

    player.addObserver(this);

    if (player.setEmbedCode(embedCode)) {
//      player.play();
    }

    /** DITA_START:<ph id="ima_custom"> **/

    OoyalaIMAManager imaManager = new OoyalaIMAManager(player, skinLayout);
    imaManager.setOnAdErrorListener(this);
    imaManager.setOnAdEventListener(this);
    imaManager.setAdsLoadedListener(this);
    imaManager.setContainerUpdatedListener(this);

    // This ad tag returns a midroll video
    imaManager.setAdUrlOverride("http://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/7521029/pb_test_mid&ciu_szs=640x480&impl=s&cmsid=949&vid=FjbGRjbzp0DV_5-NtXBVo5Rgp3Sj0R5C&gdfp_req=1&env=vp&output=xml_vast2&unviewed_position_start=1&url=[referrer_url]&description_url=[description_url]&correlator=[timestamp]");
    // imaManager.setAdTagParameters(null);
    /** DITA_END:</ph> **/
  }

  @Override
  public void onAdError(AdErrorEvent adErrorEvent) {
    Log.d(TAG, String.format("IMA Ad Manager: error %s.", adErrorEvent.getError().getMessage()));
  }

  @Override
  public void onAdEvent(AdEvent adEvent) {
    Log.d(TAG, String.format("IMA Ad Manager: event %s.", adEvent.getType().name()));
  }

  @Override
  public void onAdsManagerLoaded(AdsManagerLoadedEvent adsManagerLoadedEvent) {
    Log.d(TAG, "IMA Ad Manager: loaded");
  }

  @Override
  public void onAdsContainerUpdated(AdDisplayContainer adDisplayContainer) {
    Log.d(TAG, "IMA Display Container Updated");
  }
}
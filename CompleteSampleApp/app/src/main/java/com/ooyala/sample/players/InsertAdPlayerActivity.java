package com.ooyala.sample.players;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.ooyala.android.ads.ooyala.OoyalaAdSpot;
import com.ooyala.android.OoyalaManagedAdsPlugin;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.ads.vast.VASTAdSpot;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.performance.PerformanceMonitor;
import com.ooyala.android.performance.PerformanceMonitorBuilder;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.sample.R;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * This activity illustrates how you can insert Ooyala and VAST advertisements programmatically
 * through the SDK
 *
 * If you want to play an advertisement immediately, you can set the time of your Ad Spot to
 * the current playhead time
 *
 *
 */
public class InsertAdPlayerActivity extends AbstractHookActivity {
	public final static String getName() {
		return "Insert Ad at Runtime";
	}

	public static PerformanceMonitor performanceMonitor;

	@Override
	void completePlayerSetup(boolean asked) {
		if (asked) {
			//Initialize the player
			OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);

			Options options = new Options.Builder().setUseExoPlayer(true).build();
			player = new OoyalaPlayer(PCODE, new PlayerDomain(DOMAIN), options);
			playerLayoutController = new OoyalaPlayerLayoutController(playerLayout, player);
			player.enableSSL(false);
			player.addObserver(this);

			//  Set up performance monitoring to watch standard events and ads events.
			performanceMonitor = PerformanceMonitorBuilder.getStandardAdsMonitor(player);

			if (player.setEmbedCode(EMBED_CODE)) {
				//Uncomment for Auto-Play
				//player.play();
			}

			/** DITA_START:<ph id="insert_ad_vast"> **/
			//Setup the left button, which will immediately insert a VAST advertisement
			Button leftButton = (Button) findViewById(R.id.doubleLeftButton);
			leftButton.setText("Insert VAST Ad");
			leftButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					OoyalaManagedAdsPlugin plugin = player.getManagedAdsPlugin();
					try {
						VASTAdSpot vastAd = new VASTAdSpot(player.getPlayheadTime(), player.getDuration(), null, null, new URL("http://xd-team.ooyala.com.s3.amazonaws.com/ads/VastAd_Preroll.xml"));
						plugin.insertAd(vastAd);
					} catch (MalformedURLException e) {
						Log.e(TAG, "VAST Ad Tag was malformed");
						e.printStackTrace();
					}
				}
			});
			/** DITA_END:</ph> **/

			//Setup the right button, which will immediately insert an Ooyala advertisement
			Button rightButton = (Button) findViewById(R.id.doubleRightButton);
			rightButton.setText("Insert Ooyala Ad");
			rightButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					OoyalaManagedAdsPlugin plugin = player.getManagedAdsPlugin();
					OoyalaAdSpot ooyalaAd = new OoyalaAdSpot(player.getPlayheadTime(), null, null, "Zvcmp0ZDqD6xnQVH8ZhWlxH9L9bMGDDg");
					plugin.insertAd(ooyalaAd);
				}
			});
		}
	}

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player_double_button_layout);
		completePlayerSetup(asked);
	}
}

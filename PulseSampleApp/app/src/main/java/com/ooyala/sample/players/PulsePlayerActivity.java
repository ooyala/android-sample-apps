package com.ooyala.sample.players;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.ooyala.pulse.ContentMetadata;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.item.Video;
import com.ooyala.android.pulseintegration.OoyalaPulseManager;
import com.ooyala.android.skin.OoyalaSkinLayout;
import com.ooyala.android.skin.OoyalaSkinLayoutController;
import com.ooyala.android.skin.configuration.SkinOptions;
import com.ooyala.pulse.Pulse;
import com.ooyala.pulse.PulseSession;
import com.ooyala.pulse.PulseVideoAd;
import com.ooyala.pulse.RequestSettings;
import com.ooyala.sample.R;
import com.ooyala.sample.utils.VideoItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * The player activity that is responsible for playing a content video and its associated ads.
 */
public class PulsePlayerActivity extends AbstractHookActivity {

	public final static String getName() {
		return "Pulse Player";
	}

	final String PCODE = "tlM2k6i2-WrXX1DE_b8zfhui_eQN";
	final String DOMAIN = "http://ooyala.com";
	private OoyalaSkinLayout skinLayout;
	private OoyalaPulseManager pulseManager;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getName());
		setContentView(R.layout.player_skin_simple_layout);
		completePlayerSetup(asked);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		destroyPlayer();
	}

	@Override
	void completePlayerSetup(boolean asked) {
		if (asked) {
			final VideoItem videoItem = getVideoItem();
			// Get the SkinLayout from our layout xml
			skinLayout = (OoyalaSkinLayout) findViewById(R.id.ooyalaSkin);
			// Create the OoyalaPlayer, with some built-in UI disabled
			PlayerDomain domain = new PlayerDomain(DOMAIN);
			Options options = new Options.Builder().setShowPromoImage(false).setUseExoPlayer(true).build();

			player = new OoyalaPlayer(PCODE, domain, options);

			//Create the SkinOptions, and setup the LayoutController
			SkinOptions skinOptions = new SkinOptions.Builder().build();
			playerSkinLayoutController = new OoyalaSkinLayoutController(getApplication(), skinLayout, player, skinOptions);
			playerSkinLayoutController.addObserver(this);
			player.addObserver(this);

			//Create an instance of OoyalaPulseManager and set a listener.
			pulseManager = new OoyalaPulseManager(player);

			pulseManager.setListener(new OoyalaPulseManager.Listener() {
				/*
					Called by the plugin to let us create the Pulse session; the metadata retrieved from Backlot is provided here
				*/
				@Override
				public PulseSession createPulseSession(OoyalaPulseManager ooyalaPulseManager, Video video, String pulseHost, ContentMetadata contentMetadata, RequestSettings requestSettings) {
					// Replace some of the Backlot metadata with our own local data
					List<Float> midrollPositions = new ArrayList<>();
					for (float f : videoItem.getMidrollPositions()) {
						midrollPositions.add(f);
					}
					requestSettings.setLinearPlaybackPositions(midrollPositions);
					contentMetadata.setTags(Arrays.asList(videoItem.getTags()));
					contentMetadata.setIdentifier(videoItem.getContentId());
					contentMetadata.setCategory(videoItem.getCategory());

					Pulse.setPulseHost(pulseHost, null, null);
					return Pulse.createSession(contentMetadata, requestSettings);
				}

				@Override
				public void openClickThrough(OoyalaPulseManager ooyalaPulseManager, PulseVideoAd pulseVideoAd) {
					Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(pulseVideoAd.getClickthroughURL().toString()));
					startActivity(intent);

					// adClickThroughTriggered should be reported when the user has opened the
					// clickthrough link in a browser.
					// Note: If there are multiple browsers installed on device, the user will
					// be asked choose a browser or cancel. An accurate implementation should
					// only call adClickThroughTriggered if the browser was actually opened.
					pulseVideoAd.adClickThroughTriggered();
				}
			});
			player.setEmbedCode(videoItem.getContentCode());
		}
	}

	/**
	 * Create a VideoItem from the bundled information send to this activity.
	 * @return The created {@link VideoItem}.
	 */
	public VideoItem getVideoItem() {
		VideoItem videoItem = new VideoItem();
		videoItem.setTags(getIntent().getExtras().getStringArray("contentMetadataTags"));
		videoItem.setMidrollPositions(getIntent().getExtras().getFloatArray("midrollPositions"));
		videoItem.setContentTitle(getIntent().getExtras().getString("contentTitle"));
		videoItem.setContentId(getIntent().getExtras().getString("contentId"));
		videoItem.setCategory(getIntent().getExtras().getString("category"));
		videoItem.setContentCode(getIntent().getExtras().getString("embedCode"));
		return videoItem;
	}

	private void destroyPlayer() {
		pulseManager = null;

		if (player != null) {
			player.destroy();
			player = null;
		}
		if (skinLayout != null) {
			skinLayout.release();
		}
		if (playerSkinLayoutController != null) {
			playerSkinLayoutController.onDestroy();
			playerSkinLayoutController = null;
		}
	}
}

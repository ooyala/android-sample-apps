package com.ooyala.fullscreensampleapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.skin.OoyalaSkinLayout;
import com.ooyala.android.skin.OoyalaSkinLayoutController;
import com.ooyala.android.util.DebugMode;

import androidx.recyclerview.widget.RecyclerView;

public class MediaPlayer implements Player, LifeCycle, DefaultHardwareBackBtnHandler {
	private static final String TAG = MediaPlayer.class.getSimpleName();

	private static MediaPlayer PLAYER;

	private OoyalaPlayer player;
	private OoyalaSkinLayoutController playerLayoutController;
	private OoyalaSkinLayout ooyalaSkinLayout;
	private Activity activity;
	private RecyclerView recyclerView;
	private Data data;

	public static MediaPlayer getInstance() {
		if (PLAYER == null) {
			PLAYER = new MediaPlayer();
		}
		return PLAYER;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	/**
	 * Set the instance of {@link RecyclerView}. The recycler view is used to let the player handles
	 * touches in the special areas like seek bar and volume bar.
	 *
	 * @param recyclerView is root container.
	 */
	public void setRecyclerView(RecyclerView recyclerView) {
		this.recyclerView = recyclerView;
	}

	/**
	 * Stretch OoyalaSkinLayout to dimensions of the display window.
	 * Show/Hide system ui (notification and navigation bar) depending if layout is in fullscreen.
	 *
	 * @param fullscreen true if fullscreen mode is expected to turn on, false in the other case.
	 */
	public void setFullscreenMode(boolean fullscreen) {
		if (ooyalaSkinLayout != null) {
			ooyalaSkinLayout.setFullscreen(fullscreen);
		}
	}

	public void init(OoyalaSkinLayout ooyalaSkinLayout, Data data) {
		this.ooyalaSkinLayout = ooyalaSkinLayout;
		this.data = data;

		if (player != null) {
			DebugMode.logD(TAG, "The player's instance is ready. Set the embed code");
			player.setEmbedCode(data.getEmbedCode());
			return;
		}

		DebugMode.logD(TAG, "Initialize a player and play");
		PlayerDomain domain = new PlayerDomain(data.getDomain());
		Options options = new Options.Builder()
				.setShowPromoImage(false)
				.setUseExoPlayer(true)
				.build();

		player = new OoyalaPlayer(data.getPcode(), domain, options);
		playerLayoutController = new OoyalaSkinLayoutController(activity.getApplication(), ooyalaSkinLayout, player);
		// Default hardware back button handler was destroyed in ReactInstanceManager so we need to set it up again.
		playerLayoutController.onResume(activity, this);
		playerLayoutController.setRootRecyclerView(recyclerView);
		player.setEmbedCode(data.getEmbedCode());
	}

	@Override
	public void play() {
		play(0);
	}

	@Override
	public void play(int playHeadTime) {
		if (player != null) {
			player.play(playHeadTime);
		}
	}

	@Override
	public void pause() {
		if (player != null) {
			player.pause();
		}
	}

	@Override
	public boolean isPlaying() {
		return player != null && player.isPlaying();
	}

	@Override
	public int getPlayheadTime() {
		return player != null ? player.getPlayheadTime() : 0;
	}

	@Override
	public void seekTo(int miliseconds) {
		if (player != null) {
			player.seek(data.getPlayedHeadTime());
		}
	}

	/** Start DefaultHardwareBackBtnHandler **/
	@Override
	public void invokeDefaultOnBackPressed() {
		activity.onBackPressed();
	}
	/** End DefaultHardwareBackBtnHandler **/

	@Override
	public void onStart() {
		if (null != player) {
			player.resume();
		}
	}

	@Override
	public void onStop() {
		if (player != null) {
			player.suspend();
		}
	}

	@Override
	public void onPause() {
		if (playerLayoutController != null) {
			playerLayoutController.onPause();
		}
	}

	@Override
	public void onResume() {
		if (playerLayoutController != null && activity != null) {
			playerLayoutController.onResume(activity, this);
		}
	}

	@Override
	public void onDestroy() {
		destroyPlayer();

		activity = null;
	}

	@Override
	public void onBackPressed() {
		if (playerLayoutController != null) {
			playerLayoutController.onBackPressed();
		}
	}

	private void destroyPlayer() {
		if (player != null) {
			player.destroy();
			player = null;
		}
		if (ooyalaSkinLayout != null) {
			ooyalaSkinLayout.release();
			ooyalaSkinLayout = null;
		}
		if (null != playerLayoutController) {
			playerLayoutController.onDestroy();
			playerLayoutController = null;
		}
	}

	OoyalaSkinLayout getPlayerLayout() {
		if (ooyalaSkinLayout == null) {
			DebugMode.logD(TAG, "Player layout was initialized");
			ooyalaSkinLayout = (OoyalaSkinLayout) LayoutInflater
					.from(activity)
					.inflate(R.layout.ooyala_player_skin_layout, null, false);
		} else if (ooyalaSkinLayout.getParent() != null) {
			DebugMode.logD(TAG, "Player layout was removed from the parent");
			((ViewGroup) ooyalaSkinLayout.getParent()).removeView(ooyalaSkinLayout);
		}
		return ooyalaSkinLayout;
	}
}

package com.ooyala.fullscreensampleapp;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.skin.OoyalaSkinLayout;
import com.ooyala.android.skin.OoyalaSkinLayoutController;
import com.ooyala.android.util.DebugMode;

public class MediaPlayer implements Player, LifeCycle, DefaultHardwareBackBtnHandler {
	private static final String TAG = "PLAYER-5406"; //MediaPlayer.class.getSimpleName();

	private static MediaPlayer PLAYER;

	private OoyalaPlayer player;
	private OoyalaSkinLayoutController playerLayoutController;
	private OoyalaSkinLayout ooyalaSkinLayout;
	private Activity activity;
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

	public void init(OoyalaSkinLayout ooyalaSkinLayout, Data data) {
		this.ooyalaSkinLayout = ooyalaSkinLayout;
		this.data = data;

		if (player != null) {
			Log.i(TAG, "The player's instance is ready. Set the embed code");
			player.setEmbedCode(data.getEmbedCode());
			return;
		}

		Log.i(TAG, "Initialize a player and play");
		PlayerDomain domain = new PlayerDomain(data.getDomain());
		Options options = new Options.Builder()
				.setShowPromoImage(false)
				.setUseExoPlayer(true)
				.build();

		player = new OoyalaPlayer(data.getPcode(), domain, options);
		playerLayoutController = new OoyalaSkinLayoutController(activity.getApplication(), ooyalaSkinLayout, player);
		// Default hardware back button handler was destroyed in ReactInstanceManager so we need to set it up again.
		playerLayoutController.onResume(activity, this);
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
		if (player != null && player.getState() != OoyalaPlayer.State.PAUSED) {
			player.pause();
		}

	}

	@Override
	public boolean isPlaying() {
		if (player != null) {
			return player.isPlaying();
		}
		return false;
	}

	@Override
	public boolean isInitialised() {
		return player != null;
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

	@Override
	public void mutePlayer(boolean mute) {
		if (player != null) {
			player.setVolume(mute ? 0 : 1);
		}
	}

	@Override
	public boolean isPlayerMuted() {
		return player != null && player.getVolume() == 1;
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
		}
		if (null != playerLayoutController) {
			playerLayoutController.onDestroy();
			playerLayoutController = null;
		}
	}

	OoyalaSkinLayout getPlayerLayout() {
		if (ooyalaSkinLayout == null) {
			DebugMode.logD("PLAYER-5406", "getPlayerLayout(): ooyalaSkinLayout == null, creating");
			ooyalaSkinLayout = (OoyalaSkinLayout) LayoutInflater
					.from(activity)
					.inflate(R.layout.ooyala_player_skin_layout, null, false);
		} else if (ooyalaSkinLayout.getParent() != null) {
			DebugMode.logD("PLAYER-5406", "getPlayerLayout():ooyalaSkinLayout.getParent() != null: remove view");
			((ViewGroup) ooyalaSkinLayout.getParent()).removeView(ooyalaSkinLayout);
		}
		return ooyalaSkinLayout;
	}
}

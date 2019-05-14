package com.ooyala.sample.interfaces;

import androidx.annotation.Nullable;
import com.ooyala.android.OoyalaPlayer;

public interface Player {
	void play();

	void play(int playHeadTime);

	void seekTo(int miliseconds);

	void pause();

	boolean isPlaying();

	boolean isPauseNeeded();

	int getPlayheadTime();

    @Nullable
    OoyalaPlayer.State getState();
}

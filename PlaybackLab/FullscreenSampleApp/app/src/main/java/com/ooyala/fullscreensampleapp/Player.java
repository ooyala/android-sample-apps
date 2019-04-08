package com.ooyala.fullscreensampleapp;

public interface Player {
	void play();

	void play(int playHeadTime);

	void seekTo(int miliseconds);

	void pause();

	void mutePlayer(boolean mute);

	boolean isPlayerMuted();

	boolean isPlaying();

	boolean isInitialised();

	int getPlayheadTime();
}

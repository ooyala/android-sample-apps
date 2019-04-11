package com.ooyala.fullscreensampleapp;

public interface Player {
	void play();

	void play(int playHeadTime);

	void seekTo(int miliseconds);

	void pause();

	boolean isPlaying();

	int getPlayheadTime();
}

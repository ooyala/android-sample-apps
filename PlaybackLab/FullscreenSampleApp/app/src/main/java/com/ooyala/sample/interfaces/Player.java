package com.ooyala.sample.interfaces;

public interface Player {
	void play();

	void play(int playHeadTime);

	void seekTo(int miliseconds);

	void pause();

	boolean isPlaying();

	int getPlayheadTime();
}

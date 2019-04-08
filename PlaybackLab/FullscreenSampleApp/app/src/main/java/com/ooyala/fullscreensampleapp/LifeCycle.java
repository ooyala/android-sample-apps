package com.ooyala.fullscreensampleapp;

public interface LifeCycle {
	void onStart();

	void onStop();

	void onPause();

	void onResume();

	void onDestroy();

	void onBackPressed();
}

package com.ooyala.sample.interfaces;

public interface LifeCycle {
	void onStart();

	void onStop();

	void onPause();

	void onResume();

	void onDestroy();

	void onBackPressed();
}

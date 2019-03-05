package com.ooyala.sample;

import android.content.Context;

import com.google.android.exoplayer2.upstream.cache.Cache;
import com.ooyala.android.offline.VideoCache;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
/**
 * Placeholder application to facilitate overriding Application methods for debugging and testing.
 */
public class DemoApplication extends MultiDexApplication {
	private static final String DOWNLOAD_CONTENT_DIRECTORY = "downloads";

	public synchronized Cache getDownloadCache() {
		return VideoCache.getInstance(this, DOWNLOAD_CONTENT_DIRECTORY);
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}
}

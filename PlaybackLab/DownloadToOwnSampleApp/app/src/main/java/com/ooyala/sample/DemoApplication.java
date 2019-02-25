package com.ooyala.sample;

import android.app.Application;

import com.google.android.exoplayer2.upstream.cache.Cache;
import com.ooyala.android.offline.VideoCache;

/**
 * Placeholder application to facilitate overriding Application methods for debugging and testing.
 */
public class DemoApplication extends Application {
	private static final String DOWNLOAD_CONTENT_DIRECTORY = "downloads";

	public synchronized Cache getDownloadCache() {
		return VideoCache.getInstance(this, DOWNLOAD_CONTENT_DIRECTORY);
	}
}

package com.ooyala.sample;

import android.content.Context;

import com.google.android.exoplayer2.upstream.cache.Cache;
import com.ooyala.android.offline.TaskInfo;
import com.ooyala.android.offline.VideoCache;

import java.util.HashMap;
import java.util.Map;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
/**
 * Placeholder application to facilitate overriding Application methods for debugging and testing.
 */
public class DemoApplication extends MultiDexApplication {
	private static final String DOWNLOAD_CONTENT_DIRECTORY = "downloads";
	private static Map<String, TaskInfo> DOWNLOAD_TASKS = new HashMap<>();

	public synchronized Cache getDownloadCache() {
		return VideoCache.getInstance(this, DOWNLOAD_CONTENT_DIRECTORY);
	}

	public void addDownloadTask(String embedCode, TaskInfo info) {
		DOWNLOAD_TASKS.put(embedCode, info);
	}

	public TaskInfo retrieveCurrentTaskInfo(String embedCode) {
		return DOWNLOAD_TASKS.get(embedCode);
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}
}

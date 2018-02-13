/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ooyala.sample;

import android.app.Application;

import com.google.android.exoplayer2.BuildConfig;
import com.google.android.exoplayer2.source.dash.manifest.DashManifest;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;

/**
 * Placeholder application to facilitate overriding Application methods for debugging and testing.
 * The class is created to simplify the code.
 */
public class DemoApplication extends Application {

  protected static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
  protected String userAgent;
  protected DashManifest dashManifest;

  @Override
  public void onCreate() {
    super.onCreate();
    userAgent = Util.getUserAgent(this, "ExoPlayerDemo");
  }

  public boolean useExtensionRenderers() {
    return BuildConfig.FLAVOR.equals("withExtensions");
  }

  /**
   * Produces DataSource factory instances through that media data is loaded.
   *
   * @param bandwidthMeter null or {@link DefaultBandwidthMeter}
   * @return new instance of {@link DataSource.Factory}
   */
  public DataSource.Factory buildDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
    return new DefaultDataSourceFactory(this, bandwidthMeter,
      buildHttpDataSourceFactory(bandwidthMeter));
  }

  /**
   * Produces HttpDataSource factory instances.
   *
   * @param bandwidthMeter null or {@link DefaultBandwidthMeter}
   * @return new instance of {@link HttpDataSource.Factory}
   */
  public HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
    return new DefaultHttpDataSourceFactory(userAgent, bandwidthMeter);
  }

  /**
   * Returns a new DataSource factory.
   *
   * @param useBandwidthMeter Whether to set {@link #BANDWIDTH_METER} as a listener to the new
   *                          DataSource factory.
   * @return A new DataSource factory.
   */
  public DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {
    return buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
  }

  /**
   * Returns a new HttpDataSource factory.
   *
   * @param useBandwidthMeter Whether to set {@link #BANDWIDTH_METER} as a listener to the new
   *                          DataSource factory.
   * @return A new HttpDataSource factory.
   */
  public HttpDataSource.Factory buildHttpDataSourceFactory(boolean useBandwidthMeter) {
    return buildHttpDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
  }

  /**
   * Returns a new DefaultDataSourceFactory factory.
   *
   * @return new instance of {@link DefaultDataSourceFactory}
   */
  protected DefaultDataSourceFactory buildDefaultDataSourceFactory() {
    return new DefaultDataSourceFactory(this, userAgent);
  }
}

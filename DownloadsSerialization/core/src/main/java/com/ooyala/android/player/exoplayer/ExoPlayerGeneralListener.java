package com.ooyala.android.player.exoplayer;

import com.google.android.exoplayer2.drm.DefaultDrmSessionManager;
import com.google.android.exoplayer2.source.AdaptiveMediaSourceEventListener;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.text.TextRenderer;

interface ExoPlayerGeneralListener extends

    /**
     * Listener of ExtractorMediaSource events.
     * Provides one period that loads data from a Uri and extracted using an Extractor.
     */
    ExtractorMediaSource.EventListener,

    /**
     * Interface for callbacks to be notified of adaptive MediaSource events.
     */
    AdaptiveMediaSourceEventListener,

    /**
     * Listener of DefaultDrmSessionManager events.
     * A DrmSessionManager that supports playbacks using MediaDrm.
     */
    DefaultDrmSessionManager.EventListener,

    /**
     * Receives output from a TextRenderer.
     */
    TextRenderer.Output {
}

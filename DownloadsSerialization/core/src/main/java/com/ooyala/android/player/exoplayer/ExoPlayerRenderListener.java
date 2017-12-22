package com.ooyala.android.player.exoplayer;

import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.metadata.MetadataRenderer;
import com.google.android.exoplayer2.video.VideoRendererEventListener;

interface ExoPlayerRenderListener extends

    /**
     * Listener of audio Renderer events.
     */
    AudioRendererEventListener,

    /**
     * Listener of video Renderer events.
     */
    VideoRendererEventListener,

    /**
     * Receives output from a MetadataRenderer.
     */
    MetadataRenderer.Output {
}

package com.ooyala.android.player.exoplayer;

import android.view.Surface;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.emsg.EventMessage;
import com.google.android.exoplayer2.metadata.id3.ApicFrame;
import com.google.android.exoplayer2.metadata.id3.CommentFrame;
import com.google.android.exoplayer2.metadata.id3.GeobFrame;
import com.google.android.exoplayer2.metadata.id3.Id3Frame;
import com.google.android.exoplayer2.metadata.id3.PrivFrame;
import com.google.android.exoplayer2.metadata.id3.TextInformationFrame;
import com.google.android.exoplayer2.metadata.id3.UrlLinkFrame;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.ooyala.android.util.DebugMode;

class EventLogger implements ExoPlayerRenderListener {

  private static final String TAG = EventLogger.class.getSimpleName();

  // Audio renderer event listener
  @Override
  public void onAudioEnabled(DecoderCounters counters) {
    DebugMode.logD(TAG, "Audio enabled");
  }

  @Override
  public void onAudioSessionId(int audioSessionId) {
    DebugMode.logD(TAG, "Audio session: id: " + audioSessionId);
  }

  @Override
  public void onAudioDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {
    DebugMode.logD(TAG, String.format("Audio decoder initialized: decoderName: %s, " +
            "initializedTimestampMs: %d, initializationDurationMs: %d",
        decoderName, initializedTimestampMs, initializationDurationMs));
  }

  @Override
  public void onAudioInputFormatChanged(Format format) {
    DebugMode.logD(TAG, "Audio input format changed: " + Format.toLogString(format));
  }

  @Override
  public void onAudioTrackUnderrun(int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {
    DebugMode.logD(TAG, String.format("Audio track underrun: bufferSize: %d, " +
            "bufferSizeMs: %d, elapsedSinceLastFeedMs: %d",
        bufferSize, bufferSizeMs, elapsedSinceLastFeedMs));
  }

  @Override
  public void onAudioDisabled(DecoderCounters counters) {
    DebugMode.logD(TAG, "Audio disabled");
  }

  // Video renderer event listener
  @Override
  public void onVideoEnabled(DecoderCounters counters) {
    DebugMode.logD(TAG, "Video enabled");
  }

  @Override
  public void onVideoDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {
    DebugMode.logD(TAG, String.format("Video decoder initialized: decoderName: %s, initializedTimestampMs: %d, initializationDurationMs: %d",
        decoderName, initializedTimestampMs, initializationDurationMs));
  }

  @Override
  public void onVideoInputFormatChanged(Format format) {
    DebugMode.logD(TAG, "Video input format changed: " + Format.toLogString(format));
  }

  @Override
  public void onDroppedFrames(int count, long elapsedMs) {
    DebugMode.logD(TAG, String.format("Dropped frames: count: %d, elapsedMs: %d", count, elapsedMs));
  }

  @Override
  public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
    DebugMode.logD(TAG, String.format("Video size changed: width: %d, height: %d, unappliedRotationDegrees: %d, pixelWidthHeightRatio: %f",
        width, height, unappliedRotationDegrees, pixelWidthHeightRatio));
  }

  @Override
  public void onRenderedFirstFrame(Surface surface) {
    DebugMode.logD(TAG, "Render first frame");
  }

  @Override
  public void onVideoDisabled(DecoderCounters counters) {
    DebugMode.logD(TAG, "Video disabled");
  }

  // MetadataRenderer event listener
  @Override
  public void onMetadata(Metadata metadata) {
    DebugMode.logD(TAG, "Metadata: " + getMetadataInfo(metadata));
  }

  private String getMetadataInfo(Metadata metadata) {
    String metadataInfo = "";
    for (int i = 0; i < metadata.length(); i++) {
      Metadata.Entry entry = metadata.get(i);
      if (entry instanceof TextInformationFrame) {
        TextInformationFrame textInformationFrame = (TextInformationFrame) entry;
        metadataInfo += String.format("Text information frame: id: %s, value: %s ", textInformationFrame.id, textInformationFrame.value);

      } else if (entry instanceof UrlLinkFrame) {
        UrlLinkFrame urlLinkFrame = (UrlLinkFrame) entry;
        metadataInfo += String.format("Url link frame: id: %s, url: %s ", urlLinkFrame.id, urlLinkFrame.url);

      } else if (entry instanceof PrivFrame) {
        PrivFrame privFrame = (PrivFrame) entry;
        metadataInfo += String.format("Priv frame: id: %s, owner: %s ", privFrame.id, privFrame.owner);

      } else if (entry instanceof GeobFrame) {
        GeobFrame geobFrame = (GeobFrame) entry;
        metadataInfo += String.format("Geob frame: id: %s, mimeType:  %s, filename: %s, description: %s ",
            geobFrame.id, geobFrame.mimeType, geobFrame.filename, geobFrame.description);

      } else if (entry instanceof ApicFrame) {
        ApicFrame apicFrame = (ApicFrame) entry;
        metadataInfo += String.format("Apic frame: id: %s, mimeType:  %s, description: %s ",
            apicFrame.id, apicFrame.mimeType, apicFrame.description);

      } else if (entry instanceof CommentFrame) {
        CommentFrame commentFrame = (CommentFrame) entry;
        metadataInfo += String.format("Comment frame: id: %s, language:  %s, description: %s ", commentFrame.id,
            commentFrame.language, commentFrame.description);

      } else if (entry instanceof Id3Frame) {
        Id3Frame id3Frame = (Id3Frame) entry;
        metadataInfo += String.format("Id3 frame: id: %s ", id3Frame.id);

      } else if (entry instanceof EventMessage) {
        EventMessage eventMessage = (EventMessage) entry;
        metadataInfo += String.format("Event message: scheme:  %s, id: %d, value: %s ",
            eventMessage.schemeIdUri, eventMessage.id, eventMessage.value);
      }
    }
    return metadataInfo;
  }

  static String getMediaSourceInfo(DataSpec dataSpec, int dataType, int trackType, Format trackFormat,
                                   long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs) {

    return String.format("dataSpec: %s, dataType: %d, trackType: %d, mediaStartTimeMs: %d, mediaEndTimeMs: %d, elapsedRealtimeMs: %d",
        dataSpec.toString(), dataType, trackType, mediaStartTimeMs, mediaEndTimeMs, elapsedRealtimeMs)
        + " bitrate: " + (trackFormat != null ? trackFormat.bitrate : "n/a");
  }
}

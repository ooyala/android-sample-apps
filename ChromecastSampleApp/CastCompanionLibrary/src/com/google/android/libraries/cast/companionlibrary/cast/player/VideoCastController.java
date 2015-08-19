/*
 * Copyright (C) 2015 Google Inc. All Rights Reserved.
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

package com.google.android.libraries.cast.companionlibrary.cast.player;

import com.google.android.gms.cast.MediaStatus;

import android.graphics.Bitmap;

/**
 * An interface that can be used to display a remote controller for the video that is playing on
 * the cast device.
 */
public interface VideoCastController {

    int CC_ENABLED = 1;
    int CC_DISABLED = 2;
    int CC_HIDDEN = 3;

    int NEXT_PREV_VISIBILITY_POLICY_HIDDEN = 1;
    int NEXT_PREV_VISIBILITY_POLICY_DISABLED = 2;
    int NEXT_PREV_VISIBILITY_POLICY_ALWAYS = 3;

    /**
     * Sets the bitmap for the album art
     */
    void setImage(Bitmap bitmap);

    /**
     * Sets the title
     */
    void setTitle(String text);

    /**
     * Sets the subtitle
     */
    void setSubTitle(String text);

    /**
     * Sets the playback state, and the idleReason (this is only used when the state is idle).
     * Values that can be passed to this method are from {@link MediaStatus}
     */
    void setPlaybackStatus(int state);

    /**
     * Assigns a {@link OnVideoCastControllerListener} listener to be notified of the changes in
     * the {@link }VideoCastController}
     */
    void setOnVideoCastControllerChangedListener(OnVideoCastControllerListener listener);

    /**
     * Sets the type of stream. {@code streamType} can be
     * {@link com.google.android.gms.cast.MediaInfo#STREAM_TYPE_LIVE} or
     * {@link com.google.android.gms.cast.MediaInfo#STREAM_TYPE_BUFFERED}
     */
    void setStreamType(int streamType);

    /**
     * Updates the position and total duration for the seekbar that presents the progress of media.
     * Both of these need to be provided in milliseconds.
     */
    void updateSeekbar(int position, int duration);

    /**
     * Adjust the visibility of control widgets on the UI.
     */
    void updateControllersStatus(boolean enabled);

    /**
     * Can be used to show a loading icon during processes that could take time.
     */
    void showLoading(boolean visible);

    /**
     * Closes the activity related to the UI.
     */
    void closeActivity();

    /**
     * This can be used to adjust the UI for playback of live versus pre-recorded streams. Certain
     * UI widgets may need to be updated when playing a live stream. For example, the progress bar
     * may not be needed for a live stream while it may be required for a pre-recorded stream.
     */
    void adjustControllersForLiveStream(boolean isLive);

    /**
     * Updates the visual status of the Closed Caption icon. Possible states are provided by
     * <code>CC_ENABLED, CC_DISABLED, CC_HIDDEN</code>
     */
    void setClosedCaptionState(int status);

    void onQueueItemsUpdated(int queueLength, int position);

    void setNextPreviousVisibilityPolicy(int policy);
}

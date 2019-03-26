package com.skin.ooyalaskinsampleapplication.ooyala;

/**
 * Created by anshul.s on 15-02-2017.
 */

public interface MultiMediaPlayListener {

    enum VideoMilestone {
        TWENTY_FIVE_PERCENT,
        FIFTY_PERCENT,
        SEVENTY_FIVE_PERCENT
    }

    void startedPlaying();

    void pausedPlaying();

    void stoppedPlaying();

    void completedPlaying();

    void headTimeChanged(long playHeadTime, long totalDuration);

    void completedMilestone(VideoMilestone videoMilestone);

    void seekStarted();

    void seekCompleted();

    void adStarted();

    void adCompleted();

    void adSkipped();

    void errorOccured(String message);
}


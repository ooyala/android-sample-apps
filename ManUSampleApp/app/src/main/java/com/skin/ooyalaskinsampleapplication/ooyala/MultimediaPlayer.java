package com.skin.ooyalaskinsampleapplication.ooyala;

/**
 * Created by anshul.s on 15-02-2017.
 */

public interface MultimediaPlayer {

    void play(String contentId);

    void play(int playHeadTime, String contentId);

    void pause();

    void suspend();

    void resume();

    boolean isPlaying();

    boolean isInitialised();

    void stop();

    void seekTo(int miliseconds);

    void mutePlayer(boolean mute);

    boolean isPlayerMuted();


}


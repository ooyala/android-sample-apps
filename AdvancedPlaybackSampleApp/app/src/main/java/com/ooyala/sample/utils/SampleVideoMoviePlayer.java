package com.ooyala.sample.utils;

import com.ooyala.android.player.MoviePlayer;
import com.ooyala.android.player.StreamPlayer;


public class SampleVideoMoviePlayer extends MoviePlayer {

    @Override
    protected StreamPlayer createStreamPlayer() {
        return new SampleVideoStreamPlayer();
    }

}

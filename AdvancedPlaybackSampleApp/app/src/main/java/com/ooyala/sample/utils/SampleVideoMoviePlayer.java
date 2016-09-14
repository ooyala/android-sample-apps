package com.ooyala.sample.utils;

import com.ooyala.android.player.MoviePlayer;
import com.ooyala.android.player.StreamPlayer;

/**
 * Created by achaudhari on 9/9/16.
 */
public class SampleVideoMoviePlayer extends MoviePlayer {

    @Override
    protected StreamPlayer createStreamPlayer() {
        return new SampleVideoStreamPlayer();
    }

}

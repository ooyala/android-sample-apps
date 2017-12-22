package com.ooyala.android;

/**
 * Created by ukumar on 8/30/16.
 */
public class SeekInfo {

    private final double seekStart;

    private final double seekEnd;
    private final double totalDuration;

    public SeekInfo(double seekStart,double seekEnd, double totalDuration) {
        this.seekStart = seekStart;
        this.seekEnd = seekEnd;
        this.totalDuration = totalDuration;
    }

    public double getSeekStart() {
        return seekStart;
    }

    public double getSeekEnd() {
        return seekEnd;
    }

    public double getTotalDuration() {
        return totalDuration;
    }
}

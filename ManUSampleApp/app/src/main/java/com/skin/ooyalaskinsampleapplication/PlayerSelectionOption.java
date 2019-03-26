package com.skin.ooyalaskinsampleapplication;

/**
 * This is used to store information of a sample activity for use in a Map or List
 */
public class PlayerSelectionOption {
    private String embedCode;
    private String pcode;
    private String domain;
    private boolean isAutoPlay;
    private State state = new State();
    private int playedHeadTime;


    public PlayerSelectionOption(String embedCode, String pcode, String domain) {
        this.embedCode = embedCode;

        this.pcode = pcode;
        this.domain = domain;
    }

    public boolean isAutoPlay() {
        return isAutoPlay;
    }

    public void setAutoPlay(boolean autoPlay) {
        isAutoPlay = autoPlay;
    }

    /**
     * Get the pcode for this sample
     *
     * @return the pcode
     */
    public String getPcode() {
        return pcode;
    }

    /**
     * Get the domain for this sample
     *
     * @return the domain
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Get the embed code for this sample
     *
     * @return the embed code
     */
    public String getEmbedCode() {
        return this.embedCode;
    }


    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public int getPlayedHeadTime() {
        return playedHeadTime;
    }

    public void setPlayedHeadTime(int playedHeadTime) {
        this.playedHeadTime = playedHeadTime;
    }
}

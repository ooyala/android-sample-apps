package com.ooyala.sample.utils;

/**
 *  A model object for video content.
 *
 */

public class VideoItem {

    /**
     *  An array of string tags used to target ad campaigns.
     */
    private String[] tags = null;

    /**
     *  The title of this content, for displaying in the content list.
     */
    private String contentTitle;

    /**
     *  A identifier for this content that used to identify this content in
     *  the Pulse session request.
     */
    private String contentId;

    /**
     *  The URL to the video content.
     */
    private String contentUrl;

    /**
     *  An array of numbers of positions (in seconds) where midroll ad breaks
     *  may occur.
     */
    private float[] midrollPositions = null;

    /**
     *  A string category used to target ad campaigns.
     */
    private String category;

    /**
     * The embed code for the video to be played.
     */
    private String contentCode;

    ///////////////////Default Constructor//////////////////////////////////

    public VideoItem() {
        super();
    }

    ///////////////////Setter/Getter methods////////////////////////////////
    
    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String getContentTitle() {
        return contentTitle;
    }

    public void setContentTitle(String contentTitle) {
        this.contentTitle = contentTitle;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public float[] getMidrollPositions() {
        return midrollPositions;
    }

    public void setMidrollPositions(float[] midrollPositions) {
        this.midrollPositions = midrollPositions;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContentCode() {
        return contentCode;
    }

    public void setContentCode(String contentCode) {
        this.contentCode = contentCode;
    }
}

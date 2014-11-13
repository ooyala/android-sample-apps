package com.ooyala.demo.vo;

import java.net.URLEncoder;
import java.util.Date;

public class VideoInfoVO {
    private String title;
    private String description;
    private String embedCode;
    private String thumbnail;
    private String encodedThumbnail;
    private int likes;
    private Date updated;
    private int plays;

    public VideoInfoVO() {
    }

    public VideoInfoVO(final String title, final String description, final String embedCode, final String thumbnail) {
        this.title = title;
        this.description = description;
        this.embedCode = embedCode;
        this.thumbnail = thumbnail;
        this.encodedThumbnail = URLEncoder.encode(thumbnail);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getEmbedCode() {
        return embedCode;
    }

    public void setEmbedCode(final String embedCode) {
        this.embedCode = embedCode;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(final String thumbnail) {
        this.thumbnail = thumbnail;
        this.encodedThumbnail = URLEncoder.encode(thumbnail);
    }

    public String getEncodedThumbnail() {
        return encodedThumbnail;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(final int likes) {
        this.likes = likes;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(final Date updated) {
        this.updated = updated;
    }

    public int getPlays() {
        return plays;
    }

    public void setPlays(final int plays) {
        this.plays = plays;
    }
}

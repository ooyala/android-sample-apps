package com.ooyala.sample.utils;

/**
 * Created by ileanapadilla on 11/15/17.
 */

public class DownloadableAsset {

    private int status;
    private String name;
    private String embedCode;
    private String pCode;

    public DownloadableAsset(String name, String embedCode, String pCode){
        this.status = 0;
        this.name = name;
        this.embedCode = embedCode;
        this.pCode = pCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getpCode() {
        return pCode;
    }

    public void setpCode(String pCode) {
        this.pCode = pCode;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getEmbedCode() {
        return embedCode;
    }

    public void setEmbedCode(String embedCode) {
        this.embedCode = embedCode;
    }

}

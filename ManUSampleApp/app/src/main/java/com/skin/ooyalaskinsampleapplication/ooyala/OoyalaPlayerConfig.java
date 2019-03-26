package com.skin.ooyalaskinsampleapplication.ooyala;

/**
 * Created by anshul.s on 15-02-2017.
 */

public class OoyalaPlayerConfig {
    private String embedCode;
    private MultiMediaPlayListener multiMediaPlayListener;
    private String pcode;
    private String domain;


    public OoyalaPlayerConfig(String embedCode, String pcode, String domain, MultiMediaPlayListener multiMediaPlayListener) {
        this.embedCode = embedCode;
        this.multiMediaPlayListener = multiMediaPlayListener;
        this.pcode = pcode;
        this.domain = domain;
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

    /**
     * Get the MultiMediaPlayListener to use for this sample
     *
     * @return the activity to launch
     */
    public MultiMediaPlayListener getMultiMediaPlayListener() {
        return this.multiMediaPlayListener;
    }
}
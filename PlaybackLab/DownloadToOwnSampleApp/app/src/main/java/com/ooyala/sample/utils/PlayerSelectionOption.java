package com.ooyala.sample.utils;

import android.app.Activity;

/**
 * This is used to store information of a sample activity for use in a Map or List
 *
 */
public class PlayerSelectionOption {
  private String embedCode;
  private Class <? extends Activity> activity;
  private String pcode;
  private String domain;
  private String apiKey;
  private String secretKey;
  private String accountId;

  public PlayerSelectionOption(String embedCode, String pcode, String apiKey, String secretKey, String accountId, String domain, Class<? extends Activity> activity) {
    this.embedCode = embedCode;
    this.activity = activity;
    this.pcode = pcode;
    this.domain = domain;
    this.apiKey = apiKey;
    this.secretKey = secretKey;
    this.accountId = accountId;
  }

  /**
   * Get the pcode for this sample
   * @return the pcode
   */
  public String getPcode() {
    return pcode;
  }

  /**
   * Get the domain for this sample
   * @return the domain
   */
  public String getDomain() {
    return domain;
  }

  /**
   * Get the embed code for this sample
   * @return the embed code
   */
  public String getEmbedCode() {
    return this.embedCode;
  }

  /**
   * Get the apikey for this sample
   * @return the apikey
   */
  public String getApiKey() {
    return apiKey;
  }

  /**
   * Get the secretkey for this sample
   * @return the secretKey
   */
  public String getSecretKey() {
    return secretKey;
  }

  /**
   * Get the accountid for this sample
   * @return the accountId
   */
  public String getAccountId() {
    return accountId;
  }

  /**
   * Get the activity to use for this sample
   * @return the activity to launch
   */
  public Class <? extends Activity> getActivity() {
    return this.activity;
  }
}

package com.ooyala.android.offline;

import com.ooyala.android.EmbedTokenGenerator;

import java.io.File;

/**
 * A class that holds offline DASH options to download/store DASH videos to local folders on the device
 */
public class DashOptions {

  /**
   * A Builder class
   */
  public static class Builder {
    // mandatory fields
    private String pcode;
    private String embedCode;
    private String domain;
    private File folder;

    // optional fields
    private EmbedTokenGenerator tokenGenerator;
    private int connectionTimeoutInMillisecond;
    private int readTimeoutInMillisecond;
    private int bitrate;

    /**
     * Create a builder
     * @param pcode - the pcode
     * @param embedCode - the embed code
     * @param domain - the domain
     * @param folder - the folder for offline DASH to store
     */
    public Builder(String pcode, String embedCode, String domain, File folder) {
      this.pcode = pcode;
      this.embedCode = embedCode;
      this.domain = domain;
      this.folder = folder;

      this.connectionTimeoutInMillisecond = 10 * 1000;
      this.readTimeoutInMillisecond = 10 * 1000;
      this.bitrate = Integer.MAX_VALUE;
    }

    /**
     * set the secure URL generator, used if OPT is enabled for the stream
     * @param generator the embed token generator
     * @return the builder
     */
    public Builder setEmbedTokenGenerator(EmbedTokenGenerator generator) {
      this.tokenGenerator = generator;
      return this;
    }

    /**
     * set connection timeout, in milliseconds, default value is 10 seconds.
     * @param timeout
     * @return the builder
     */
    public Builder setConnectionTimeout(int timeout) {
      this.connectionTimeoutInMillisecond = timeout;
      return this;
    }

    /**
     * set read timeout, in milliseconds, default value is 10 seconds.
     * @param timeout
     * @return the builder
     */
    public Builder setReadTimeout(int timeout) {
      this.readTimeoutInMillisecond = timeout;
      return this;
    }

    /**
     * Set the desired video bitrate to be downloaded, optional
     * if the desired bitrate video stream does not exist, the stream that has the closest bitrate
     * is chosen
     * @param bitrate the desired video bitrate, default is the highest bitrate
     * @return the builder
     */
    public Builder setBitrate(int bitrate) {
      this.bitrate = bitrate;
      return this;
    }

    /**
     * Build the option
     * @return the DASH option object
     */
    public DashOptions build() {
      return new DashOptions(pcode, embedCode, domain, tokenGenerator, folder, bitrate, connectionTimeoutInMillisecond, readTimeoutInMillisecond);
    }
  }

  private final String pcode;
  private final String embedCode;
  private final String domain;
  private final File folder;

  // optional fields
  private final EmbedTokenGenerator tokenGenerator;
  private final int connectionTimeoutInMillisecond;
  private final int readTimeoutInMillisecond;
  private final int bitrate;

  /**
   * the private constructor
   * @param pcode
   * @param embedCode
   * @param domain
   * @param generator
   * @param folder
   * @param bitrate
   * @param connectionTimeout
   * @param readTimeout
   */
  private DashOptions(String pcode,
                      String embedCode,
                      String domain,
                      EmbedTokenGenerator generator,
                      File folder,
                      int bitrate,
                      int connectionTimeout,
                      int readTimeout) {

    this.pcode = pcode;
    this.embedCode = embedCode;
    this.domain = domain;
    this.tokenGenerator = generator;
    this.folder = folder;
    this.bitrate = bitrate;
    this.connectionTimeoutInMillisecond = connectionTimeout;
    this.readTimeoutInMillisecond = readTimeout;
  }

  /**
   *
   * @return the pcode
   */
  public String getPcode() {
    return pcode;
  }

  /**
   * @return the embedCode
   */
  public String getEmbedCode() {
    return embedCode;
  }

  /**
   *
   * @return the domain string
   */
  public String getDomain() {
    return domain;
  }

  /**
   *
   * @return the embed token generator
   */
  public EmbedTokenGenerator getTokenGenerator() {
    return tokenGenerator;
  }

  /**
   * @return the local folder for the DASH streams
   */
  public File getFolder() {
    return folder;
  }

  /**
   * @return the desired bitrate
   */
  public int getBitrate() {
    return bitrate;
  }

  /**
   *
   * @return connection timeout in millisecond
   */
  public int getConnectionTimeout() {
    return connectionTimeoutInMillisecond;
  }

  /**
   *
   * @return read timeout in millisecond
   */
  public int getReadTimeout() {
    return readTimeoutInMillisecond;
  }

}

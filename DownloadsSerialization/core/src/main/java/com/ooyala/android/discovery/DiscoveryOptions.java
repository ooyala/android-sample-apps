package com.ooyala.android.discovery;

/**
 * Created by zchen on 12/9/15.
 */
public class DiscoveryOptions {

  public static enum Type {
    Momentum,
    Popular,
    SimilarAssets
  };

  /**
   * Supports a fluid syntax for configuration.
   */
  public static class Builder {
    private Type type;
    private long limit;
    private long timeoutInMilliSecond;

    /**
     * The constructor
     */
    public Builder() {
      type = Type.SimilarAssets;
      limit = 10;
      timeoutInMilliSecond = 10000;
    }

    /**
     * Set discovery type.
     * @param type - The discovery type, the default value is similarassets
     * @return the Builder to continue constructing the CastOptions
     */
    public Builder setType(Type type) {
      this.type = type;
      return this;
    }

    /**
     * Set the discovery result limits
     * @param limit - the max number of records discovery should provide
     * The default value is 10.
     * @return the Builder to continue constructing the CastOptions
     */
    public Builder setLimit(long limit) {
      this.limit = limit;
      return this;
    }

    /**
     * Set the timeout in milisecond
     * this will enable CastCompanionLibrary's VideoCastManager.FEATURE_NOTIFICATION
     * @param timeout - the timeout value in miliseconds
     * The default value is 10 seconds.
     * @return the Builder to continue constructing the CastOptions
     */
    public Builder setTimeout(long timeout) {
      this.timeoutInMilliSecond = timeout;
      return this;
    }

    public DiscoveryOptions build() {
      return new DiscoveryOptions(type, limit, timeoutInMilliSecond);
    }
  }

  private final Type type;
  private final long limit;
  private final long timeoutInMilliSecond;

  private DiscoveryOptions(Type type, long limit, long timeout) {
    this.type = type;
    this.limit = limit;
    this.timeoutInMilliSecond = timeout;
  }

  /**
   * get the discovery type
   * @return the type
   */
  public Type getType() {
    return type;
  }

  /**
   * get the max number of records per request
   * @return the limit
   */
  public long getLimit() {
    return limit;
  }

  /**
    * get the timout value in milliseconds
    * @return the timeout
    */
  public long getTimoutInMilliSeconds() {
    return timeoutInMilliSecond;
  }

}

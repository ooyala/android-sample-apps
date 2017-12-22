package com.ooyala.android.analytics;

import com.ooyala.android.util.DebugMode;
import com.ooyala.android.DeviceInfo;

/**
 * This class configures all the IQ Analytics related information that needs to be configured with default values or customer given values.
 *
 * Use IqConfiguration.Builder() to configure your IQ Analytics as needed.
 */
public class IqConfiguration {
    private static final String TAG = IqConfiguration.class.getSimpleName();
    private final String playerID;
    private final String analyticsJSURL;
    private final String domain;
    private final String backendEndpointURL;
    private final DeviceInfo deviceInfo;

    /**
     * Initialize an IqConfiguration object with all configurations.
     * You should not be creating one of these directly, but use the Builder() class
     * @param playerID
     * @param analyticsJSURL
     * @param domain
     * @param backendEndpointURL
     * @param deviceInfo
     */
    public IqConfiguration(String playerID,
                           String analyticsJSURL,
                           String domain,
                           String backendEndpointURL,
                           DeviceInfo deviceInfo) {
        this.playerID = playerID;
        this.analyticsJSURL = analyticsJSURL;
        this.domain = domain;
        this.backendEndpointURL = backendEndpointURL;
        this.deviceInfo = deviceInfo;
    }

    /**
     * Used to retrieve the player ID
     * @return playerID
     */
    public String getPlayerID() {
        return playerID;
    }

    /**
     * Used to retrieve Analytics JS URL
     * @return analyticsJSURL
     */
    public String getAnalyticsJSURL() {
        return analyticsJSURL;
    }

    /**
     * Used to retrieve the domain
     * @return domain
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Used to retrieve Backend Endpoint URL
     * @return backendEndpointURL
     */
    public String getBackendEndpointURL() {
        return backendEndpointURL;
    }

    /**
     * Used to retrieve Device Info
     * @return deviceInfo
     */
    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    /**
     * Builds the object of IQ Analytics configurations.
     */
    public static class Builder {

        private String playerID;
        private String analyticsJSURL;
        private String domain;
        private String backendEndpointURL;
        private DeviceInfo deviceInfo;

        /**
         * Constructor. sets default values to the fields.
         */
        public Builder() {
            this.playerID = "ooyala_android";
            this.analyticsJSURL = "http://analytics.ooyala.com/static/v3/analytics.js";
            this.domain = null;
            this.backendEndpointURL = "http://l.ooyala.com/v3/analytics/events";
            this.deviceInfo = new DeviceInfo();
        }

        /**
         * The PlayerID value used in IQ Analytics reporting
         * @param playerID
         * @return the Builder to continue fluid construction
         */
        public Builder setPlayerID(String playerID) {
            this.playerID = playerID;
            return this;
        }

        /**
         * The URL where Ooyala SDK downloads analytics.js, the IQ Analytics reporting library.
         * @param analyticsJSURL
         * @return the Builder to continue fluid construction
         */
        public Builder setAnalyticsJSURL(String analyticsJSURL) {
            this.analyticsJSURL = analyticsJSURL;
            return this;
        }

        /**
         * The domain/traffic source url used for IQ Analytics reporting.
         * If null, IQ will use the domain provided in the OoyalaPlayer initializer.
         * @param domain
         * @return the Builder to continue fluid construction
         */
        public Builder setDomain(String domain) {
            this.domain = domain;
            return this;
        }

        /**
         * The analytics endpoint used for reporting.
         * @param backendEndpointURL
         * @return the Builder to continue fluid construction
         */
        public Builder setBackendEndpointURL(String backendEndpointURL) {
            this.backendEndpointURL = backendEndpointURL;
            return this;
        }

        /**
         * The analytics endpoint used for reporting.
         * @param deviceInfo
         * @return the Builder to continue fluid construction
         */
        public Builder setDeviceInfo(DeviceInfo deviceInfo) {
            this.deviceInfo = deviceInfo;
            return this;
        }

        /**
         * Finalize the Builder into an IqConfiguration class
         * @return the fully qualified IqConfiguration class
         */
        public IqConfiguration build(){
            return new IqConfiguration(playerID,
                    analyticsJSURL,
                    domain,
                    backendEndpointURL,
                    deviceInfo);
        }
    }

    /**
     * Provides the default IqConfiguration
     * @return the default configuration
     */
    public static IqConfiguration getDefaultIqConfiguration() {
            return new Builder().build();
    }

    /**
     * Log all of the parameters that are part of the Options class
     */
    public void logOptionsData() {
        if (deviceInfo != null){
            DebugMode.logD(TAG,
                    "this.playerID = " + playerID + "\n" +
                    "this.analyticsJSURL = " + analyticsJSURL + "\n" +
                    "this.domain = " + domain + "\n" +
                    "this.backendEndpointURL = " + backendEndpointURL + "\n" +
                    deviceInfo.toString());
        } else {
            DebugMode.logD(TAG,
                    "this.playerID = " + playerID + "\n" +
                    "this.analyticsJSURL = " + analyticsJSURL + "\n" +
                    "this.domain = " + domain + "\n" +
                    "this.backendEndpointURL = " + backendEndpointURL);
        }
    }
}

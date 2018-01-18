package com.ooyala.android;

import android.os.Build;

public class DeviceInfo {
    private String browser;  /** The browser where the player is being viewed: android */
    private String browserVersion;   /** The browser version where the player is being viewed: 0 */
    private String os;   /** Name of the operating system */
    private String osVersion;    /** Version of the operating system */
    private String deviceType;   /** 'tablet' | 'mobile' | 'smarttv' | 'tablet/mobile' */
    private String deviceBrand;  /** Brand name of the device: android */
    private String model;    /** Model name of the device, for example: Fire TV */

    public DeviceInfo(){
        browser = "android_sdk";
        browserVersion = "0";
        os = "android";
        osVersion = Build.VERSION.RELEASE;
        deviceType = "tablet/mobile";
        deviceBrand = Build.BRAND;
        model = Build.MODEL;
    }

    public DeviceInfo(String browser, String browserVersion, String os, String osVersion, String deviceType, String deviceBrand, String model){
        this.browser = browser;
        this.browserVersion = browserVersion;
        this.os = os;
        this.osVersion = osVersion;
        this.deviceType = deviceType;
        this.deviceBrand = deviceBrand;
        this.model = model;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getBrowserVersion() {
        return browserVersion;
    }

    public void setBrowserVersion(String browserVersion) {
        this.browserVersion = browserVersion;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceBrand() {
        return deviceBrand;
    }

    public void setDeviceBrand(String deviceBrand) {
        this.deviceBrand = deviceBrand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }


    @Override
    public String toString() {
        return "this.deviceInfo {" + "\n" +
            "  browser = " + browser + "\n" +
            "  browserVersion = " + browserVersion + "\n" +
            "  os = " + os + "\n" +
            "  osVersion = " + osVersion + "\n" +
            "  deviceType = " + deviceType + "\n" +
            "  deviceBrand = " + deviceBrand + "\n" +
            "  model = " + model + "\n" +
            "}";
    }
}
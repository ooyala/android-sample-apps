package com.ooyala.android;

import com.ooyala.android.util.DebugMode;
import org.json.JSONException;
import org.json.JSONObject;
class UserInfo {
  private static final String TAG = UserInfo.class.getSimpleName();
  static final String KEY_USER_ACCOUNT_ID = "account_id";
  static final String KEY_USER_CONTINENT = "continent";
  static final String KEY_USER_COUNTRY = "country";
  static final String KEY_USER_DEVICE = "device";
  static final String KEY_USER_DOMAIN = "domain";
  static final String KEY_USER_IPADDRESS = "ip_address";
  static final String KEY_USER_LANGUAGE = "language";
  static final String KEY_USER_TIMEZONE = "timezone";

  public String accountId; /** The account ID of the authorized user */
  public String continent; /** The continent of origin for the authorization*/
  public String country; /** The country of origin for the authorization */
  public String device; /** The device provided to the Authorization*/
  public String domain; /** The domain provided to the Authorization*/
  public String ipAddress; /** The IP address of the Authorized user */
  public String language; /** The language provided to the Authorization */
  public String timezone; /** The timezone of the authorized user */

  public UserInfo(JSONObject data) {
    try {
      if(!data.isNull(KEY_USER_ACCOUNT_ID)) {
        accountId = data.getString(KEY_USER_ACCOUNT_ID);
      }
      if(!data.isNull(KEY_USER_CONTINENT)) {
        continent = data.getString(KEY_USER_CONTINENT);
      }
      if(!data.isNull(KEY_USER_COUNTRY)) {
        country = data.getString(KEY_USER_COUNTRY);
      }
      if(!data.isNull(KEY_USER_DEVICE)) {
        device = data.getString(KEY_USER_DEVICE);
      }
      if(!data.isNull(KEY_USER_DOMAIN)) {
        domain = data.getString(KEY_USER_DOMAIN);
      }
      if(!data.isNull(KEY_USER_LANGUAGE)) {
        ipAddress = data.getString(KEY_USER_IPADDRESS);
      }
      if(!data.isNull(KEY_USER_LANGUAGE)) {
        language = data.getString(KEY_USER_LANGUAGE);
      }
      if(!data.isNull(KEY_USER_TIMEZONE)) {
        timezone = data.getString(KEY_USER_TIMEZONE);
      }
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      DebugMode.logE( TAG, "Caught!", e );
    }
  }

  public String getAccountId() {
     return accountId;
  }
  public String getContient() {
    return continent;
 }
  public String getCountry() {
    return country;
 }
  public String getDevice() {
    return device;
 }
  public String getDomain() {
    return domain;
 }
  public String getIpAddress() {
    return ipAddress;
 }
  public String getLanguage() {
    return language;
 }
 public String getTimezone() {
    return timezone;
 }

}

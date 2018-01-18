package com.ooyala.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.ooyala.android.util.DebugMode;

import java.util.Date;

/**
 * A helper class to manage setting and getting of the Auth token
 */
class AuthTokenManager {
  private static final String TAG = AuthTokenManager.class.getSimpleName();
  protected static final String KEY_AUTH_TOKEN_EXPIRES = "auth_token_expires";
  private String authToken; // ALWAYS use getters and setters for this
  private Date authTokenExpires;
  private Context context;
  private UserInfo userInfo;
  private int heartbeatInterval;

  public AuthTokenManager(Context context) {
    this.context = context;
    this.heartbeatInterval = 300;
  }


  void setAuthToken(String authToken) {
    this.authToken = authToken;
    if (this.context != null) {
      SharedPreferences preferences = this.context.getSharedPreferences(OoyalaPlayer.PREFERENCES_NAME, 4);
      SharedPreferences.Editor editor = preferences.edit();

      if (authToken == null) {
        editor.remove("authToken");
      } else {
        editor.putString("authToken", authToken);
        DebugMode.logD(TAG, "Auth Token Set");
      }
      editor.commit();
    }
  }

  /**
   * @see #getAuthTokenExpires()
   */
  public String getAuthToken() {
    if (authToken == null) {
      if (this.context != null) {
        SharedPreferences preferences = this.context.getSharedPreferences(OoyalaPlayer.PREFERENCES_NAME, 4);
        authToken = preferences.getString("authToken", "");
      } else {
        authToken = "";
      }
    }
    return authToken;
  }

  /**
   * Clear the auth token in the local cache and from the preferences if its expired
   * Perform this for general cleanup
   */
  public void clearAuthTokenIfExpired() {
    if (isAuthTokenExpired()) {
      DebugMode.logD(TAG, "Expired Auth Token - Clearing");
      setAuthToken(null);
    }
  }

  /**
   * @param authTokenExpires seconds since 1970 UTC.
   */
  void setAuthTokenExpires(Long authTokenExpires) {
    this.authTokenExpires = authTokenExpires == null ? null : new Date(authTokenExpires * 1000);
    if (this.context != null) {
      SharedPreferences preferences = this.context.getSharedPreferences(OoyalaPlayer.PREFERENCES_NAME, 4);
      SharedPreferences.Editor editor = preferences.edit();
      editor.putLong(KEY_AUTH_TOKEN_EXPIRES, this.authTokenExpires.getTime());
      editor.commit();
    }
  }

  /**
   * @return possibly null Date before which the authToken is valid, on and after which it is invalid.
   * @see #getAuthToken()
   */
  public Date getAuthTokenExpires() {
    if (authTokenExpires == null && this.context != null) {
      SharedPreferences preferences = this.context.getSharedPreferences(OoyalaPlayer.PREFERENCES_NAME, 4);
      Long l = preferences.getLong( KEY_AUTH_TOKEN_EXPIRES, -1 );
      if( l != -1 ) {
        authTokenExpires = new Date( l );
      }
    }
    return authTokenExpires;
  }

  public UserInfo getUserInfo() {
    return userInfo;
  }

  public void setUserInfo(UserInfo userInfo) {
    this.userInfo = userInfo;
  }

  public int getHeartbeatInterval() {
    return heartbeatInterval;
  }

  public void setHeartbeatInterval(int heartbeatInterval) {
    this.heartbeatInterval = heartbeatInterval;
  }

  /**
   * Check if AuthToken exists
   * @return true if it is set in the shared preferences, false otherwise
   */
  public boolean isAuthTokenSet() {
    return !TextUtils.isEmpty(getAuthToken());
  }

  /**
   * Checks the expiration of the authToken, and compares it to the current time.
   * @return true if token is expired, false otherwise
   */
  public boolean isAuthTokenExpired() {
    boolean expired = false;
    if (isAuthTokenSet()) {
      final Date expires = getAuthTokenExpires();
      if (expires != null) {
        expired = new Date().compareTo(expires) >= 0;
      }
    }
    return expired;
  }
}

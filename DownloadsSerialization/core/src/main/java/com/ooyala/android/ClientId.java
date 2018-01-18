package com.ooyala.android;

import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;

public class ClientId {
  private static String _clientId;
  private static final String CLIENT_ID_KEY = "clientId";
  private static final String TAG = ClientId.class.getName();
  
  /**
   * Get a client ID
   * @return the UUID that persists across launch
   */
  public static String getId(Context context) {
    if (_clientId == null || _clientId.length() <= 0) {
      _clientId = ClientId.loadOrCreateId(context);
    }
    return _clientId;
  }

  /**
   * Set the id that overrides the default value
   * Ooyala WILL NOT persist this id so user needs to call this
   * every time the app is launched
   * @param clientId the id string customer like to use
   */
  public static void setId(String clientId) {
    _clientId = clientId;
  }

  /**
   * Clear the id.
   * When getId is called, a new id will be generated
   */
  public static void resetId(Context context) {
    _clientId = "";
    ClientId.storeClientId(context, _clientId);
  }

  private static String loadOrCreateId(Context context) {
    SharedPreferences preferences = context.getSharedPreferences(
        OoyalaPlayer.PREFERENCES_NAME, 4);
    String id = preferences.getString(CLIENT_ID_KEY, "");
    if (id.length() <= 0) {
      id = UUID.randomUUID().toString();
      ClientId.storeClientId(context, id);
    }
    return id;
  }

  private static void storeClientId(Context context, String id) {
    SharedPreferences preferences = context.getSharedPreferences(
        OoyalaPlayer.PREFERENCES_NAME, 4);
    SharedPreferences.Editor editor = preferences.edit();
    editor.putString(CLIENT_ID_KEY, id);
    editor.commit();
  }

}

package com.ooyala.android.item;

import java.util.List;

import org.json.JSONObject;

/**
 * Stores the info and metatdata for the specified movie.
 *
 */
public interface AuthorizableItem extends JSONUpdatableItem {
  /**
   * Authorize response codes
   */
  public interface AuthCode {
    /** The authorization code was invalid */
    public static int UNKNOWN = -2;
    /** The authorization has not been requested for this item */
    public static int NOT_REQUESTED = -1;
    /** The minimum value for auth codes from the server */
    public static int MIN_AUTH_CODE = 0;
    /** The item is authorized */
    public static int AUTHORIZED = 0;
    /** The item's parent is unauthorized */
    public static int UNAUTHORIZED_PARENT = 1;
    /** The item is not authorized for this domain */
    public static int UNAUTHORIZED_DOMAIN = 2;
    /** The item is not authorized for this location */
    public static int UNAUTHORIZED_LOCATION = 3;
    /** The item has been requested before its flight time */
    public static int BEFORE_FLIGHT_TIME = 4;
    /** The item has been requested after its flight time */
    public static int AFTER_FLIGHT_TIME = 5;
    /** The item has been requested outside of its recurring flight time */
    public static int OUTSIDE_RECURRING_FLIGHT_TIMES = 6;
    /** The item's embed code is invalid */
    public static int BAD_EMBED_CODE = 7;
    /** The signature of the request is invalid */
    public static int INVALID_SIGNATURE = 8;
    /** The request had missing params */
    public static int MISSING_PARAMS = 9;
    /** The server is missing its rule set */
    public static int MISSING_RULE_SET = 10;
    /** The item is unauthorized */
    public static int UNAUTHORIZED = 11;
    /** The request was missing the pcode */
    public static int MISSING_PCODE = 12;
    /** The item is not authorized for this device */
    public static int UNAUTHORIZED_DEVICE = 13;
    /** The request's token was invalid */
    public static int INVALID_TOKEN = 14;
    /** The request's token was expired */
    public static int TOKEN_EXPIRED = 15;
    /** The maximum value for auth codes from the server */
    public static int UNAUTHORIZED_MULTI_SYND_GROUP = 16;
    public static int PROVIDER_DELETED = 17;
    public static int TOO_MANY_ACTIVE_STREAMS = 18;
    public static int MISSING_ACCOUNT_ID = 19;
    public static int NO_ENTITLEMENTS_FOUND = 20;
    public static int NON_ENTITLED_DEVICE = 21;
    public static int NON_REGISTERED_DEVICE = 22;
    public static int MAX_AUTH_CODE = 23;
  }

  /*
   * Text descriptions of the auth responses
   */
  public static String[] authCodeDescription = {
    "authorized",
    "unauthorized parent",
    "unauthorized domain",
    "unauthorized location",
    "current time is before the flight start time",
    "current time is after the flight end time",
    "current time is outside any availability period",
    "this is not a recognized embed code",
    "invalid signature",
    "missing parameters",
    "missing rule set",
    "unauthorized",
    "missing pcode",
    "unauthorized device",
    "invalid token",
    "movie expired",
    "unauthorized multi-synd group",
    "This provider was deleted",
    "Too many open videos. Close other videos on this account and try again in a few minutes",
    "Missing Account ID",
    "No Entitlements Found",
    "Non-entitled Device",
    "Non-registered Device",
    "unauthorized",
   };
  /**
   * Whether or not this AuthorizableItem is authorized
   * @return true if authorized, false if not
   */
  public boolean isAuthorized();

  /**
   * The Auth Code from the authorization request
   * @return an int with the status of the authorization request
   */
  public int getAuthCode();

  /**
   * Update the AuthorizableItem using the specified data
   *
   * @param data
   *          the data to use to update this AuthorizableItem
   * @return a ReturnState based on if the data matched or not (or parsing
   *         failed)
   */
  public ReturnState update(JSONObject data);

  /**
   * For internal use only. The embed codes to authorize for the
   * AuthorizableItemInternal
   *
   * @return the embed codes to authorize as a List
   */
  public List<String> embedCodesToAuthorize();

  public boolean isHeartbeatRequired();
}

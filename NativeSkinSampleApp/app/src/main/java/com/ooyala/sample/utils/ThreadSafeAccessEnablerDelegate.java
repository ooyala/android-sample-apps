package com.ooyala.sample.utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.adobe.adobepass.accessenabler.api.IAccessEnablerDelegate;
import com.adobe.adobepass.accessenabler.models.Event;
import com.adobe.adobepass.accessenabler.models.MetadataKey;
import com.adobe.adobepass.accessenabler.models.MetadataStatus;
import com.adobe.adobepass.accessenabler.models.Mvpd;

import java.io.IOException;
import java.util.ArrayList;

public class ThreadSafeAccessEnablerDelegate extends Handler implements IAccessEnablerDelegate {
  private IAccessEnablerDelegate delegate;

  public static final int SET_REQUESTOR_COMPLETE = 0;
  public static final int SET_AUTHN_STATUS = 1;
  public static final int SET_TOKEN = 2;
  public static final int TOKEN_REQUEST_FAILED = 3;
  public static final int SELECTED_PROVIDER = 4;
  public static final int DISPLAY_PROVIDER_DIALOG = 5;
  public static final int NAVIGATE_TO_URL = 6;
  public static final int SEND_TRACKING_DATA = 7;
  public static final int SET_METADATA_STATUS = 8;
  public static final int PRE_AUTH_RESOURCES = 9;

  public ThreadSafeAccessEnablerDelegate(IAccessEnablerDelegate delegate) {
    this.delegate = delegate;
  }

  private Bundle getBundle(int method) {
    Bundle b = new Bundle();
    b.putInt("method", method);
    return b;
  }

  private void createAndSendMessage(Bundle b) {
    Message m = obtainMessage();
    m.setData(b);
    sendMessage(m);
  }

  @Override
  public void handleMessage(Message msg) {
      Bundle b = msg.getData();

      switch(b.getInt("method")) {
      case SET_REQUESTOR_COMPLETE:
        delegate.setRequestorComplete(b.getInt("status"));
        break;
      case SET_AUTHN_STATUS:
        delegate.setAuthenticationStatus(b.getInt("status"), b.getString("errorCode"));
        break;
      case SET_TOKEN:
        delegate.setToken(b.getString("token"), b.getString("resourceId"));
        break;
      case TOKEN_REQUEST_FAILED:
        delegate.tokenRequestFailed(b.getString("resourceId"), b.getString("errorCode"), b.getString("errorDescription"));
        break;
      case SELECTED_PROVIDER:
        delegate.selectedProvider((Mvpd)b.getSerializable("mvpd"));
        break;
      case DISPLAY_PROVIDER_DIALOG:
        ArrayList<Mvpd> deserializedMvpds = new ArrayList<Mvpd>();
        for(String mvpd:b.getStringArrayList("mvpds")) {
          try {
            deserializedMvpds.add(Mvpd.deserialze(mvpd));
          } catch (IOException e) {
//            DebugMode.logE(TAG, "Caught!", e);
          } catch (ClassNotFoundException e) {
//            DebugMode.logE(TAG, "Caught!", e);
          }
        }
        delegate.displayProviderDialog(deserializedMvpds);
        break;
      case NAVIGATE_TO_URL:
        delegate.navigateToUrl(b.getString("url"));
        break;
      case SEND_TRACKING_DATA:
        Event e = new Event(b.getInt("type"));
        e.setErrorCode(b.getString("errorCode"));
        e.setErrorDetail(b.getString("errorDetail"));
        delegate.sendTrackingData(e, b.getStringArrayList("data"));
        break;
      case SET_METADATA_STATUS:
        delegate.setMetadataStatus((MetadataKey)b.getSerializable("key"), (MetadataStatus)b.getSerializable("result"));
        break;
      case PRE_AUTH_RESOURCES:
        delegate.preauthorizedResources(b.getStringArrayList("resources"));
      }
  }

  @Override
  public void displayProviderDialog(ArrayList<Mvpd> mvpds) {
    ArrayList<String> serializedMvpds = new ArrayList<String>();
    for (Mvpd mvpd : mvpds) {
      try {
        serializedMvpds.add(mvpd.serialize());
      } catch (IOException e) {
//        DebugMode.logE(TAG, "Caught!", e);
      }
    }
    Bundle b = getBundle(DISPLAY_PROVIDER_DIALOG);
    b.putStringArrayList("mvpds", serializedMvpds);
    createAndSendMessage(b);
  }

  @Override
  public void navigateToUrl(String url) {
    Bundle b = getBundle(NAVIGATE_TO_URL);
    b.putString("url", url);
    createAndSendMessage(b);
  }

  @Override
  public void selectedProvider(Mvpd mvpd) {
    Bundle b = getBundle(SELECTED_PROVIDER);
    b.putSerializable("mvpd", mvpd);
    createAndSendMessage(b);
  }

  @Override
  public void sendTrackingData(Event event, ArrayList<String> data) {
    Bundle b = getBundle(SEND_TRACKING_DATA);
    b.putInt("type", event.getType());
    b.putString("errorCode", event.getErrorCode());
    b.putString("errorDetail", event.getErrorDetail());
    b.putStringArrayList("data", data);
    createAndSendMessage(b);
  }

  @Override
  public void setAuthenticationStatus(int status, String errorCode) {
    Bundle b = getBundle(SET_AUTHN_STATUS);
    b.putInt("status", status);
    b.putString("errorCode", errorCode);
    createAndSendMessage(b);
  }

  @Override
  public void setMetadataStatus(MetadataKey key, MetadataStatus result) {
    Bundle b = getBundle(SET_METADATA_STATUS);
    b.putSerializable("key", key);
    b.putSerializable("result", result);
    createAndSendMessage(b);
  }

  @Override
  public void setRequestorComplete(int status) {
    Bundle b = getBundle(SET_REQUESTOR_COMPLETE);
    b.putInt("status", status);
    createAndSendMessage(b);
  }

  @Override
  public void setToken(String token, String resourceId) {
    Bundle b = getBundle(SET_TOKEN);
    b.putString("token", token);
    b.putString("resourceId", resourceId);
    createAndSendMessage(b);
  }

  @Override
  public void tokenRequestFailed(String resourceId, String errorCode, String errorDescription) {
    Bundle b = getBundle(TOKEN_REQUEST_FAILED);
    b.putString("resourceId", resourceId);
    b.putString("errorCode", errorCode);
    b.putString("errorDescription", errorDescription);
    createAndSendMessage(b);
  }

  @Override
  public void preauthorizedResources(ArrayList<String> resources) {
    Bundle b = getBundle(PRE_AUTH_RESOURCES);
    b.putStringArrayList("resources", resources);
    createAndSendMessage(b);
  }
}

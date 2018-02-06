package com.ooyala.chromecastv3sampleapp.cast;


import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManagerListener;

class CastSessionListener implements SessionManagerListener<CastSession> {
  private ConnectStatusListener connectStatusListener;

  CastSessionListener(ConnectStatusListener connectStatusListener) {
    this.connectStatusListener = connectStatusListener;
  }

  //region Implementation of SessionManagerListener<CastSession>

  @Override
  public void onSessionEnded(CastSession session, int error) {
    connectStatusListener.onApplicationDisconnect(session);
  }

  @Override
  public void onSessionResumed(CastSession session, boolean wasSuspended) {
    connectStatusListener.onApplicationConnected(session);
  }

  @Override
  public void onSessionResumeFailed(CastSession session, int error) {
  }

  @Override
  public void onSessionStarted(CastSession session, String sessionId) {
    connectStatusListener.onApplicationConnected(session);
  }

  @Override
  public void onSessionStartFailed(CastSession session, int error) {
    connectStatusListener.onApplicationDisconnect(session);
  }

  @Override
  public void onSessionStarting(CastSession session) {
  }

  @Override
  public void onSessionEnding(CastSession session) {
    connectStatusListener.onApplicationDisconnect(session);
  }

  @Override
  public void onSessionResuming(CastSession session, String sessionId) {
  }

  @Override
  public void onSessionSuspended(CastSession session, int reason) {
  }
  //endregion

  interface ConnectStatusListener {
    void onApplicationConnected(CastSession castSession);

    void onApplicationDisconnect(CastSession session);
  }
}

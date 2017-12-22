package com.ooyala.android;

import java.io.IOException;

import android.content.Context;
import android.os.Handler;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.identifier.AdvertisingIdClient.Info;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.ooyala.android.OoyalaException.OoyalaErrorCode;
import com.ooyala.android.util.DebugMode;

public class AdvertisingIdUtils {

  public interface IAdvertisingIdListener {
    void onAdvertisingIdSuccess( String advertisingId );
    void onAdvertisingIdError( OoyalaException oe );
  }

  private static final String TAG = "AdvertisingIdUtils";
  private static String s_advertisingId;

  public static synchronized void setAdvertisingId( String advertisingId ) {
    s_advertisingId = advertisingId;
    DebugMode.logD(TAG, "s_advertisingId = " + s_advertisingId);
  }
  public static synchronized String getAdvertisingId() {
    return s_advertisingId;
  }

  public static void getAndSetAdvertisingId( final Context context, final IAdvertisingIdListener listener ) {
    final Handler h = new Handler( context.getMainLooper() );
    final Runnable r = new Runnable() {
      @Override
      public void run() {
        try {
          // NOTE: sometimes this is not returning at all fast; it is unclear how long it can take.
          final Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo( context );
          postAdvertisingIdSuccess( h, adInfo.getId(), listener );
        }
        catch (IllegalStateException e) {
          postAdvertisingIdError( h, e, listener );
        }
        catch (GooglePlayServicesRepairableException e) {
          postAdvertisingIdError( h, e, listener );
        }
        catch (IOException e) {
          postAdvertisingIdError( h, e, listener );
        }
        catch (GooglePlayServicesNotAvailableException e) {
          postAdvertisingIdError( h, e, listener );
        }
      }
    };
    final Thread t = new Thread( r, "getAndSetAdvertisingId" );
    t.setUncaughtExceptionHandler( new Thread.UncaughtExceptionHandler() {
      @Override
      public void uncaughtException(Thread thread, Throwable t) {
        if( t instanceof Exception ) { // i figure we shouldn't swallow Errors.
          postAdvertisingIdError( h, (Exception)t, listener );
        }
      }
    } );
    t.start();
  }

  private static void postAdvertisingIdSuccess( final Handler h, final String advertisingId, final IAdvertisingIdListener listener ) {
    DebugMode.logV( TAG, "postAdvertisingIdSuccess" + advertisingId );
    h.post( new Runnable() {
      @Override
      public void run() {
        listener.onAdvertisingIdSuccess( advertisingId );
        }
      }
    );
  }

  private static void postAdvertisingIdError( final Handler h, final Exception e, final IAdvertisingIdListener listener ) {
    DebugMode.logE( TAG, "postAdvertisingIdError", e );
    final OoyalaException oe = new OoyalaException( OoyalaErrorCode.ERROR_ADVERTISING_ID_FAILURE, e );
    h.post( new Runnable() {
      @Override
      public void run() {
        listener.onAdvertisingIdError( oe );
        }
      }
    );
  }
}

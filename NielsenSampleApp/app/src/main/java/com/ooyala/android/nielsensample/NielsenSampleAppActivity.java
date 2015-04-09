package com.ooyala.android.nielsensample;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.nielsen.app.sdk.AppSdk;
import com.nielsen.app.sdk.IAppNotifier;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.nielsensdk.NielsenAnalytics;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.android.util.DebugMode;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicInteger;

public class NielsenSampleAppActivity extends Activity implements Observer, IAppNotifier {
  private static NielsenAnalytics s_nielsenAnalytics;
  public static AtomicInteger s_activityCount = new AtomicInteger();
  public static void incrementRunningActivityCount() {
    s_activityCount.incrementAndGet();
  }
  public static void decrementRunningActivityCount() {
    int v = s_activityCount.decrementAndGet();
    if( v == 0 && s_nielsenAnalytics != null ) {
      new Handler().post(
        new Runnable() {
          @Override
          public void run() {
            DebugMode.logD( TAG, "onStop: we appear to be 'backgrounded'." );
            s_nielsenAnalytics.getNielsenAppSdk().stop();
          }
        }
      );
    }
  }

  public final static String OPT_OUT_URL_EXTRAS_KEY = "opt_out_url";
  public final static String OPT_OUT_RESULT_KEY = "opt_out_result";
  public final static int OPTOUT_REQUEST_CODE = 100;

  private final static String TAG = NielsenSampleAppActivity.class.getSimpleName();
  private final static String PCODE = "42Zms6h4wdcI1R1uFzepD-KZ0kkk";
  private final static String DOMAIN = "http://www.ooyala.com";
  private final static String NIELSEN_SFCODE = "UAT-CERT";
  private final static String NIELSEN_APPID = "T70BC66D4-C904-4DA1-AB9D-BB658F70E9A7";

  private Spinner embedSpinner;
  private HashMap<String, String> embedMap;
  private ArrayAdapter<String> embedAdapter;
  private String optOutUrl;
  private OoyalaPlayer player;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    DebugMode.setMode( DebugMode.Mode.LogAndAbort );

    setContentView(R.layout.main);
    embedSpinner = (Spinner) findViewById(R.id.embedSpinner);
    embedMap = new LinkedHashMap<String, String>();
    embedMap.put("ID3-Demo", "84aDVmcTqN3FrdLXClZgJq-GfFEDhS1a");
    embedMap.put("ID3-TravelEast", "Y5aHlyczqJaJ2Mh6BNWLXfpcmxOaKzcx");
    embedMap.put("ID3-TravelLive", "w3MXlyczp03XOkXoGecg4L8xLIyOiPnR");
    embedMap.put("ID3-FoodEast1", "12YnlyczrWcZvPbIJJTV7TmeVi3tgGPa");
    embedMap.put("ID3-FoodEast2", "B1YXlyczpFZhH6GgBSrrO6VWI6aiMKw0");
    embedMap.put("CMS-Demo", "ZhMmkycjr4jlHIjvpIIimQSf_CjaQs48");
    embedMap.put("CMS-NoAds", "FzYjJzczo3_M3OjkeIta-IIFcPGSGxci");
    embedMap.put("CMS-WithAds", "x3YjJzczqREV-5RDiemsrdqki1FYu2NT");
    embedMap.put("CMS-14Minutes", "JyanIxdDoj9MhKbVEmTJEG8O4QF5xExb");

    embedAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item);
    embedSpinner.setAdapter(embedAdapter);
    for (String key : embedMap.keySet()) {
      embedAdapter.add(key);
    }
    embedAdapter.notifyDataSetChanged();

    OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);
    player = new OoyalaPlayer(PCODE, new PlayerDomain(DOMAIN));
    OoyalaPlayerLayoutController playerLayoutController = new OoyalaPlayerLayoutController(playerLayout, player);
    player = playerLayoutController.getPlayer();

    s_nielsenAnalytics = new NielsenAnalytics( this, player, this, NIELSEN_APPID, "0.1", "NielsenTestApp", NIELSEN_SFCODE, getCustomConfig(), getCustomMetadata() );

    player.addObserver(this);

    final Button optInOutButton = (Button)findViewById( R.id.optInOutButton );
    optInOutButton.setOnClickListener( new OnClickListener() {
      @Override
      public void onClick( View v ) {
        showOptInOutUI();
      }
    } );

    final Button setButton = (Button) findViewById(R.id.setButton);
    setButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        final String embed = embedMap.get(embedSpinner.getSelectedItem());
        if (player.setEmbedCode(embed)) {
          TextView urlText = (TextView) findViewById(R.id.urlText);
          urlText.setText("");
          OoyalaPlayer.enableCustomHLSPlayer = true;
          player.play();
        } else {
          Log.d(TAG, "Something Went Wrong!");
        }
      }
    });

  }

  private String getOptOutUrl() {
    if( s_nielsenAnalytics != null && s_nielsenAnalytics.isValid() ) {
      return s_nielsenAnalytics.getNielsenAppSdk().userOptOutURLString();
    }
    else {
      return null;
    }
  }

  private void showOptInOutUI() {
    final String url = getOptOutUrl();
    if( url == null || url.trim().length() == 0 ) {
      showRestartRequiredMessage();
    }
    else {
      Intent i = new Intent(this, OptOutActivity.class);
      final Bundle pars = new Bundle();
      if (i != null && pars != null) {
        pars.putString(OPT_OUT_URL_EXTRAS_KEY, url);
        i.putExtras(pars);
        startActivityForResult( i, OPTOUT_REQUEST_CODE );
      }
    }
  }

  private void showRestartRequiredMessage() {
    AlertDialog.Builder builder = new AlertDialog.Builder( this );
    builder.setTitle( "No Opt-Out URL" );
    builder.setMessage( "If networking was disabled, please enable networking & restart this app." );
    builder.setNeutralButton( "OK", new DialogInterface.OnClickListener() {
      @Override
      public void onClick( DialogInterface dialog, int which ) {
        dialog.dismiss();
      }
    } );
    builder.show();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult( requestCode, resultCode, data );
    if( resultCode == RESULT_OK ) {
      if( requestCode == OPTOUT_REQUEST_CODE && s_nielsenAnalytics != null ) {
        final String uoo = data.getStringExtra( OPT_OUT_RESULT_KEY );
        Log.d( TAG, "onActivityResult: uoo = " + uoo );
        s_nielsenAnalytics.getNielsenAppSdk().userOptOut( uoo );
      }
    }
  }

  public void onAppSdkEvent(long timestamp, int code, String description) {
    switch( code ) {
      case AppSdk.EVENT_INITIATE:
        Log.d( TAG, "EVENT_INITIATE" );
        break;
      case AppSdk.EVENT_STARTUP:
        Log.d( TAG, "EVENT_STARTUP" );
        break;
      case AppSdk.EVENT_SHUTDOWN:
        Log.d( TAG, "EVENT_SHUTDOWN" );
        break;
      case AppSdk.EVENT_FATAL:
        Log.d( TAG, "EVENT_FATAL" );
        break;
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (player != null && player.getState() != OoyalaPlayer.State.SUSPENDED) {
      player.suspend();
    }
  }

  @Override
  protected void onStop() {
    DebugMode.logD( TAG, "onStop" );
    super.onStop();
    NielsenSampleAppActivity.decrementRunningActivityCount();
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (player != null && player.getState() == OoyalaPlayer.State.SUSPENDED) {
      player.resume();
    }
  }

  @Override
  protected void onStart() {
    DebugMode.logD( TAG, "onStart" );
    super.onStart();
    NielsenSampleAppActivity.incrementRunningActivityCount();
  }

  @Override
  public void update(Observable observable, Object data) {
//    Log.d( TAG, "update: " + data );
  }

  private JSONObject getCustomConfig() {
    final JSONObject json = new JSONObject();
    try {
      json.put( "nol_devDebug", "true" ); // do NOT do this for production apps!
    }
    catch( JSONException e ) {
      Log.e( TAG, "getCustomConfig()", e );
    }
    return json;
  }

  private JSONObject getCustomMetadata() {
    return null;
  }
}

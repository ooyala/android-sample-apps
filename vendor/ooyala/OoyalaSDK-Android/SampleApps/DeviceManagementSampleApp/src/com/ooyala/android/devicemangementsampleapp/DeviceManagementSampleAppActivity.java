package com.ooyala.android.devicemangementsampleapp;

import java.io.IOException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.drm.DrmErrorEvent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.ooyala.android.EmbedTokenGenerator;
import com.ooyala.android.EmbedTokenGeneratorCallback;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.sampleapp.R;

public class DeviceManagementSampleAppActivity extends Activity implements EmbedTokenGenerator, Observer{

  final String EMBED    = "fill me in";
  final String PCODE    = "fill me in";
  final String DOMAIN   = "http://www.ooyala.com";
  final String SAS      = "player.ooyala.com/sas";
  final String HTTP_SAS = "http://" + SAS;

  private OoyalaPlayer player;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);
    OoyalaPlayerLayoutController playerLayoutController = new OoyalaPlayerLayoutController(playerLayout, PCODE, new PlayerDomain(DOMAIN), this);
    player = playerLayoutController.getPlayer();
    player.addObserver(this);
    if (player.setEmbedCode(EMBED)) {
      player.play();
    } else {
      Log.d(this.getClass().getName(), "Something Went Wrong!");
    }
  }

  @Override
  public void getTokenForEmbedCodes(List<String> embedCodes,
      EmbedTokenGeneratorCallback callback) {
    //add embed token/OPT in the setEmbedToken() example below
    // HTTP_SAS + "/embed_token/pcode/embed_code?account_id=account&api_key=apikey&expires=expires&signature=signature"
    callback.setEmbedToken("fill me in");
  }

  // make http requests async
  public void promptNickname() {
    new AsyncTask<Void,Void,String>() {
      @Override
      protected String doInBackground( Void... params ) {
        final String lastResultUrl = HTTP_SAS + "/api/v1/device_management/auth_token/"
            + player.getAuthToken() + "/last_result";
        final DefaultHttpClient httpClient = new DefaultHttpClient();
        final HttpGet httpGet = new HttpGet(lastResultUrl);
        try {
          final HttpResponse httpResponse = httpClient.execute(httpGet);
          final int responseCode = httpResponse.getStatusLine().getStatusCode();
          Log.v( "DEVICE MANAGEMENT", "responseCode = " + responseCode );
          if (responseCode == 200) {
            final HttpEntity httpEntity = httpResponse.getEntity();
            final String output = EntityUtils.toString(httpEntity);
            final JSONObject jsonObject = new JSONObject(output);
            final String publicDeviceId = jsonObject.getString("public_device_id");
            if( "new device registered".equals( jsonObject.getString("result") ) ) {
              return publicDeviceId;
            }
          }
        }
        catch( ClientProtocolException cpe ) {
          cpe.printStackTrace();
        }
        catch( IOException ioe ) {
          ioe.printStackTrace();
        }
        catch( JSONException je ) {
          je.printStackTrace();
        }
        return null;
      }

      @Override
      protected void onPostExecute( String publicDeviceId ) {
        if( publicDeviceId == null ) { return; }
        Log.v( "DEVICE MANAGEMENT", "publicDeviceId = " + publicDeviceId );
        final AlertDialog.Builder alert = new AlertDialog.Builder(DeviceManagementSampleAppActivity.this);
        final EditText input = new EditText(DeviceManagementSampleAppActivity.this);
        final String nicknameUrl = HTTP_SAS
            + "/api/v1/device_management/auth_token/" + player.getAuthToken()
            + "/devices/" + publicDeviceId;
        alert.setTitle("Device Registration");
        alert.setMessage("Enter Device Nickname: ");
        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int whichButton) {
            new Thread() {
              @Override
              public void run() {
                try {
                  final String value = "{\"nickname\":\""
                      + input.getText().toString() + "\"}";
                  final HttpPut httpPut = new HttpPut(nicknameUrl);
                  httpPut.setEntity(new StringEntity(value));
                  final DefaultHttpClient httpClient = new DefaultHttpClient();
                  final HttpResponse httpResponse = httpClient.execute(httpPut);
                  final int responseCode = httpResponse.getStatusLine().getStatusCode();
                  Log.v( "DEVICE MANAGEMENT", "responseCode = " + responseCode );
                } catch (ClientProtocolException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
                } catch (IOException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
                }
              }
            }.start();
            return;
          }
        });
        alert.setNegativeButton("Cancel",
            new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int whichButton) {
            // TODO Auto-generated method stub
            return;
          }
        });
        alert.show();
      }
    }.execute();
  }

  @Override
  public void update(Observable arg0, Object arg1) {
    final OoyalaPlayer player = (OoyalaPlayer) arg0;
    final String notification = arg1.toString();
    Log.d("DEVICE MANAGEMENT", "Notification Recieved: " + arg1 + " - state: " + player.getState());
    //android 3+
    if (notification.equals(OoyalaPlayer.PLAY_STARTED_NOTIFICATION)) {
      promptNickname();
    }
    if (notification.equals(OoyalaPlayer.ERROR_NOTIFICATION)) {
      final String errorMsg = player.getError() == null ? null : player
          .getError().getMessage();
      if (errorMsg != null
          && errorMsg.equals(DrmErrorEvent.TYPE_PROCESS_DRM_INFO_FAILED + "")) {
        final String lastResultUrl = HTTP_SAS + "/api/v1/device_management/auth_token/"
            + player.getAuthToken() + "/last_result";
        new Thread() {
          @Override
          public void run() {
            try {
              final DefaultHttpClient httpClient = new DefaultHttpClient();
              final HttpGet httpGet = new HttpGet(lastResultUrl);
              final HttpResponse httpResponse = httpClient.execute(httpGet);
              final HttpEntity httpEntity = httpResponse.getEntity();
              final String output = EntityUtils.toString(httpEntity);
              final JSONObject jsonObject = new JSONObject(output);
              final String result = jsonObject.getString("result");
              if (result.equals("device limit reached")) {
                // device management error, device limit reached
              } else {
                // widevine specific error
              }
            }
            catch (ClientProtocolException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            } catch (IOException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            } catch (JSONException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
          }
        }.start();
      } else if (errorMsg != null
          && errorMsg.equals(DrmErrorEvent.TYPE_NO_INTERNET_CONNECTION + "")) {
        // widevine specific error
      }
      // else if {} ... check the DRMErrorEvent doc for other Widevine errors
    }
  }
}

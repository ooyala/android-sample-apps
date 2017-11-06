package com.ooyala.sample.players;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ooyala.android.EmbedTokenGenerator;
import com.ooyala.android.EmbedTokenGeneratorCallback;
import com.ooyala.android.EmbeddedSecureURLGenerator;
import com.ooyala.android.offline.DashDownloader;
import com.ooyala.android.offline.DashOptions;
import com.ooyala.android.util.SDCardLogcatOoyalaEventsLogger;
import com.ooyala.sample.R;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class OfflineDownloadActivity extends Activity implements DashDownloader.Listener, EmbedTokenGenerator {
  final String TAG = this.getClass().toString();

  private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

  String EMBED = "null";
  final String PCODE  = "BjcWYyOu1KK2DiKOkF41Z2k0X57l";
  final String DOMAIN = "http://ooyala.com";

  // Write the sdk events text along with events count to log file in sdcard if the log file already exists
  SDCardLogcatOoyalaEventsLogger Playbacklog= new SDCardLogcatOoyalaEventsLogger();

  protected TextView progressView;
  protected Handler handler;
  protected DashDownloader downloader;

  private final String APIKEY = "";
  private final String SECRET = "";

  // An account ID, if you are using Concurrent Streams or Entitlements
  private final String ACCOUNT_ID = "";

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle(getIntent().getExtras().getString("selection_name"));
    setContentView(R.layout.offline_downloader);
    EMBED = getIntent().getExtras().getString("embed_code");
    progressView = (TextView)findViewById(R.id.progress_text);
    progressView.setText("progress: 0");
    handler = new Handler(getMainLooper());

    final File folder = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);

    // Use this DashOptions to download an asset without OPT
//    DashOptions options = new DashOptions.Builder(PCODE, EMBED, DOMAIN, folder).build();
    // Use this DashOptions to download an asset with OPT
    DashOptions options = new DashOptions.Builder(PCODE, EMBED, DOMAIN, folder).setEmbedTokenGenerator(this).build();
    downloader = new DashDownloader(this, options, this);

    Button startButton = (Button)findViewById(R.id.start_button);
    startButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (ContextCompat.checkSelfPermission(OfflineDownloadActivity.this, WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
          ActivityCompat.requestPermissions(OfflineDownloadActivity.this, new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
          downloader.startDownload();
        }
      }
    });

    Button cancelButton = (Button)findViewById(R.id.cancel_button);
    cancelButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        downloader.abort();
      }
    });

    Button resetButton = (Button)findViewById(R.id.reset_button);
    resetButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (downloader.deleteAll()) {
          progressView.setText("deletion completed");
        } else {
          progressView.setText("deletion failed");
        }
      }
    });
  }

  public void onCompletion() {
    handler.post(new Runnable() {
      @Override
      public void run() {
        long expiration = downloader.getLicenseExpirationDate();
        String expirationString = expiration == DashDownloader.INFINITE_DURATION ? "infinite" : String.valueOf(expiration);
        progressView.setText("Completed! license expires in " + expirationString);
      }
    });
  }

  public void onAbort() {
    handler.post(new Runnable() {
      @Override
      public void run() {
        progressView.setText("Aborted");
      }
    });
  }

  public void onError(final Exception ex) {
    handler.post(new Runnable() {
      @Override
      public void run() {
        progressView.setText("Error:" + ex.getLocalizedMessage());
      }
    });
  }

  public void onPercentage(int percentCompleted) {
    final String progress = "progress:" + percentCompleted;

    handler.post(new Runnable() {
      @Override
      public void run() {
        progressView.setText(progress);
      }
    });
  }

  private void onDeletion(final boolean success) {
    handler.post(new Runnable() {
      @Override
      public void run() {
        progressView.setText(success ? " deletion completed" : "deletion failed");

      }
    });
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (requestCode == PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
      if (grantResults.length > 0
              && grantResults[0] == PERMISSION_GRANTED) {
        downloader.startDownload();
      } else {
        Toast.makeText(this, "You don't have permissions to download in this app", Toast.LENGTH_SHORT).show();
      }
    }
  }

  @Override
  public void getTokenForEmbedCodes(List<String> embedCodes, EmbedTokenGeneratorCallback callback) {
    String embedCodesString = "";
    for (String ec : embedCodes) {
      if (ec.equals("")) embedCodesString += ",";
      embedCodesString += ec;
    }

    HashMap<String, String> params = new HashMap<String, String>();
    params.put("account_id", ACCOUNT_ID);

    /* Uncommenting this will bypass all syndication rules on your asset
       This will not work unless you have a working API Key and Secret.
       This is one reason why you shouldn't keep the Secret in your app/source control */
//     params.put("override_syndication_group", "override_all_synd_groups");

    String uri = "/sas/embed_token/" + PCODE + "/" + embedCodesString;

    EmbeddedSecureURLGenerator urlGen = new EmbeddedSecureURLGenerator(APIKEY, SECRET);

    URL tokenUrl = urlGen.secureURL("http://player.ooyala.com", uri, params);

    callback.setEmbedToken(tokenUrl.toString());
  }
}

package com.ooyala.sample.players;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ooyala.android.EmbedTokenGenerator;
import com.ooyala.android.EmbedTokenGeneratorCallback;
import com.ooyala.android.EmbeddedSecureURLGenerator;
import com.ooyala.android.offline.DashDownloader;
import com.ooyala.android.offline.LicenseDownloader;
import com.ooyala.android.offline.options.DownloadOptions;
import com.ooyala.android.util.DebugMode;
import com.ooyala.android.util.SDCardLogcatOoyalaEventsLogger;
import com.ooyala.sample.R;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class OfflineDownloadActivity extends Activity implements DashDownloader.Listener, EmbedTokenGenerator {
  final String TAG = this.getClass().toString();

  private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
  private static final int MAX_RETRY_COUNT = 3;

  String EMBED;
  String PCODE;
  String DOMAIN;

  // Write the sdk events text along with events count to log file in sdcard if the log file already exists
  SDCardLogcatOoyalaEventsLogger Playbacklog= new SDCardLogcatOoyalaEventsLogger();

  protected TextView progressView;
  protected Handler handler;
  protected DashDownloader downloader;
  protected int progressCompleted;

  private int retry_count;

  private String APIKEY = "";
  private String SECRET = "";

  // An account ID, if you are using Concurrent Streams or Entitlements
  private String ACCOUNT_ID = "";

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle(getIntent().getExtras().getString("selection_name"));
    setContentView(R.layout.offline_downloader);

    EMBED = getIntent().getExtras().getString("embed_code");
    PCODE = getIntent().getExtras().getString("pcode");
    DOMAIN = getIntent().getExtras().getString("domain");
    APIKEY = getIntent().getExtras().getString("api_key");
    SECRET = getIntent().getExtras().getString("secret_key");
    ACCOUNT_ID = getIntent().getExtras().getString("account_id");

    progressView = findViewById(R.id.progress_text);
    progressView.setText("progress: 0");
    handler = new Handler(getMainLooper());

    final File folder = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);

    // Use this DashOptions to download an asset without OPT
//    DashOptions options = new DashOptions.Builder(PCODE, EMBED, DOMAIN, folder).build();
    // Use this DashOptions to download an asset with OPT
    DownloadOptions options = new DownloadOptions.Builder(PCODE, EMBED, DOMAIN, folder).setEmbedTokenGenerator(this).build();
    downloader = new DashDownloader(this, options, this);

    Button startButton = findViewById(R.id.start_button);
    startButton.setOnClickListener(v -> {
      if (ContextCompat.checkSelfPermission(OfflineDownloadActivity.this, WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(OfflineDownloadActivity.this, new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
      } else {
        downloader.startDownload();
      }
    });

    Button cancelButton = findViewById(R.id.cancel_button);
    cancelButton.setOnClickListener(v -> downloader.abort());

    Button resetButton = findViewById(R.id.reset_button);
    resetButton.setOnClickListener(v -> {
      downloader.abort();
      boolean isDeleted = downloader.deleteAll();
      onDeletion(isDeleted);
    });
  }

  @Override
  public void onCompletion(String embedCode) {
    handler.post(() -> {
      long expiration = downloader.getLicenseExpirationDate();
      String expirationString = expiration == LicenseDownloader.INFINITE_DURATION ? "infinite" : String.valueOf(expiration);
      progressView.setText("Completed! license expires in " + expirationString);
    });
  }

  @Override
  public void onAbort(String embedCode) {
    handler.post(() -> progressView.setText("Aborted"));
  }

  @Override
  public void onError(String embedCode, final Exception ex) {
    if (progressCompleted <= 99 && retry_count < MAX_RETRY_COUNT) {
      retry_count++;
      DebugMode.logD(TAG, "Retrying to download : " + retry_count);

      handler.post(() -> {
        progressView.setText(progressCompleted + " Retrying to download ..");
        downloader.startDownload();
      });
    } else {
      handler.post(() -> progressView.setText("Error:" + ex.getLocalizedMessage()));
    }
  }

  public void onPercentage(int percentCompleted) {
    progressCompleted = percentCompleted;
    final String progress = "progress:" + percentCompleted;

    handler.post(() -> progressView.setText(progress));
  }

  private void onDeletion(final boolean success) {
    handler.post(() -> progressView.setText(success ? " Deletion completed" : "Deletion failed"));
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

    HashMap<String, String> params = new HashMap<>();
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

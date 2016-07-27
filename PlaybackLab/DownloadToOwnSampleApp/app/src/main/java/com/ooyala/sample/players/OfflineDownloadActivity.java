package com.ooyala.sample.players;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ooyala.android.offline.DashDownloader;
import com.ooyala.android.offline.DashOptions;
import com.ooyala.android.util.SDCardLogcatOoyalaEventsLogger;
import com.ooyala.sample.R;

import java.io.File;

public class OfflineDownloadActivity extends Activity implements DashDownloader.Listener {
  final String TAG = this.getClass().toString();

  String EMBED = null;
  final String PCODE  = "FoeG863GnBL4IhhlFC1Q2jqbkH9m";
  final String DOMAIN = "http://ooyala.com";

  // Write the sdk events text along with events count to log file in sdcard if the log file already exists
  SDCardLogcatOoyalaEventsLogger Playbacklog= new SDCardLogcatOoyalaEventsLogger();

  protected TextView progressView;
  protected Handler handler;

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

    DashOptions options = new DashOptions.Builder(PCODE, EMBED, DOMAIN, folder).build();
    final DashDownloader downloader = new DashDownloader(this, options, this);

    Button startButton = (Button)findViewById(R.id.start_button);
    startButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        downloader.startDownload();
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
        progressView.setText("Completed!");
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

}

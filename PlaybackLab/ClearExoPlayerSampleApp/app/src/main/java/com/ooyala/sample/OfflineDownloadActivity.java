package com.ooyala.sample;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.drm.DrmSession;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.drm.OfflineLicenseHelper;
import com.google.android.exoplayer2.drm.UnsupportedDrmException;
import com.google.android.exoplayer2.offline.Downloader;
import com.google.android.exoplayer2.offline.DownloaderConstructorHelper;
import com.google.android.exoplayer2.source.dash.DashUtil;
import com.google.android.exoplayer2.source.dash.manifest.DashManifest;
import com.google.android.exoplayer2.source.dash.manifest.RepresentationKey;
import com.google.android.exoplayer2.source.dash.offline.DashDownloader;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;
import com.ooyala.sample.ClearExoPlayerSampleApp.R;

import java.io.File;
import java.io.IOException;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class OfflineDownloadActivity extends Activity {
  final String TAG = this.getClass().toString();

  private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

  private TextView progressView;
  private Handler handler;

  private DashDownloader dashDownloader;
  private DownloaderConstructorHelper helper;
  private Thread downloader = null;
  private SimpleCache cache;
  private File tempFolder;

  private String drmLicenseUrl;
  private String drmSchemeUuid;
  private Intent offlineIntent;
  private byte[] offlineLicenseKeySetId;
  private boolean isDownloaded = false;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle(getIntent().getExtras().getString("selection_name"));
    setContentView(R.layout.offline_downloader);

    final Intent intent = getIntent();
    String action = intent.getAction();
    Uri[] uris = null;
    if (Constants.ACTION_VIEW.equals(action)) {
      uris = new Uri[]{intent.getData()};
    }

    if (intent.hasExtra(Constants.DRM_LICENSE_URL)) {
      drmLicenseUrl = intent.getStringExtra(Constants.DRM_LICENSE_URL);
    }

    if (intent.hasExtra(Constants.DRM_SCHEME_EXTRA)) {
      drmSchemeUuid = intent.getStringExtra(Constants.DRM_SCHEME_EXTRA);
    }

    if (uris != null) {
      try {
        offlineIntent = new Intent(this, PlayerActivity.class);
        offlineIntent.setData(uris[0]);

        tempFolder = Util.createTempDirectory(this, "ExoPlayerTest");
        cache = new SimpleCache(tempFolder, new NoOpCacheEvictor());

        DemoApplication app = (DemoApplication) getApplication();
        helper = new DownloaderConstructorHelper(cache, app.buildDataSourceFactory(true));
        dashDownloader = new DashDownloader(uris[0], helper);
        dashDownloader.selectRepresentations(new RepresentationKey[]{new RepresentationKey(0, 0, 0)});
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    handler = new Handler(getMainLooper());
    progressView = findViewById(R.id.progress_text);
    progressView.setText("progress: 0");

    Button startButton = findViewById(R.id.start_button);
    startButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        download();
      }
    });

    Button resetButton = findViewById(R.id.reset_button);
    resetButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        reset();
      }
    });

    Button playOfflineButton = findViewById(R.id.play_offline_button);
    playOfflineButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        playOffline();
      }
    });
  }

  private void download() {
    if (ContextCompat.checkSelfPermission(OfflineDownloadActivity.this,
      WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(OfflineDownloadActivity.this,
        new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
    } else {
      downloader = getDownloader();
      if (!downloader.isAlive() && !downloader.isInterrupted()) {
        downloader.start();
      }
    }
  }

  private void reset() {
    if (dashDownloader != null && downloader != null) {
      try {
        downloader.interrupt();
        downloader = null;
        isDownloaded = false;
        dashDownloader.remove();
        progressView.setText("Deletion completed");
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private void playOffline() {
    if (isDownloaded) {
      try {
        ((DemoApplication) getApplication()).dashManifest = dashDownloader.getManifest();

        offlineIntent.setAction(Constants.ACTION_VIEW);
        offlineIntent.putExtra(Constants.OFFLINE_LICENCE_KEY_SET_ID, offlineLicenseKeySetId);
        offlineIntent.putExtra(Constants.OFFLINE_MODE, true);
        offlineIntent.putExtra(Constants.OFFLINE_FOLDER, tempFolder.getAbsolutePath());
        offlineIntent.putExtra(Constants.DRM_SCHEME_EXTRA, drmSchemeUuid);
        startActivity(offlineIntent);

      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private byte[] downloadLicence() {
    try {
      HttpDataSource.Factory httpDataSourceFactory = ((DemoApplication) getApplication())
        .buildHttpDataSourceFactory(false);
      DataSource dataSource = httpDataSourceFactory.createDataSource();
      DashManifest dashManifest = dashDownloader.getManifest();
      DrmInitData drmInitData = DashUtil.loadDrmInitData(dataSource, dashManifest.getPeriod(0));
      OfflineLicenseHelper<FrameworkMediaCrypto> offlineLicenseHelper =
        OfflineLicenseHelper.newWidevineInstance(drmLicenseUrl, httpDataSourceFactory);

      return offlineLicenseHelper.downloadLicense(drmInitData);

    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (DrmSession.DrmSessionException e) {
      e.printStackTrace();
    } catch (UnsupportedDrmException e) {
      e.printStackTrace();
    }
    return null;
  }

  private Thread getDownloader() {
    if (downloader != null && downloader.isAlive()) {
      return downloader;
    }
    return new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          if (dashDownloader != null) {
            dashDownloader.download(new Downloader.ProgressListener() {
              @Override
              public void onDownloadProgress(Downloader downloader, float downloadPercentage, long downloadedBytes) {
                onPercentage(downloadPercentage);
                if (downloadPercentage >= 100.0f) {
                  isDownloaded = true;
                  // Try to download an offline licence.
                  // The offline licence in the example is requested every time to simplify the logic.
                  // Normally here should the full licence workflow.
                  offlineLicenseKeySetId = downloadLicence();
                }
              }
            });
          }
        } catch (IOException e) {
          e.printStackTrace();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    });
  }

  private void onPercentage(float percentCompleted) {
    final String progress = "progress:" + percentCompleted;
    handler.post(new Runnable() {
      @Override
      public void run() {
        progressView.setText(progress);
      }
    });
  }
}

package com.ooyala.android.offline;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.format.DateUtils;

import com.google.android.exoplayer2.source.dash.DashSegmentIndex;
import com.google.android.exoplayer2.source.dash.manifest.AdaptationSet;
import com.google.android.exoplayer2.source.dash.manifest.DashManifest;
import com.google.android.exoplayer2.source.dash.manifest.DashManifestParser;
import com.google.android.exoplayer2.source.dash.manifest.Period;
import com.google.android.exoplayer2.source.dash.manifest.RangedUri;
import com.google.android.exoplayer2.source.dash.manifest.Representation;
import com.ooyala.android.DefaultPlayerInfo;
import com.ooyala.android.OoyalaAPIClient;
import com.ooyala.android.OoyalaException;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.PlayerInfo;
import com.ooyala.android.Utils;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.item.Stream;
import com.ooyala.android.item.Video;
import com.ooyala.android.player.exoplayer.ExoStreamPlayer;
import com.ooyala.android.util.DebugMode;
import com.ooyala.android.util.DownloadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

/**
 * A class that handles DASH offline playback
 */
public class DashDownloader {

  private static final String TAG = DashDownloader.class.getSimpleName();
  public static final long INFINITE_DURATION = 9223372036854775807L; // 2^63 -1
  public static final String LICENSE_FILE = "license.key";
  public static final String LICENSE_CURRENT_TIME = "currentTime";

  public interface Listener {

    void onCompletion(String embedCode);

    void onAbort(String embedCode);

    void onError(String embedCode, Exception e);

    void onPercentage(int percentCompleted);
  }

  private final Context context;
  private final DashOptions options;
  private final Listener listener;
  private Video video;
  private Future pendingTask;
  private int segmentsCount;
  private int completedSegmentsCount;
  private boolean isAborted;

  /**
   * Constructor
   *
   * @param context  a context
   * @param options  the DASH downloader options
   * @param listener an event listener
   */
  public DashDownloader(Context context, DashOptions options, Listener listener) {
    this.context = context;
    this.options = options;
    this.listener = listener;
  }

  /**
   * This is to start/resume the download process
   * All the segments already downloaded will be skipped.
   * If users do not want to re-us
   */
  public void startDownload() {
    isAborted = false;
    if (options.getEmbedCode() == null || options.getEmbedCode().isEmpty()) {
      handleError(new Exception("Embed code is empty"));
      return;
    }

    if (options.getFolder() == null || !options.getFolder().exists()) {
      handleError(new Exception("Download folder is null"));
      return;
    }

    final File folder = new File(options.getFolder(), options.getEmbedCode());
    if (!folder.exists()) {
      if (!folder.mkdir()) {
        handleError(new Exception("Cannot create folder " + folder.getAbsolutePath()));
        return;
      }
    }

    // All the real work happens on a working thread.
    pendingTask = Utils.sharedExecutorService().submit(new Runnable() {
      @Override
      public void run() {
        // step 0: authorize
        video = authorization();

        if (video == null) {
          handleError(new Exception("Authorization failed"));
          return;
        }

        Stream stream = video.getStream();
        if (stream == null) {
          handleError(new Exception("Stream is null"));
          return;
        }

        String mpdUrl = stream.getUrlFormat().equals(Stream.STREAM_URL_FORMAT_B64) ?
            stream.decodedURL().toString().trim() : stream.getUrl().trim();
        if (mpdUrl.isEmpty()) {
          handleError(new Exception("MPD url is empty"));
          return;
        }

        // step 1: download MPD file
        File mpdFile = downloadMpd(mpdUrl, folder);
        if (mpdFile == null || !mpdFile.exists()) {
          handleError(new Exception("Failed to download mpd"));
          return;
        }
        // step 2: parse MPD file and find representations to download.
        DashManifest mpd = parseMpd(mpdUrl, mpdFile);
        if (mpd == null) {
          handleError(new Exception("Cannot parse mpd"));
          return;
        }

        // step 2-1: save Stream json


        // step 3: get representations to download
        List<List<String>> segmentsToDownload = getSegments(mpd);
        segmentsCount = segmentsToDownload.get(0).size() + segmentsToDownload.get(1).size();
        completedSegmentsCount = 0;

        // step 4: download init segments, which may contain pssh box
        // step 4-2: download all the segments, for now. ExoPlayer requires all segments downloaded
        // to start license request.
        for (int i = 0; i < segmentsToDownload.size(); ++i) {
          Boolean success = downloadSegments(folder, segmentsToDownload.get(i));
          if (!success) {
            handleError(new Exception("Failed to download init segments"));
            return;
          }
        }

        // step 5: request offline license, if necessary
        if (stream.getWidevineServerPath() != null) {
          // DRM DASH, check if the license file already exist
          File licenseFile = new File(folder, DashDownloader.LICENSE_FILE);
          if (licenseFile.exists()) {
            if (getLicenseExpirationDate() <= System.currentTimeMillis() / DateUtils.SECOND_IN_MILLIS) {
              // remove expired license
              licenseFile.delete();
            }
          }

          if (!licenseFile.exists()) {
            requestLicense(mpdUrl, mpdFile, stream.getWidevineServerPath());
            return;
          }
        }

        // notify completion
        if (listener != null) {
          listener.onCompletion(options.getEmbedCode());
        }
      }
    });
  }

  /**
   * Abort the download
   * Ooyala will cancel all the pending download tasks.
   * If all segments are downloaded this will be ignored.
   */
  public void abort() {
    isAborted = true;
    if (pendingTask != null) {
      pendingTask.cancel(true);
    }
    if (listener != null) {
      listener.onAbort(options.getEmbedCode());
    }
  }

  /**
   * Completely remove a folder and its contents for an offline video
   * This is to remove the folder before reuse the same folder name.
   *
   * @return true on success, false otherwise
   */
  public boolean deleteAll() {
    File folder = new File(options.getFolder(), options.getEmbedCode());
    if (!folder.exists()) {
      DebugMode.logD(TAG, "Folder does not exist");
      return true;
    }

    boolean success = deleteFolder(folder);
    if (!success) {
      DebugMode.logE(TAG, "Failed to delete folder " + folder.getAbsolutePath());
    }
    return success;
  }

  /**
   * get the license expiration data from the license file.
   * This should be called after video files are downloaded in the folder specified.
   *
   * @return the license expiration date, in seconds from epoch. 0 if license file does not exist.
   */
  public long getLicenseExpirationDate() {
    File folder = new File(options.getFolder(), options.getEmbedCode());
    if (!folder.exists()) {
      return 0;
    }

    File licenseFile = new File(folder, LICENSE_FILE);
    if (!licenseFile.exists()) {
      return 0;
    }

    Map<String, String> status = Utils.mapFromFile(licenseFile);
    if (status == null) {
      return 0;
    }

    String remaining = status.get("LicenseDurationRemaining");
    String currentTime = status.get(LICENSE_CURRENT_TIME);

    if (remaining == null || currentTime == null) {
      return 0;
    }

    long remainingDuration = Long.parseLong(remaining);
    // Android 5.x and above get LicenseDurationRemaining = ININITE_DURATION
    // Android 4.3 and 4.4 get LicenseDurationRemaining = 0
    if (remainingDuration == INFINITE_DURATION || (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && 0 == remainingDuration)) {
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && 0 == remainingDuration) {
        DebugMode.logI(TAG, "License has a LicenseDurationRemaining value of 0. Interpreting the value as an Infinite license.");
      }
      return INFINITE_DURATION;
    } else {
      return remainingDuration + Long.parseLong(currentTime);
    }
  }

  /**
   * A helper function to delete a folder recurisively
   *
   * @param folder the local folder to store the mpd file
   * @return <code>true</code> if and only if the file or directory is
   * successfully deleted; <code>false</code> otherwise
   */
  private boolean deleteFolder(File folder) {
    if (folder.isDirectory()) {
      File[] listFiles = folder.listFiles();
      if (listFiles != null) {
        for (File child : folder.listFiles()) {
          boolean success;
          if (child.isFile()) {
            success = child.delete();
          } else {
            success = deleteFolder(child);
          }
          if (!success) {
            DebugMode.logE(TAG, "Failed to delete file or folder " + child.getAbsolutePath());
          }
        }
      }
    }
    return folder.delete();
  }

  /**
   * Download MPD file to a local folder
   *
   * @param mpdUrl the remote url for the mpd file
   * @param folder the local folder to store the mpd file
   * @return the mpd file on success, null otherwise
   */
  private File downloadMpd(String mpdUrl, File folder) {
    final String mpdFilename = mpdUrl.substring(mpdUrl.lastIndexOf('/') + 1);
    if (!validateMpdFilename(mpdFilename, folder)) {
      return null;
    }
    final File mpdFile = new File(folder, mpdFilename);
    Future<Boolean> f = Utils.sharedExecutorService().submit(new DownloadTask(mpdUrl, mpdFile, listener, options.getEmbedCode()));

    try {
      if (f.get()) {
        handleProgress(1, "MPD file downloaded");
        return mpdFile;
      } else {
        handleError(new Exception("Failed to download mpd file from " + mpdUrl));
      }
    } catch (InterruptedException | ExecutionException ex) {
      handleError(ex);
    }

    return null;
  }

  private boolean validateMpdFilename(String mpdFilename, File folder) {
    if (mpdFilename == null || mpdFilename.length() <= 0) {
      handleError(new Exception("MPD filename is empty"));
      return false;
    }

    if (!mpdFilename.contains(".mpd")) {
      handleError(new Exception("MPD filename format is incorrect"));
      return false;
    }

    List<String> mpdFiles = new ArrayList<>();
    for (String s : folder.list()) {
      if (s.contains(".mpd")) {
        mpdFiles.add(s);
      }
    }

    if (mpdFiles.size() > 1) {
      handleError(new Exception("Multiple mpd files exist under the folder"));
      return false;
    } else if (mpdFiles.size() <= 0) {
      return true;
    } else {
      if (mpdFiles.get(0).equals(mpdFilename)) {
        return true;
      } else {
        handleError(new Exception("MPD file " + mpdFiles.get(0) + " already exists while try to download " + mpdFilename));
        return false;
      }
    }
  }

  /**
   * Log the error, and notify the listener
   *
   * @param ex the exception that causes the error
   */
  private void handleError(Exception ex) {
    if (isAborted) {
      // user abort, ignore errors.
      return;
    }

    DebugMode.logE(TAG, ex.getMessage(), ex);
    if (listener != null) {
      listener.onError(options.getEmbedCode(), ex);
    }
  }

  /**
   * Notify the listener when progress is updated
   * This is getting called when a segment is downloaded
   *
   * @param percentage
   * @param message
   */
  private void handleProgress(int percentage, String message) {
    DebugMode.logD(TAG, "Download progress " + percentage + " : " + message);
  }

  /**
   * Parse the downloaded MPD file
   *
   * @param mpdFile the MPD file to parse
   * @return the MPD object
   */
  private DashManifest parseMpd(String url, File mpdFile) {
    DashManifestParser parser = new DashManifestParser();

    FileInputStream fis = null;
    DashManifest mpd = null;
    try {
      fis = new FileInputStream(mpdFile);
      mpd = parser.parse(Uri.parse(url), fis);
      fis.close();
    } catch (IOException ex) {
      DebugMode.logE(TAG, "IOException error" + ex.getLocalizedMessage(), ex);
    }
    return mpd;
  }

  /**
   * Get segment file names from MPD
   *
   * @param mpd the MPD file
   * @return a list that contains init segments and remaining segments
   */
  private List<List<String>> getSegments(DashManifest mpd) {
    List<List<String>> results = new ArrayList<>();
    results.add(new ArrayList<String>());
    results.add(new ArrayList<String>());

    for (int i = 0; i < mpd.getPeriodCount(); ++i) {
      Period period = mpd.getPeriod(i);
      long duration = mpd.getPeriodDurationUs(i);
      for (AdaptationSet as : period.adaptationSets) {
        for (Representation r : as.representations) {
          addRepresentationSegments(r, duration, results);
        }
      }
    }
    return results;
  }

  /**
   * Given a list of segments, start the process to download
   *
   * @param folder   the local folder to store segments
   * @param segments a list of segment urls
   * @return true on success, false if any segment fails
   */
  private Boolean downloadSegments(File folder, List<String> segments) {
    boolean success = true;
    CompletionService<Boolean> ecs = new ExecutorCompletionService<Boolean>(Utils.sharedExecutorService());
    List<Future<Boolean>> futures = new ArrayList<Future<Boolean>>();
    int fileAlreadyExistCount = 0;

    for (String segment : segments) {
      String segmentFilename = segment.substring(segment.lastIndexOf('/') + 1);
      // get rid of parameters
      int index = segmentFilename.indexOf("?");
      if (index > 0) {
        segmentFilename = segmentFilename.substring(0, index);
      }

      final File segmentFile = new File(folder, segmentFilename);
      if (segmentFile.exists()) {
        fileAlreadyExistCount++;
      } else {
        DebugMode.logD(TAG, "Futures add " + segment + " : " + segmentFile.toString());
        futures.add(ecs.submit(new DownloadTask(segment, segmentFile, listener, options.getEmbedCode())));
      }
    }

    if (fileAlreadyExistCount > 0) {
      DebugMode.logD(TAG, "Already downloaded files:" + fileAlreadyExistCount);
      handleSegmentDownloaded(fileAlreadyExistCount);
    }
    try {
      for (int i = 0; i < futures.size(); ++i) {
        if (!ecs.take().get()) {
          success = false;
          break;
        } else {
          handleSegmentDownloaded(1);
        }
      }
    } catch (InterruptedException | ExecutionException ex) {
      success = false;
    } finally {
      for (Future<Boolean> f : futures) {
        f.cancel(true);
      }
    }
    return success;
  }

  /**
   * Add segments from an MPD represetnation
   *
   * @param representation the chosen representation
   * @param duration       the duration of the period, this is required to compute the last segment
   * @param results        the list to add segments
   */
  private void addRepresentationSegments(Representation representation, long duration, List<List<String>> results) {
    if (representation != null) {
      RangedUri initUri = representation.getInitializationUri();
      String uriString = initUri.resolveUriString(representation.baseUrl);
      results.get(0).add(uriString);

      DashSegmentIndex index = representation.getIndex();
      int firstIndex = index.getFirstSegmentNum();
      int lastIndex = index.getSegmentCount(duration);

      for (int segIndex = firstIndex; segIndex <= lastIndex; ++segIndex) {
        RangedUri segUri = index.getSegmentUrl(segIndex);
        if (segUri != null) {
          String segUriString = segUri.resolveUriString(representation.baseUrl);
          results.get(1).add(segUriString);
        }
      }
    }
  }

  /**
   * Start license request, currently it requires to create exoplayer
   *
   * @param mpdFile the MPD file
   * @return true on success, false otherwise
   */
  private Boolean requestLicense(final String mpdUrl, final File mpdFile, final String licenseServerUrl) {
    Handler handler = new Handler(context.getMainLooper());
    handler.post(new Runnable() {
      @Override
      public void run() {
        ExoStreamPlayer player = new ExoStreamPlayer();
        player.startLicenseRequest(context, mpdUrl, mpdFile, licenseServerUrl, listener, options.getEmbedCode());
      }
    });

    return true;
  }

  /**
   * This is get called when a single segment download is completed, update/notify progress
   */
  private void handleSegmentDownloaded(int completedSegments) {
    completedSegmentsCount += completedSegments;
    int percentage = 1 + 98 * completedSegmentsCount / segmentsCount;
    DebugMode.logD(TAG, "Downloaded " + completedSegmentsCount + " out of " + segmentsCount + " segments, percent: " + percentage);
    if (listener != null) {
      listener.onPercentage(percentage);
    }
  }

  private Video authorization() {
    Options configOptions = new Options.Builder()
        .setConnectionTimeout(options.getConnectionTimeout())
        .setReadTimeout(options.getReadTimeout())
        .build();
    PlayerDomain playerDomain = new PlayerDomain(options.getDomain());
    OoyalaAPIClient api = new OoyalaAPIClient(options.getPcode(), playerDomain, options.getTokenGenerator(), configOptions);

    Set<String> supportedFormats = new HashSet<>();
    supportedFormats.add(Stream.DELIVERY_TYPE_DASH);
    PlayerInfo playerInfo = new DefaultPlayerInfo(supportedFormats, null);
    try {
      return api.authorizeDownload(options.getEmbedCode(), playerInfo);
    } catch (OoyalaException ex) {
      handleError(ex);
    }

    return null;
  }
}

package com.ooyala.sample.players;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ooyala.android.EmbedTokenGenerator;
import com.ooyala.android.EmbedTokenGeneratorCallback;
import com.ooyala.android.EmbeddedSecureURLGenerator;
import com.ooyala.android.offline.DownloadListener;
import com.ooyala.android.offline.Downloader;
import com.ooyala.android.offline.DownloaderFactory;
import com.ooyala.android.offline.TaskInfo;
import com.ooyala.android.offline.options.OoyalaDownloadOptions;
import com.ooyala.android.util.DebugMode;
import com.ooyala.sample.DemoApplication;
import com.ooyala.sample.R;
import com.ooyala.sample.utils.Utils;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class OoyalaOfflineDownloadActivity extends Activity implements DownloadListener, DialogInterface.OnClickListener,
		EmbedTokenGenerator {
	final String TAG = this.getClass().toString();

	private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
	private static final int UPDATE_TIME = 1000;
	private static final int MAX_RETRY_COUNT = 3;
	private static final float MIN_PROGRESS = 0.f;
	private static final float MAX_PROGRESS = 100.f;
	private static TaskInfo TASK_INFO;

	private String EMBED;
	private String PCODE;
	private String DOMAIN;

	// An account ID, if you are using Concurrent Streams or Entitlements
	private String ACCOUNT_ID = "";
	private String APIKEY = "";
	private String SECRET = "";

	private Downloader downloader;
	private OoyalaDownloadOptions options;
	private Collection<Integer> bitrateValues = new ArrayList<>();
	private File folder;
	private int retryCount;

	private View dialogView;
	private TextView progressView;
	private ListView bitrateList;
	private ArrayAdapter<String> bitrateTitles;
	private AlertDialog.Builder builder;

	private Handler handler;
	private Runnable updateProgress = () -> {
		handler.postDelayed(this.updateProgress, UPDATE_TIME);
		if (TASK_INFO != null) {
			float progress = Utils.clamp(downloader.getDownloadPercentage(TASK_INFO.taskId),
					MIN_PROGRESS, MAX_PROGRESS);
			if (progress > MIN_PROGRESS && progress <= MAX_PROGRESS
					&& TASK_INFO.state == TaskInfo.STATE_STARTED) {
				String text = getString(R.string.progress_text, progress);
				progressView.setText(text);
			}
		}
	};

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getIntent().getExtras().getString("selection_name"));
		setContentView(R.layout.ooyala_offline_downloader);

		EMBED = getIntent().getExtras().getString("embed_code");
		PCODE = getIntent().getExtras().getString("pcode");
		DOMAIN = getIntent().getExtras().getString("domain");
		APIKEY = getIntent().getExtras().getString("api_key");
		SECRET = getIntent().getExtras().getString("secret_key");
		ACCOUNT_ID = getIntent().getExtras().getString("account_id");

		progressView = findViewById(R.id.progress_text);
		handler = new Handler(getMainLooper());

		folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
		options = new OoyalaDownloadOptions.Builder(PCODE, EMBED, DOMAIN, folder)
				.setEmbedTokenGenerator(this)
				.build();
		DownloaderFactory factory = new DownloaderFactory();
		downloader = factory.createOoyalaDownloader(this, ((DemoApplication) getApplication()).getDownloadCache(), options);

		Button startButton = findViewById(R.id.start_button);
		startButton.setOnClickListener(v -> {
			if (ContextCompat.checkSelfPermission(OoyalaOfflineDownloadActivity.this,
					WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(OoyalaOfflineDownloadActivity.this,
						new String[]{WRITE_EXTERNAL_STORAGE},
						PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
			} else {
				downloader.startDownload();
				handler.post(updateProgress);
			}
		});

		Button pauseButton = findViewById(R.id.pause_button);
		pauseButton.setOnClickListener(v -> {
			handler.removeCallbacks(updateProgress);
			float progress = Utils.clamp(downloader.getDownloadPercentage(TASK_INFO.taskId),
					MIN_PROGRESS, MAX_PROGRESS);
			String text = getString(R.string.paused_text, progress);
			progressView.setText(text);
			downloader.cancel();
		});

		Button deleteButton = findViewById(R.id.delete_button);
		deleteButton.setOnClickListener(v -> {
			if (TASK_INFO == null) {
				progressView.setText(R.string.deletion_completed_text);
				return;
			}
			downloader.cancel();
			downloader.delete(TASK_INFO.taskId);
		});

		Button requestButton = findViewById(R.id.request_bitrate_and_start_button);
		requestButton.setOnClickListener(v -> downloader.requestBitrates());
	}

	@Override
	protected void onStart() {
		super.onStart();
		downloader.addListener(this);
		handler.post(updateProgress);
	}

	@Override
	protected void onStop() {
		super.onStop();
		downloader.removeListener();
		handler.removeCallbacks(updateProgress);
	}

	@Override
	public void onStarted(TaskInfo taskInfo) {
		if (!taskInfo.isRemoveAction) {
			OoyalaOfflineDownloadActivity.TASK_INFO = taskInfo;
		}
	}

	@Override
	public void onCompleted(TaskInfo taskInfo) {
		handler.post(() -> progressView.setText(getString(R.string.completed_text)));
	}

	@Override
	public void onCanceled(TaskInfo taskInfo) {
		handler.post(() -> progressView.setText(getString(R.string.canceled_text)));
	}

	@Override
	public void onDeleted(TaskInfo taskInfo) {
		onDeletion(true);
	}

	@Override
	public void onFailed(TaskInfo taskInfo, final Throwable ex) {
		if (taskInfo.isRemoveAction) {
			onDeletion(false);
		} else {
			if (retryCount < MAX_RETRY_COUNT) {
				DebugMode.logD(TAG, "Retrying to download : " + retryCount);

				retryCount++;
				handler.post(() -> {
					progressView.setText(getString(R.string.retry_text, taskInfo.downloadPercentage));
					downloader.startDownload();
				});
			} else {
				handler.post(() -> progressView.setText(getString(R.string.error_text, ex.getLocalizedMessage())));
			}
		}
	}

	@Override
	public void onBitratesObtained(HashMap<String, Integer> bitrates) {
		builder = new AlertDialog.Builder(this)
				.setTitle(R.string.download_description)
				.setPositiveButton(android.R.string.ok, this)
				.setNegativeButton(android.R.string.cancel, null);

		LayoutInflater dialogInflater = LayoutInflater.from(builder.getContext());
		dialogView = dialogInflater.inflate(R.layout.download_dialog, null);

		bitrateTitles = new ArrayAdapter<>(
				builder.getContext(), android.R.layout.simple_list_item_single_choice);
		bitrateTitles.addAll(bitrates.keySet());

		bitrateList = dialogView.findViewById(R.id.bitrate_list);
		bitrateList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		bitrateList.setAdapter(bitrateTitles);

		if (!bitrates.isEmpty()) {
			bitrateValues.addAll(bitrates.values());
			builder.setView(dialogView);
		}
		builder.create().show();
	}

	private void onDeletion(final boolean success) {
		handler.post(() -> progressView.setText(success ? R.string.deletion_completed_text : R.string.deletion_failed_text));
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
		StringBuilder embedCodesString = new StringBuilder();
		for (String ec : embedCodes) {
			if (ec.equals("")) embedCodesString.append(",");
			embedCodesString.append(ec);
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

	@Override
	public void onClick(DialogInterface dialog, int which) {
		int bitrate = Integer.MAX_VALUE;
		Iterator<Integer> it = bitrateValues.iterator();
		for (int i = 0; i < bitrateList.getChildCount(); i++) {
			int currentBitrate = it.next();
			if (bitrateList.isItemChecked(i)) {
				bitrate = currentBitrate;
				break;
			}
		}
		options = new OoyalaDownloadOptions.Builder(PCODE, EMBED, DOMAIN, folder)
				.setEmbedTokenGenerator(this)
				.setBitrate(bitrate)
				.build();
		downloader.setOptions(options);
		downloader.startDownload();
		handler.post(updateProgress);
	}
}

package com.ooyala.sample.lists;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ooyala.android.offline.DashDownloader;
import com.ooyala.android.offline.DashOptions;
import com.ooyala.sample.R;
import com.ooyala.sample.players.OfflineSkinPlayerActivity;
import com.ooyala.sample.players.OoyalaSkinOPTPlayerActivity;
import com.ooyala.sample.utils.DownloadState;
import com.ooyala.sample.utils.DownloadableAsset;
import com.ooyala.sample.utils.PlayerSelectionOption;
import com.ooyala.sample.utils.TokenGenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.ooyala.sample.utils.DownloadState.*;

public class DownloadSerializationActivity extends Activity implements DashDownloader.Listener {

    public final static String getName() {
        return "Download Serialization";
    }
    final String TAG = this.getClass().toString();
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    final String DOMAIN = "http://ooyala.com";

    //Here you can define how many simultaneous downloads can happen.
    protected  final int DOWNLOADS_ALLOWED = 3;

    protected Handler handler;
    protected DashDownloader downloader;
    protected LinearLayout linearLayoutVertical;

    private List<DownloadableAsset> downloadQueue;
    private List<DownloadableAsset> onHoldQueue;
    private List<DownloadableAsset> assets;
    private String currentPCode;
    final File folder = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_activity);

        assets = new ArrayList<>();
        // if you would like to use OPT, please fill the gaps in the following order:
        // new PlayerSelectionOption(embedCode, pcode, apiKey, secretKey, accountId, domain, activity)

        assets.add(new DownloadableAsset("HEVC", new PlayerSelectionOption("hrODl0ZTE6X4qlmpiUGbx84nI9Uva6TE", "BjcWYyOu1KK2DiKOkF41Z2k0X57l", "", "", "", DOMAIN, OoyalaSkinOPTPlayerActivity.class)));
        assets.add(new DownloadableAsset("Widevine DASH", new PlayerSelectionOption("BuY3RsMzE61s6nTC5ct6R-DOapuPt5f7", "FoeG863GnBL4IhhlFC1Q2jqbkH9m", "", "", "", DOMAIN, OoyalaSkinOPTPlayerActivity.class)));
        assets.add(new DownloadableAsset("Playready + DASH", new PlayerSelectionOption("tpYTlnMzE6m4S-a1Yonj5ydnVwQXBGyI", "FoeG863GnBL4IhhlFC1Q2jqbkH9m", "", "", "", DOMAIN, OoyalaSkinOPTPlayerActivity.class)));
        assets.add(new DownloadableAsset("OTS Test", new PlayerSelectionOption("04c3IyYzE6WLNzHuPDcBrMgUsDP7nTYq", "35d4ec4fa05645289a127682acc29325", "", "", "", DOMAIN, OoyalaSkinOPTPlayerActivity.class)));
        assets.add(new DownloadableAsset("Enterprise OTS Test", new PlayerSelectionOption("hsdHIyYzE668escyHgrFiednk4831Un3", "529095912bec4ab7aefe23d6b11fdf2a", "", "", "", DOMAIN, OoyalaSkinOPTPlayerActivity.class)));
        assets.add(new DownloadableAsset("Logan", new PlayerSelectionOption("dqcGlqOTE6U2FJ8LTxvDV9P_GPzeae_G", "c7ed739d6ef43f1a13577fac2109d22", "", "", "", DOMAIN, OoyalaSkinOPTPlayerActivity.class)));
        assets.add(new DownloadableAsset("Dron construction", new PlayerSelectionOption("lvN3lpZDE604CHXxbOwswqz4daAPgDq7", "1mbWoyOuPzcGLtowbsFNlVHbepva", "", "", "", DOMAIN, OoyalaSkinOPTPlayerActivity.class)));
        assets.add(new DownloadableAsset("Waves in sunset", new PlayerSelectionOption("hqZHQ1YjE6tZiEdyKMMiY-kHlxPaFqpG", "1mbWoyOuPzcGLtowbsFNlVHbepva", "", "", "", DOMAIN, OoyalaSkinOPTPlayerActivity.class)));
        assets.add(new DownloadableAsset("Sunrise", new PlayerSelectionOption("44NHQ1YjE6YGVqOuKUkkmfqGrh2gjTBN", "1mbWoyOuPzcGLtowbsFNlVHbepva", "", "", "", DOMAIN, OoyalaSkinOPTPlayerActivity.class)));
        assets.add(new DownloadableAsset("Jellyfish", new PlayerSelectionOption("1kZHBpZDE6y90G_NyEf6-tvG6_-BWCd-", "1mbWoyOuPzcGLtowbsFNlVHbepva", "", "", "", DOMAIN, OoyalaSkinOPTPlayerActivity.class)));
        assets.add(new DownloadableAsset("Decomposing doll", new PlayerSelectionOption("lxcmI2YjE6WCVstI4M1RcaNhyujitVsO", "1mbWoyOuPzcGLtowbsFNlVHbepva", "", "", "", DOMAIN, OoyalaSkinOPTPlayerActivity.class)));
        assets.add(new DownloadableAsset("Tagsgraphic", new PlayerSelectionOption("hhY3BpZDE60HyUMUrMTU3bX0AXMBFk4y", "1mbWoyOuPzcGLtowbsFNlVHbepva", "", "", "", DOMAIN, OoyalaSkinOPTPlayerActivity.class)));
        assets.add(new DownloadableAsset("Clouds", new PlayerSelectionOption("M2Y3BpZDE6FyMp_xQEaL_ZPwNOXQnQXL", "1mbWoyOuPzcGLtowbsFNlVHbepva", "", "", "", DOMAIN, OoyalaSkinOPTPlayerActivity.class)));
        assets.add(new DownloadableAsset("Swimming", new PlayerSelectionOption("QyY3BpZDE6pBgGxV4OCGykDKBbj6ZuMs", "1mbWoyOuPzcGLtowbsFNlVHbepva", "", "", "", DOMAIN, OoyalaSkinOPTPlayerActivity.class)));
        assets.add(new DownloadableAsset("Butterfly", new PlayerSelectionOption("N4YnBpZDE6P7EIno_y74X9GNhQdJQABt", "1mbWoyOuPzcGLtowbsFNlVHbepva", "", "", "", DOMAIN, OoyalaSkinOPTPlayerActivity.class)));
        assets.add(new DownloadableAsset("Big Buck Bunny", new PlayerSelectionOption("ljNDE2YzE6KTFgw7hfC6IeXZJ_UBlVSK", "1mbWoyOuPzcGLtowbsFNlVHbepva", "", "", "", DOMAIN, OoyalaSkinOPTPlayerActivity.class)));

        handler = new Handler(getMainLooper());

        downloadQueue = new ArrayList<>();
        onHoldQueue = new ArrayList<>();

        // Use this DashOptions to download an asset without OPT
//    DashOptions options = new DashOptions.Builder(PCODE, EMBED, DOMAIN, folder).build();


        linearLayoutVertical = findViewById(R.id.linearLayoutVertical);

        //Adding items to linearLayout
        for (final DownloadableAsset asset : assets) {
            final PlayerSelectionOption option = asset.getPlayerSelectionOption();

            LinearLayout linearLayout = new LinearLayout(this);

            //Displaying asset name
            TextView itemText = new TextView(this);
            itemText.setText(asset.getName());
            itemText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Launch an offline player with selected embed code
                    Intent intent = new Intent(DownloadSerializationActivity.this, option.getActivity());
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    intent.putExtra("embed_code", option.getEmbedCode());
                    intent.putExtra("pcode", option.getPcode());
                    intent.putExtra("api_key", option.getApiKey());
                    intent.putExtra("secret_key", option.getSecretKey());
                    intent.putExtra("account_id", option.getAccountId());
                    intent.putExtra("domain", option.getDomain());
                    intent.putExtra("selection_name", asset.getName());
                    startActivity(intent);
                }
            });
            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(700, ViewGroup.LayoutParams.WRAP_CONTENT, 0.9F);
            linearLayout.addView(itemText, 0, textParams);

            //This button will start an asset download
            Button startButton = new Button(this);
            startButton.setId(option.getEmbedCode().hashCode());
            startButton.setBackgroundResource(android.R.drawable.stat_sys_download);
            LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(120, 120, 0.1F);
            linearLayout.addView(startButton, 1, buttonParams);
            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ContextCompat.checkSelfPermission(DownloadSerializationActivity.this, WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(DownloadSerializationActivity.this, new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    } else {
                        // Use this DashOptions to download an asset with OPT
                        currentPCode = option.getPcode();
                        DashOptions options = new DashOptions.Builder(currentPCode, option.getEmbedCode(), DOMAIN, folder)
                            .setEmbedTokenGenerator(new TokenGenerator(asset.getPlayerSelectionOption()))
                            .build();
                        downloader = new DashDownloader(DownloadSerializationActivity.this, options, DownloadSerializationActivity.this);

                        if (downloadQueue.size() < DOWNLOADS_ALLOWED) {
                            downloadQueue.add(asset);
                            downloader.startDownload();
                            asset.setStatus(DOWNLOADING);
                            Log.d("Log", "Start downloading : " + option.getEmbedCode());
                            Log.d("Log", "Download queue size:" + downloadQueue.size());
                            Log.d("Log", "On hold queue size: " + onHoldQueue.size());
                        } else {
                            onHoldQueue.add(asset);
                            asset.setStatus(WAITING);
                            Log.d("Log", "Waiting to download : " + option.getEmbedCode());
                            Log.d("Log", "On hold queue size: " + onHoldQueue.size());
                        }
                        updateDownloadButton(asset.getPlayerSelectionOption().getEmbedCode(), asset.getStatus());
                    }
                }
            });

            linearLayoutVertical.addView(linearLayout);
            //Updating button state if asset is already downloaded in device
            updateDownloadButton(asset.getPlayerSelectionOption().getEmbedCode(), asset.getStatus());
        }

        //This deletes all assets already downloaded
        Button deleteButton = (Button)findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDownloadedAssets();
                if (downloader != null && downloader.deleteAll()) {
                    //we reset download and waiting queue
                    downloadQueue = new ArrayList<>();
                    onHoldQueue = new ArrayList<>();
                    Log.d("Log","Deletion completed");
                    deleteDownloadedAssets();
                    Log.d("Log", "Both queues size is 0 ");
                } else {
                    Log.d("Log","Downloader was never started");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        for (final DownloadableAsset asset : assets) {
            PlayerSelectionOption option = asset.getPlayerSelectionOption();
            File folder = new File(android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_MOVIES), option.getEmbedCode());
            if (folder.exists() && folder.length() > 0){
                asset.setStatus(COMPLETED);
                //Updating button state if asset is already downloaded in device
                updateDownloadButton(asset.getPlayerSelectionOption().getEmbedCode(), asset.getStatus());
            }
        }
    }

    /**
     * This reviews on hold queue to get the next asset to be downloaded
     */
    public void getNextAssetToDownload(){
        Iterator<DownloadableAsset> iterator = onHoldQueue.iterator();
        while(iterator.hasNext()){
            DownloadableAsset asset = iterator.next();
            if (downloadQueue.size() < DOWNLOADS_ALLOWED) {
                PlayerSelectionOption option = asset.getPlayerSelectionOption();
                DashOptions options = new DashOptions.Builder(option.getPcode(), option.getEmbedCode(), DOMAIN, folder)
                    .setEmbedTokenGenerator(new TokenGenerator(option))
                    .build();
                downloader = new DashDownloader(DownloadSerializationActivity.this, options, DownloadSerializationActivity.this);
                downloader.startDownload();
                downloadQueue.add(asset);
                asset.setStatus(WAITING);
                updateDownloadButton(asset.getPlayerSelectionOption().getEmbedCode(), asset.getStatus());
                Log.d("Log", "\n Downloading " + asset.getName());
                iterator.remove();
                Log.d("Log", "Start downloading : " + option.getEmbedCode());
                Log.d("Log", "Download queue size:" + downloadQueue.size());
                Log.d("Log","On hold queue size: " + onHoldQueue.size());
            }
        }
    }

    /**
     * This updates the UI buttons with the asset status
     * @param embedCode downloadable embedCode asset
     * @param status downloadable status
     */
    public void updateDownloadButton(String embedCode, DownloadState status) {
        for (int i = 0; i < linearLayoutVertical.getChildCount(); i++) {
            if (linearLayoutVertical.getChildAt(i) instanceof LinearLayout){
                LinearLayout l = (LinearLayout)linearLayoutVertical.getChildAt(i);
                Button b = (Button) l.getChildAt(1);
                if (b.getId() == embedCode.hashCode()) {
                    switch (status) {
                        case COMPLETED:
                            b.setBackgroundResource(android.R.drawable.checkbox_on_background);
                            break;
                        case WAITING:
                        case DOWNLOADING:
                            b.setBackgroundResource(android.R.drawable.ic_popup_sync);
                            break;
                        case ERROR:
                        case ABORT:
                            b.setBackgroundResource(android.R.drawable.stat_notify_error);
                            break;
                        case NOT_DOWNLOADED:
                            b.setBackgroundResource(android.R.drawable.stat_sys_download);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    /**
     * This deletes all downloaded assets
     */
    public void deleteDownloadedAssets(){
        boolean result;
        for (final DownloadableAsset asset : assets) {
            PlayerSelectionOption option = asset.getPlayerSelectionOption();
            File folder = new File(android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_MOVIES), option.getEmbedCode());
            if (folder.exists() && folder.isDirectory() && folder.listFiles() != null){
                for (File file : folder.listFiles()) {
                    result = file.delete();
                    Log.d("Log", "Deletion of" + asset.getName() + ": " + result);
                }
                folder.delete();
            }
            asset.setStatus(NOT_DOWNLOADED);
            updateDownloadButton(asset.getPlayerSelectionOption().getEmbedCode(), asset.getStatus());
        }
    }


    @Override
    public void onCompletion(final String embedCode) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                long expiration = downloader.getLicenseExpirationDate();
                //String expirationString = expiration == DashDownloader.INFINITE_DURATION ? "infinite" : String.valueOf(expiration);
                Log.d("Log", getAssetNameByEmbedCode(embedCode) + " download completed!");
                updateDownloadButton(embedCode, COMPLETED);
                removeAssetByEmbedCode(embedCode,downloadQueue);
                getNextAssetToDownload();
            }
        });
    }

    public void onAbort(final String embedCode) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d("Log", "Download aborted " + embedCode);
                removeAssetByEmbedCode(embedCode,downloadQueue);
                updateDownloadButton(embedCode, ABORT);
                getNextAssetToDownload();
            }
        });
    }

    public void onError(final String embedCode, final Exception ex) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                String errorMessage = "Error on " + embedCode + ": "+ ex.getLocalizedMessage();
                Log.d("Log","\n" + errorMessage);
                Toast.makeText(DownloadSerializationActivity.this, errorMessage, Toast.LENGTH_LONG).show();

                File folder = new File(android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_MOVIES), embedCode);
                folder.delete();
                removeAssetByEmbedCode(embedCode,downloadQueue);
                updateDownloadButton(embedCode, ERROR);
                getNextAssetToDownload();
            }
        });
    }

    public void onPercentage(int percentCompleted) {
        final String progress = " on progress:" + percentCompleted;

        handler.post(new Runnable() {
            @Override
            public void run() {
                //progressView.setText("\n" + progress + progressView.getText() + "\n" + progressView.getText());
            }
        });
    }

    private void onDeletion(final boolean success) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d("Log", success ? " Deletion completed" : "Deletion failed");
            }
        });
    }

    /**
     * Used to remove an asset from any list: download queue or on hold queue.
     * @param embedCode asset embedCode
     * @param assetsList list to remove asset
     * @return success
     */
    public boolean removeAssetByEmbedCode(String embedCode, List<DownloadableAsset> assetsList){
        Iterator<DownloadableAsset> iterator = assetsList.iterator();
        while (iterator.hasNext()) {
            DownloadableAsset asset = iterator.next();
            PlayerSelectionOption option = asset.getPlayerSelectionOption();
            if (option.getEmbedCode().equals(embedCode)) {
                iterator.remove();
                Log.d("Log", "Removed asset from : " + assetsList);
                Log.d("Log","Queue size: " + assetsList.size());
                return true;
            }
        }
        return false;
    }

    /**
     * Used to retrieve asset name to be displayed on screen
     * @param embedCode assets embedCode
     * @return
     */
    public String getAssetNameByEmbedCode(String embedCode){
        Iterator<DownloadableAsset> iterator = assets.iterator();
        while (iterator.hasNext()) {
            DownloadableAsset asset = iterator.next();
            PlayerSelectionOption option = asset.getPlayerSelectionOption();
            if (option.getEmbedCode().equals(embedCode)) {
                return asset.getName();
            }
        }
        return null;
    }
}

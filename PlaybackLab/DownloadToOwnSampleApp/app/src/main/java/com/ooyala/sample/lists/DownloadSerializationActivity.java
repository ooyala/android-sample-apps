package com.ooyala.sample.lists;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ooyala.android.EmbedTokenGenerator;
import com.ooyala.android.EmbedTokenGeneratorCallback;
import com.ooyala.android.EmbeddedSecureURLGenerator;
import com.ooyala.android.offline.DashDownloader;
import com.ooyala.android.offline.DashOptions;
import com.ooyala.android.util.SDCardLogcatOoyalaEventsLogger;
import com.ooyala.sample.R;
import com.ooyala.sample.players.OfflineSkinPlayerActivity;
import com.ooyala.sample.utils.DownloadableAsset;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class DownloadSerializationActivity extends Activity implements DashDownloader.Listener, EmbedTokenGenerator {

    public final static String getName() {
        return "Download Serialization";
    }
    final String TAG = this.getClass().toString();
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    String PCODE = "";
    final String DOMAIN = "http://ooyala.com";

    //Here you can define how many simultaneous downloads can happen.
    protected  final int DOWNLOADS_ALLOWED = 3;

    //Defining assets states
    protected final int NOT_DOWNLOADED = 0;
    protected final int DOWNLOADING = 1;
    protected final int WAITING = 2;
    protected final int COMPLETED = 3;
    protected final int ERROR = 4;
    protected final int ABORT = 5;


    // Write the sdk events text along with events count to log file in sdcard if the log file already exists
    SDCardLogcatOoyalaEventsLogger Playbacklog= new SDCardLogcatOoyalaEventsLogger();

    protected Handler handler;
    protected DashDownloader downloader;
    protected LinearLayout linearLayoutVertical;

    // An account ID, if you are using Concurrent Streams or Entitlements
    private final String ACCOUNT_ID = "";
    private final String APIKEY = "";
    private final String SECRET = "";


    private List<DownloadableAsset> downloadQueue;
    private List<DownloadableAsset> onHoldQueue;
    private List<DownloadableAsset> assets;
    final File folder = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_activity);

        assets = new ArrayList<>();
        assets.add(new DownloadableAsset("Widevine DASH", "BuY3RsMzE61s6nTC5ct6R-DOapuPt5f7","FoeG863GnBL4IhhlFC1Q2jqbkH9m"));
        assets.add(new DownloadableAsset("Playready + DASH", "tpYTlnMzE6m4S-a1Yonj5ydnVwQXBGyI", "FoeG863GnBL4IhhlFC1Q2jqbkH9m"));
        assets.add(new DownloadableAsset("OTS Test", "04c3IyYzE6WLNzHuPDcBrMgUsDP7nTYq", "35d4ec4fa05645289a127682acc29325"));
        assets.add(new DownloadableAsset("Enterprise OTS Test", "hsdHIyYzE668escyHgrFiednk4831Un3", "529095912bec4ab7aefe23d6b11fdf2a"));
        assets.add(new DownloadableAsset("Logan", "dqcGlqOTE6U2FJ8LTxvDV9P_GPzeae_G", "c7ed739d6ef43f1a13577fac2109d22"));
        assets.add(new DownloadableAsset("Dron construction", "lvN3lpZDE604CHXxbOwswqz4daAPgDq7", "1mbWoyOuPzcGLtowbsFNlVHbepva"));
        assets.add(new DownloadableAsset("Waves in sunset", "hqZHQ1YjE6tZiEdyKMMiY-kHlxPaFqpG", "1mbWoyOuPzcGLtowbsFNlVHbepva"));
        assets.add(new DownloadableAsset("Sunrise", "44NHQ1YjE6YGVqOuKUkkmfqGrh2gjTBN", "1mbWoyOuPzcGLtowbsFNlVHbepva"));
        assets.add(new DownloadableAsset("Jellyfish", "1kZHBpZDE6y90G_NyEf6-tvG6_-BWCd-", "1mbWoyOuPzcGLtowbsFNlVHbepva"));
        assets.add(new DownloadableAsset("Decomposing doll", "lxcmI2YjE6WCVstI4M1RcaNhyujitVsO", "1mbWoyOuPzcGLtowbsFNlVHbepva"));
        assets.add(new DownloadableAsset("Tagsgraphic", "hhY3BpZDE60HyUMUrMTU3bX0AXMBFk4y", "1mbWoyOuPzcGLtowbsFNlVHbepva"));
        assets.add(new DownloadableAsset("Clouds", "M2Y3BpZDE6FyMp_xQEaL_ZPwNOXQnQXL", "1mbWoyOuPzcGLtowbsFNlVHbepva"));
        assets.add(new DownloadableAsset("Swimming", "QyY3BpZDE6pBgGxV4OCGykDKBbj6ZuMs", "1mbWoyOuPzcGLtowbsFNlVHbepva"));
        assets.add(new DownloadableAsset("Butterfly", "N4YnBpZDE6P7EIno_y74X9GNhQdJQABt", "1mbWoyOuPzcGLtowbsFNlVHbepva"));
        assets.add(new DownloadableAsset("Big Buck Bunny ", "ljNDE2YzE6KTFgw7hfC6IeXZJ_UBlVSK", "1mbWoyOuPzcGLtowbsFNlVHbepva"));

        handler = new Handler(getMainLooper());

        downloadQueue = new ArrayList<>();
        onHoldQueue = new ArrayList<>();

        // Use this DashOptions to download an asset without OPT
//    DashOptions options = new DashOptions.Builder(PCODE, EMBED, DOMAIN, folder).build();


        linearLayoutVertical = findViewById(R.id.linearLayoutVertical);

        //Adding items to linearLayout
        for (final DownloadableAsset a :assets) {
            LinearLayout linearLayout = new LinearLayout(this);

            //Displaying asset name
            TextView itemText = new TextView(this);
            itemText.setText(a.getName());
            itemText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Launch an offline player with selected embed code
                    Intent intent = new Intent(DownloadSerializationActivity.this, OfflineSkinPlayerActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    intent.putExtra("embed_code", a.getEmbedCode());
                    intent.putExtra("pcode", a.getpCode());
                    intent.putExtra("domain", DOMAIN);
                    intent.putExtra("selection_name", a.getName());
                    startActivity(intent);
                }
            });
            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(700, ViewGroup.LayoutParams.WRAP_CONTENT,0.9F);
            linearLayout.addView(itemText, 0, textParams);

            //This button will start an asset download
            Button startButton = new Button(this);
            startButton.setId(a.getEmbedCode().hashCode());
            startButton.setBackgroundResource(android.R.drawable.stat_sys_download);
            LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(120, 120,0.1F);
            linearLayout.addView(startButton, 1, buttonParams);
            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ContextCompat.checkSelfPermission(DownloadSerializationActivity.this, WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(DownloadSerializationActivity.this, new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    } else {
                        // Use this DashOptions to download an asset with OPT
                        DashOptions options = new DashOptions.Builder(a.getpCode(), a.getEmbedCode(), DOMAIN, folder).setEmbedTokenGenerator(DownloadSerializationActivity.this).build();
                        downloader = new DashDownloader(DownloadSerializationActivity.this, options, DownloadSerializationActivity.this);

                        if (downloadQueue.size() < DOWNLOADS_ALLOWED) {
                            downloadQueue.add(a);
                            downloader.startDownload();
                            a.setStatus(DOWNLOADING);
                            Log.d("Log", "Start downloading : " + a.getEmbedCode());
                            Log.d("Log", "Download queue size:" + downloadQueue.size());
                            Log.d("Log", "On hold queue size: " + onHoldQueue.size());
                        } else {
                            onHoldQueue.add(a);
                            a.setStatus(WAITING);
                            Log.d("Log", "Waiting to download : " + a.getEmbedCode());
                            Log.d("Log", "On hold queue size: " + onHoldQueue.size());
                        }
                        updateDownloadButton(a);
                    }
                }
            });

            linearLayoutVertical.addView(linearLayout);
            //Updating button state if asset is already downloaded in device
            updateDownloadButton(a);
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

        for (final DownloadableAsset a :assets) {
            File folder = new File(android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_MOVIES), a.getEmbedCode());
            if (folder.exists() && folder.length() > 0){
                a.setStatus(COMPLETED);
                //Updating button state if asset is already downloaded in device
                updateDownloadButton(a);
            }
        }
    }

    /**
     * This reviews on hold queue to get the next asset to be downloaded
     */
    public void getNextAssetToDownload(){
        Iterator<DownloadableAsset> iterator = onHoldQueue.iterator();
        while(iterator.hasNext()){
            DownloadableAsset a = iterator.next();
            if (downloadQueue.size() < DOWNLOADS_ALLOWED){
                DashOptions options = new DashOptions.Builder(a.getpCode(), a.getEmbedCode(), DOMAIN, folder).setEmbedTokenGenerator(DownloadSerializationActivity.this).build();
                downloader = new DashDownloader(DownloadSerializationActivity.this, options, DownloadSerializationActivity.this);
                downloader.startDownload();
                downloadQueue.add(a);
                a.setStatus(WAITING);
                updateDownloadButton(a);
                Log.d("Log","\n Downloading " + a.getName());
                iterator.remove();
                Log.d("Log", "Start downloading : " + a.getEmbedCode());
                Log.d("Log", "Download queue size:" + downloadQueue.size());
                Log.d("Log","On hold queue size: " + onHoldQueue.size());
            }
        }
    }

    /**
     * This updates the UI buttons with the asset status
     * @param asset downloadable asset
     */
    public void updateDownloadButton(DownloadableAsset asset){
        for (int i=0; i<linearLayoutVertical.getChildCount(); i++){
            if (linearLayoutVertical.getChildAt(i) instanceof LinearLayout){
                LinearLayout l = (LinearLayout)linearLayoutVertical.getChildAt(i);
                Button b = (Button) l.getChildAt(1);
                if (b.getId() == asset.getEmbedCode().hashCode()){
                    int status = asset.getStatus();
                    if (status == COMPLETED ){
                        b.setBackgroundResource(android.R.drawable.checkbox_on_background);
                    }if (status == WAITING || status == DOWNLOADING){
                        b.setBackgroundResource(android.R.drawable.ic_popup_sync);
                    }else if (status == ERROR || status == ABORT){
                        b.setBackgroundResource(android.R.drawable.stat_notify_error);
                    }else if (status == NOT_DOWNLOADED){
                        b.setBackgroundResource(android.R.drawable.stat_sys_download);
                    }
                }
            }
        }
    }

    /**
     * This deletes all downloaded assets
     */
    public void deleteDownloadedAssets(){
        boolean result = false;
        for (final DownloadableAsset a :assets) {
            File folder = new File(android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_MOVIES), a.getEmbedCode());
            if (folder.exists() && folder.isDirectory()){
                for (File f :folder.listFiles()) {
                    result = f.delete();
                    Log.d("Log", "Deletion of" + a.getName() + ": " + result);
                }
                folder.delete();
            }
            a.setStatus(NOT_DOWNLOADED);
            updateDownloadButton(a);
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
                updateDownloadButton(new DownloadableAsset(embedCode, COMPLETED));
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
                updateDownloadButton(new DownloadableAsset(embedCode, ABORT));
                getNextAssetToDownload();
            }
        });
    }

    public void onError(final String embedCode, final Exception ex) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d("Log","\n Error on " + embedCode + ": "+ ex.getLocalizedMessage());
                removeAssetByEmbedCode(embedCode,downloadQueue);
                updateDownloadButton(new DownloadableAsset(embedCode, ERROR));
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
            DownloadableAsset a = iterator.next();
            if (a.getEmbedCode() == embedCode) {
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
            DownloadableAsset a = iterator.next();
            if (a.getEmbedCode() == embedCode) {
                return a.getName();
            }
        }
        return null;
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

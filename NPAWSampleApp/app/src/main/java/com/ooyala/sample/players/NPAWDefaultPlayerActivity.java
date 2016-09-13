package com.ooyala.sample.players;

  import android.app.Activity;
  import android.os.Bundle;
  import android.util.Log;

  import com.npaw.youbora.plugins.PluginOoyala;
  import com.npaw.youbora.youboralib.utils.YBLog;
  import com.ooyala.android.OoyalaPlayer;
  import com.ooyala.android.OoyalaNotification;
  import com.ooyala.android.OoyalaPlayerLayout;
  import com.ooyala.android.PlayerDomain;
  import com.ooyala.android.ui.OoyalaPlayerLayoutController;
  import com.ooyala.sample.R;
  import com.ooyala.sample.utils.BasicPlaybackSampleAppLog;
  import com.ooyala.sample.utils.youbora.YouboraConfigManager;

  import java.util.Map;
  import java.util.Observable;
  import java.util.Observer;


/**
 * This activity illustrates Ooyala's Integration with NPAW Youbora Quality of Service tools
 * You can find more information on Ooyala's Support website, or from your CSM
 */
public class NPAWDefaultPlayerActivity extends Activity implements Observer {
  final String TAG = this.getClass().toString();

  String EMBED = null;
  String PCODE = null;
  String DOMAIN = null;
  int count = 0;

  protected OoyalaPlayerLayoutController playerLayoutController;
  protected OoyalaPlayer player;
  private PluginOoyala pluginOoyala;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle(getIntent().getExtras().getString("selection_name"));
    setContentView(R.layout.player_simple_layout);
    EMBED = getIntent().getExtras().getString("embed_code");
    PCODE = getIntent().getExtras().getString("pcode");
    DOMAIN = getIntent().getExtras().getString("domain");

    //Initialize the player
    OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);
    player = new OoyalaPlayer(PCODE, new PlayerDomain(DOMAIN));
    playerLayoutController = new OoyalaPlayerLayoutController(playerLayout, player);
    player.addObserver(this);

    if (player.setEmbedCode(EMBED)) {
      player.play();
    } else {
      Log.e(TAG, "Asset Failure");
    }

		//Youbora plugin creation and start monitoring
    YBLog.setDebugLevel(YBLog.YBLogLevelHTTPRequests);

    // Get the Youbora Config map from the helper manager
    Map<String, Object> options = YouboraConfigManager.getYouboraConfig(getApplicationContext());

    // Get title from the example
    ((Map<String, Object>)options.get("media")).put("title", getIntent().getExtras().getString("selection_name"));

    pluginOoyala = new PluginOoyala(options);
    pluginOoyala.startMonitoring(player);
  }

  @Override
  protected void onStop() {
    super.onStop();
    Log.d(TAG, "Player Activity Stopped");
    if (player != null) {
      player.suspend();
    }
    if (isFinishing()) {
      pluginOoyala.stopMonitoring();
    } else {
      pluginOoyala.pauseMonitoring();
    }
  }

  @Override
  protected void onRestart() {
    super.onRestart();
    Log.d(TAG, "Player Activity Restarted");
    if (player != null) {
      player.resume();
    }
    pluginOoyala.resumeMonitoring();
  }

  /**
   * Listen to all notifications from the OoyalaPlayer
   */
  @Override
  public void update(Observable arg0, Object argN) {
    if (arg0 != player) {
      return;
    }

    final String arg1 = OoyalaNotification.getNameOrUnknown(argN);
    if (arg1 == OoyalaPlayer.TIME_CHANGED_NOTIFICATION_NAME) {
      return;
    }

    if (arg1 == OoyalaPlayer.ERROR_NOTIFICATION_NAME) {
      final String msg = "Error Event Received";
      if (player != null && player.getError() != null) {
        Log.e(TAG, msg, player.getError());
      } else {
        Log.e(TAG, msg);
      }

      return;
    }

    // Hook to write Notifications to a temporary file on the device/emulator
    // Keeps track of incoming notifications and makes sure count is right
    count++;
    String text = "Notification Received: " + arg1 + " - state: " + player.getState() + "count: " + count;

    // Write the event text along with event count to log file in sdcard if the log file exists
    BasicPlaybackSampleAppLog BasicPlaybacklog = new BasicPlaybackSampleAppLog();
    BasicPlaybacklog.writeToSdcardLog(count, text);

    Log.d(TAG, "Notification Received: " + arg1 + " - state: " + player.getState());
  }

}

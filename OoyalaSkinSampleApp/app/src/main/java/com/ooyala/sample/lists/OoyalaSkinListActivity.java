package com.ooyala.sample.lists;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ooyala.sample.R;
import com.ooyala.sample.players.OoyalaSkinPlayerActivity;
import com.ooyala.sample.utils.PlayerSelectionOption;

import java.util.LinkedHashMap;
import java.util.Map;

public class OoyalaSkinListActivity extends Activity implements OnItemClickListener {
  private static final int LOAD_REACT_BUNDLE_PERMISSION_REQ_CODE = 666;

  public final static String getName() {
    return "Skin Playback";
  }

  private static Map<String, PlayerSelectionOption> selectionMap;
  ArrayAdapter<String> selectionAdapter;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle(getName());

    selectionMap = new LinkedHashMap<String, PlayerSelectionOption>();
    selectionMap.put("4:3 Aspect Ratio", new PlayerSelectionOption("FwaXZjcjrkydIftLal2cq9ymQMuvjvD8", "c0cTkxOqALQviQIGAHWY5hP0q9gU", "http://www.ooyala.com", OoyalaSkinPlayerActivity.class));
    selectionMap.put( "MP4 Video", new PlayerSelectionOption("h4aHB1ZDqV7hbmLEv4xSOx3FdUUuephx", "c0cTkxOqALQviQIGAHWY5hP0q9gU", "http://www.ooyala.com", OoyalaSkinPlayerActivity.class) );
    selectionMap.put( "HLS Video", new PlayerSelectionOption("Y1ZHB1ZDqfhCPjYYRbCEOz0GR8IsVRm1", "c0cTkxOqALQviQIGAHWY5hP0q9gU", "http://www.ooyala.com", OoyalaSkinPlayerActivity.class) );
    selectionMap.put( "Ooyala Encrypted HLS", new PlayerSelectionOption("ZtZmtmbjpLGohvF5zBLvDyWexJ70KsL-", "c0cTkxOqALQviQIGAHWY5hP0q9gU", "http://www.ooyala.com", OoyalaSkinPlayerActivity.class) );
    selectionMap.put( "VOD with CCs", new PlayerSelectionOption("92cWp0ZDpDm4Q8rzHfVK6q9m6OtFP-ww", "c0cTkxOqALQviQIGAHWY5hP0q9gU", "http://www.ooyala.com", OoyalaSkinPlayerActivity.class) );
    selectionMap.put( "Clear HLS High Profile", new PlayerSelectionOption("FlNXNsMjE6SvZO6iFmPgfXhG_PKX1yTJ", "c0cTkxOqALQviQIGAHWY5hP0q9gU", "http://www.ooyala.com", OoyalaSkinPlayerActivity.class) );
    selectionMap.put( "Clear HLS Main Profile", new PlayerSelectionOption("42cnNsMjE62UDH0JlCssXEPhxlhj1YBN", "c0cTkxOqALQviQIGAHWY5hP0q9gU", "http://www.ooyala.com", OoyalaSkinPlayerActivity.class) );
    selectionMap.put( "Clear HLS Baseline", new PlayerSelectionOption("V5ZnNsMjE6NRd7hfQYZibvDufNgB_233", "c0cTkxOqALQviQIGAHWY5hP0q9gU", "http://www.ooyala.com", OoyalaSkinPlayerActivity.class) );

    selectionMap.put("VAST2 Ad Pre-roll", new PlayerSelectionOption("Zlcmp0ZDrpHlAFWFsOBsgEXFepeSXY4c", "BidTQxOqebpNk1rVsjs2sUJSTOZc", "http://www.ooyala.com", OoyalaSkinPlayerActivity.class));
    selectionMap.put("VAST2 Ad Mid-roll", new PlayerSelectionOption("pncmp0ZDp7OKlwTPJlMZzrI59j8Imefa", "BidTQxOqebpNk1rVsjs2sUJSTOZc", "http://www.ooyala.com", OoyalaSkinPlayerActivity.class));
    selectionMap.put("VAST2 Ad Post-roll", new PlayerSelectionOption("Zpcmp0ZDpaB-90xK8MIV9QF973r1ZdUf", "BidTQxOqebpNk1rVsjs2sUJSTOZc", "http://www.ooyala.com", OoyalaSkinPlayerActivity.class));
    selectionMap.put("VAST2 Ad Wrapper", new PlayerSelectionOption("5ybGV3ZTrqvbymwBC6ThtupFBptOp1rP", "IxMWwxOtIoprfDnNvx0aBd97TcIk", "http://www.ooyala.com", OoyalaSkinPlayerActivity.class));
    selectionMap.put("Ooyala Ad Pre-roll", new PlayerSelectionOption("M4cmp0ZDpYdy8kiL4UD910Rw_DWwaSnU", "BidTQxOqebpNk1rVsjs2sUJSTOZc", "http://www.ooyala.com", OoyalaSkinPlayerActivity.class));
    selectionMap.put("Ooyala Ad Mid-roll", new PlayerSelectionOption("xhcmp0ZDpnDB2-hXvH7TsYVQKEk_89di", "BidTQxOqebpNk1rVsjs2sUJSTOZc", "http://www.ooyala.com", OoyalaSkinPlayerActivity.class));
    selectionMap.put("Ooyala Ad Post-roll", new PlayerSelectionOption("Rjcmp0ZDr5yFbZPEfLZKUveR_2JzZjMO", "BidTQxOqebpNk1rVsjs2sUJSTOZc", "http://www.ooyala.com", OoyalaSkinPlayerActivity.class));
    selectionMap.put("Multi Ad combination", new PlayerSelectionOption("Ftcmp0ZDoz8tALmhPcN2vMzCdg7YU9lc", "BidTQxOqebpNk1rVsjs2sUJSTOZc", "http://www.ooyala.com", OoyalaSkinPlayerActivity.class));

    selectionMap.put("VAST 3.0 2 Podded Ad", new PlayerSelectionOption("tjN2h1MDE65xMHMqNvvU0fYVBi6sFl1M", "ltOGIyOq4Waxz7r-q6FsUpEfl4dg", "http://www.ooyala.com", OoyalaSkinPlayerActivity.class));
    selectionMap.put("VAST 3.0 Ad With All Of The New Events", new PlayerSelectionOption("x3MWp1MDE6yIedkirT0to1t1uy2oA8y3", "ltOGIyOq4Waxz7r-q6FsUpEfl4dg", "http://www.ooyala.com", OoyalaSkinPlayerActivity.class));
    selectionMap.put("VAST 3.0 Ad With Icon", new PlayerSelectionOption("1qNGp1MDE6GBl7PkPlWA_FA9NJDhBp_I", "ltOGIyOq4Waxz7r-q6FsUpEfl4dg", "http://www.ooyala.com", OoyalaSkinPlayerActivity.class));
    selectionMap.put("VAST 3.0 Skippable Ad Long", new PlayerSelectionOption("55MjA5MTE6iZTRzrTtLoxUy78YbffT2G", "ltOGIyOq4Waxz7r-q6FsUpEfl4dg", "http://www.ooyala.com", OoyalaSkinPlayerActivity.class));
    selectionMap.put("VAST 3.0 Ad Wrapper", new PlayerSelectionOption("Fkcm9lMjE6GOcDOtPSiRnNCRhqsz-96e", "BidTQxOqebpNk1rVsjs2sUJSTOZc", "http://www.ooyala.com",  OoyalaSkinPlayerActivity.class));

    selectionMap.put("VMAP PreMidPost Single", new PlayerSelectionOption("41MzA5MTE65SC6VQAS3H3d9rX-hwHQSK", "BidTQxOqebpNk1rVsjs2sUJSTOZc", "http://www.ooyala.com", OoyalaSkinPlayerActivity.class));
    selectionMap.put("VMAP PreMid VASTAdData", new PlayerSelectionOption("10eGE0MjE6TZG6mdHfJJEAGnxbuEv1Vi", "BidTQxOqebpNk1rVsjs2sUJSTOZc", "http://www.ooyala.com", OoyalaSkinPlayerActivity.class));

    setContentView(R.layout.list_activity_layout);

    //Create the adapter for the ListView
    selectionAdapter = new ArrayAdapter<String>(this, R.layout.list_activity_list_item);
    for(String key : selectionMap.keySet()) {
      selectionAdapter.add(key);
    }
    selectionAdapter.notifyDataSetChanged();

    //Load the data into the ListView
    ListView selectionListView = (ListView) findViewById(R.id.mainActivityListView);
    selectionListView.setAdapter(selectionAdapter);
    selectionListView.setOnItemClickListener(this);

    //Force request android.permission.SYSTEM_ALERT_WINDOW
    // that is ignored in Manifest on Marshmallow devices.
    // Can be used if necessary to use native-skin source code
//    if(Build.VERSION_CODES.M == Build.VERSION.SDK_INT) {
//      showDevOptionsDialog();
//    }
  }

  private void showDevOptionsDialog() {
    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
    startActivityForResult(intent, LOAD_REACT_BUNDLE_PERMISSION_REQ_CODE);
  }
  

  @Override
  public void onItemClick(AdapterView<?> l, View v, int pos, long id) {
    PlayerSelectionOption selection =  selectionMap.get(selectionAdapter.getItem(pos));
    Class<? extends Activity> selectedClass = selection.getActivity();

    //Launch the correct activity with the embed code as an extra
    Intent intent = new Intent(this, selectedClass);
    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    intent.putExtra("embed_code", selection.getEmbedCode());
    intent.putExtra("selection_name", selectionAdapter.getItem(pos));
    intent.putExtra("pcode", selection.getPcode());
    intent.putExtra("domain", selection.getDomain());
    startActivity(intent);
    return;
  }
}

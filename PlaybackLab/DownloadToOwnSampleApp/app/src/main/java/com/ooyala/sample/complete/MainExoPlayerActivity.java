package com.ooyala.sample.complete;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ooyala.sample.R;
import com.ooyala.sample.lists.BasicPlaybackListActivity;
import com.ooyala.sample.lists.DownloadSerializationActivity;
import com.ooyala.sample.lists.OfflineAnalyticsListActivity;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This is the opening activity for the app.
 * @author michael.len
 *
 */
public class MainExoPlayerActivity extends AppCompatActivity implements OnItemClickListener {
  final String TAG = this.getClass().toString();

  private static Map<String, Class<? extends Activity>> activityMap;
  ArrayAdapter<String> mainListAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.list_activity_layout);
    activityMap = new LinkedHashMap<String, Class<? extends Activity>>();
    mainListAdapter = new ArrayAdapter<String>(this, R.layout.list_activity_list_item);

    // Begin List of Sample List Activities
    // If you add to this, you must add your activities to AndroidManifest
//    activityMap.put(AdvancedPlaybackListActivity.getName(), AdvancedPlaybackListActivity.class);
//    activityMap.put(ContentProtectionListActivity.getName(), ContentProtectionListActivity.class);
//    activityMap.put(FreewheelListActivity.getName(), FreewheelListActivity.class);
//    activityMap.put(IMAListActivity.getName(), IMAListActivity.class);
//    activityMap.put(OptionsListActivity.getName(), OptionsListActivity.class);
//    activityMap.put(BasicPlaybackListActivity.getName(), BasicPlaybackListActivity.class);
//    activityMap.put(OoyalaAPIListActivity.getName(), OoyalaAPIListActivity.class);
//    activityMap.put(NPAWYouboraListActivity.getName(), NPAWYouboraListActivity.class);
    activityMap.put(BasicPlaybackListActivity.getName(), BasicPlaybackListActivity.class);
    activityMap.put(DownloadSerializationActivity.getName(), DownloadSerializationActivity.class);
    activityMap.put(AddAssetActivity.getName(), AddAssetActivity.class);

    for(String key : activityMap.keySet()) {
      mainListAdapter.add(key);
    }
    mainListAdapter.notifyDataSetChanged();

    ListView mainListView = (ListView) findViewById(R.id.mainActivityListView);
    mainListView.setAdapter(mainListAdapter);
    mainListView.setOnItemClickListener(this);
  }

  @Override
  public void onItemClick(AdapterView<?> l, View v, int pos, long id) {
    Class<? extends Activity> selectedClass = activityMap.get(mainListAdapter.getItem(pos));

    Intent intent = new Intent(this, selectedClass);
    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    //startActivity(intent);
    startActivityForResult(intent, 0);
    return;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.offlineAnalyticsMenu:
        startActivity(new Intent(this, OfflineAnalyticsListActivity.class));
        return true;
    }
    return super.onOptionsItemSelected(item);
  }
}

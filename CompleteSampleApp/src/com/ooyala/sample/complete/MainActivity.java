package com.ooyala.sample.complete;

import java.util.LinkedHashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ooyala.sample.R;
import com.ooyala.sample.lists.AdvancedPlaybackListActivity;

/**
 * This is the opening activity for the app.
 * @author michael.len
 *
 */
public class MainActivity extends Activity implements OnItemClickListener {
  final String TAG = this.getClass().toString();

  private static Map<String, Class<? extends Activity>> activityMap;
  ArrayAdapter<String> mainListAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.list_activity_layout);

    activityMap = new LinkedHashMap<String, Class<? extends Activity>>();
    activityMap.put(AdvancedPlaybackListActivity.getName(), AdvancedPlaybackListActivity.class);

    mainListAdapter = new ArrayAdapter<String>(this, R.layout.list_activity_list_item);
    mainListAdapter.addAll(activityMap.keySet());
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
    startActivity(intent);
    return;
  }
}

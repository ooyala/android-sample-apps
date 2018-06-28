package com.ooyala.sample.complete;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ooyala.sample.R;
import com.ooyala.sample.lists.ContentProtectionListActivity;

import com.ooyala.sample.players.CustomActivity;


import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This is the opening activity for the app.
 *
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
        mainListAdapter = new ArrayAdapter<String>(this, R.layout.list_activity_list_item);
        activityMap.put(ContentProtectionListActivity.getName(), ContentProtectionListActivity.class);
        activityMap.put(CustomActivity.getName(), CustomActivity.class);
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
        startActivity(intent);
        return;
    }
}

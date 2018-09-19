package com.ooyala.sample.lists;

import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.util.DebugMode;
import com.ooyala.sample.R;
import com.ooyala.sample.utils.OfflineAnalyticsAdapter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class OfflineAnalyticsListActivity extends Activity {
  private static final String TAG = OfflineAnalyticsListActivity.class.getSimpleName();
  private RecyclerView recyclerView;
  private RecyclerView.Adapter adapter;
  private RecyclerView.LayoutManager layoutManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_offline_analytics_list);

    recyclerView = (RecyclerView) findViewById(R.id.recyclerViewOfflineAnalytics);
    recyclerView.setHasFixedSize(true);

    layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);

    List<String> offlineFileNames = OoyalaPlayer.getOfflineAnalyticsFilenames(getApplicationContext());

    adapter = new OfflineAnalyticsAdapter(offlineFileNames);
    recyclerView.setAdapter(adapter);
  }
}

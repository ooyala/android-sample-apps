package com.ooyala.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends Activity implements OnItemClickListener {
	private static final String TAG = MainActivity.class.getName();

	@BindView(R.id.activity_list)
	ListView activityList;

	private Map<String, Class<? extends Activity>> activityMap;
	private ArrayAdapter<String> activityListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.list_activity_layout);
		ButterKnife.bind(this);

		populateActivityList();

		activityListAdapter = new ArrayAdapter<>(this, R.layout.list_activity_list_item);
		activityListAdapter.addAll(activityMap.keySet());
		activityListAdapter.notifyDataSetChanged();

		activityList.setAdapter(activityListAdapter);
		activityList.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> l, View v, int pos, long id) {
		Class<? extends Activity> selectedClass = activityMap.get(activityListAdapter.getItem(pos));

		Intent intent = new Intent(this, selectedClass);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(intent);
	}

	private void populateActivityList() {
		activityMap = new LinkedHashMap<>();
		activityMap.put(SinglePlayerActivity.getName(), SinglePlayerActivity.class);
		activityMap.put(MultiplePlayerActivity.getName(), MultiplePlayerActivity.class);
	}
}

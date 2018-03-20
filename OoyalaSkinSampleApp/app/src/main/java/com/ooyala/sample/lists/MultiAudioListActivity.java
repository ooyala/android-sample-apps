package com.ooyala.sample.lists;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ooyala.sample.R;
import com.ooyala.sample.players.MultiAudioActivity;
import com.ooyala.sample.utils.PlayerSelectionOption;

import java.util.LinkedHashMap;
import java.util.Map;

public class MultiAudioListActivity extends Activity implements OnItemClickListener {

  public final static String getName() {
    return MultiAudioListActivity.class.getSimpleName();
  }

  private static Map<String, PlayerSelectionOption> selectionMap;
  private ArrayAdapter<String> selectionAdapter;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle(getName());

    String pcode = "k0a2gyOt0QGNJLSuzKfdY4R-hw2b";
    String domain = "http://www.ooyala.com";

    selectionMap = new LinkedHashMap<>();

    selectionMap.put("English/German tracks", new PlayerSelectionOption("wxNjRwZTE6bxMBkrQbfM_BwokGf0pyOm", pcode, domain, MultiAudioActivity.class));
    selectionMap.put("Undefined/Undefined asset1", new PlayerSelectionOption("xmaTRwZTE6lCuPd3H82AteBFUvNHtrHu", pcode, domain, MultiAudioActivity.class));
    selectionMap.put("Undefined/Undefined asset2", new PlayerSelectionOption("Y1cTRwZTE6Mc47fc_8n161HVYQYu5l0d", pcode, domain, MultiAudioActivity.class));
    selectionMap.put("No config Eng main+commentary/Ger main+commentary", new PlayerSelectionOption("txcjRwZTE6aMSqloxZZ3hZhTqz5hEaY9", pcode, domain, MultiAudioActivity.class));
    selectionMap.put("No config Eng main+commentary/Ger main+commentary", new PlayerSelectionOption("EweDRwZTE6ipjPDynkEotrpTvvYgDvr7", pcode, domain, MultiAudioActivity.class));
    selectionMap.put("Mixed language code/Undefined", new PlayerSelectionOption("o3eTRwZTE6TBJnp6gPJia25dtOcrugUk", pcode, domain, MultiAudioActivity.class));
    
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

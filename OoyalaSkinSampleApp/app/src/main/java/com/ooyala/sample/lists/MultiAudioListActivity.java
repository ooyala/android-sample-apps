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

    selectionMap.put("MT-1 ENG/GER", new PlayerSelectionOption("wxNjRwZTE6bxMBkrQbfM_BwokGf0pyOm", pcode, domain, MultiAudioActivity.class));
    selectionMap.put("MT-2 UND/UND", new PlayerSelectionOption("xmaTRwZTE6lCuPd3H82AteBFUvNHtrHu", pcode, domain, MultiAudioActivity.class));
    selectionMap.put("MT-3 UND/UND", new PlayerSelectionOption("Y1cTRwZTE6Mc47fc_8n161HVYQYu5l0d", pcode, domain, MultiAudioActivity.class));
    selectionMap.put("MT-4 ENG(main)/ENG(comm)/GER(main)/GER(comm)+no config", new PlayerSelectionOption("txcjRwZTE6aMSqloxZZ3hZhTqz5hEaY9", pcode, domain, MultiAudioActivity.class));
    selectionMap.put("MT-5 ENG(main)/ENG(comm)/GER(main)/GER(comm)+config", new PlayerSelectionOption("EweDRwZTE6ipjPDynkEotrpTvvYgDvr7", pcode, domain, MultiAudioActivity.class));
    selectionMap.put("MT-6 Mixed with Language code and UND", new PlayerSelectionOption("o3eTRwZTE6TBJnp6gPJia25dtOcrugUk", pcode, domain, MultiAudioActivity.class));

//    selectionMap.put("HLS 1 AAC 2 tracks", new PlayerSelectionOption("Rwc3luZTE6JAVo4Tb70Ioc-9goU1fswD", pcode, domain, MultiAudioActivity.class));
//    selectionMap.put("HLS 2 AAC 2 tracks", new PlayerSelectionOption("5mc3luZTE6B0LNGpB0EP4yD88iN0w0oV", pcode, domain, MultiAudioActivity.class));
//    selectionMap.put("HLS 3 AAC 2 tracks", new PlayerSelectionOption("E3c3luZTE6xm2eFoeJbdbXtioSVXwphu", pcode, domain, MultiAudioActivity.class));
//    selectionMap.put("HLS 4 AAC 2 tracks", new PlayerSelectionOption("95cnluZTE6nhistXkPbbHoDYof-rGfdp", pcode, domain, MultiAudioActivity.class));
//    selectionMap.put("HLS 5 AAC 4 tracks", new PlayerSelectionOption("V1cnluZTE6V_Vr-fL9R5lygWV_94b1Qd", pcode, domain, MultiAudioActivity.class));
//    selectionMap.put("HLS 6 AC-3 4 tracks", new PlayerSelectionOption("dzcnluZTE6vSOn4wpbInqO5tePgv6XuB", pcode, domain, MultiAudioActivity.class));
//    selectionMap.put("HLS 7 AC-3, AAC 6 tracks", new PlayerSelectionOption("Z5bnluZTE6ssZFw1KKdFYmvBTDDTeodl", pcode, domain, MultiAudioActivity.class));
//
//    selectionMap.put("DASH 1 AAC 2 tracks", new PlayerSelectionOption("xuNzBvZTE6mLZ3rvNXnPyJUYeUmb-KLF", pcode, domain, MultiAudioActivity.class));
//    selectionMap.put("DASH 2 AAC 2 tracks", new PlayerSelectionOption("ltODBvZTE6wwaDQC8v4SpUmjCGQNj1Vh", pcode, domain, MultiAudioActivity.class));
//    selectionMap.put("DASH 3 AAC 2 tracks", new PlayerSelectionOption("NxODBvZTE6cVtkKNv7VCSU0bziaz8UDW", pcode, domain, MultiAudioActivity.class));
//    selectionMap.put("DASH 4 AAC 2 tracks", new PlayerSelectionOption("R1ODBvZTE6SKvILoZU_YMRRp3v6rKgsZ", pcode, domain, MultiAudioActivity.class));
//    selectionMap.put("DASH 5 AAC 4 tracks", new PlayerSelectionOption("95ODBvZTE68on54VxqQ8tjXBCAjkh-Cf", pcode, domain, MultiAudioActivity.class));
//    selectionMap.put("DASH 6 AC-3 4 tracks", new PlayerSelectionOption("EzOTBvZTE6PizT-ApwUFX7Ed8dR-t810", pcode, domain, MultiAudioActivity.class));
//    selectionMap.put("DASH 7 AC-3, AAC 6 tracks", new PlayerSelectionOption("s1OTBvZTE6vOwAQnjyU_mk3_sdVqeZp6", pcode, domain, MultiAudioActivity.class));


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

package com.ooyala.sample.lists;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ooyala.sample.PreconfiguredAdPlayerActivity;
import com.ooyala.sample.R;
import com.ooyala.sample.utils.PlayerSelectionOption;

import java.util.LinkedHashMap;
import java.util.Map;

public class AdListActivity extends Activity implements OnItemClickListener {
  public final static String getName() {
    return "Google IMA Integration";
  }

  private static Map<String, PlayerSelectionOption> selectionMap;
  ArrayAdapter<String> selectionAdapter;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setTitle(getName());
    setSelectionMap();
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
    intent.putExtra("pcode", selection.getPcode());
    intent.putExtra("domain", selection.getDomain());
    startActivity(intent);
    return;
  }

  private void setSelectionMap() {
    selectionMap = new LinkedHashMap<>();

    // Debug video
    // The tag TEST means that video has been uploaded through the test account
    selectionMap.put("TEST Correct asset", new PlayerSelectionOption("g0d3BpNTE6GTTPK7o4ZyIcGqc878DqJ6", "R2NDYyOhSRhYj0UrUVgcdWlFVP-H", "http://www.ooyala.com", PreconfiguredAdPlayerActivity.class) );
    selectionMap.put("TEST IMA FLAT Pre-Mid-Post", new PlayerSelectionOption("w4azd4YzE6jbItvcx3P4YOF4SAxlXrVD", "378512f55dd44fc287d0fa609353c268", "http://www.ooyala.com", PreconfiguredAdPlayerActivity.class) );
    selectionMap.put("TEST IMA 360 Pre-Mid-Post", new PlayerSelectionOption("tmYXh3YzE6CbulYLlO_F8Cy9kX2uRx5g", "378512f55dd44fc287d0fa609353c268", "http://www.ooyala.com", PreconfiguredAdPlayerActivity.class) );
    selectionMap.put("TEST VAST FLAT Pre-Mid-Post", new PlayerSelectionOption("tjajh4YzE6c0HwEH_KdBT_Wr5_1O_RmY", "378512f55dd44fc287d0fa609353c268", "http://www.ooyala.com", PreconfiguredAdPlayerActivity.class) );
    selectionMap.put("TEST VAST 360 Pre-Mid-Post", new PlayerSelectionOption("Zlajh4YzE6nQ5OmSVbXQaNcW-L7JtixS", "378512f55dd44fc287d0fa609353c268", "http://www.ooyala.com", PreconfiguredAdPlayerActivity.class) );

    selectionMap.put("DEBUG VAST FLAT Pre-Mid-Post", new PlayerSelectionOption("t1aDh4YzE6bpEepHd7tu1zM5QnVaPETq", "570d91bf920b42cbae587bb1447e6fd8", "http://www.ooyala.com", PreconfiguredAdPlayerActivity.class) );
    selectionMap.put("DEBUG VAST 360 Pre-Mid-Post", new PlayerSelectionOption("FrNjl4YzE6ToULL3jzPrx18DijBChMVX", "570d91bf920b42cbae587bb1447e6fd8", "http://www.ooyala.com", PreconfiguredAdPlayerActivity.class) );

    // Flat video
    selectionMap.put("FLAT IMA Ad-Rules Midroll", new PlayerSelectionOption("VlaG9lcTqeUU18adfd1DVeQ8YekP3H4l", "R2NDYyOhSRhYj0UrUVgcdWlFVP-H", "http://www.ooyala.com", PreconfiguredAdPlayerActivity.class) );
    selectionMap.put("FLAT IMA Ad-Rules Postroll", new PlayerSelectionOption("BnaG9lcTqLXQNyod7ON8Yv3eDas2Oog6", "R2NDYyOhSRhYj0UrUVgcdWlFVP-H", "http://www.ooyala.com", PreconfiguredAdPlayerActivity.class) );
    selectionMap.put("FLAT IMA Podded Preroll", new PlayerSelectionOption("1wNjE3cDox0G3hQIWxTjsZ8MPUDLSkDY", "R2NDYyOhSRhYj0UrUVgcdWlFVP-H", "http://www.ooyala.com", PreconfiguredAdPlayerActivity.class) );
    selectionMap.put("FLAT IMA Podded Midroll", new PlayerSelectionOption("1yNjE3cDodUEfUfp2WNzHkCZCMb47MUP", "R2NDYyOhSRhYj0UrUVgcdWlFVP-H", "http://www.ooyala.com", PreconfiguredAdPlayerActivity.class) );
    selectionMap.put("FLAT IMA Podded Postroll", new PlayerSelectionOption("1sNjE3cDoN3ZewFm1238ce730J4BMrEJ", "R2NDYyOhSRhYj0UrUVgcdWlFVP-H", "http://www.ooyala.com", PreconfiguredAdPlayerActivity.class) );
    selectionMap.put("FLAT IMA Podded Pre-Mid-Post", new PlayerSelectionOption("ZrOTE3cDoXo2sLOWzQPxjS__M-Qk32Co", "R2NDYyOhSRhYj0UrUVgcdWlFVP-H", "http://www.ooyala.com", PreconfiguredAdPlayerActivity.class) );
    selectionMap.put("FLAT IMA Skippable", new PlayerSelectionOption("FhbGRjbzq8tfaoA3dhfxc2Qs0-RURJfO", "R2NDYyOhSRhYj0UrUVgcdWlFVP-H", "http://www.ooyala.com", PreconfiguredAdPlayerActivity.class) );
    selectionMap.put("FLAT IMA Pre, Mid and Post Skippable", new PlayerSelectionOption("10NjE3cDpj8nUzYiV1PnFsjC6nEvPQAE", "R2NDYyOhSRhYj0UrUVgcdWlFVP-H", "http://www.ooyala.com", PreconfiguredAdPlayerActivity.class) );
    selectionMap.put("FLAT IMA Application-Configured", new PlayerSelectionOption("Y1ZHB1ZDqfhCPjYYRbCEOz0GR8IsVRm1", "c0cTkxOqALQviQIGAHWY5hP0q9gU", "http://www.ooyala.com", PreconfiguredAdPlayerActivity.class) );
    selectionMap.put("FLAT IMA Non Ad-Rules Preroll", new PlayerSelectionOption("FlbGRjbzptyEbStMiMLcyNQE6l6TMgwq", "R2NDYyOhSRhYj0UrUVgcdWlFVP-H", "http://www.ooyala.com", PreconfiguredAdPlayerActivity.class) );
    selectionMap.put("FLAT IMA Non Ad-Rules Midroll", new PlayerSelectionOption("xrbGRjbzoBJUwtSLOHrcceTvMBe5pZdN", "R2NDYyOhSRhYj0UrUVgcdWlFVP-H", "http://www.ooyala.com", PreconfiguredAdPlayerActivity.class) );
    selectionMap.put("FLAT IMA Non Ad-Rules Postroll", new PlayerSelectionOption("FjbGRjbzp0DV_5-NtXBVo5Rgp3Sj0R5C", "R2NDYyOhSRhYj0UrUVgcdWlFVP-H", "http://www.ooyala.com", PreconfiguredAdPlayerActivity.class) );
    selectionMap.put("FLAT IMA Non Ad-Rules Quad Midroll", new PlayerSelectionOption("J3bHFpNTE6E2o2Fx_kZMyusc6m8SENCe", "R2NDYyOhSRhYj0UrUVgcdWlFVP-H", "http://www.ooyala.com", PreconfiguredAdPlayerActivity.class) );
    selectionMap.put("FLAT IMA Non Ad-Rules Pre-Mid-Mid-Post", new PlayerSelectionOption("g0d3BpNTE6GTTPK7o4ZyIcGqc878DqJ6", "R2NDYyOhSRhYj0UrUVgcdWlFVP-H", "http://www.ooyala.com", PreconfiguredAdPlayerActivity.class) );
    selectionMap.put("FLAT No Ads", new PlayerSelectionOption("JiOTdrdzqAujYa5qvnOxszbrTEuU5HMt", "c0cTkxOqALQviQIGAHWY5hP0q9gU", "http://www.ooyala.com", PreconfiguredAdPlayerActivity.class) );

    // Video 360
    selectionMap.put("Ooyala Preroll", new PlayerSelectionOption("NibG1rYzE6B7m54kL380ZXwEsUUy4bIe", "570d91bf920b42cbae587bb1447e6fd8", "http://www.ooyala.com", PreconfiguredAdPlayerActivity.class) );

    selectionMap.put("IMA Preroll", new PlayerSelectionOption("Izbm1rYzE6Hr19rd1wK74qeraVA7xSLx", "bb4c1914044a40c2af381c5ac4c98618", "http://www.ooyala.com", PreconfiguredAdPlayerActivity.class) );
    selectionMap.put("IMA Midroll", new PlayerSelectionOption("Q0dWFtYzE6RFRGuFP0WzuPE5dvBzJ8_R", "570d91bf920b42cbae587bb1447e6fd8", "http://www.ooyala.com", PreconfiguredAdPlayerActivity.class) );
    selectionMap.put("IMA Postroll", new PlayerSelectionOption("N4bmNtYzE63wuc3QizkmmkA0HDZou83_", "570d91bf920b42cbae587bb1447e6fd8", "http://www.ooyala.com", PreconfiguredAdPlayerActivity.class) );
    selectionMap.put("IMA Pre-Mid-Post", new PlayerSelectionOption("J0dmFtYzE675zb3G_f6UsvggJYTXVsF4", "570d91bf920b42cbae587bb1447e6fd8", "http://www.ooyala.com", PreconfiguredAdPlayerActivity.class) );
    selectionMap.put("IMA Podded Pre-Mid-Post", new PlayerSelectionOption("0wd2FtYzE6b3_hyeGPsLYUKzOIqXHKFi", "570d91bf920b42cbae587bb1447e6fd8", "http://www.ooyala.com", PreconfiguredAdPlayerActivity.class) );

    selectionMap.put("VAST Preroll", new PlayerSelectionOption("o5bm1rYzE6Iv00WKa3Wd67QUuulRGtTb", "570d91bf920b42cbae587bb1447e6fd8", "http://www.ooyala.com", PreconfiguredAdPlayerActivity.class) );
    selectionMap.put("VAST Midroll", new PlayerSelectionOption("F3bW1rYzE6gd0C5kJ8ETeB-0yeawf2Cd", "570d91bf920b42cbae587bb1447e6fd8", "http://www.ooyala.com", PreconfiguredAdPlayerActivity.class) );
    selectionMap.put("VAST Postroll", new PlayerSelectionOption("h4dGFtYzE6pjfgMHC5ioFOiaq5BywAL6", "570d91bf920b42cbae587bb1447e6fd8", "http://www.ooyala.com", PreconfiguredAdPlayerActivity.class) );
    selectionMap.put("VAST Pre-Mid-Post", new PlayerSelectionOption("g4YmNsYzE6zLuWf3eCAtcOdi0--i081X", "570d91bf920b42cbae587bb1447e6fd8", "http://www.ooyala.com", PreconfiguredAdPlayerActivity.class) );
    selectionMap.put("VAST Podded Pre-Mid-Post", new PlayerSelectionOption("ZpZGxoYzE6oThg4Hapb2gwUC-HBDJy8T", "570d91bf920b42cbae587bb1447e6fd8", "http://www.ooyala.com", PreconfiguredAdPlayerActivity.class) );

    selectionMap.put("Video without AD", new PlayerSelectionOption("ZwdTE5YzE69c3U3cXy2CCzfnCkzMMqUP", "bb4c1914044a40c2af381c5ac4c98618", "http://www.ooyala.com", PreconfiguredAdPlayerActivity.class) );
  }
}

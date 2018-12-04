package com.ooyala.sample.lists;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.ooyala.sample.R;
import com.ooyala.sample.players.AudioOnlyPlayerActivity;
import com.ooyala.sample.utils.IntentBuilder;
import com.ooyala.sample.utils.PlayerSelectionOption;

import java.util.LinkedHashMap;
import java.util.Map;

public class AudioOnlyListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

  private Map<String, PlayerSelectionOption> selectionMap;
  private ArrayAdapter<String> selectionAdapter;

  public final static String getName() {
    return "Audio Only";
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle(getName());

    selectionMap = new LinkedHashMap();

    selectionMap.put("m4a, profile: m4a, ogg", createSelection("A3aTBmZzE6bzQUkQlDOrUu4cjfOlCGPa", "Q1bW0yOsRxnrzAjzXI2wUlZp9h53"));
    selectionMap.put("ogg, profile: m4a, ogg", createSelection("ZiaTBmZzE6UC7HhgAxvrsPr_uOefMbxo", "Q1bW0yOsRxnrzAjzXI2wUlZp9h53"));
    selectionMap.put("wav, profile: m4a, ogg", createSelection("VkaTBmZzE6TrMuqKM8KOQrh0ZkWzBrUd", "Q1bW0yOsRxnrzAjzXI2wUlZp9h53"));
    selectionMap.put("aac, profile: m4a, ogg", createSelection("M5aTBmZzE6VAwftjIbrYe9zUvDSysa0h", "Q1bW0yOsRxnrzAjzXI2wUlZp9h53"));
    selectionMap.put("mp3, profile: m4a, ogg", createSelection("81aTBmZzE6UeGl0QQkilTMaYKJsN0NaV", "Q1bW0yOsRxnrzAjzXI2wUlZp9h53"));
    selectionMap.put("wma, profile: m4a, ogg", createSelection("wzaTBmZzE6Eka-FxWRs1LdL2FmY6wh32", "Q1bW0yOsRxnrzAjzXI2wUlZp9h53"));

    selectionMap.put("HLS m4a, profile: aac", createSelection("NybzBmZzE6n6LYZgsxJNthUnAn1_xrcV", "Q1bW0yOsRxnrzAjzXI2wUlZp9h53"));
    selectionMap.put("HLS oog, profile: aac", createSelection("I4cDBmZzE6GRYVnZyNOBhmeVUEi_DluP", "Q1bW0yOsRxnrzAjzXI2wUlZp9h53"));
    selectionMap.put("HLS wav, profile: aac", createSelection("owcDBmZzE6vIJX2Bj1CxEvaAe51IDZcm", "Q1bW0yOsRxnrzAjzXI2wUlZp9h53"));
    selectionMap.put("HLS aac, profile: aac", createSelection("F4bzBmZzE6mGid-fhpfjDSgl3wjuHSH0", "Q1bW0yOsRxnrzAjzXI2wUlZp9h53"));
    selectionMap.put("HLS mp3, profile: aac", createSelection("cycDBmZzE65z1xEgnm12hAc5m-Iaquzl", "Q1bW0yOsRxnrzAjzXI2wUlZp9h53"));
    selectionMap.put("HLS wma, profile: aac", createSelection("NhcDBmZzE6T1qiE2DVxz5i2mlIRS9amI", "Q1bW0yOsRxnrzAjzXI2wUlZp9h53"));

    selectionMap.put("HLS m4a, profile: m4a, ogg, aac", createSelection("h2bzBmZzE6vbQbJB_eSc1iw621QmHiGg", "Q1bW0yOsRxnrzAjzXI2wUlZp9h53"));
    selectionMap.put("HLS ogg, profile: m4a, ogg, aac", createSelection("A0cDBmZzE6h0sZ1gVCXjcoiCvP6p4vPM", "Q1bW0yOsRxnrzAjzXI2wUlZp9h53"));
    selectionMap.put("HLS wav, profile: m4a, ogg, aac", createSelection("dlcDBmZzE62u1lr_DpSH5x5V_6tKbHRl", "Q1bW0yOsRxnrzAjzXI2wUlZp9h53"));
    selectionMap.put("HLS aac, profile: m4a, ogg, aac", createSelection("U2cDBmZzE6ydkJesPIx4tlaamwjJ4mXf", "Q1bW0yOsRxnrzAjzXI2wUlZp9h53"));
    selectionMap.put("HLS mp3, profile: m4a, ogg, aac", createSelection("RwbzBmZzE67zcx60ZZ1UbxOJk1RJ5Z3l", "Q1bW0yOsRxnrzAjzXI2wUlZp9h53"));
    selectionMap.put("HLS wma, profile: m4a, ogg, aac", createSelection("hjcDBmZzE6wQt4G8gajSX-CmThnkuXua", "Q1bW0yOsRxnrzAjzXI2wUlZp9h53"));

    setContentView(R.layout.list_activity_layout);

    selectionAdapter = new ArrayAdapter<>(this, R.layout.list_activity_list_item);
    selectionAdapter.addAll(selectionMap.keySet());
    selectionAdapter.notifyDataSetChanged();

    ListView selectionListView = findViewById(R.id.mainActivityListView);
    selectionListView.setAdapter(selectionAdapter);
    selectionListView.setOnItemClickListener(this);
  }

  private PlayerSelectionOption createSelection(String embedCode, String pcode) {
    return new PlayerSelectionOption(embedCode, pcode, "http://www.ooyala.com", AudioOnlyPlayerActivity.class);
  }

  @Override
  public void onItemClick(AdapterView<?> l, View v, int pos, long id) {
    PlayerSelectionOption selection = selectionMap.get(selectionAdapter.getItem(pos));
    Intent intent = new IntentBuilder()
        .setActivity(selection.getActivity())
        .setEmbedCode(selection.getEmbedCode())
        .setPCode(selection.getPcode())
        .setDomain(selection.getDomain())
        .setAutoPlay(true)
        .build(this);
    startActivity(intent);
  }
}

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
    selectionMap = new LinkedHashMap<>();

    String pcode = "BzY2syOq6kIK6PTXN7mmrGVSJEFj";
    String domain = "http://www.ooyala.com";

    selectionMap = new LinkedHashMap<>();
    selectionMap.put("1 track + No CC", new PlayerSelectionOption("c0YzVwZTE6mYDzgMMYwoB_bWgD7XB3P-", pcode, domain, MultiAudioActivity.class));
    selectionMap.put("Several tracks + No CC", new PlayerSelectionOption("c3cGZwZTE6lh5w1Jm6hK0LAEpP_EKn-6", pcode, domain, MultiAudioActivity.class));
    selectionMap.put("1 track + CC", new PlayerSelectionOption("92cmZwZTE613kYlorJkkSJXAw4DnFRxv", pcode, domain, MultiAudioActivity.class));
    selectionMap.put("Several tracks + CC", new PlayerSelectionOption("E4bDRwZTE6rMB8oYrzOsuHSPz0XM0dAV", pcode, domain, MultiAudioActivity.class));
    selectionMap.put("Change Track", new PlayerSelectionOption("c3cGZwZTE6lh5w1Jm6hK0LAEpP_EKn-6", pcode, domain, MultiAudioActivity.class));
    selectionMap.put("Change CC", new PlayerSelectionOption("E4bDRwZTE6rMB8oYrzOsuHSPz0XM0dAV", pcode, domain, MultiAudioActivity.class));
    selectionMap.put("DASH", new PlayerSelectionOption("tyOTBvZTE6Gq2ib-GtRchnRCQmk3jLRT", pcode, domain, MultiAudioActivity.class));
    selectionMap.put("HLS", new PlayerSelectionOption("tsYjVwZTE6zgfSBx4_gvsVuFuwy844q_", pcode, domain, MultiAudioActivity.class));
    selectionMap.put("7+ tracks + CC", new PlayerSelectionOption("p5amdwZTE6NgCrrm18YoUTrgJsI7D26f", pcode, domain, MultiAudioActivity.class));
    selectionMap.put("Mid-roll", new PlayerSelectionOption("FrZGVyZTE6Bv2s5QwtytNT48VJfq9vdh", pcode, domain, MultiAudioActivity.class));
    selectionMap.put("Post-roll", new PlayerSelectionOption("ppZGVyZTE6dX1q1yTHtygM4lyiRwDFDM", pcode, domain, MultiAudioActivity.class));
    selectionMap.put("MP4 without CC", new PlayerSelectionOption("g0Z2hwZTE6i-ODpQETJqhOCt2jOE6Y2c", pcode, domain, MultiAudioActivity.class));
    selectionMap.put("MP4 with CC", new PlayerSelectionOption("xkaGhwZTE6hghIcejjoTfu0BG2sLdtxp", pcode, domain, MultiAudioActivity.class));

    selectionMap.put("Azure Clear HLS Var1", new PlayerSelectionOption("F0YnhyZTE64tQDm5QMdcpjp_hMI8Xqya", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Azure DRM HLS Var1", new PlayerSelectionOption("FtY3hyZTE6_YTA3Q8SohK9KYIeSAEVl3", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,MultiAudioActivity.class));
    selectionMap.put("Azure Clear DASH Var1", new PlayerSelectionOption("ppZXhyZTE6uIA15GG6E2CmJvzFgA8bvl", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Azure DRM DASH Var1", new PlayerSelectionOption("h3ZXhyZTE6ATBTgah1HUOrCx5S2i2NNe", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Azure eHLS Var1", new PlayerSelectionOption("8wMnhyZTE6i6Y54Sz9X7T2f3oT5LU7sq", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Azure Clear HLS/DASH Var1", new PlayerSelectionOption("I1bXdyZTE6KCZ9ByP8oc7wxzmnPciIYP", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Azure fpts/DASH DRM Var1", new PlayerSelectionOption("V3MHhyZTE65obsJsmfRV8-OP1nqyOJqP", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));

    selectionMap.put("Azure Clear HLS Var1-copy", new PlayerSelectionOption("M1YnhyZTE6X6-QO5AmLg1f623BYEDioW", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Azure DRM HLS Var1-copy", new PlayerSelectionOption("k5ZHhyZTE60bYC8uPfP75GoENXPWa-f_", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,MultiAudioActivity.class));
    selectionMap.put("Azure Clear DASH Var1-copy", new PlayerSelectionOption("ZuZHhyZTE6bGxbwDy9zN5zRvFlHydgky", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Azure DRM DASH Var1-copy", new PlayerSelectionOption("hmZnhyZTE6nkRmjIZjBZF5fuEjXxR4D4", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Azure eHLS Var1-copy", new PlayerSelectionOption("gzM3hyZTE6FYgJOHmTfEu0gJTGTNEzjw", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Azure Clear HLS/DASH Var1-copy", new PlayerSelectionOption("hwcXdyZTE6FLIL9U0UwZeHblMZ_CidXI", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Azure fpts/DASH DRM Var1-copy", new PlayerSelectionOption("ZzdndyZTE6xbnLFc9zFQpVeZhu2M-JtF", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));

    selectionMap.put("Azure Clear HLS Var2", new PlayerSelectionOption("43bnhyZTE64pp4S7s2Ip59h9-u0Tf38R", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Azure DRM HLS Var2", new PlayerSelectionOption("dzbXhyZTE6tHVS5qvVQz0ftWGYta-x4F", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,MultiAudioActivity.class));
    selectionMap.put("Azure Clear DASH Var2", new PlayerSelectionOption("pla3hyZTE670TKlBa0Yoa--Pcp_3PHkH", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Azure DRM DASH Var2", new PlayerSelectionOption("ByanhyZTE6CxieRrvQ7dDfz43R3-CRkc", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Azure eHLS Var2", new PlayerSelectionOption("1jb3hyZTE6WpUyiXBv-51xhsM_vsoVNi", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Azure Clear HLS/DASH Var2", new PlayerSelectionOption("sydXhyZTE6TLCs-wSUnJ2485uGRRJGB1", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Azure fpts/DASH DRM Var2", new PlayerSelectionOption("g1cHhyZTE6khrdWCRZXgwf4wRvqgZ701", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));

    selectionMap.put("Azure Clear HLS Var2-copy", new PlayerSelectionOption("NubnhyZTE6pyMfC0JDjxd5c4Ge5Tpb7t", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Azure DRM HLS Var2-copy", new PlayerSelectionOption("ljbXhyZTE6gNwjO0MEWyVm6a9k6JoDuK", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,MultiAudioActivity.class));
    selectionMap.put("Azure Clear DASH Var2-copy", new PlayerSelectionOption("xobHhyZTE6p6DIACBCyGLPhWpX6kIEPO", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Azure DRM DASH Var2-copy", new PlayerSelectionOption("03anhyZTE6Sg9I6qwgV4m2n_wh5Z9WsZ", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Azure eHLS Var2-copy", new PlayerSelectionOption("4yb3hyZTE6x8VOh3aPPDHk8lJskxaS07", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Azure Clear HLS/DASH Var2-copy", new PlayerSelectionOption("55c3hyZTE6o0K1AiasoFiOmUd2Mmo_x0", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Azure fpts/DASH DRM Var2-copy", new PlayerSelectionOption("lucHhyZTE6qO0F6nIckIAKoZJZxQATpr", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));

    selectionMap.put("Azure Clear HLS Var5", new PlayerSelectionOption("p1eXhyZTE6qeKu5YljlqjIvtnlvkWig4", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Azure DRM HLS Var5", new PlayerSelectionOption("Z0MHlyZTE6vRDsTlYedKM3DNYga6EaX8", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,MultiAudioActivity.class));
    selectionMap.put("Azure Clear DASH Var5", new PlayerSelectionOption("5hMXlyZTE6Xymmf1HTLOCRITvBp_ysk1", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Azure DRM DASH Var5", new PlayerSelectionOption("xmMnlyZTE6PxtdbxL6jvECG2v6J7U99K", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Azure eHLS Var5", new PlayerSelectionOption("xyeHhyZTE6L_DdyHGh06RQNHxwvpLg37", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Azure Clear HLS/DASH Var5", new PlayerSelectionOption("J0dnhyZTE6jxN699wKmxLB5GfSB-kt0K", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Azure fpts/DASH DRM Var5", new PlayerSelectionOption("YzeHhyZTE6JYi37FwBsbIae7ozD7T-Xf", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));

    selectionMap.put("Azure Clear HLS Var5-copy", new PlayerSelectionOption("c3MHlyZTE6kUd0xoMGG2LA6sbmM3HMrI", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Azure DRM HLS Var5-copy", new PlayerSelectionOption("VmMHlyZTE6qbE17I3XOhy6MgsWhTfAup", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,MultiAudioActivity.class));
    selectionMap.put("Azure Clear DASH Var5-copy", new PlayerSelectionOption("Z3MXlyZTE6wLcXll9l-OTo0JVbu5G3vB", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Azure DRM DASH Var5-copy", new PlayerSelectionOption("o1MnlyZTE6A2GxgCRw3JMgFtcvP4IFaQ", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Azure eHLS Var5-copy", new PlayerSelectionOption("o2eXhyZTE6WtQVY1rlNyYvDGT1PW7OaZ", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Azure Clear HLS/DASH Var5-copy", new PlayerSelectionOption("9jd3hyZTE6Ybqbo_VsYHEpvUlJg21pQx", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Azure fpts/DASH DRM Var5-copy", new PlayerSelectionOption("Vnd3hyZTE6i6cwJPa46i2ge1Ao28Xgh-", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));

    selectionMap.put("Elemental HLS MT-Variant-1", new PlayerSelectionOption("9oc3ZzZTE6rkpKJVCyGg_vFSWTQ5LOUB", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Elemental DASH MT-Variant-1", new PlayerSelectionOption("p4c3ZzZTE6SFvgjVPd1hXX3YfjOpJAhu", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Elemental eHLS MT-Variant-1", new PlayerSelectionOption("E4dHZzZTE6-5aCf147EC-uqRTkucqKWB", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Elemental DASH&HLS MT-Variant-1", new PlayerSelectionOption("1pdHZzZTE6Wuz26flXA1i-hP-uPv2Hjh", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));

    selectionMap.put("Elemental HLS MT-Variant-2", new PlayerSelectionOption("8wdnZzZTE68i13f1fEuC_FnRaMGw0hS1", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Elemental DASH MT-Variant-2", new PlayerSelectionOption("JqdXZzZTE60dB_qoVJfwbJCgEZcBcget", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Elemental eHLS MT-Variant-2", new PlayerSelectionOption("84dnZzZTE6BRpY86hFPpUKWjN6BqVPJr", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Elemental DASH&HLS MT-Variant-2", new PlayerSelectionOption("AzdXZzZTE6R1nkl0ATTLv1krq-OX4RDZ", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));

    selectionMap.put("Elemental HLS MT-Variant-2copy", new PlayerSelectionOption("11Y3lzZTE6lyIUF6bbYSBPEHWkjTFpow", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Elemental DASH MT-Variant-2copy", new PlayerSelectionOption("80Z3lzZTE6Ga-FWBY2VGEkRxyjbvq79s", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Elemental eHLS MT-Variant-2copy", new PlayerSelectionOption("h2ZHlzZTE6-YQJIitmIV5RwlJBWHOwKa", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Elemental DASH&HLS MT-Variant-2copy", new PlayerSelectionOption("9kZnlzZTE6us2h0_rfFGcIdLYSyNGRU5", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));

    selectionMap.put("Elemental HLS MT-Variant-6", new PlayerSelectionOption("kzd3ZzZTE6OtnaIbZKlPL5agHYNKS2cq", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Elemental DASH MT-Variant-6", new PlayerSelectionOption("F1dnZzZTE6IHFms28cye2csu9RGn4Wpp", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Elemental eHLS MT-Variant-6", new PlayerSelectionOption("tpdnZzZTE6qe66Fn558NrjrGBfiNbY3r", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Elemental DASH&HLS MT-Variant-6", new PlayerSelectionOption("1xdnZzZTE6mhDfTDBDfGZf6LcftUdRRI", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));

    selectionMap.put("Elemental HLS MT-Variant-6copy", new PlayerSelectionOption("RyMDB0ZTE63oqMajyUgE1P0QKR6mln2E", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Elemental DASH MT-Variant-6copy", new PlayerSelectionOption("d2YTB0ZTE6sNL25zSPLd_Osiq2d1gKUo", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Elemental eHLS MT-Variant-6copy", new PlayerSelectionOption("4zNDB0ZTE63Dup-47FIZKwBJUn7DRSST", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Elemental DASH&HLS MT-Variant-6copy", new PlayerSelectionOption("FtOTB0ZTE6zQl3KyGyGmhrHpYfTtfoRC", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));

    selectionMap.put("Elemental HLS MT-Variant-5", new PlayerSelectionOption("xmMXdzZTE6Bydd2tPBEOg-q5yvQgjebE", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Elemental DASH MT-Variant-5", new PlayerSelectionOption("N3MndzZTE6lNdlnQqZ4CxK8TgQIj_7Qf", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Elemental eHLS MT-Variant-5", new PlayerSelectionOption("ViNXdzZTE6CxtLfMiTQH4Me8jz-p2cbS", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));
    selectionMap.put("Elemental DASH&HLS MT-Variant-5", new PlayerSelectionOption("o0NHdzZTE6xc6g06MbXie5f9oXtPYbzR", "k0a2gyOt0QGNJLSuzKfdY4R-hw2b", domain,  MultiAudioActivity.class));

    pcode = "k0a2gyOt0QGNJLSuzKfdY4R-hw2b";
    selectionMap.put("English/German tracks", new PlayerSelectionOption("wxNjRwZTE6bxMBkrQbfM_BwokGf0pyOm", pcode, domain, MultiAudioActivity.class));
    selectionMap.put("Undefined/Undefined asset1", new PlayerSelectionOption("xmaTRwZTE6lCuPd3H82AteBFUvNHtrHu", pcode, domain, MultiAudioActivity.class));
    selectionMap.put("Undefined/Undefined asset2", new PlayerSelectionOption("Y1cTRwZTE6Mc47fc_8n161HVYQYu5l0d", pcode, domain, MultiAudioActivity.class));
    selectionMap.put("No config Eng main+commentary/Ger main+commentary", new PlayerSelectionOption("txcjRwZTE6aMSqloxZZ3hZhTqz5hEaY9", pcode, domain, MultiAudioActivity.class));
    selectionMap.put("Config Eng main+commentary/Ger main+commentary", new PlayerSelectionOption("EweDRwZTE6ipjPDynkEotrpTvvYgDvr7", pcode, domain, MultiAudioActivity.class));
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

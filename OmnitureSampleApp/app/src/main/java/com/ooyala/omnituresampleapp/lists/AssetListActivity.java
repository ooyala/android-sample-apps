package com.ooyala.omnituresampleapp.lists;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ooyala.omnituresampleapp.R;
import com.ooyala.omnituresampleapp.utils.AssetDataSource;
import com.ooyala.omnituresampleapp.utils.PlayerSelectionOption;

import java.util.Map;

public class AssetListActivity extends AppCompatActivity implements OnItemClickListener {

    ArrayAdapter<String> listAdapter;
    Map<String, PlayerSelectionOption> assets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_list);

        assets = AssetDataSource.getAssets();
        listAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                assets.keySet().toArray(new String[assets.keySet().size()]));

        ListView assetList = (ListView) findViewById(R.id.list_view);
        assetList.setAdapter(listAdapter);
        assetList.setOnItemClickListener(this);
    }

    /** OnItemClickListener methods **/

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PlayerSelectionOption selection =  assets.get(listAdapter.getItem(position));
        Class<? extends Activity> selectedClass = selection.getActivity();

        //Launch the correct activity with the embed code as an extra
        Intent intent = new Intent(this, selectedClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra("embed_code", selection.getEmbedCode());
        intent.putExtra("pcode", selection.getPcode());
        intent.putExtra("selection_name", listAdapter.getItem(position));
        startActivity(intent);
    }
}

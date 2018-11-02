package com.ooyala.sample.players;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.ooyala.sample.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This activity illustrates how we can dynamically add and play video using Skin SDK by adding the embed code and PCode.
 * You can Select OoyalaDefault for BasicSkinPlayback also use it for playing Ooyala and VAST advertisements.
 * Also You can select Assets with GoogleIMA and Freewheel by selecting the respective players.
 *
 * @author skumar
 */
public class AddAssetActivity extends Activity {

  public static String getName() {
    return "Add & Play";
  }
  private EditText embedCodeEditText;
  private EditText pCodeEditText;
  private EditText apiKeyEditText;
  private EditText secretEditText;
  private EditText accountIdEditText;
  private Spinner playerSpinner;
  private Spinner formatSpinner;
  private String embedCode;
  private String pCode;
  private String secret;
  private String apiKey;
  private String accountId;
  private String playerActivity;
  private CheckBox autoPlayCheckBox;
  private String selectedFormat;
  private Spinner hevcSpinner;
  private CheckBox envStgCheckBox;


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.embed_pcode_layout);
    addItemsOnSpinner();
    embedCodeEditText = (EditText) findViewById(R.id.embed_edit_text);
    apiKeyEditText = (EditText) findViewById(R.id.apikey_edit_text);
    secretEditText = (EditText) findViewById(R.id.secret_edit_text);
    accountIdEditText = (EditText) findViewById(R.id.accountId_edit_text);
    pCodeEditText = (EditText) findViewById(R.id.pcode_edit_text);
    autoPlayCheckBox = (CheckBox) findViewById(R.id.auto_play_check_box);
    envStgCheckBox = (CheckBox) findViewById(R.id.set_env_stg);
    initButtonListeners();
  }

  private void initButtonListeners() {
    Button setAssetButton = findViewById(R.id.open_button);
    setAssetButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        embedCode = embedCodeEditText.getText().toString();
        pCode = pCodeEditText.getText().toString();
        secret = secretEditText.getText().toString();
        apiKey = apiKeyEditText.getText().toString();
        accountId = accountIdEditText.getText().toString();
        playerActivity =  String.valueOf(playerSpinner.getSelectedItem());
        selectedFormat = String.valueOf(formatSpinner.getSelectedItem());
        if (embedCode.isEmpty()) {
          Toast.makeText(AddAssetActivity.this, "Embed code can't be empty!", Toast.LENGTH_LONG).show();
          return;
        }
        if (pCode.isEmpty()) {
          pCode = "c0cTkxOqALQviQIGAHWY5hP0q9gU";
        }
        startPlayerActivity();
      }
    });
  }

  private void addItemsOnSpinner() {
    playerSpinner = (Spinner) findViewById(R.id.select_player);
    List<String> list = new ArrayList<String>();
    list.add("OoyalaDefault");
    list.add("GoogleIMA");
    list.add("Freewheel");
    list.add("Geoblocking");
    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
            android.R.layout.simple_spinner_item, list);
    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    playerSpinner.setAdapter(dataAdapter);

    formatSpinner = (Spinner) findViewById(R.id.selectFormat);
    list = new ArrayList<String>();
    list.add("default");
    list.add("dash");
    list.add("akamai_hd2_vod_hls");
    list.add("mp4");
    list.add("m3u8");
    list.add("hls");
    list.add("akamai_hd2_hls");
    dataAdapter = new ArrayAdapter<String>(this,
            android.R.layout.simple_spinner_item, list);
    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    formatSpinner.setAdapter(dataAdapter);

    hevcSpinner = (Spinner) findViewById(R.id.hevc_mode);
    list = new ArrayList<String>();
    list.add("NoPreference");
    list.add("HEVCPreferred");
    list.add("HEVCNotPreferred");
    dataAdapter = new ArrayAdapter<String>(this,
            android.R.layout.simple_spinner_item, list);
    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    hevcSpinner.setAdapter(dataAdapter);
  }

  private void startPlayerActivity() {
    //PlayerActivity map
    HashMap<String, Class<? extends Activity>> pActivity = new HashMap<String, Class<? extends Activity>>();
    pActivity.put("OoyalaDefault",OoyalaSkinPlayerActivity.class);
    pActivity.put("GoogleIMA",PreconfiguredIMAPlayerActivity.class);
    pActivity.put("Freewheel",PreconfiguredFreewheelPlayerActivity.class);
    pActivity.put("Geoblocking",GeoBlockingActivity.class);
    //Launch the correct activity
    Intent intent = new Intent(this, pActivity.get(playerActivity));
    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    intent.putExtra("embed_code", embedCode);
    intent.putExtra("pcode", pCode);
    intent.putExtra("className",this.getClass().getSimpleName());
    intent.putExtra("domain", "http://www.ooyala.com");
    intent.putExtra("autoPlay", autoPlayCheckBox.isChecked() ? true : false);
    intent.putExtra("secret", secret);
    intent.putExtra("apiKey", apiKey);
    intent.putExtra("accountId", accountId);
    intent.putExtra("selectedFormat", selectedFormat);
    intent.putExtra("hevc_mode", String.valueOf(hevcSpinner.getSelectedItem()));
    intent.putExtra("is_staging", envStgCheckBox.isChecked() ? true : false);
    startActivity(intent);
  }
}
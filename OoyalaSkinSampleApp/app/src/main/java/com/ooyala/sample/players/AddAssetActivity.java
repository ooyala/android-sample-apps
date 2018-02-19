package com.ooyala.sample.players;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
  private Spinner spinner;
  private String embedCode;
  private String pCode;
  private String playerActivity;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.embed_pcode_layout);
    addItemsOnSpinner();
    embedCodeEditText = (EditText) findViewById(R.id.embed_edit_text);
    pCodeEditText = (EditText) findViewById(R.id.pcode_edit_text);
    initButtonListeners();
  }

  private void initButtonListeners() {
    Button setAssetButton = findViewById(R.id.open_button);
    setAssetButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        embedCode = embedCodeEditText.getText().toString();
        pCode = pCodeEditText.getText().toString();
        playerActivity =  String.valueOf(spinner.getSelectedItem());
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
    spinner = (Spinner) findViewById(R.id.select_player);
    List<String> list = new ArrayList<String>();
    list.add("OoyalaDefault");
    list.add("GoogleIMA");
    list.add("Freewheel");
    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
            android.R.layout.simple_spinner_item, list);
    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinner.setAdapter(dataAdapter);
  }

  private void startPlayerActivity() {
    //PlayerActivity map
    HashMap<String, Class<? extends Activity>> pActivity = new HashMap<String, Class<? extends Activity>>();
    pActivity.put("OoyalaDefault",OoyalaSkinPlayerActivity.class);
    pActivity.put("GoogleIMA",PreconfiguredIMAPlayerActivity.class);
    pActivity.put("Freewheel",PreconfiguredFreewheelPlayerActivity.class);

    //Launch the correct activity
    Intent intent = new Intent(this, pActivity.get(playerActivity));
    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    intent.putExtra("embed_code", embedCode);
    intent.putExtra("pcode", pCode);
    intent.putExtra("domain", "http://www.ooyala.com");
    startActivity(intent);
  }
}
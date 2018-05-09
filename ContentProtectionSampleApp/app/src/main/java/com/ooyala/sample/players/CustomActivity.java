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
        import  android.widget.Toast;

        import com.ooyala.sample.R;

        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;

        /**
 * This activity illustrates how we can dynamically add and play video using Skin SDK by adding the embed code and PCode.
  * You can Select OoyalaDefault for BasicSkinPlayback also use it for playing Ooyala and VAST advertisements.
  * Also You can select Assets with GoogleIMA and Freewheel by selecting the respective players.
  *
         *  * @author skumar
  */
        public class CustomActivity extends Activity {

          public static String getName() {
            return "Custom Activity";
          }
  private EditText embedCodeEditText;
  private EditText pCodeEditText;
  private EditText apikeyEditText;
  private EditText secretEditText;
  private EditText accountidEditText;
  private String embedCode;
  private String pCode;
  private String apiKey;
  private String secret;
  private String accountId;

  private CheckBox autoPlayCheckBox;
  private boolean autoPlay;

  @Override
 public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.embed_pcode_layout);
            embedCodeEditText = (EditText) findViewById(R.id.embed_edit_text);
            pCodeEditText = (EditText) findViewById(R.id.pcode_edit_text);
            apikeyEditText = (EditText) findViewById(R.id.apikey_edit_text);
            secretEditText = (EditText) findViewById(R.id.secret_edit_text);
            accountidEditText = (EditText) findViewById(R.id.accountid_edit_text);
            autoPlayCheckBox = (CheckBox) findViewById(R.id.auto_play_check_box);
            initButtonListeners();
          }

          private void initButtonListeners() {
            Button setAssetButton = findViewById(R.id.play_button);
            setAssetButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
            embedCode = embedCodeEditText.getText().toString();
            pCode = pCodeEditText.getText().toString();
            apiKey = apikeyEditText.getText().toString();
            secret = secretEditText.getText().toString();
            accountId = accountidEditText.getText().toString();
                        autoPlay = autoPlayCheckBox.isChecked() ? true : false;
                        if (embedCode.isEmpty()) {
                              Toast.makeText(CustomActivity.this, "Embed code can't be empty!", Toast.LENGTH_LONG).show();
                              return;
                            }
                        if (pCode.isEmpty()) {
                            Toast.makeText(CustomActivity.this, "PCode can't be empty!", Toast.LENGTH_LONG).show();
                            return;
                            }
                        startPlayerActivity();
                      }
    });
          }


          private void startPlayerActivity() {
            //PlayerActivity map
                    //Launch the correct activity
            Intent intent = new Intent(this, OoyalaPlayerTokenPlayerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intent.putExtra("embed_code", embedCode);
            intent.putExtra("pcode", pCode);
            intent.putExtra("domain", "http://www.ooyala.com");
            intent.putExtra("autoPlay", autoPlay);
            intent.putExtra("apikey", apiKey);
            intent.putExtra("secret", secret);
            intent.putExtra("accountid", accountId);
            startActivity(intent);
          }
}
package com.ooyala.sample.complete;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ooyala.sample.R;
import com.ooyala.sample.lists.BasicPlaybackListActivity;

import androidx.appcompat.app.AppCompatActivity;

public class AddAssetActivity extends AppCompatActivity {

    public final static String getName() {
        return "Custom BasicPlayback";
    }

    private EditText embedCodeEditText;
    private EditText pCodeEditText;
    private String embedCode;
    private String pCode;
    private EditText apiKeyEditText;
    private EditText secretKeyEditText;
    private EditText accountIdEditText;
    private String apiKey;
    private String secretKey;
    private String accountId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_asset);

        embedCodeEditText = (EditText) findViewById(R.id.embed_edit_text);
        pCodeEditText = (EditText) findViewById(R.id.pcode_edit_text);

        apiKeyEditText = (EditText) findViewById(R.id.APIKey_edit_text);
        secretKeyEditText = (EditText) findViewById(R.id.SecretKey_edit_text);
        accountIdEditText = (EditText) findViewById(R.id.AccountId_edit_text);

        initButtonListeners();
    }

    private void initButtonListeners() {
        Button setAssetButton = findViewById(R.id.open_button);
        setAssetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                embedCode = embedCodeEditText.getText().toString();
                pCode = pCodeEditText.getText().toString();

                apiKey = apiKeyEditText.getText().toString();
                secretKey = secretKeyEditText.getText().toString();
                accountId = accountIdEditText.getText().toString();

                if (embedCode.isEmpty()) {
                    Toast.makeText(AddAssetActivity.this, "Embed code can't be empty!", Toast.LENGTH_LONG).show();
                    return;
                }
                if (pCode.isEmpty()) {
                    Toast.makeText(AddAssetActivity.this, "Pcode can't be empty!", Toast.LENGTH_LONG).show();
                    return;
                }
                startPlayerActivity();

                if (apiKey.isEmpty()) {
                    Toast.makeText(AddAssetActivity.this, "APIKey can't be empty!", Toast.LENGTH_LONG).show();
                    return;
                }

                if (secretKey.isEmpty()) {
                    Toast.makeText(AddAssetActivity.this, "SecretKey can't be empty!", Toast.LENGTH_LONG).show();
                    return;
                }

                if (accountId.isEmpty()) {
                    Toast.makeText(AddAssetActivity.this, "AccountID can't be empty!", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });
    }

    private void startPlayerActivity() {


        //Launch the correct activity
        Intent intent = new Intent(this, BasicPlaybackListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra("embed_code", embedCode);
        intent.putExtra("pcode", pCode);
        intent.putExtra("api_key", apiKey);
        intent.putExtra("secret_key", secretKey);
        intent.putExtra("account_id", accountId);
        //startActivity(intent);
        startActivityForResult(intent, 1);
    }

}

package com.ooyala.sample.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.ooyala.sample.SampleApplication;

/**
 * An activity that start SimpleCastPlayerActivity or SkinCastPlayerActivity depending on the chosen skin options
 */
public class ExpandedControllerActivity extends Activity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    startActivity(new Intent(this, ((SampleApplication) getApplication()).getExpandedControllerActivity()));
  }
}

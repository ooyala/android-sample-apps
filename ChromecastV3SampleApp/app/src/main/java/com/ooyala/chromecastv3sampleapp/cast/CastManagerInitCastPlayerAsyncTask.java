package com.ooyala.chromecastv3sampleapp.cast;

import android.os.AsyncTask;

import com.ooyala.android.CastModeOptions;
import com.ooyala.android.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * An async task that can handle the blocking embed token generation needed to init the player
 */
public class CastManagerInitCastPlayerAsyncTask extends AsyncTask<Void, Integer, String> {
  private CastManager manager;
  private CastModeOptions options;

  CastManagerInitCastPlayerAsyncTask(CastManager manager, CastModeOptions options) {
    super();
    this.manager = manager;
    this.options = options;
  }

  @Override
  protected String doInBackground(Void...params) {
    List<String> embedCodes = new ArrayList<>();
    embedCodes.add(this.options.getEmbedCode());
    return Utils.blockingGetEmbedTokenForEmbedCodes(this.options.getGenerator(), embedCodes);
  }

  @Override
  protected void onPostExecute(String token) {
    super.onPostExecute(token);
    if (!isCancelled()) {
      manager.initCast(this.options, token);
    }
  }

}

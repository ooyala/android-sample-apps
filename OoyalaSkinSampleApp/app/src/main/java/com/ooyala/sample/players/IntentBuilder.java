package com.ooyala.sample.players;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class IntentBuilder {

  private Class<? extends Activity> activityClass;
  private Context context;
  private String embedCode;
  private String pcode;
  private String domain;
  private boolean autoPlay;

  public IntentBuilder(Context context) {
    this.context = context;
  }

  public IntentBuilder setActivity(Class<? extends Activity> activityClass) {
    this.activityClass = activityClass;
    return this;
  }

  public IntentBuilder setEmbedCode(String embedCode) {
    this.embedCode = embedCode;
    return this;
  }

  public IntentBuilder setPCode(String pcode) {
    this.pcode = pcode;
    return this;
  }

  public IntentBuilder setDomain(String domain) {
    this.domain = domain;
    return this;
  }

  public IntentBuilder setAutoPlay(boolean autoPlay){
    this.autoPlay = autoPlay;
    return this;
  }

  public Intent build() {
    Intent intent = new Intent(context, activityClass)
        .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        .putExtra(AbstractHookActivity.EXTRA_EMBED_CODE, embedCode)
        .putExtra(AbstractHookActivity.EXTRA_PCODE, pcode)
        .putExtra(AbstractHookActivity.EXTRA_DOMAIN, domain)
        .putExtra(AbstractHookActivity.EXTRA_AUTO_PLAY, autoPlay);
    return intent;
  }
}

package com.ooyala.sample.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.ooyala.sample.players.AbstractHookActivity;

public class IntentBuilder {

  private Class<? extends Activity> activityClass;
  private String embedCode;
  private String pcode;
  private String domain;
  private boolean autoPlay;

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

  public Intent build(Context context) {
    return new Intent(context, activityClass)
        .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        .putExtra(AbstractHookActivity.EXTRA_EMBED_CODE, embedCode)
        .putExtra(AbstractHookActivity.EXTRA_PCODE, pcode)
        .putExtra(AbstractHookActivity.EXTRA_DOMAIN, domain)
        .putExtra(AbstractHookActivity.EXTRA_AUTO_PLAY, autoPlay);
  }
}

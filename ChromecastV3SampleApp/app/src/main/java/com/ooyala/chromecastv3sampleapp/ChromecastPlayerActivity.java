package com.ooyala.chromecastv3sampleapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.google.android.gms.cast.framework.CastButtonFactory;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.chromecastv3sampleapp.cast.CastManager;

import java.util.Observable;
import java.util.Observer;

public class ChromecastPlayerActivity extends AppCompatActivity implements Observer {

  /*
   * The API Key and Secret should not be saved inside your applciation (even in git!).
   * However, for debugging you can use them to locally generate Ooyala Player Tokens.
   */
  private final String APIKEY = "fill me in";
  private final String SECRET = "fill me in";
  private final String ACCOUNT_ID = "accountID";

  CastManager castManager;

  private String embedCode;
  private String pcode;
  private String domain;


  private OoyalaPlayer player;
  private OoyalaPlayerLayout ooyalaPlayerLayout;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.player_activity);
    setupActionBar();

    // onClick of a DefaultMiniController only provides an embedcode through the extras
    parseBundle(getIntent().getExtras());
    initOoyala();
    castManager = new CastManager(this, player);
  }

  private void parseBundle(Bundle extras) {
    if (extras != null) {
      embedCode = extras.getString("embedcode");
      embedCode2 = extras.getString("embedcode2");
      pcode = extras.getString("pcode");
      domain = extras.getString("domain");
    }
  }

  private void initOoyala() {
    PlayerDomain playerDomain = new PlayerDomain(domain);
    Options options = new Options.Builder().setUseExoPlayer(true).build();
    ooyalaPlayerLayout = findViewById(R.id.ooyalaPlayer);
    player = new OoyalaPlayer(pcode, playerDomain, options);
    new OoyalaPlayerLayoutController(ooyalaPlayerLayout, player);
    player.addObserver(this);
    player.setEmbedCode(embedCode);
    player.play();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    boolean createOptionsMenu = super.onCreateOptionsMenu(menu);
    getMenuInflater().inflate(R.menu.browse, menu);
    CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), menu,
        R.id.media_route_menu_item);
    return createOptionsMenu;
  }

  private void setupActionBar() {
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
  }

  /**
   * Listen to all notifications from the OoyalaPlayer
   */
  @Override
  public void update(Observable arg0, Object argN) {
    if (castManager != null) {
      castManager.update(arg0, argN);
    }
  }
}


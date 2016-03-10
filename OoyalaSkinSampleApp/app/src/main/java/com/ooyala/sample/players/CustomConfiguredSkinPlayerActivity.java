package com.ooyala.sample.players;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ooyala.sample.R;
import com.ooyala.sample.fragments.SkinPlayerFragment;

/**
 * Created by dkorobov on 3/9/16.
 */
public class CustomConfiguredSkinPlayerActivity extends AppCompatActivity {

  private static String name = "Configured Skin Player";
  private SkinPlayerFragment skinPlayerFragment;

  public static String getName() {
    return name;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getSupportActionBar().setDisplayShowTitleEnabled(false);
    setContentView(R.layout.configurable_player_skin_layout);
    skinPlayerFragment = (SkinPlayerFragment) getSupportFragmentManager().findFragmentById(R.id.video_fragment);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.skin_menu, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    skinPlayerFragment.resizePlayer(item.getItemId());
    return super.onOptionsItemSelected(item);
  }
}

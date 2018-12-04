package com.ooyala.sample.players;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.ooyala.sample.R;
import com.ooyala.sample.utils.fragments.SkinPlayerFragment;

public class ResizablePlayerActivity extends AppCompatActivity implements DefaultHardwareBackBtnHandler {

  private static String name = "Resizable Skin Player";
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
  public void invokeDefaultOnBackPressed() {
    super.onBackPressed();
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
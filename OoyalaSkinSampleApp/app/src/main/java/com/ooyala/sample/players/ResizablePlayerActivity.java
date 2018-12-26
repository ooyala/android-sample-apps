package com.ooyala.sample.players;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.ooyala.sample.R;
import com.ooyala.sample.utils.fragments.SkinPlayerFragment;

public class ResizablePlayerActivity extends AppCompatActivity implements DefaultHardwareBackBtnHandler, View.OnSystemUiVisibilityChangeListener {

  private static String name = "Resizable Skin Player";
  private SkinPlayerFragment skinPlayerFragment;
  private Toolbar toolbar;
  private boolean fullscreen;
  private int lastMenuItemId = UNSET_ID;
  private static final int FULLSCREEN = 6;
  public static final int UNSET_ID = -1;

  public static String getName() {
    return name;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.configurable_player_skin_layout);
    skinPlayerFragment = (SkinPlayerFragment) getSupportFragmentManager().findFragmentById(R.id.video_fragment);
    toolbar = findViewById(R.id.toolbar);

    // need to use Toolbar instead of ActionBar because of a bug
    // after toggling setSystemUiVisibility
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayShowTitleEnabled(false);

    final View decorView = getWindow().getDecorView();
    decorView.setOnSystemUiVisibilityChangeListener(this);
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
    lastMenuItemId = item.getItemId();
    skinPlayerFragment.resizePlayer(lastMenuItemId, getSupportActionBar().getHeight(), fullscreen);
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);

    skinPlayerFragment.resizePlayer(lastMenuItemId, getSupportActionBar().getHeight(), fullscreen);
  }

  @Override
  public void onSystemUiVisibilityChange(int visibility) {
    fullscreen = visibility == FULLSCREEN;

    setViewVisibility(toolbar, fullscreen);

    skinPlayerFragment.resizePlayer(lastMenuItemId, getSupportActionBar().getHeight(), fullscreen);
  }

  private void setViewVisibility(View view, boolean fullscreen) {
    if (fullscreen && view.getVisibility() == View.VISIBLE) {
      view.setVisibility(View.GONE);
    } else if (!fullscreen && view.getVisibility() == View.GONE) {
      view.setVisibility(View.VISIBLE);
    }
  }
}
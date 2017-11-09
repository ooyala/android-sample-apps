package com.ooyala.sample.screen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;

import com.ooyala.sample.R;
import com.ooyala.sample.interfaces.ItemClickedInterface;
import com.ooyala.sample.utils.VideoData;

import static com.ooyala.android.util.TvHelper.isTargetDeviceTV;


public class MainActivity extends AppCompatActivity implements ItemClickedInterface {

  private Toolbar toolbar;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_activity);
    toolbar = (Toolbar) findViewById(R.id.toolbar);

    setupToolbar();
    showRecyclerFragment();

    getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
      @Override
      public void onBackStackChanged() {
        if (getSupportActionBar() != null) {
          boolean showHomeAsUp = getSupportFragmentManager().getBackStackEntryCount() > 0;
          getSupportActionBar().setDisplayHomeAsUpEnabled(showHomeAsUp);
          if (!showHomeAsUp) {
            toolbar.setTitle(R.string.app_name);
          }
        }
      }
    });
    if(isTargetDeviceTV(this)){
      getSupportActionBar().hide();
    }
  }

  @Override
  public void onBackPressed() {
    FragmentManager supportFragmentManager = getSupportFragmentManager();
    if (supportFragmentManager.getBackStackEntryCount() >= 1) {
      supportFragmentManager.popBackStack();
    } else {
      super.onBackPressed();
    }
  }

  @Override
  public void onItemClicked(VideoData data) {
    showVideoFragment(data);
  }

  public void showVideoFragment(VideoData data) {
    final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    fragmentTransaction.replace(R.id.container, VideoFragment.createVideoFragment(data), VideoFragment.TAG).addToBackStack(null).commit();
    toolbar.setTitle(data.getTitle());
  }

  private void showRecyclerFragment() {
    final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    fragmentTransaction.replace(R.id.container, new VideoRecyclerFragment(), VideoRecyclerFragment.TAG).commit();
  }

  private void setupToolbar() {
    setSupportActionBar(toolbar);
    ActionBar supportActionBar = getSupportActionBar();
    if (supportActionBar != null) {
      supportActionBar.setTitle(R.string.app_name);
      toolbar.bringToFront();
      toolbar.setNavigationOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          MainActivity.this.onBackPressed();
        }
      });
    }
  }
}

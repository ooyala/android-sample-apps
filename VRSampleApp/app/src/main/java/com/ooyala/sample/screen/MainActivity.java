package com.ooyala.sample.screen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ooyala.sample.R;
import com.ooyala.sample.fragmentfactory.FragmentFactory;
import com.ooyala.sample.interfaces.VideoChooseInterface;
import com.ooyala.sample.interfaces.TvControllerInterface;
import com.ooyala.sample.utils.VideoData;

import static android.view.KeyEvent.KEYCODE_BACK;
import static com.ooyala.android.util.TvHelper.isTargetDeviceTV;


public class MainActivity extends AppCompatActivity implements VideoChooseInterface {

  private Toolbar toolbar;
  private FragmentFactory fragmentFactory;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_activity);
    toolbar = (Toolbar) findViewById(R.id.toolbar);

    fragmentFactory = new FragmentFactory();

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

    hideToolbarForTv();
  }

  private void hideToolbarForTv() {
    if (isTargetDeviceTV(this) && getSupportActionBar() != null) {
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
  public void onVideoChoose(VideoData data) {
    VideoFragment fragment = FragmentFactory.getFragmentByAdType(data.getAdType());
    fragment.setArguments(data);
    showVideoFragment(fragment);
    toolbar.setTitle(data.getTitle());
    toolbar.hideOverflowMenu();
  }

  public void showVideoFragment(VideoFragment fragment) {
    final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    fragmentTransaction.replace(R.id.container, fragment, VideoFragment.TAG).addToBackStack(null).commit();
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

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if(item.getItemId() == R.id.menu_add_video){
      DialogFragment dialogFragment = new EmbedCodeDialogFragment();
      dialogFragment.show(getSupportFragmentManager(), EmbedCodeDialogFragment.class.getSimpleName());
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KEYCODE_BACK) {
      this.onBackPressed();
      return true;
    } else {
      for (Fragment fragment : getSupportFragmentManager().getFragments()) {
        if (fragment instanceof TvControllerInterface) {
          ((TvControllerInterface) fragment).onKeyDown(keyCode, event);
        }
      }
      return super.onKeyDown(keyCode, event);
    }
  }

  @Override
  public boolean onKeyUp(int keyCode, KeyEvent event) {
    if (keyCode != KEYCODE_BACK) {
      for (Fragment fragment : getSupportFragmentManager().getFragments()) {
        if (fragment instanceof TvControllerInterface) {
          ((TvControllerInterface) fragment).onKeyUp(keyCode, event);
        }
      }
      return super.onKeyUp(keyCode, event);
    }
    return true;
  }
}

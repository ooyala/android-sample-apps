package com.ooyala.sample.screen;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ooyala.sample.R;
import com.ooyala.sample.fragmentfactory.FragmentFactory;
import com.ooyala.sample.interfaces.VideoChooseInterface;
import com.ooyala.sample.utils.VideoData;

import static com.ooyala.android.util.TvHelper.isTargetDeviceTV;


public class MainActivity extends AppCompatActivity implements VideoChooseInterface {

  private Toolbar toolbar;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_activity);
    toolbar = findViewById(R.id.toolbar);

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
    setupToolbar();
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
    getSupportActionBar().setTitle(R.string.app_name);
    getSupportActionBar().show();
    toolbar.bringToFront();
    toolbar.showOverflowMenu();

    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        MainActivity.this.onBackPressed();
      }
    });
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
}

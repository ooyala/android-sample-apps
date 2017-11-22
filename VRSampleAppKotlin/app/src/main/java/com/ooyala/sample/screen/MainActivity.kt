package com.ooyala.sample.screen

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.view.KeyEvent.KEYCODE_BACK
import com.ooyala.android.util.TvHelper.isTargetDeviceTV
import com.ooyala.sample.R
import com.ooyala.sample.VideoFragment
import com.ooyala.sample.fragmentfactory.FragmentFactory
import com.ooyala.sample.interfaces.VideoChooseInterface
import com.ooyala.sample.interfaces.TvControllerInterface
import com.ooyala.sample.utils.VideoItemType
import com.ooyala.sample.utils.VideoData
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity(), VideoChooseInterface {

  private val fragmentFactory = FragmentFactory()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main_activity)
    setupToolbar()
    showRecyclerFragment()
    supportFragmentManager.addOnBackStackChangedListener { onBackStackChanged() }
    hideToolbarForTv()
  }

  private fun hideToolbarForTv() {
    if (isTargetDeviceTV(this)) {
      supportActionBar?.hide()
    }
  }

  override fun onBackPressed() {
    if (supportFragmentManager.backStackEntryCount >= 1) {
      supportFragmentManager.popBackStack()
    } else {
      super.onBackPressed()
    }
  }

  override fun onVideoChoose(data: VideoData) {
    if (data.type == VideoItemType.VIDEO) {
      val fragment = fragmentFactory.getFragmentByType(data.adType)
      fragment.setArguments(data)
      openVideoFragment(fragment)
      toolbar.title = data.title;
      toolbar.hideOverflowMenu()
    }
  }

  private fun openVideoFragment(fragment: VideoFragment) {
    val fragmentTransaction = supportFragmentManager.beginTransaction()
    fragmentTransaction.replace(R.id.container, fragment, VideoFragment.TAG).addToBackStack(null).commit()

  }

  private fun showRecyclerFragment() {
    val fragmentTransaction = supportFragmentManager.beginTransaction()
    fragmentTransaction.replace(R.id.container, VideoRecyclerFragment(), VideoRecyclerFragment.TAG).commit()
  }

  private fun setupToolbar() {
    setSupportActionBar(toolbar)
    toolbar?.bringToFront()
    toolbar.setNavigationOnClickListener { onBackPressed() }
  }

  private fun onBackStackChanged() {
    val isBackStackNonEmpty = supportFragmentManager.backStackEntryCount > 0
    supportActionBar?.setDisplayHomeAsUpEnabled(isBackStackNonEmpty)
    if (!isBackStackNonEmpty) {
      toolbar.title = getString(R.string.app_name)
    }
  }

  override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
    if (keyCode == KEYCODE_BACK) {
      this.onBackPressed()
      return true
    } else {
      for (fragment in supportFragmentManager.fragments) {
        if (fragment is TvControllerInterface) {
          fragment.onKeyDown(keyCode, event)
        }
      }
      return super.onKeyDown(keyCode, event)
    }
  }

  override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
    if (keyCode != KEYCODE_BACK) {
      for (fragment in supportFragmentManager.fragments) {
        if (fragment is TvControllerInterface) {
          fragment.onKeyUp(keyCode, event)
        }
      }
      return super.onKeyUp(keyCode, event)
    } else {
      return true
    }
  }


  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.menu, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (item.itemId == R.id.menu_add_video) {
      val dialogFragment = EmbedCodeDialogFragment()
      dialogFragment.show(supportFragmentManager, EmbedCodeDialogFragment::class.java!!.getSimpleName())
    }
    return super.onOptionsItemSelected(item)
  }
}

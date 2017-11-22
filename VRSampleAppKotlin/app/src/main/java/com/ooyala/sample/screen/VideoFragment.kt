package com.ooyala.sample

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ooyala.android.OoyalaNotification
import com.ooyala.android.OoyalaPlayer
import com.ooyala.android.PlayerDomain
import com.ooyala.android.configuration.FCCTVRatingConfiguration
import com.ooyala.android.configuration.Options
import com.ooyala.android.player.vrexoplayer.glvideoview.effects.VrMode
import com.ooyala.android.skin.OoyalaSkinLayoutController
import com.ooyala.android.skin.configuration.SkinOptions
import com.ooyala.android.util.SDCardLogcatOoyalaEventsLogger
import com.ooyala.sample.interfaces.OnButtonPressedInterface
import com.ooyala.sample.utils.VideoData
import kotlinx.android.synthetic.main.video_fragment.*
import java.util.*


open class VideoFragment() : Fragment(), Observer, OnButtonPressedInterface {

  override fun onBackPressed() {
    playerController.switchVRMode(VrMode.NONE)
  }

  override fun onKeyUp(keyCode: Int, event: KeyEvent) {
    playerController.onKeyUp(keyCode, event)
  }

  override fun onKeyDown(keyCode: Int, event: KeyEvent) {
    playerController.onKeyDown(keyCode, event)
  }

  companion object {
    val TAG = VideoFragment::class.java.canonicalName
    const val PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1
  }

  private val logger = SDCardLogcatOoyalaEventsLogger()
  private var writeStoragePermissionGranted = false

  private lateinit var embedCode: String
  private lateinit var pCode: String
  private lateinit var domain: String
  protected var player: OoyalaPlayer? = null
  private lateinit var playerController: OoyalaSkinLayoutController;

  constructor(data: VideoData) : this() {
    this.embedCode = data.embedCode!!
    this.domain = data.domain!!
    this.pCode = data.pCode!!
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
          inflater?.inflate(R.layout.video_fragment, container, false)

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    if (ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
      requestPermissions(arrayOf(WRITE_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE)
    } else {
      initPlayer()
    }
  }

  override fun onResume() {
    super.onResume()
    player?.resume()
  }

  override fun onPause() {
    super.onPause()
    player?.suspend()
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    if (requestCode == PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
      if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
        writeStoragePermissionGranted = true
      }
      initPlayer()
    }
  }

  override fun update(o: Observable?, arg: Any?) {
    val notification = OoyalaNotification.getNameOrUnknown(arg)
    if (notification != OoyalaPlayer.TIME_CHANGED_NOTIFICATION_NAME) {
      val text = "Notification Received: $notification - state:  + ${player?.state}"
      Log.d(TAG, text)
      if (writeStoragePermissionGranted) {
        Log.d(TAG, "Writing log to SD card")
        logger.writeToSdcardLog(text)
      }
    }
      changeToolbarVisibilityInFullscreenMode(arg)
  }

  open fun initAdManager() {
  }

  private fun changeToolbarVisibilityInFullscreenMode(arg: Any?) {
    val notificationName = OoyalaNotification.getNameOrUnknown(arg)
    if (notificationName == OoyalaSkinLayoutController.FULLSCREEN_CHANGED_NOTIFICATION_NAME) {
      if ((arg as OoyalaNotification).data == java.lang.Boolean.TRUE) {
        (getActivity() as AppCompatActivity).supportActionBar!!.hide()
      } else {
        (getActivity() as AppCompatActivity).supportActionBar!!.show()
      }
    }
  }

  private fun initPlayer() {
    val tvRatingConfiguration = FCCTVRatingConfiguration.Builder().setDurationSeconds(5).build()
    val options = Options.Builder()
            .setTVRatingConfiguration(tvRatingConfiguration)
            .setBypassPCodeMatching(true)
            .setUseExoPlayer(true)
            .setShowNativeLearnMoreButton(false)
            .setShowPromoImage(false)
            .build()

    player = OoyalaPlayer(pCode, PlayerDomain(domain), options)
    player?.addObserver(this)

    val skinOptions = SkinOptions.Builder().build()
    playerController = OoyalaSkinLayoutController(activity.application, playerSkinLayout, player, skinOptions)
    playerController.addObserver(this)

    initAdManager()

    player?.embedCode = embedCode
  }
}
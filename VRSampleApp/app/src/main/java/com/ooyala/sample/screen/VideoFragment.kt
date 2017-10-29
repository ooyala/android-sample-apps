package com.ooyala.sample

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.facebook.react.common.ApplicationHolder.getApplication
import com.ooyala.android.OoyalaNotification
import com.ooyala.android.OoyalaPlayer
import com.ooyala.android.PlayerDomain
import com.ooyala.android.configuration.FCCTVRatingConfiguration
import com.ooyala.android.configuration.Options
import com.ooyala.android.imasdk.OoyalaIMAManager
import com.ooyala.android.skin.OoyalaSkinLayoutController
import com.ooyala.android.skin.configuration.SkinOptions
import com.ooyala.android.util.SDCardLogcatOoyalaEventsLogger
import com.ooyala.sample.R
import com.ooyala.sample.utils.VideoData
import kotlinx.android.synthetic.main.video_fragment.*
import java.util.*


class VideoFragment() : Fragment(), Observer {

  companion object {
    val TAG = VideoFragment::class.java.canonicalName
    const val PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1
  }

  private val logger = SDCardLogcatOoyalaEventsLogger()
  private var writeStoragePermissionGranted = false

  private lateinit var embedCode: String
  private lateinit var pCode: String
  private lateinit var domain: String
  private var hasIma: Boolean = false
  private var player: OoyalaPlayer? = null

  constructor(data: VideoData) : this() {
    this.embedCode = data.embedCode!!
    this.domain = data.domain!!
    this.pCode = data.pCode!!
    this.hasIma = data.hasIma!!
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
          inflater?.inflate(R.layout.video_fragment, container, false)

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    if (ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(activity, arrayOf(WRITE_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE)
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
      val text = "Notification Received: $arg - state:  + ${player?.state}"
      Log.d(TAG, text)
      if (writeStoragePermissionGranted) {
        Log.d(TAG, "Writing log to SD card")
        logger.writeToSdcardLog(text)
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
    val playerController = OoyalaSkinLayoutController(activity.application, playerSkinLayout, player, skinOptions)
    playerController.addObserver(this)

    if (hasIma) {
      @SuppressWarnings("unused")
      val imaManager = OoyalaIMAManager(player)
    }
    player?.embedCode = embedCode
  }
}
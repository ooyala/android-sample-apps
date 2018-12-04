package com.ooyala.sample

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler
import com.ooyala.android.OoyalaNotification
import com.ooyala.android.OoyalaPlayer
import com.ooyala.android.PlayerDomain
import com.ooyala.android.configuration.FCCTVRatingConfiguration
import com.ooyala.android.configuration.Options
import com.ooyala.android.skin.OoyalaSkinLayoutController
import com.ooyala.android.skin.configuration.SkinOptions
import com.ooyala.android.util.SDCardLogcatOoyalaEventsLogger
import com.ooyala.android.vrsdk.player.VRPlayerFactory
import com.ooyala.sample.utils.VideoData
import kotlinx.android.synthetic.main.video_fragment.*
import java.util.*


open class VideoFragment() : Fragment(), Observer, DefaultHardwareBackBtnHandler {

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
  private var playerController: OoyalaSkinLayoutController? = null

  fun setArguments(data: VideoData) {
    val args = Bundle()
    args.putString("embedCode", data.embedCode)
    args.putString("pCode", data.pCode)
    args.putString("domain", data.domain)
    this.arguments = args
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val inflated = inflater?.inflate(R.layout.video_fragment, container, false)
    val arguments = getArguments()
    if (arguments != null) {
      this.embedCode = arguments.getString("embedCode")
      this.pCode = arguments.getString("pCode")
      this.domain = arguments.getString("domain")
    }
    return inflated
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    if (context?.let { ContextCompat.checkSelfPermission(it, WRITE_EXTERNAL_STORAGE) } != PERMISSION_GRANTED) {
      requestPermissions(arrayOf(WRITE_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE)
    } else {
      writeStoragePermissionGranted = true
      initPlayer()
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    playerController?.onDestroy()
  }


  override fun onResume() {
    super.onResume()
    player?.resume()
    playerController?.onResume(activity, this)
  }

  override fun onPause() {
    super.onPause()
    player?.suspend()
    playerController?.onPause()
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    if (requestCode == PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
      if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
        writeStoragePermissionGranted = true
      }
      initPlayer()
    }
  }

  override fun onConfigurationChanged(newConfig: Configuration?) {
    super.onConfigurationChanged(newConfig)
    player?.configurationChanged(newConfig)
  }

  override fun update(o: Observable?, arg: Any?) {
    val notification = OoyalaNotification.getNameOrUnknown(arg)
    if (notification != OoyalaPlayer.TIME_CHANGED_NOTIFICATION_NAME) {
      val text = "Notification Received: $notification - state: ${player?.state}"
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
    player?.registerFactory(VRPlayerFactory())
    player?.addObserver(this)

    val skinOptions = SkinOptions.Builder().build()
    playerController = OoyalaSkinLayoutController(activity!!.application, playerSkinLayout, player, skinOptions)
    playerController?.addObserver(this)

    player?.embedCode = embedCode

    initAdManager()
  }

  override fun invokeDefaultOnBackPressed() {
    activity!!.onBackPressed()
  }
}
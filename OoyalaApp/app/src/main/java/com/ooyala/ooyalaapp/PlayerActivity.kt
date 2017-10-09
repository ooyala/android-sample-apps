package com.ooyala.ooyalaapp

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.ooyala.ExoStreamPlayer

import kotlinx.android.synthetic.main.activity_player.*

class PlayerActivity : AppCompatActivity() {

    var ooyalaExoPlayer : ExoStreamPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val uri = Uri.parse("https://player.ooyala.com/player/iphone/Y1ZHB1ZDqfhCPjYYRbCEOz0GR8IsVRm1.m3u8")

        ooyalaExoPlayer = ExoStreamPlayer(this)
        ooyalaExoPlayer?.prepare(uri)
        exoPlayerView.player = ooyalaExoPlayer?.player
        ooyalaExoPlayer?.play()
    }
}

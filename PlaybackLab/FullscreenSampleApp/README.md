# RecyclerView Sample App

This app shows how we currently support the OoyalaPlayer in a RecyclerView. The name of the app is misleading since it is called FullscreenSampleApp, but it only shows how to add OoyalaPlayers as items in a RecyclerView.

We'll change the name in the future.

## Requirements

1. Android Studio 2.3.2 or above
1. Android SDK installed

## Description

The main idea is to use the only one instance of Ooyala player (SinglePlayerActivity) or several instances of Ooyala player (MultiplePlayerActivity) and reuse it when onBind is called in Adapter.
OoyalaSkinLayout is initialized only once, the parent Frame layout adds OoyalaSkinLayout once the next item of RecyclerView is seen and removes it when scrolling reveals another video.
In the sample app, multiple players should be embedded and perform the following:

- As the user scrolls, the video autoplays when the player is fully in view.
- Once scrolling continues, that player should be automatically paused.
- Once scrolling reveals another player and scrolling stops, playback should be automatically started.
- If the user pauses the video, the video will be paused.
- Once scrolling reveals the player with the video that was paused by the user, the video won't play automatically.
- The playhead time is saved for each video and each video starts from the time it was paused.
- If the user unmutes any video player, all future videos should start up unmuted.
Embedded container should use most of the screen when viewed vertically, except for a header taking up some space title "Video Feed Sample App". Video embeds are separated by the text of the video title centered inline on the video list.
- If the user moves the device to view horizontally while a video is being played, it automatically goes full-screen on the video.
- If the user returns to portrait mode, the video becomes viewed as embedded in video list again.
- If a video is not playing while viewed horizontally, then the list is expanded to full width.

## Activities
- MainActivity
- SinglePlayerActivity demonstrates how to use RecyclerView with the only one instance of OoyalaPlayer
- MultiplePlayerActivity demonstrates how to use RecyclerView with several reusable instances of OoyalaPlayer

## Recommendation
In skin.json the following fields have to be set as shown below:

<pre><code>
"endScreen": {
    "screenToShowOnEnd": "default",
    "showReplayButton": true,
    "replayIconStyle": {
      "color": "white",
      "opacity": 1
    },
    "showTitle": false,
    "showDescription": false,
    "infoPanelPosition": "topLeft"
  }
</code></pre>

<pre><code>
  "upNext": {
    "showUpNext": false,
    "timeToShow": 10
  }
</code></pre>
Especially, "screenToShowOnEnd" must be set as "default" and "showUpNext" -  false.
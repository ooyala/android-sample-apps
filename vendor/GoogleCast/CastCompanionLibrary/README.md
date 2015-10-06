# CastCompanionLibrary-android

CastCompanionLibrary-android is a library project to enable developers integrate Cast capabilities into their applications faster and easier.

## Dependencies
* google-play-services_lib library from the Android SDK (at least version 7.8+)
* android-support-v7-appcompat (version 22 or above)
* android-support-v7-mediarouter (version 22 or above)

## Setup Instructions
* Set up the project dependencies

## Documentation
See the "CastCompanionLibrary.pdf" inside the project for a more extensive documentation.

## References and How to report bugs
* [Cast Developer Documentation](http://developers.google.com/cast/)
* [Design Checklist](http://developers.google.com/cast/docs/design_checklist)
* If you find any issues with this library, please open a bug here on GitHub
* Question are answered on [StackOverflow](http://stackoverflow.com/questions/tagged/google-cast)

## How to make contributions?
Please read and follow the steps in the CONTRIBUTING.md

## License
See LICENSE

## Google+
Google Cast Developers Community on Google+ [http://goo.gl/TPLDxj](http://goo.gl/TPLDxj)

## Change List

2.5.1
 * Fixed an issue where not setting the LaunchOptions would have resulted in receiver not loading. Now the
   default behavior is to launch the app with the default value of relaunchIfRunning set to false.

2.5
 * MiniController component now has an attribute "auto_setup" that if set to "true", it instructs the
   framework to fully configure the component, so that clients would only need to add the MiniController
   to their layout and the rest will be handled by the library (i.e. if that attribute is set to true,
   there is no need to register or unregister that component with the cast manger anymore). The default
   value is "false" which falls back to the old behavior.
 * You can now set the LaunchOptions soon after initializing the Cast Manager by calling VideoCastManager.setLaunchOptions()
   (same with DataCastManager).
 * A new callback (onDisconnectionReason(int reason)) has been added that can inform the registered listeners
   of the reason a disconnect has happened. Understanding the reason behind a disconnect is somewhat non-trivial
   so this will hopefully make that task easier; see the JavaDoc for more details.
 * Now you can have the library automatically try to reconnect by enabling the FEATURE_AUTO_RECONNECT after
   initializing the Cast Manager; this means clients don't need to call reconnectSessionIfPossible() if that
   feature is enabled.
 * Updated the documentation.
 * Some cleanup, fixing some JavaDocs and comments, etc.

2.4
 * Fixed a number of bugs (#205, #204, #203)
 * Prepared the library for Marshmallow permissions related to the Play Services
 * Some code cleanup

2.3.2
 * Updated the icon for "queue list" in the library.

2.3.1
 * Updated gradle build to use the latest build tool and plugin version
 * Fixed #198. This is a fix for a memory leak in the VideoCastControllerActivity so it is strongly recommended to apply this update.

2.3

 * Moved to use MediaSessionCompat and removed all references to RemoteControlClient (RCC) across the library.
 In addition, started to use the MediaStyle added to the NotificationCompat in the v7 app compat support.
 library.
 * Updated Play Services version to use 7.8+
 * Persisting the policy on showing the next/prev for the full screen controller so that it is always honored.
 * Fixed a few issue around notification visibility when app is in background.
 * These issues have been addressed: #196, #194, #178

2.2

 * Removed a duplicate callback (onRemoteMediaPlayerQueueStatusUpdated()) as it was a duplicate of
 onMediaQueueUpdated(). If your code is currently using onRemoteMediaPlayerQueueStatusUpdated(), please replace that
 with onMediaQueueUpdated() which has an identical signature.
 * Fixed issues #185, #189 and #190. For #189, a new set of resource aliases are introduced which should make
 replacing those resources with your own simpler.

2.1.1

 * Now the MediaRouter support library added back the support for the volume on the cast dialog, so CCL is hiding that again.
 * Some typo fixes.

2.1

 * Added Queue related APIs for handling autoplay and queue
 * Added "stop" button to notification and lockscreen for live streams in Lollipop and above
 * Expanded callbacks in VideoCastConsumer interface to provide feedback on success of queue related API calls
 * Extended the full-screen VideoCastControllerActivity to include next/previous for navigation through queues.
  The visibility of these new buttons can be set through VideoCastManager.setNextPreviousVisibilityPolicy(policy)
 * The MiniController now has a modified UI with an additional item for showing an upcoming media item from the queue.
 * Addressed some issues

2.0.2

 * Addressing issues #171, #174
 * DataCastConsumer.onApplicationConnectionFailed() now returns void

2.0.1

 * Improving the management of MediaRouteButton
 * Working around a bug in Play Services, see issue #170
 * Fixing a typo

2.0
#### Notice: this release introduces a restructured and updated code base that has backward-incompatible changes. Most of these changes can be handled by a simple search-and-replace action but there are some changes to the API names and signatures that may need more manual handling. Below you can find a list of changes.

 * Change in the package name: CCL now has a new package name "com.google.android.libraries.cast.companionlibrary.cast"
 * All string, dimension and color resources now have "ccl_" as prefix. This allows developers to
 work with these resources without any collision with their own apps or other libraries. In addition, some
 unused resources have been removed from the "res/*" directories.
 * CCL no longer needs a reference to your "Activity" context. Instead, only an Application Context
 is adequate when you initialize it. Any API that may need an Activity Context (for example opening the
 VideoCastControllerActivity) will ask for such context as an argument. As a result, it is recommended
 to initialize the library in your Application's onCreate() and access the VideoCastManager singleton
 instance by VideoCastManager.getInstance(). Same applies to DataCastManager.
 * Most interface names have changed:
    * IMediaAuthListener -> MediaAuthListener
    * IMediaAuthService -> MediaAuthService
    * IBaseCastConsumer -> BaseCastConsumer
    * IDataCastConsumer -> DataCastConsumer
    * IVideoCastConsumer -> VideoCastConsumer
 * Some methods have been renamed:
    * IVideoVideoCastContoller#setLine1() -> VideoCastController#setTitle()
    * IVideoVideoCastContoller#setLine2() -> VideoCastController#setSubTitle()
    * IVideoVideoCastContoller#updateClosedCaption() -> VideoCastController#setClosedCaptionState()
    * VideoCastManager#getRemoteMovieUrl() -> getRemoteMediaUrl()
    * VideoCastManager#isRemoteMoviePlaying() -> isRemoteMediaPlaying()
    * VideoCastManager#isRemoteMoviePaused() -> isRemoteMediaPaused()
    * VideoCastManager#startCastControllerActivity() -> startVideoCastControllerActivity()
    * BaseCastManager#incremenetDeviceVolume() -> adjustDeviceVolume()
    * TracksPreferenceManager#setupPreferences() -> setUpPreferences()
    * VideoCastConsumer#onRemovedNamespace() -> onNamespaceRemoved()
    * MediaAuthService#start() -> startAuthorization()
    * MediaAuthService#setOnResult() -> setMediaAuthListener()
    * MediaAuthService#abort() -> abortAuthorization()
    * MediaAuthStatus#RESULT_AUTHORIZED -> AUTHORIZED
    * MediaAuthStatus#RESULT_NOT_AUTHORIZED -> NOT_AUTHORIZED
    * MediaAuthStatus#ABORT_TIMEOUT -> TIMED_OUT
    * MediaAuthStatus#ABORT_USER_CANCELLED -> CANCELED_BY_USER
    * VideoCastController#updateClosedCaption() -> setClosedCaptionStatus()
    * Utils#fromMediaInfo() -> mediaInfoToBundle()
    * Utils#toMediaInfo() -> bundleToMediaInfo()
    * Utils#scaleCenterCrop -> scaleAndCenterCropBitmap()
    * IMiniController.setSubTitle() -> setSubtitle()
    * MediaAuthListener#onResult() -> onAuthResult()
    * MediaAuthListener#onFailure() -> onAuthFailure()
    * BaseCastManager.clearContext() has been removed (see earlier comments)
 * All the "consumer" callbacks used to be wrapped inside a try-catch block inside the library. We have
 now removed this and expect the "consumers" to handle that in the client code; the previous approach was masking
 client issues in the library while they needed to be addressed inside the client itself.
 * BaseCastManager#addMediaRouterButton(MediaRouteButton button) now has no return value (it was redundant)
 * VideoCastConsumer#onApplicationConnectionFailed() no longer returns any value.
 * BaseCastConsumer#onConnectionFailed(() no longer returns any value.
 * [New] There is a new callback "void onMediaLoadResult(int statusCode)" in VideoCastConsumer to
 inform the consumers when a load operation is finished.
 * Updated the build to use the latest gradle binaries.
 * Updated to use the latest versions of Play Services and support libraries.


# CastCompanionLibrary-android

CastCompanionLibrary-android is a library project to enable developers integrate Cast capabilities into their applications faster and easier.

## Dependencies
* google-play-services_lib library from the Android SDK (at least version 6.1)
* android-support-v7-appcompat (version 21 or above)
* android-support-v7-mediarouter (version 20 or above)

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
1.14

 * Wrapped some IllegalStateExceptions for proper handling. This should address issue #144 as well.
 * Fixing an issue that sometimes when a media finishes, start of the next media would result in
 immediate closure of the VideoCastControllerActivity
 * Fixed a corner case where sometimes when the receiver is stopped due to reaching a time out, the
  sender was disconnecting but the cast icon was not reflecting that correctly.

1.13

 * Addressed a NPE in reconnection task (issue #143)
 * Made sure we don't set the duration of a MediaInfo object to a negative number
 * Some internal cleanup

1.12
#### Notice: there are some backward-incompatible changes in this release, please read the change list carefully
 * Changing CCL to use Application Context in most everything. As a result, one does not need to set context for CCL in each
       Activity which should avoid any context leaks and prevent some random NPEs. Consequently, there will be no "dialog" supported by
       CCL unless the caller method provides an explicit context. For various versions of reconnectSessionIfPossible, we now have
       reconnection callbacks that can be used by callers to provide visual feedback, so these methods now have new signatures.
       In addition, now you can initialize Cast Managers in your Application's onCreate().
       There are some backward-incompatible changes so clients need to make adjustments if needed.
       In particular, the noteworthy breaking changes are:
     * Variations of reconnectSessionIfPossible have new arguments
     * Utils.showErrorDialog() has been removed
     * setContext() on Cast Managers is removed
     * CastManagers' getInstance(Context) are removed
 * Updating BaseCastManager, VideoCastManager and DataCastManager to use CopyOnWriteArraySet to manage consumers. Since the number of
 consumers is low, there will be no performance hit and CopyOnWriteArraySet provides a safer data structure.
 * Decoupled dependency between components interested in receiving notifications when the list of active tracks
   changes and the sources that can cause such changes. This also allowed improving the TracksChooserDialog
   to gracefully rebuild itself when needed.
 * Improving VideoCastControllerFragment's behavior when it needs to be closed.
 * Fixing an issue that upon reconstruction of VideoCastControllerActivity/Fragment, it would restart the media.
 * In VideoCastControllerFragment, moved most of the initial work to onActivityCreated() so that a rebuild of the associated activity and fragment doesn't run into a NPE
 * Adding a new API to allow clients set the MediaAuthService directly (VideoCastManager.setMediaAuthService(IMediaAuthService authService))
 * Making sure that the reconnection AsyncTask is using a thread pool regardless of the Android version,
 * Removed "Video Tracks" from Tracks Chooser Dialog since that doesn't make sense to be presented there.
 * Updating the style of Cast dialog for Lollipop to match the respective framework.
 * Adding Toolbar component to the simple cast_activity.xml layout in landscape, this was missed in a previous update where toolbar was introduced.
 * Changing the icon representing "Disconnect" in Lollipop notification
 * Fixing a bug where notification service wouldn't restart if playback was restarted on a different device.
 * Fixing an issue that when a new media was started while another one was casting, the metadata wouldn't update immediately
   in VideoCastControllerActivity to reflect the new media.
 * Updating to the latest version of Gradle suitable for the latest publicly released version of Android Studio,
 * Updating the gradle build to use the latest version of Google Play Services and selectively use cast APIs from Google Play Services.
 * Improving discovery for MediaRouteButton by resetting the count of discovered routes whenever cast discovery is stopped.
 * Updating the PDF documentation
 * Fixing some typos
 * Addressing issues #89, #130, #132, #134, #137, #138, #139, #140,

1.11
 * Added support for Notifications and Lock Screen controls (via Notifications) on Android Lollipop.
 * Updated dependencies to use Google Play Services 6.1 and Support Libraries v21.
 * Added Toolbar as a replacement for ActionBar.
 * Added support for Android Studio 0.9.1 and updated gradle support to the latest versions.
 * Fixed issues 74, 110, 115, 119.

1.10
 * Added support for Tracks and Closed Captions. See the documentation for details.
 * Refactored image loading across the library.
 * Fixed issue 105
 
1.9
 * Added the complete reconnection logic per Cast UX Checklist
 * Addressed issues 70, 75, 92, 93, 94, 96
 * Added logic to handle failure when loading a media item in the VideoCastControllerActivity
 * Added "stream duration" to MediaInfo serialization
 * Updated the gradle build file to use newer version of the Google Play services
 * Updated the documentation
 
1.5
 * Fixed the issue where VideoCastNotificationService was not setting up data namespace if one was configured
 * Fixed issue 50
 * Added aversion number that will be printed in the log statements for tracking purposes
 * Correcting the typo in the name of method checkGooglePlaySevices() by introducing a new method and deprecating the old one (issue 48)
 * Fixing many typos in comments and some resources
 * Updating documentation to reflect the correct name of callbacks for the custom namespace for VideoCastManager

1.4
 * Added support for MediaRouteButton
 * Added "alias" resources for Mini Controller play/pause/stop buttons so clients can customize them easily
 * Added a color resource to control thw color of the title of the custom VideoMediaRouteControllerDialog
 * Fixed some typos in JavaDoc

1.3
 * Fixing issue 32
 * Fixing issue 33
 * Adding a better BaseCastManager.clearContext() variation
 * Implementing enhancement 30
 * Making sure play/pause button is hidden when ProgressBar is shown in VideoMediaRouteControllerDialog
 * probably some more adjustments and bug fixes

1.2
 * Improving thread-safety in calling various ConsumerImpl callbacks
 * (backward incompatible) Changing the signature of IMediaAuthListener.onResult
 * Adding an API to BaseCastManager so clients can clear the "context" to avoid any leaks
 * Various bug fixes

1.1
 * Added gradle build scripts (make sure you have Android Support Repository)
 * For live media, the "pause" button at various places is replaced with a "stop" button
 * Refactored the VideoCastControllerActivity to enable configuration changes without losing any running process
 * Added new capabilities for clients to hook in an authorization process prior to casting a video
 * A number of bug fixes, style fixes, etc
 * Updated documentation

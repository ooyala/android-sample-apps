# Ooyala Player v4 Pulse SDK 2.x integration for Android

This project demonstrates a simple video player that displays content using the Ooyala Player while displaying
ads from Ooyala Pulse.

This project is a sample intended **only** to give a brief introduction to the SDK and help developers get started with their Android application.

This is absolutely **not** intended to be used in production or to outline best practices, but rather a simplified way of developing your application.


## Building

1. After cloning the project, download the Ooyala Video Advertising Android SDKs [here](http://help.ooyala.com/downloads).
2. Copy the [required](app/libs/README.md) libraries into the `app/libs` folder of the project.
3. Ensure that the required [OoyalaSkinSDK-Android](../vendor/Ooyala/OoyalaSkinSDK-Android/) package is available in the [Ooyala](../vendor/Ooyala) folder.
4. Open the [project file](app/build.gradle) in Android Studio.
5. Build and run the project.


## Project structure

A [PulseListActivity](app/src/main/java/com/ooyala/sample/lists/PulseListActivity.java) shows a list of available videos, along with some metadata. When a video is selected, it is opened in a [PulsePlayerActivity](app/src/main/java/com/ooyala/sample/players/PulsePlayerActivity.java).

The PulsePlayerActivity creates an OoyalaPlayer and then associates it with an instance of the OoyalaPulseManager class from the OoyalaPulseIntegration library. OoyalaPulseManager enables Pulse ads to be shown for content that is configured with a Videoplaza ad set in Backlot.

```java
OoyalaPulseManager pulseManager = new OoyalaPulseManager(player);
pulseManager.setListener(new OoyalaPulseManager.Listener() {
  /*
    Called by the plugin to let us create the Pulse session; the metadata retrieved from Backlot is provided here
  */
});
```

When the OoyalaPulseManager needs an ad session it requests one from its listener (PulsePlayerActivity in the sample app). The listener is passed request settings and content metadata that are populated from Backlot, but has the opportunity to change them.

```java
@Override
  public PulseSession createPulseSession(OoyalaPulseManager ooyalaPulseManager, Video video, String pulseHost, ContentMetadata contentMetadata, RequestSettings requestSettings) {
    // Replace some of the Backlot metadata with our own local data
    List<Float> midrollPositions = new ArrayList<>();
    for(float f : videoItem.getMidrollPositions()) {
      midrollPositions.add(f);
    }
    requestSettings.setLinearPlaybackPositions(midrollPositions);
    contentMetadata.setTags(Arrays.asList(videoItem.getTags()));
    contentMetadata.setIdentifier(videoItem.getContentId());
    contentMetadata.setCategory(videoItem.getCategory());

    Pulse.setPulseHost(pulseHost, null, null);
    return Pulse.createSession(contentMetadata, requestSettings);
  }
```

## Demo Pulse account

This integration sample uses the following Pulse account:
```
https://pulse-demo.videoplaza.tv
```

This account is configured with a set of ad campaigns to help you test your Ooyala Pulse integration. Refer to the [content library](app/src/main/res/raw/library.json) used in this sample for useful tags and categories.


## Useful information

- [The Ooyala Pulse SDK documentation](http://pulse-sdks.ooyala.com/android_2/latest/)
- [Ooyala Ad Products SDK Parameter Reference](http://help.ooyala.com/video-advertising/oadtech/ad_serving/dg/integration_sdk_parameter.html)
- [Ooyala Player v4 Pulse Integration documentation](http://apidocs.ooyala.com/android_mobilesdk/namespacecom_1_1ooyala_1_1android_1_1pulseintegration.html)

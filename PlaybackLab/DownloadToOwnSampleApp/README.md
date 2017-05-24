# DTO Sample App

The Download to Own (DTO) sample app shows how you can download a video so you can play without an Internet connection.
It is just a sample app, as a user you should not use the same design and architecture for a production app.

[Click here](http://help.ooyala.com/video-platform/concepts/mobile_sdk_android_offline_playback_download_to_own_dto.html) to know more about DTO official documentation, including requirements.

## Caveats
* The app cannot download assets in the background. You can only download the asset while the `OfflineDownloadActivity` is active.
* If you start downloading an asset and go to another screen while the download is active, you may encounter issues when trying to download again.

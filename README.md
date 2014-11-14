Ooyala SDK for Android Sample Apps
==================================

This is a repository of sample applications for the Ooyala SDK for Android.

## Provided Sample Apps

Below is a short description of the sample applications provided in the repository.

### AdvancedPlaybackSampleApp

This application was designed to illustrate some advanced functionality you can add into a Player Activity through the OoyalaSDK.


### FreewheelSampleApp

This application was designed to illustrate how correctly configured Freewheel assets play back using the Ooyala SDK.

### IMASampleApp

This application was designed to illustrate how correctly configured Google IMA assets play back using the Ooyala SDK.

## Using the CompleteSampleApp

The CompleteSampleApp is compilable on download.  You will have to perform a `make` to generate the code for the CompleteSampleApp to run.

## Make commands

The different commands available for you

#### `make clean`

1. remove already existing libraries from all sample apps
2. remove all code in the CompleteSampleApp

#### `make`, `make install`

`make` will:

1. `make clean`
2. copy all of the necessary libraries into their respective apps
3. copy all sample app code into the CompleteSampleApp


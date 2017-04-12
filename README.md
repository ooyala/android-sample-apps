Ooyala SDK for Android Sample Apps
==================================

# Introduction

This is a repository of sample applications for the Ooyala SDK for Android. Here you can try a bunch of different examples of Ooyala Mobile SDK usage, and see the code required to perform these tasks.  

In order to be successful using these applications, you should have the following experience:

1. Experience with Android Studio, Java, and Android development.
2. Understanding of the use case of Ooyala and Ooyala's Mobile SDKs.

This repository is meant to be supplementary to our Developer documentation.  Take a look at the docs here: http://help.ooyala.com/

## Requirements

Some apps include special instructions, look for a README in the app you want to try out to check if it requires something else.

Apart from that, here is what you will need:
* Android Studio v2.3.1 or above.
  * If you use a different IDE or editor we won't be able to help you with questions specific to that environment.
* Android SDK with Android platform version 25 (Android 7.1.1). We use it to compile the app.


# Getting Started

All applications in this repository should be automatically importable, compilable, and runnable.  A good place to start is to try the Basic Playback Sample App.

1. Clone this repository onto your computer: `git clone https://github.com/ooyala/android-sample-apps.git`
1. Open Android Studio
1. Either press "Open an Existing Android Studio Project" or click File > Open...
1. Navigate to android-sample-apps/BasicPlaybackSampleApp. Press Choose, or OK
1. Wait for the Gradle execution to complete. You should see "app" appear as one of the build configurations
1. Run the application (Run > Run 'App'), choose either a connected device or a simulator to run on
1. When the application loads, you will have a list of videos to choose from.  Pick any of them to view video playback

# Complete Sample App

The Complete Sample App is a project that combines all of the sample apps in the repository into a single runnable application.  This application should also be importable, compilable, and runnable out of the box just like all of the other applications.  This is the fastest way to demo all of the functionality we have added into the repository so far


# Using the Sample App Repository for filing support issues

If you have a bug within your own application, the Sample App Repository is a great way to help isolate the issue to Ooyala code. we recommend the following steps.

1. Isolate the bug to the Ooyala Sample App repo.
    1. Clone the repository onto your computer
    1. Modify one of the sample apps as necessary to simulate your application's behavior.
1. If you were able to successfully isolate the issue to our sample app, provide us the repo with your changes
    1. Fork this repository into your own Github account.
    1. Make modifications to the code and push these changes to your fork.
    1. Provide the link to your fork when you create a ticket to Support

This is the absolute fastest way for Support and Engineering to reproduce without question, and solve your issues as fast as possible.

When reproducing in sample apps, you should *Never* commit your API Secret into any repository.  If you have done so accidentally, you should either force-remove that commit from your history, or contact Technical Support to reset your API Secret.

# Reporting bugs with the Sample App Repo

If you find issues with one of the examples, or find issues with video playback.  Please file a bug with Ooyala Support through the Ooyala Support Portal http://support.ooyala.com/.

If you find bugs around the sample app that are not about video playback (i.e. unable to compile or build), you can file an issue through Github. If you file a Github Issue, we reserve the right to redirect your issue to Ooyala Support.

# Notes When Starting your own Application

Be sure to use your own Provider Code in your Ooyala Player initialization.  If you fail to do so, your viewing analytics will be lost in the process.

# Caveats

Not all of the Ooyala SDK's functionality is represented in this repository; We are constantly adding and updating, with the intention of demonstrating as many of our features as possible.  If you would like to see something added, speak to your Ooyala contact or Technical Support

Some of the more complicated samples may not be playable out of the box. These samples usually require customer-specific information that cannot be simulated with a demo application

You should *Never* commit your API Secret into any repository.  If you have done so accidentally, you should either force-remove that commit from your history, or contact Technical Support to reset your API Secret

Our Sample App Repository is designed to be automatically updated as we release new versions.  Our repository uses a 'candidate' branch, which will be updated for every release candidate we create.  These candidates, and the git tags ending in 'RC#' are for testing, and not intended to be used for customer applications.  

We do not recommend testing on any branch that is not master. These branches are not verified to be working as expected.  

Thank you for reading!

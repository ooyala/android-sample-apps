# Overview

Ooyala is testing out Maven dependencies for the Android SDKs. Going forward, maven versions will not be removed, so if you are able to successfully use these maven imports, they should continue to work going forward.

If these work for you, let us know.  If they don't, file a bug and we will look into it.

# Usage

You need to do two things to use Maven dependencies:
### Add repositories to your module's build.gradle.
You can also add the following maven line to your project's allprojects{} definition

    repositories {
        maven {
            url 'https://dl.bintray.com/ooyala-pba/maven/'
        }
    }

### Add the necessary maven compilation statements.
These statements would look like:

    compile 'com.ooyala.android:core:v4.20.0_RC2'
    compile 'com.ooyala.android:adobeAnalytics:v4.20.0_RC2'
    compile 'com.ooyala.android:cast:v4.20.0_RC2'
    compile 'com.ooyala.android:nielsen:v4.20.0_RC2'
    compile 'com.ooyala.android:ima:v4.20.0_RC2'
    compile 'com.ooyala.android:freewheel:v4.20.0_RC2'
    compile 'com.ooyala.android:skin:v4.20.0_RC2'


# Usage with Skin SDK

When you want to use the Skin SDK, you will still need to have a copy of react-native-0.33.0.aar as part of your application.  You can add this into your libs folder, and using the following repositories definition:

    repositories {
        maven {
            url 'https://dl.bintray.com/ooyala-pba/maven/'
        }        
        flatDir {
            dirs 'libs'
        }
    }

# Caveats

The following are observations of the current state of Ooyala's Android maven functionality. You should not rely on any of the behavior mentioned in this section.

* There is unspecified behavior when a customer uses multiple maven imports with differing versions.
* There is unspecified behavior when a customer wants to use the skin maven import, while also using React-Native in their application.
* If you are using Skin dependency, you will still need to include react-native-33.aar as mentioned above.
* When you use any of the non-core maven imports, the core import is no longer necessary. We may require the core to be explicitly specified in future versions.
* Versions do not support auto-incrementing and wildcards.
* With IMA, Freewheel, and other imports may not require their respective SDK libraries, that means that there may be conflicts if your application depends on different versions of those libraries, there may be unexpected results.
* MavenSampleApp, like all other skin-enabled apps, copies its assets from the android-sample-apps/vendor/ folder.
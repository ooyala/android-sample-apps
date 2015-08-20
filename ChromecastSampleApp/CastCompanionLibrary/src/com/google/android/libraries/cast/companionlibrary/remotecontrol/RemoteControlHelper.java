/*
 * Copyright (C) 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.libraries.cast.companionlibrary.remotecontrol;

import android.annotation.TargetApi;
import android.media.AudioManager;
import android.media.RemoteControlClient;
import android.os.Build;

/**
 * Contains methods to handle registering/unregistering remote control clients. These methods only
 * run on ICS+ devices. On older platform versions, all methods are no-ops.
 */
public class RemoteControlHelper {

    private static boolean sHasRemoteControlAPIs =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;

    /**
     * Registers the instance of RemoteControlClient with the AudioManager.
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static void registerRemoteControlClient(AudioManager audioManager,
            RemoteControlClientCompat remoteControlClient) {
        if (!sHasRemoteControlAPIs) {
            return;
        }
        audioManager.registerRemoteControlClient(
                (RemoteControlClient) remoteControlClient.getActualRemoteControlClientObject());
    }

    /**
     * Unregisters the instance of RemoteControlClient from the AudioManager.
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static void unregisterRemoteControlClient(AudioManager audioManager,
            RemoteControlClientCompat remoteControlClient) {
        if (!sHasRemoteControlAPIs) {
            return;
        }
        audioManager.unregisterRemoteControlClient(
                (RemoteControlClient) remoteControlClient.getActualRemoteControlClientObject());
    }
}

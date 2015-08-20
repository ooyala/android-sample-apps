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

package com.google.android.libraries.cast.companionlibrary.cast;

import com.google.android.gms.cast.MediaQueueItem;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A simple class to model a queue for bookkeeping purposes.
 */
public class MediaQueue {

    private List<MediaQueueItem> mQueueItems = new CopyOnWriteArrayList<>();
    public static final int INVALID_POSITION = -1;
    private MediaQueueItem mCurrentItem;
    private boolean mShuffle;
    private int mRepeatMode;

    public MediaQueue() {}

    public MediaQueue(List<MediaQueueItem> queueItems,
            MediaQueueItem currentItem, boolean shuffle, int repeatMode) {
        mQueueItems = queueItems;
        mCurrentItem = currentItem;
        mShuffle = shuffle;
        mRepeatMode = repeatMode;
    }

    public final List<MediaQueueItem> getQueueItems() {
        return mQueueItems;
    }

    public final void setQueueItems(List<MediaQueueItem> queue) {
        mQueueItems = queue;
    }

    public final MediaQueueItem getCurrentItem() {
        return mCurrentItem;
    }

    public final void setCurrentItem(MediaQueueItem currentItem) {
        mCurrentItem = currentItem;
    }

    public final boolean isShuffle() {
        return mShuffle;
    }

    public final void setShuffle(boolean shuffle) {
        mShuffle = shuffle;
    }

    public final int getRepeatMode() {
        return mRepeatMode;
    }

    public final void setRepeatMode(int repeatMode) {
        mRepeatMode = repeatMode;
    }

    public final int getCount() {
        return mQueueItems == null || mQueueItems.isEmpty() ? 0 : mQueueItems.size();
    }

    public final boolean isEmpty() {
        return mQueueItems == null || mQueueItems.isEmpty();
    }

    public final int getCurrentItemPosition() {
        if (mQueueItems == null) {
            return INVALID_POSITION;
        }

        if (mQueueItems.isEmpty()) {
            return 0;
        }

        return mQueueItems.indexOf(mCurrentItem);
    }

    public static int getPositionInQueue(List<MediaQueueItem> queueList, MediaQueueItem item) {
        if (item == null) {
            return INVALID_POSITION;
        }
        if (queueList == null || queueList.isEmpty()) {
            return INVALID_POSITION;
        }


        for(MediaQueueItem queueItem : queueList) {
            if (queueItem.getItemId() == item.getItemId()) {

            }
        }
        return 0;
    }
}

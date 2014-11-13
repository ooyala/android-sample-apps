package com.ooyala.demo.social;

import android.os.Handler;

public class LogoutRequestListener extends BaseRequestListener {
    private Handler mHandler;

    public LogoutRequestListener(final Handler mHandler) {
        this.mHandler = mHandler;
    }

    public void onComplete(String response, final Object state) {
        // callback should be run in the original thread,
        // not the background thread
        mHandler.post(new Runnable() {
            public void run() {
                SessionEvents.onLogoutFinish();
            }
        });
    }
}

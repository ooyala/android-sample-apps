package com.ooyala.demo.social;

import android.os.Bundle;
import android.widget.BaseAdapter;

public final class LoginDialogListener implements DialogListener {
    private final BaseAdapter arrayAdapter;

    public LoginDialogListener(final BaseAdapter arrayAdapter) {
        this.arrayAdapter = arrayAdapter;
    }

    public void onComplete(Bundle values) {
        SessionEvents.onLoginSuccess();
        if (arrayAdapter != null) {
            arrayAdapter.notifyDataSetChanged();
        }
    }

    public void onFacebookError(FacebookError error) {
        SessionEvents.onLoginError(error.getMessage());
    }

    public void onError(DialogError error) {
        SessionEvents.onLoginError(error.getMessage());
    }

    public void onCancel() {
        SessionEvents.onLoginError("Action Canceled");
    }
}

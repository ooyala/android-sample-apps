package com.ooyala.demo.social;

import android.os.Bundle;

/**
 * Callback interface for dialog requests.
 */
public interface DialogListener {

    /**
     * Called when a dialog completes.
     * <p/>
     * Executed by the thread that initiated the dialog.
     *
     * @param values Key-value string pairs extracted from the response.
     */
    public void onComplete(Bundle values);

    /**
     * Called when a Facebook responds to a dialog with an error.
     * <p/>
     * Executed by the thread that initiated the dialog.
     */
    public void onFacebookError(FacebookError e);

    /**
     * Called when a dialog has an error.
     * <p/>
     * Executed by the thread that initiated the dialog.
     */
    public void onError(DialogError e);

    /**
     * Called when a dialog is canceled by the user.
     * <p/>
     * Executed by the thread that initiated the dialog.
     */
    public void onCancel();

}

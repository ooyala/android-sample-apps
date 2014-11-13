package com.ooyala.demo;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import com.ooyala.demo.task.AsyncTask;
import com.ooyala.demo.utils.TwitterUtils;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.media.ImageUpload;
import twitter4j.media.ImageUploadFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class TwitterDialog extends Dialog {

    private static final String TAG = "TwitterDialog";

    private final EditText editText;
    private final ProgressDialog mSpinner;
    private final String defaultMessage;
    private final String imageUrl;

    public TwitterDialog(final Context context, final String defaultMessage, final String imageUrl) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(true);

        this.imageUrl = imageUrl;
        this.defaultMessage = defaultMessage;

        setContentView(R.layout.alert_dialog_text_entry);
        editText = (EditText) findViewById(R.id.post_msg);
        editText.setText(defaultMessage);
        editText.setGravity(Gravity.TOP);
        editText.setSelection(0);

        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                dismiss();
            }
        });

        findViewById(R.id.post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                PostMessageTask postMessageTask = new PostMessageTask();
                //noinspection unchecked
                postMessageTask.execute();
            }
        });

        mSpinner = new ProgressDialog(context);
        mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mSpinner.setMessage(context.getString(R.string.message_loading));

    }


    class PostMessageTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            mSpinner.show();
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(final Void... params) {
            Twitter twitter = TwitterUtils.getTwitter(getContext());
            ImageUpload upload = new ImageUploadFactory(twitter.getConfiguration()).getInstance();

            try {
                String url = upload.upload(defaultMessage, new FileInputStream(UserData.imageDownloader.getFromCache(imageUrl)));
                twitter.updateStatus(editText.getText().toString() + " " + url);
            } catch (TwitterException e) {
                Log.e(TAG, e.getMessage(), e);
            } catch (FileNotFoundException e) {
                Log.e(TAG, e.getMessage(), e);
            }


            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mSpinner.dismiss();
        }

        @Override
        protected void onCancelled(final Void aVoid) {
            super.onCancelled(aVoid);
            mSpinner.dismiss();
        }

        @Override
        protected void onPostExecute(final Void aVoid) {
            super.onPostExecute(aVoid);
            mSpinner.dismiss();
            dismiss();

        }

    }
}

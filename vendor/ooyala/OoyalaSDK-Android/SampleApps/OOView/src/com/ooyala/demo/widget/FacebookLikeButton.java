package com.ooyala.demo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import com.ooyala.demo.social.Facebook;
import com.ooyala.demo.social.SessionEvents;
import com.ooyala.demo.social.SessionStore;
import com.ooyala.demo.utils.FontUtils;

public class FacebookLikeButton extends Button {
    private SessionListener mSessionListener = new SessionListener();
    private Facebook facebook;

    public FacebookLikeButton(Context context) {
        super(context);
        init(context);
    }

    public FacebookLikeButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FacebookLikeButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        setTypeface(FontUtils.getFont(context.getAssets()));
    }

    private void init(Facebook facebook) {
        this.facebook = facebook;
        SessionEvents.addAuthListener(mSessionListener);
        SessionEvents.addLogoutListener(mSessionListener);
    }

    private class SessionListener implements SessionEvents.AuthListener, SessionEvents.LogoutListener {

        public void onAuthSucceed() {
//            setImageResource(R.drawable.logout_button);
            SessionStore.save(facebook, getContext());
        }

        public void onAuthFail(String error) {
        }

        public void onLogoutBegin() {
        }

        public void onLogoutFinish() {
            SessionStore.clear(getContext());
        }
    }

}

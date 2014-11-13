package com.ooyala.demo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import com.ooyala.demo.Constants;
import com.ooyala.demo.social.Facebook;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.media.MediaProvider;

public class TwitterUtils {
    private static final String TAG = Facebook.class.getSimpleName();


    public static boolean isTokenExists(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.TWITTER_PREFERENCE, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(Constants.P_TOKEN, null);
        String secret = sharedPreferences.getString(Constants.P_SECRET, null);

        return !(TextUtils.isEmpty(token) || TextUtils.isEmpty(secret));
    }

    private static AccessToken getAccessToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.TWITTER_PREFERENCE, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(Constants.P_TOKEN, null);
        String secret = sharedPreferences.getString(Constants.P_SECRET, null);

        if (TextUtils.isEmpty(token) || TextUtils.isEmpty(secret)) {
            return null;
        }
        return new AccessToken(token, secret);

    }

    public static Twitter getTwitter(Context context) {
        try {
            final Configuration configuration = getConfiguration(context);
            return new TwitterFactory(configuration).getInstance();

        } catch (RuntimeException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (Throwable e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return null;
    }

    public static Configuration getConfiguration(final Context context) {
        AccessToken accessToken = getAccessToken(context);
        String token = null;
        String tokenSecret = null;
        if (accessToken != null) {
            token = accessToken.getToken();
            tokenSecret = accessToken.getTokenSecret();
        }
        return new ConfigurationBuilder()
                .setMediaProviderAPIKey(Constants.MEDIA_PROVIDER_API_KEY)
                .setMediaProvider(MediaProvider.TWITPIC.getName())
                .setOAuthConsumerKey(Constants.OAUTH_CONSUMER_KEY)
                .setOAuthConsumerSecret(Constants.OAUTH_CONSUMER_SECRET)
                .setOAuthAccessToken(token)
                .setOAuthAccessTokenSecret(tokenSecret)
                .build();
    }


}
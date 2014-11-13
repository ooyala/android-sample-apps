package com.ooyala.demo.utils;

import android.util.Log;
import com.ooyala.demo.social.Facebook;
import com.ooyala.demo.social.Util;
import com.ooyala.demo.vo.VideoInfoVO;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class FacebookUtils {
    private static final String TAG = Facebook.class.getSimpleName();
    private static final String SHARES = "shares";
    private static final String HTTP_GRAPH_FACEBOOK_COM_IDS = "http://graph.facebook.com/?ids=";


    public static void loadLikes(final List<VideoInfoVO> videoInfoVOs) {
        try {

            for (VideoInfoVO videoInfoVO : videoInfoVOs) {
                URL url = new URL(HTTP_GRAPH_FACEBOOK_COM_IDS + videoInfoVO.getThumbnail());
                URLConnection urlConnection = url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();

                JSONObject rootJson = Util.parseJson(IOUtils.toString(inputStream));

                JSONObject itemJson = (JSONObject) rootJson.get(videoInfoVO.getThumbnail());
                if (itemJson.has(SHARES)) {
                    videoInfoVO.setLikes(itemJson.getInt(SHARES));
                }
            }


        } catch (RuntimeException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (Throwable e) {
            Log.e(TAG, e.getMessage(), e);
        }

    }


}
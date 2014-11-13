package com.ooyala.demo.task;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ViewAnimator;

import com.ooyala.android.OoyalaAPIClient;
import com.ooyala.android.PlayerDomain;
import com.ooyala.demo.Constants;
import com.ooyala.demo.adapter.ChannelAdapter;
import com.ooyala.demo.vo.VideoInfoVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.ooyala.demo.Constants.DESCRIPTION;
import static com.ooyala.demo.Constants.DURATION;
import static com.ooyala.demo.Constants.EMBED_CODE;
import static com.ooyala.demo.Constants.NAME;
import static com.ooyala.demo.Constants.PREVIEW_IMAGE_URL;
import static com.ooyala.demo.Constants.mostPopularComparator;
import static com.ooyala.demo.Constants.mostRecentComparator;

public class ChanelLoadTask extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = ChanelLoadTask.class.getSimpleName();

    private final Activity activity;
    private final ChannelAdapter channelAdapter;
    private final List<VideoInfoVO> originalChannelVideos;
    private final ViewAnimator viewFlipper;

    public ChanelLoadTask(final Activity activity, final ChannelAdapter channelAdapter, final List<VideoInfoVO> originalChannelVideos, final ViewAnimator viewFlipper) {
        this.activity = activity;
        this.channelAdapter = channelAdapter;
        this.originalChannelVideos = originalChannelVideos;
        this.viewFlipper = viewFlipper;
    }

    @Override
    protected void onPostExecute(final Boolean result) {
        if (result) {
            channelAdapter.setObjects(originalChannelVideos, mostPopularComparator);
        }
        viewFlipper.showNext();

    }

    @Override
    protected Boolean doInBackground(final Void... voids) {
        final OoyalaAPIClient api = new OoyalaAPIClient(Constants.OOYALA_API_KEY, Constants.OOYALA_SECRET_KEY, Constants.OOYALA_P_CODE, new PlayerDomain(Constants.OOYALA_PLAYER_DOMAIN));

        final JSONArray videos;
        JSONObject jsonObject = null;
        int cnt = 0;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("limit", "300");
        JSONObject jsonAnalyticsReport = null;

        // while (jsonAnalyticsReport == null) {
        //     jsonAnalyticsReport = api.objectFromBacklotAPI(Constants.ANALYTICS_REPORTS, params);
        // 
        //     cnt++;
        //     if (cnt > 5 && jsonAnalyticsReport == null) {
        //         return false;
        //     }
        // }
        // 
        
        cnt = 0;
        while (jsonObject == null) {
            jsonObject = api.objectFromBacklotAPI(Constants.LABEL, new HashMap<String, String>());

            cnt++;
            if (cnt > 5 && jsonObject == null) {
                return false;
            }
        }
        try {
            videos = jsonObject.getJSONArray("items");
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            return false;
        }
        originalChannelVideos.clear();
        try {
            for (int pos = 0; pos < videos.length(); pos++) {
                VideoInfoVO videoInfoVO = new VideoInfoVO();
                videoInfo(videos, pos, 0, videoInfoVO, videos.getJSONObject(pos));
                originalChannelVideos.add(videoInfoVO);
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            return false;
        }

        try {
            JSONArray jsonMetricsArray = jsonAnalyticsReport.getJSONArray("results");
            for (int pos = 0; pos < jsonMetricsArray.length(); pos++) {
                JSONObject jsonMetrics = jsonMetricsArray.getJSONObject(pos);
                JSONObject jsonVideo = jsonMetrics.getJSONObject("metrics").getJSONObject("video");
                if (!jsonMetrics.has("movie_data") || !jsonVideo.has("plays")) {
                    continue;
                }
                JSONObject jsonMovieData = jsonMetrics.getJSONObject("movie_data");
                String embedCode = jsonMovieData.getString("embed_code");
                for (VideoInfoVO channelVideo : originalChannelVideos) {
                    if (channelVideo.getEmbedCode().equals(embedCode)) {
                        channelVideo.setPlays(jsonVideo.getInt("plays"));
                    }
                }
//                Log.d(TAG, jsonMovieData.toString());

            }
        } catch (Throwable e) {
            Log.e(TAG, e.getMessage(), e);
        }

        Collections.sort(originalChannelVideos, mostRecentComparator);


        return true;
    }

    private int videoInfo(final JSONArray videos, final int pos, int index, final VideoInfoVO videoInfoVO, final JSONObject jsonObject) {
        try {

            if (videos.length() > pos) {

                String durationTime = timeStringFromMillis(jsonObject.getInt(DURATION), true);
                String description = jsonObject.getString(DESCRIPTION);
                Date updated = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.US).parse(jsonObject.getString("updated_at"));
                String thumb = jsonObject.getString(PREVIEW_IMAGE_URL);
                videoInfoVO.setTitle(jsonObject.getString(NAME));
                videoInfoVO.setDescription((description == null ? "No Description" : description) + " (" + durationTime + ")");
                videoInfoVO.setUpdated(updated);
                videoInfoVO.setEmbedCode(jsonObject.getString(EMBED_CODE));
                videoInfoVO.setThumbnail(thumb);

            }
            index++;
            return index;
        } catch (RuntimeException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (Throwable e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return 0;
    }

    private String timeStringFromMillis(int millis, boolean includeHours) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis + (8 * 60 * 60 * 1000));
        SimpleDateFormat sdf = new SimpleDateFormat(includeHours ? "HH:mm:ss" : "mm:ss");
        return sdf.format(c.getTime());
    }


}

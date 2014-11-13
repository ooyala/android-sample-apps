package com.ooyala.demo.task;

import android.os.AsyncTask;
import com.ooyala.demo.UserData;
import com.ooyala.demo.utils.FacebookUtils;
import com.ooyala.demo.vo.VideoInfoVO;

import java.util.List;

public class FacebookLoadTask extends AsyncTask<Void, Void, Void> {
    private final List<VideoInfoVO> originalChannelVideos;

    public FacebookLoadTask(final List<VideoInfoVO> originalChannelVideos) {
        this.originalChannelVideos = originalChannelVideos;
    }

    @Override
    protected Void doInBackground(final Void... params) {
        FacebookUtils.loadLikes(originalChannelVideos);
        UserData.isFacebookLikesLoaded = true;
        return null;
    }
}

package com.ooyala.demo;

import com.ooyala.demo.social.Facebook;
import com.ooyala.demo.utils.ImageDownloader;
import com.ooyala.demo.vo.SortBy;
import com.ooyala.demo.vo.VideoInfoVO;

import java.util.ArrayList;
import java.util.List;

public class UserData {
    public static SortBy SORT_BY = SortBy.MostRecent;
    public static boolean isSort = false;
    public static boolean isFacebookLikesLoaded = false;

    /**
     * Facebook api
     */
    public static final Facebook FACEBOOK = new Facebook();

    /**
     * Image downloader helper
     */
    public static final ImageDownloader imageDownloader = new ImageDownloader();

    /**
     * List of videos
     */
    public static final List<VideoInfoVO> originalChannelVideos = new ArrayList<VideoInfoVO>();
}

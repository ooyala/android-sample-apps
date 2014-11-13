package com.ooyala.demo.utils;

import com.ooyala.demo.vo.VideoInfoVO;

import java.util.Comparator;

public final class MostFavoriteComparator implements Comparator<VideoInfoVO> {
    @Override
    public int compare(final VideoInfoVO v1, final VideoInfoVO v2) {
        return v2.getLikes() > v1.getLikes() ? 1 : (v2.getLikes() < v1.getLikes() ? -1 : 0);
    }
}

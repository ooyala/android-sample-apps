package com.ooyala.demo.utils;

import com.ooyala.demo.vo.VideoInfoVO;

import java.util.Comparator;

public final class MostPopularComparator implements Comparator<VideoInfoVO> {
    @Override
    public int compare(final VideoInfoVO v1, final VideoInfoVO v2) {
        return v2.getPlays() > v1.getPlays() ? 1 : (v2.getPlays() < v1.getPlays() ? -1 : 0);
    }
}

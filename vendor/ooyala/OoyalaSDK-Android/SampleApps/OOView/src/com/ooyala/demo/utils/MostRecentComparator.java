package com.ooyala.demo.utils;

import com.ooyala.demo.vo.VideoInfoVO;

import java.util.Comparator;

public final class MostRecentComparator implements Comparator<VideoInfoVO> {
    @Override
    public int compare(final VideoInfoVO v1, final VideoInfoVO v2) {
        return v2.getUpdated().compareTo(v1.getUpdated());
    }
}

package com.ooyala.sample;

import java.util.ArrayList;
import java.util.List;

final class Constants {
	private Constants() {

	}

	static final int PLAY_DELAY = 500;

	static List<Data> populateData() {
	    String pCode = "c0cTkxOqALQviQIGAHWY5hP0q9gU";
	    String domain = "http://ooyala.com";

        List<Data> dataList = new ArrayList<>();
        dataList.add(new Data("01YXgwYTrOLeUiHStOloFvUMIND47Bd4", pCode, domain));
        dataList.add(new Data("MwdHY0YTpNpYivE5SLxLc-wDBZUZCkqH", pCode, domain));
        dataList.add(new Data("5mZGtkYjqqz66Pl_mh3oZ8-PbNLKp1G7", pCode, domain));
        dataList.add(new Data("N3cGp0ZDrML1PxJUsRq2w2kMD777AwVF", pCode, domain));
        dataList.add(new Data("xjNGlqMzE68288rje9drMpgRrTNOxI1I", pCode, domain));
        dataList.add(new Data("N3cGp0ZDrML1PxJUsRq2w2kMD777AwVF", pCode, domain));
        dataList.add(new Data("5mZGtkYjqqz66Pl_mh3oZ8-PbNLKp1G7", pCode, domain));
        dataList.add(new Data("MwdHY0YTpNpYivE5SLxLc-wDBZUZCkqH", pCode, domain));
        dataList.add(new Data("01YXgwYTrOLeUiHStOloFvUMIND47Bd4", pCode, domain));
        return dataList;
    }
}

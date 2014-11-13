package com.ooyala.demo.utils;

import android.content.res.AssetManager;
import android.graphics.Typeface;

public class FontUtils {
    private static Typeface leagueGothic;

    public static Typeface getFont(final AssetManager assetManager) {
        if (leagueGothic == null) {
            leagueGothic = Typeface.createFromAsset(assetManager, "League_Gothic.ttf");
        }
        return leagueGothic;
    }

}

package com.ooyala.omnituresampleapp.utils;

import com.ooyala.omnituresampleapp.players.BasicPlayerActivity;

import java.util.LinkedHashMap;
import java.util.Map;

public class AssetDataSource {

    private AssetDataSource() { }

    public static Map<String, PlayerSelectionOption> getAssets() {
        Map<String, PlayerSelectionOption> map = new LinkedHashMap<>();
        map.put("MP4 video", new PlayerSelectionOption("h4aHB1ZDqV7hbmLEv4xSOx3FdUUuephx", "c0cTkxOqALQviQIGAHWY5hP0q9gU", BasicPlayerActivity.class));
        map.put("Ooyala Preroll", new PlayerSelectionOption("M4cmp0ZDpYdy8kiL4UD910Rw_DWwaSnU", "BidTQxOqebpNk1rVsjs2sUJSTOZc", BasicPlayerActivity.class));
        map.put("Ooyala Midroll", new PlayerSelectionOption("xhcmp0ZDpnDB2-hXvH7TsYVQKEk_89di", "BidTQxOqebpNk1rVsjs2sUJSTOZc", BasicPlayerActivity.class));
        map.put("Ooyala Postroll", new PlayerSelectionOption("Rjcmp0ZDr5yFbZPEfLZKUveR_2JzZjMO", "BidTQxOqebpNk1rVsjs2sUJSTOZc", BasicPlayerActivity.class));
        map.put("VAST Preroll", new PlayerSelectionOption("Zlcmp0ZDrpHlAFWFsOBsgEXFepeSXY4c", "BidTQxOqebpNk1rVsjs2sUJSTOZc", BasicPlayerActivity.class));
        map.put("VAST Midroll", new PlayerSelectionOption("pncmp0ZDp7OKlwTPJlMZzrI59j8Imefa", "BidTQxOqebpNk1rVsjs2sUJSTOZc", BasicPlayerActivity.class));
        map.put("VAST Postroll", new PlayerSelectionOption("Zpcmp0ZDpaB-90xK8MIV9QF973r1ZdUf", "BidTQxOqebpNk1rVsjs2sUJSTOZc", BasicPlayerActivity.class));

        return map;
    }
}

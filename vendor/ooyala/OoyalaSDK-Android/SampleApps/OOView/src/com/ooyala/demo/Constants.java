package com.ooyala.demo;

import android.graphics.Color;
import com.ooyala.demo.utils.MostFavoriteComparator;
import com.ooyala.demo.utils.MostPopularComparator;
import com.ooyala.demo.utils.MostRecentComparator;
import com.ooyala.demo.vo.VideoInfoVO;

import java.util.Comparator;

public class Constants {
    /**
     * facebook api keys
     */
    public static final String OOYALA_API_KEY = "QwcHY6ELE0J1Suq2RTKpm7NGfNgY.E6947";
    public static final String OOYALA_SECRET_KEY = "TjHz0fojdkHqukG6HRUkbyLXlCs7IGKHvzb6BsiF";
    public static final String OOYALA_P_CODE = "QwcHY6ELE0J1Suq2RTKpm7NGfNgY";
    public static final String OOYALA_PLAYER_DOMAIN = "www.ooyala.com";

    public static final String ANALYTICS_REPORTS = "/analytics/reports/account/performance/videos/2012-01-01...2012-03-15";
    public static final String LABEL = "/labels/ed9210711835467199e1e86bb59f1bbb/assets";


    public static final String URL_FACEBOOK_LIKE_TEMPLATE = "https://www.facebook.com/plugins/like.php?locale=en_US&href=%s&send=false&layout=button_count&show_faces=false&action=like&colorscheme=light";

    /**
     * twitter api keys
     */
    public static final String OAUTH_CONSUMER_KEY = "iLCjZfLG3Xn3lMQfKUjsw";
    public static final String OAUTH_CONSUMER_SECRET = "uUlWaEtk7dRlwlzkifqry9dyWtC7pG5OfB2pJ7yrlb4";
    public static final String MEDIA_PROVIDER_API_KEY = "a319dbd8d11498bd42f75eb93f168625";

    /**
     * Sort comparators
     */
    public static final Comparator<VideoInfoVO> mostPopularComparator = new MostPopularComparator();
    public static final Comparator<VideoInfoVO> mostRecentComparator = new MostRecentComparator();
    public static final Comparator<VideoInfoVO> mostFavoriteComparator = new MostFavoriteComparator();


    /**
     * Preference key names
     */
    public static final String DURATION = "duration";
    public static final String DESCRIPTION = "description";
    public static final String NAME = "name";
    public static final String EMBED_CODE = "embed_code";
    public static final String BITMAP = "bitmap";
    public static final String PREVIEW_IMAGE_URL = "preview_image_url";

    public static final String P_TOKEN = "p_token";
    public static final String P_SECRET = "p_secret";
    public static final String P_SCREEN_NAME = "p_screen_name";
    public static final String P_USER_ID = "p_user_id";
    public static final String TWITTER_PREFERENCE = "twitterPrefs";


    public static final int TRANSPARENT = Color.argb(0, 0, 0, 0);
    public static final CharSequence EMPTY = "";
}

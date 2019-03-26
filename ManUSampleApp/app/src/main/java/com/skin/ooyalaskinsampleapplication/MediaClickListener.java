package com.skin.ooyalaskinsampleapplication;

import com.ooyala.android.skin.OoyalaSkinLayout;
import com.skin.ooyalaskinsampleapplication.ooyala.MultiMediaPlayListener;

/**
 * Created by amudha.p on 10/4/2016.
 */

public interface MediaClickListener {


    void onMediaPlayCLicked(int position);

    void onMediaPlay(OoyalaSkinLayout ooyalaSkinLayout, Object object, MultiMediaPlayListener multiMediaPlayListener, String embedcode);

    void onMediaComplete(int Position);


}

package com.ooyala.sample.utils.fragments;

import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.skin.OoyalaSkinLayout;
import com.ooyala.android.skin.OoyalaSkinLayoutController;
import com.ooyala.android.skin.configuration.SkinOptions;
import com.ooyala.sample.R;

public class SkinPlayerFragment extends Fragment {
  private static final String EMBED = "92cWp0ZDpDm4Q8rzHfVK6q9m6OtFP-ww";
  final String TAG = this.getClass().toString();

  final String PCODE  = "c0cTkxOqALQviQIGAHWY5hP0q9gU";
  final String DOMAIN = "http://ooyala.com";

  private OoyalaPlayer player;
  private OoyalaSkinLayout skinLayout;



  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.configurable_skin_fragment, container, false);

    // Get the SkinLayout from our layout xml
    skinLayout = (OoyalaSkinLayout)view.findViewById(R.id.ooyalaSkin);

    // Create the OoyalaPlayer, with some built-in UI disabled
    PlayerDomain domain = new PlayerDomain(DOMAIN);
    Options options = new Options.Builder().setShowAdsControls(false).setShowPromoImage(false).build();
    player = new OoyalaPlayer(PCODE, domain, options);

    //Create the SkinOptions, and setup React
    SkinOptions skinOptions = new SkinOptions.Builder().build();
    OoyalaSkinLayoutController controller = new OoyalaSkinLayoutController(getActivity().getApplication(), skinLayout, player, skinOptions);

    if (player.setEmbedCode(EMBED)) {
      //Uncomment for autoplay
      //player.play();
    }
    else {
      Log.e(TAG, "Asset Failure");
    }
    return view;
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.skin_menu, menu);
  }
  
  public void resizePlayer(int type) {
    Display display = getActivity().getWindowManager().getDefaultDisplay();
    Point size = new Point();
    display.getSize(size);
    int width = size.x;
    int height = size.y;

    switch (type) {
      case R.id.action_wide:
        height = height / 2;
        break;
      case R.id.action_tall:
        width = width / 2;
        break;
      case R.id.action_fill:
        width = ViewGroup.LayoutParams.MATCH_PARENT;
        height = ViewGroup.LayoutParams.MATCH_PARENT;
    }
    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
    skinLayout.setLayoutParams(params);
  }

}

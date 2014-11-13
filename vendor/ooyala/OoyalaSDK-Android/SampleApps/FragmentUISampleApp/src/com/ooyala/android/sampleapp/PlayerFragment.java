package com.ooyala.android.sampleapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.OoyalaPlayerLayoutController;

public class PlayerFragment extends Fragment {
  private static final String EMBED  = "lrZmRiMzrr8cP77PPW0W8AsjjhMJ1BBe";  //Embed Code, or Content ID
  private static final String PCODE  = "R2d3I6s06RyB712DN0_2GsQS-R-Y";
  private static final String DOMAIN = "www.ooyala.com";
  private static final String SCRUB_TIME_KEY = "scrubTime";
  OoyalaPlayer player;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.player_fragment_view, container, false);
  }

  @Override
  public void onActivityCreated (Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) getView().findViewById(R.id.ooyalaPlayer);
    OoyalaPlayerLayoutController playerLayoutController = new OoyalaPlayerLayoutController(playerLayout, PCODE, DOMAIN);
    player = playerLayoutController.getPlayer();
    if (player.setEmbedCode(EMBED)) {
      player.play();
      SharedPreferences prefs = getActivity().getPreferences( Context.MODE_PRIVATE );
      int scrubTime = prefs.getInt( SCRUB_TIME_KEY, 0 );
      player.seek( scrubTime );
    } else {
      Log.d(this.getClass().getName(), "Something Went Wrong!");
      player = null;
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    if (player != null) {
      SharedPreferences prefs = getActivity().getPreferences( Context.MODE_PRIVATE );
      SharedPreferences.Editor editor = prefs.edit();
      int scrubTime = player.getPlayheadTime();
      editor.putInt( SCRUB_TIME_KEY, scrubTime );
      editor.commit();
      player.suspend();
    }
  }

}

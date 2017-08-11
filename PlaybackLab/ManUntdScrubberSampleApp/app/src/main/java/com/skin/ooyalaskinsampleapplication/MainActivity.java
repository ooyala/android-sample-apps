package com.skin.ooyalaskinsampleapplication;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.skin.OoyalaSkinLayout;
import com.ooyala.android.skin.OoyalaSkinLayoutController;
import com.ooyala.android.skin.configuration.SkinOptions;
import com.ooyala.android.util.SDCardLogcatOoyalaEventsLogger;
import com.testfairy.TestFairy;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class MainActivity extends Activity implements Observer, DefaultHardwareBackBtnHandler {


    protected OoyalaSkinLayoutController playerLayoutController;
    protected OoyalaPlayer player;
    String EMBED = null;
    String PCODE = null;
    String DOMAIN = null;
    SDCardLogcatOoyalaEventsLogger Playbacklog = new SDCardLogcatOoyalaEventsLogger();


    RecyclerView mRecyclerView;
    PlayerAdapter mAdapter;
    //RecyclerView.LayoutManager mLayoutManager;
    StaggeredGridLayoutManager linearLayoutManager;
    int currentVisibleItem;
    int mLastVideoPositionClicked = -1;
    PlayListener myListener = new PlayListener() {
        @Override
        public void playVideo(PlayerSelectionOption playerSelectionOption, OoyalaSkinLayout ooyalaSkinLayout) {
            DOMAIN = playerSelectionOption.getDomain();
            PCODE = playerSelectionOption.getPcode();
            EMBED = playerSelectionOption.getEmbedCode();

            // Create the OoyalaPlayer, with some built-in UI disabled
            PlayerDomain domain = new PlayerDomain(DOMAIN);
            Options options = new Options.Builder().setShowNativeLearnMoreButton(false).setShowPromoImage(false).build();
            player = new OoyalaPlayer(PCODE, domain, options);
            JSONObject overrides = createSkinOverrides();
            SkinOptions skinOptions = new SkinOptions.Builder().setSkinOverrides(overrides).build();
            playerLayoutController = new OoyalaSkinLayoutController(getApplication(), ooyalaSkinLayout, player, skinOptions);

            onPlay(ooyalaSkinLayout, null);
        }
    };
    private List<PlayerSelectionOption> movieList = new ArrayList<>();
    private int mOffset = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        TestFairy.begin(this, "bcc6573fb0d27f7d2b16fa94490fabc3d1799829");
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_home);
        populateList();
        //movieList.get(0).setAutoPlay(true);


        mAdapter = new PlayerAdapter(movieList, myListener);
        linearLayoutManager = new StaggeredGridLayoutManager(1, 1);
        //mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int visibleItem = 0;
            int lastVisibleItem = 0;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                try {
                    int[] arrayOfItems = new int[2];
                    visibleItem = 0;
                    lastVisibleItem = 0;
                    //This logic needs to changed
                    arrayOfItems = linearLayoutManager.findFirstCompletelyVisibleItemPositions(arrayOfItems);
                    visibleItem = arrayOfItems[0];

                    //Log.d("sesha", "sesha" + visibleItem);

                    if (newState == 0 && visibleItem > 0 &&
                            mLastVideoPositionClicked != visibleItem) {
                        if (mLastVideoPositionClicked != -1) {
                            movieList.get(mLastVideoPositionClicked).setAutoPlay(false);
                        }
                        movieList.get(visibleItem).setAutoPlay(true);
                        mAdapter.notifyDataSetChanged();
                        mLastVideoPositionClicked = visibleItem;

                    }


                   /* if(mLastVideoPositionClicked!=visibleItem) {
                        playVideo(movieList.get(visibleItem), (OoyalaSkinLayout) recyclerView.getChildAt(visibleItem).findViewById(R.id.ooyala_player_skin));
                        mLastVideoPositionClicked=visibleItem;
                    }else{
                        player.suspend();
                        player=null;

                    }*/


                } catch (Exception ex) {
                    //LoggerUtils.e("OnScrollStateChanged ==>", ex.toString());
                }
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                mOffset = mOffset + dy;

                //
                currentVisibleItem = linearLayoutManager.findFirstVisibleItemPositions(null)[0];

            }
        });

    }


    public void populateList() {
        movieList.add(new PlayerSelectionOption("M2ZGlnMzE6xukA138hptAWkq68p5Aeqa", "kwY2YyOrdiiEe4TsOYOlWUDZ2T8i", "http://ooyala.com"));
        movieList.add(new PlayerSelectionOption("JiOTdrdzqAujYa5qvnOxszbrTEuU5HMt", "c0cTkxOqALQviQIGAHWY5hP0q9gU", "http://www.ooyala.com"));
        movieList.add(new PlayerSelectionOption("tjN2h1MDE65xMHMqNvvU0fYVBi6sFl1M", "ltOGIyOq4Waxz7r-q6FsUpEfl4dg", "http://www.ooyala.com"));
        movieList.add(new PlayerSelectionOption("tjN2h1MDE65xMHMqNvvU0fYVBi6sFl1M", "ltOGIyOq4Waxz7r-q6FsUpEfl4dg", "http://www.ooyala.com"));
        movieList.add(new PlayerSelectionOption("M2ZGlnMzE6xukA138hptAWkq68p5Aeqa", "kwY2YyOrdiiEe4TsOYOlWUDZ2T8i", "http://ooyala.com"));
        movieList.add(new PlayerSelectionOption("JiOTdrdzqAujYa5qvnOxszbrTEuU5HMt", "c0cTkxOqALQviQIGAHWY5hP0q9gU", "http://www.ooyala.com"));
        movieList.add(new PlayerSelectionOption("tjN2h1MDE65xMHMqNvvU0fYVBi6sFl1M", "ltOGIyOq4Waxz7r-q6FsUpEfl4dg", "http://www.ooyala.com"));
        movieList.add(new PlayerSelectionOption("tjN2h1MDE65xMHMqNvvU0fYVBi6sFl1M", "ltOGIyOq4Waxz7r-q6FsUpEfl4dg", "http://www.ooyala.com"));
        movieList.add(new PlayerSelectionOption("M2ZGlnMzE6xukA138hptAWkq68p5Aeqa", "kwY2YyOrdiiEe4TsOYOlWUDZ2T8i", "http://ooyala.com"));
        movieList.add(new PlayerSelectionOption("JiOTdrdzqAujYa5qvnOxszbrTEuU5HMt", "c0cTkxOqALQviQIGAHWY5hP0q9gU", "http://www.ooyala.com"));
        // movieList.add(new PlayerSelectionOption("tjN2h1MDE65xMHMqNvvU0fYVBi6sFl1M", "ltOGIyOq4Waxz7r-q6FsUpEfl4dg", "http://www.ooyala.com"));
        //movieList.add(new PlayerSelectionOption("tjN2h1MDE65xMHMqNvvU0fYVBi6sFl1M", "ltOGIyOq4Waxz7r-q6FsUpEfl4dg", "http://www.ooyala.com"));
    }


    public void onPlay(OoyalaSkinLayout skinLayout, Object object) {
        //Create the SkinOptions, and setup React
        player.addObserver(MainActivity.this);
        playerLayoutController.onResume(MainActivity.this, this);

        if (player.setEmbedCode(EMBED)) {
            //Uncomment for autoplay
            player.play();
        } else {
            Log.e("ooyala player", "Asset Failure");
        }
    }

    /**
     * Start DefaultHardwareBackBtnHandler
     **/
    @Override
    public void invokeDefaultOnBackPressed() {
        super.onBackPressed();
    }
    /** End DefaultHardwareBackBtnHandler **/

    /**
     * Start Activity methods for Skin
     **/
    @Override
    protected void onPause() {
        super.onPause();
        if (playerLayoutController != null) {
            playerLayoutController.onPause();
        }
        Log.d("ooyala player", "Player Activity Stopped");
        if (player != null) {
            player.suspend();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (playerLayoutController != null) {
            playerLayoutController.onResume(this, this);
        }
        Log.d("ooyala player", "Player Activity Restarted");
        if (player != null) {
            player.resume();
        }
    }

    @Override
    public void onBackPressed() {
        if (playerLayoutController != null) {
            playerLayoutController.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (playerLayoutController != null) {
            playerLayoutController.onDestroy();
        }
    }

    private JSONObject createSkinOverrides() {
        JSONObject overrides = new JSONObject();
        JSONObject startScreenOverrides = new JSONObject();
        JSONObject playIconStyleOverrides = new JSONObject();
        try {
            // start screen
            playIconStyleOverrides.put("color", "red");
            startScreenOverrides.put("pauseIconPosition", "bottomLeft");
            startScreenOverrides.put("pauseIconStyle", playIconStyleOverrides);
            overrides.put("pauseScreen", startScreenOverrides);

        } catch (Exception e) {
            Log.e("rvg", "Exception Thrown", e);
        }
        return overrides;
    }

    @Override
    public void update(Observable arg0, Object argN) {
        {
            if (arg0 != player) {
                return;
            }

            final String arg1 = OoyalaNotification.getNameOrUnknown(argN);
            if (arg1 == OoyalaPlayer.TIME_CHANGED_NOTIFICATION_NAME) {
                return;
            }

            if (arg1 == OoyalaPlayer.ERROR_NOTIFICATION_NAME) {
                final String msg = "Error Event Received";
                if (player != null && player.getError() != null) {
                    Log.e("ooyala player", msg, player.getError());
                } else {
                    Log.e("ooyala player", msg);
                }
                return;
            }

            // Automation Hook: to write Notifications to a temporary file on the device/emulator
            String text = "Notification Received: " + arg1 + " - state: " + player.getState();
            // Automation Hook: Write the event text along with event count to log file in sdcard if the log file exists
            Playbacklog.writeToSdcardLog(text);

            Log.d("ooyala player", "Notification Received: " + arg1 + " - state: " + player.getState());
        }

    }

}


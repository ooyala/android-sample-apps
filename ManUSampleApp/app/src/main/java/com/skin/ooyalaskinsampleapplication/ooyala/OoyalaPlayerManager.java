package com.skin.ooyalaskinsampleapplication.ooyala;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
//import com.ooyala.android.imasdk.OoyalaIMAConfiguration;
//import com.ooyala.android.imasdk.OoyalaIMAManager;
import com.ooyala.android.skin.OoyalaSkinLayout;
import com.ooyala.android.skin.OoyalaSkinLayoutController;
import com.ooyala.android.skin.configuration.SkinOptions;
import com.skin.ooyalaskinsampleapplication.PlayerSelectionOption;
import com.skin.ooyalaskinsampleapplication.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;


public class OoyalaPlayerManager implements Observer, MultimediaPlayer {


    protected OoyalaSkinLayoutController playerLayoutController;
    protected com.ooyala.android.OoyalaPlayer mPlayer;
    protected OoyalaSkinLayout ooyalaSkinLayout;
    //
    private PlayerSelectionOption videoDoc;
    //
    private boolean isPlaying;
    //
    OoyalaPlayerConfig ooyalaPlayerConfig;
    MultiMediaPlayListener multiMediaPlayListener;
    //
    private static OoyalaPlayerManager ooyalaPlayerManager;
    private Activity activity;

    private static final String TAG = OoyalaPlayerManager.class.getSimpleName();

    public static OoyalaPlayerManager getInstance() {
        if (ooyalaPlayerManager == null) {
            ooyalaPlayerManager = new OoyalaPlayerManager();
        }
        return ooyalaPlayerManager;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void init(Activity activity, OoyalaPlayerConfig ooyalaPlayerConfig, OoyalaSkinLayout ooyalaSkinLayout, Object doc) {
        this.activity = activity;
        this.ooyalaPlayerConfig = ooyalaPlayerConfig;
        this.ooyalaSkinLayout = ooyalaSkinLayout;
        this.videoDoc = (PlayerSelectionOption) doc;
        this.multiMediaPlayListener = ooyalaPlayerConfig.getMultiMediaPlayListener();
    }

    @Override
    public void update(Observable arg0, Object argN) {

        try {
            if (arg0 != mPlayer) {
                return;
            }
            //
            notifyPlayer(argN);

        } catch (Exception ex) {
        }
    }


    @Override
    public void play(String contentId) {
        play(0, contentId);
    }

    @Override
    public void play(int playHeadTime, String contentId) {

        try {

            //If its existing code or set the new code.. start from there only.
          /*  if (mPlayer != null && mPlayer.getEmbedCode() != null &&
                    mPlayer.getEmbedCode().equals(ooyalaPlayerConfig.getEmbedCode())) {
                seekTo(playHeadTime);
                mPlayer.play();
                return;
            }*/

            if (mPlayer != null &&
                    mPlayer.setEmbedCode(ooyalaPlayerConfig.getEmbedCode())) {
                seekTo(playHeadTime);
                mPlayer.play();
                Log.i("OoyalaPlayerManager", "play good playHeadTime: "+playHeadTime );
                return;
            }


            Log.i("OoyalaPlayerManager", "play bad scenario");

            PlayerDomain domain = new PlayerDomain(ooyalaPlayerConfig.getDomain());
            Options options = new Options.Builder().setShowPromoImage(false).setShowNativeLearnMoreButton(false).build();
            mPlayer = new com.ooyala.android.OoyalaPlayer(ooyalaPlayerConfig.getPcode(), domain, options);

            //Create the SkinOptions, and setup React
            //JSONObject overrides = createSkinOverrides();
            SkinOptions skinOptions = new SkinOptions.Builder().build();

            playerLayoutController = new OoyalaSkinLayoutController(activity.getApplication(), ooyalaSkinLayout, mPlayer, skinOptions);

            //Add observer to listen to fullscreen open and close events
            playerLayoutController.addObserver(this);
            mPlayer.addObserver(this);


            //OoyalaIMAConfiguration imaConfig = new OoyalaIMAConfiguration.Builder().build();
            //OoyalaIMAManager ooyalaIMAManager = new OoyalaIMAManager(mPlayer, ooyalaSkinLayout, imaConfig);

            HashMap<String, String> tagParameters = new HashMap<>();

            try {
                tagParameters.put("cust_params", "platform%3Dandroid%26content-id%3D"+ URLEncoder.encode(contentId, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            //ooyalaIMAManager.setAdTagParameters(tagParameters);

            if (mPlayer.setEmbedCode(ooyalaPlayerConfig.getEmbedCode())) {
                mPlayer.play(playHeadTime);
            } else {
            }
        } catch (Exception exception) {
        }
    }

    @Override
    public void resume() {
        if (mPlayer != null) {
            mPlayer.resume();
        }
    }

    @Override
    public void pause() {

        Log.d(TAG, "Player Activity Stopped");
        if (playerLayoutController != null) {
            //playerLayoutController.onPause();
        }
        if (mPlayer != null && isPlaying()) {
            //mPlayer.pause();
        }

    }

    @Override
    public boolean isPlaying() {
        if (mPlayer != null) {
            return mPlayer.isPlaying();
        }
        return false;
    }

    @Override
    public boolean isInitialised() {
        return mPlayer != null;
    }

    public void suspend() {
        if (mPlayer != null) {
            mPlayer.suspend();
        }
    }

    @Override
    public void stop() {
        Log.i("OoyalaPlayerManager", "stop");
        if (playerLayoutController != null) {
            playerLayoutController.onPause();
            playerLayoutController.destroy();
        }
        if (mPlayer != null) {

            mPlayer.suspend();
            mPlayer.destroy();
            mPlayer = null;

            playerLayoutController = null;
            ooyalaSkinLayout = null;
            //

            //if (multiMediaPlayListener != null && isPlaying) {
            if (multiMediaPlayListener != null) {
                //multiMediaPlayListener.stoppedPlaying();
            }
        }

    }

    @Override
    public void seekTo(int miliseconds) {
        if (mPlayer != null) {
            mPlayer.seek(videoDoc.getPlayedHeadTime());
        }
    }

    @Override
    public void mutePlayer(boolean mute) {
        if (mPlayer != null) {
            if (mute) {
                mPlayer.setVolume(0);
            } else {
                mPlayer.setVolume(1);
            }
        }
    }

    @Override
    public boolean isPlayerMuted() {
        return mPlayer != null && mPlayer.getVolume() == 1;
    }


    /**
     * @param argN
     */
    private void notifyPlayer(Object argN) {
        final String arg1 = OoyalaNotification.getNameOrUnknown(argN);
        switch (arg1) {
            case com.ooyala.android.OoyalaPlayer.TIME_CHANGED_NOTIFICATION_NAME:
                if (videoDoc != null) {
                    //save the played time to model siva govindan
                    videoDoc.setPlayedHeadTime(mPlayer.getPlayheadTime());
                }
            case com.ooyala.android.OoyalaPlayer.ERROR_NOTIFICATION_NAME:
                final String msg = "Error Event Received";

                /*if (multiMediaPlayListener != null) {
                    suspend();
                    multiMediaPlayListener.errorOccured(msg);
                    play(videoDoc.getPlayedHeadTime(), false);
                }*/
                break;
            case com.ooyala.android.OoyalaPlayer.STATE_CHANGED_NOTIFICATION_NAME:
                if (mPlayer != null) {
                    if (mPlayer.getState().toString().equals("PAUSED") && isPlaying) {
                        if (multiMediaPlayListener != null) {
                            multiMediaPlayListener.pausedPlaying();
                            isPlaying = false;
                        }
                    }
                }
                break;
            case com.ooyala.android.OoyalaPlayer.PLAY_STARTED_NOTIFICATION_NAME:
                if (multiMediaPlayListener != null) {
                    multiMediaPlayListener.startedPlaying();
                    isPlaying = true;
                }
                break;
            case com.ooyala.android.OoyalaPlayer.PLAY_COMPLETED_NOTIFICATION_NAME:
                if (multiMediaPlayListener != null) {
                    multiMediaPlayListener.completedPlaying();
                    isPlaying = false;
                }
                break;
            case com.ooyala.android.OoyalaPlayer.SEEK_STARTED_NOTIFICATION_NAME:
                if (multiMediaPlayListener != null) {
                    multiMediaPlayListener.seekStarted();
                }
                break;
            case com.ooyala.android.OoyalaPlayer.SEEK_COMPLETED_NOTIFICATION_NAME:
                if (multiMediaPlayListener != null) {
                    multiMediaPlayListener.seekCompleted();
                }
                break;
            case com.ooyala.android.OoyalaPlayer.AD_STARTED_NOTIFICATION_NAME:
                if (multiMediaPlayListener != null) {
                    multiMediaPlayListener.adStarted();
                }
                break;
            case com.ooyala.android.OoyalaPlayer.AD_COMPLETED_NOTIFICATION_NAME:
                if (multiMediaPlayListener != null) {
                    multiMediaPlayListener.adCompleted();
                }
                break;
            case com.ooyala.android.OoyalaPlayer.AD_SKIPPED_NOTIFICATION_NAME:
                if (multiMediaPlayListener != null) {
                    multiMediaPlayListener.adSkipped();
                }
                break;
            case OoyalaSkinLayoutController.FULLSCREEN_CHANGED_NOTIFICATION_NAME:
                break;

        }
    }

    /**
     * @return
     */
    public OoyalaSkinLayout getModelOoyalaSkinLayout() {
        if (ooyalaSkinLayout == null) {

            ooyalaSkinLayout = (OoyalaSkinLayout) LayoutInflater.from(activity).inflate(R.layout.ooyala_player_skin, null, false);
            OoyalaPlayerManager.getInstance().stop();
        } else if (ooyalaSkinLayout.getParent() != null) {
            ((ViewGroup) ooyalaSkinLayout.getParent()).removeView(ooyalaSkinLayout);
        }

        return ooyalaSkinLayout;
    }
}

package com.ooyala.demo;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.ui.OptimizedOoyalaPlayerLayoutController;
import com.ooyala.android.PlayerDomain;
import com.ooyala.demo.dao.DBAdapter;
import com.ooyala.demo.social.DialogError;
import com.ooyala.demo.social.DialogListener;
import com.ooyala.demo.social.FacebookError;
import com.ooyala.demo.social.LikeWebClient;
import com.ooyala.demo.social.TwitterActivity;
import com.ooyala.demo.utils.TwitterUtils;

import java.net.URLEncoder;

public class PlayerActivity extends Activity {

    public static Bitmap bitmap;
    public static String url;

    private OoyalaPlayer player;
    private Screen screen = Screen.image;
    private View socialLayout;
    private OptimizedOoyalaPlayerLayoutController playerLayoutController;
    private Animation fadeOutAnimation;
    private Animation fadeInAnimation;

    private static boolean isShareComplete = false;
    private String thumbnailUrl;
    private String embedCode;
    private String title;

    enum Screen {
        image, player
    }


    @Override
    protected void onResume() {
        if (isShareComplete) {
            isShareComplete = false;
            showTwitterDialog();
        }
        if (player != null) {
            player.resume();
        }

        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.suspend();
        }
    }


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Bundle extras = getIntent().getExtras();
        bitmap = extras.getParcelable(Constants.BITMAP);
        embedCode = extras.getString(Constants.EMBED_CODE);
        thumbnailUrl = extras.getString(Constants.PREVIEW_IMAGE_URL);
        title = extras.getString(Constants.NAME);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.player);

        Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        fadeInAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        animation.setDuration(700);
        fadeInAnimation.setDuration(500);

        final ViewFlipper viewFlipper = (ViewFlipper) findViewById(R.id.flipper);

        final CheckBox watchListView = (CheckBox) findViewById(R.id.watch_list);
        final ImageView twitterShare = (ImageView) findViewById(R.id.social_share);

        final WebView facebookLikeButton = (WebView) findViewById(R.id.facebook_like);
        facebookLikeButton.getSettings().setJavaScriptEnabled(true);
        facebookLikeButton.setBackgroundColor(Constants.TRANSPARENT);
        facebookLikeButton.setFocusableInTouchMode(false);
        facebookLikeButton.setFocusable(false);
        facebookLikeButton.setWebViewClient(new LikeWebClient(this, UserData.FACEBOOK, null));

        final DBAdapter dbAdapter = new DBAdapter(this);

        final OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.player);
        socialLayout = findViewById(R.id.social_layout);
        twitterShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                player.pause();


                if (TwitterUtils.isTokenExists(PlayerActivity.this)) {
                    showTwitterDialog();
                    return;
                }


                TwitterActivity.setListener(new DialogListener() {
                    @Override
                    public void onComplete(final Bundle values) {
                        isShareComplete = true;
                    }

                    @Override
                    public void onFacebookError(final FacebookError e) {
                    }

                    @Override
                    public void onError(final DialogError e) {
                    }

                    @Override
                    public void onCancel() {
                    }
                });

                Intent intent = new Intent(PlayerActivity.this, TwitterActivity.class);

                startActivity(intent);
            }
        });
        socialLayout.setAnimation(animation);

        boolean exists = dbAdapter.existsInWatchList(Constants.OOYALA_API_KEY, embedCode);
        watchListView.setChecked(exists);

        watchListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (dbAdapter.existsInWatchList(Constants.OOYALA_API_KEY, embedCode)) {
                    dbAdapter.removeToWatchList(Constants.OOYALA_API_KEY, embedCode);
                } else {
                    dbAdapter.addToWatchList(Constants.OOYALA_API_KEY, embedCode);
                }
            }
        });

        playerLayoutController = new OptimizedOoyalaPlayerLayoutController(playerLayout, Constants.OOYALA_P_CODE, new PlayerDomain(Constants.OOYALA_PLAYER_DOMAIN));
        playerLayoutController.getLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                    return;
                }
                if (player != null && playerLayoutController.getControls() != null) {
                    try {
                        playerLayoutController.getControls().show();
                    } catch (RuntimeException ignored) {
                        return;
                    }
                }


                if (socialLayout.getVisibility() != View.VISIBLE) {
                    socialLayout.setAnimation(fadeInAnimation);
                    socialLayout.getAnimation().start();

                    socialLayout.setVisibility(View.VISIBLE);
                } else {
                    socialLayout.setAnimation(fadeOutAnimation);
                    socialLayout.getAnimation().start();

                    socialLayout.setVisibility(View.GONE);
                }
            }
        });

        player = playerLayoutController.getPlayer();

        final ImageView thumb = (ImageView) findViewById(R.id.video_thumb);
        thumb.setVisibility(View.VISIBLE);


        fadeOutAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);


        fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(final Animation animation) {
            }

            @Override
            public void onAnimationEnd(final Animation animation) {
                if (screen == Screen.player) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    player.play();
                }
            }

            @Override
            public void onAnimationRepeat(final Animation animation) {
            }
        });
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(final Animation animation) {
            }

            @Override
            public void onAnimationEnd(final Animation animation) {
                if (screen == Screen.image) {
                    screen = Screen.player;
                    viewFlipper.showNext();
                }

            }

            @Override
            public void onAnimationRepeat(final Animation animation) {
            }
        });
        viewFlipper.setAnimateFirstView(true);
        viewFlipper.setInAnimation(fadeInAnimation);
        viewFlipper.setOutAnimation(fadeOutAnimation);
        viewFlipper.setAnimation(animation);

        String url = String.format(Constants.URL_FACEBOOK_LIKE_TEMPLATE, URLEncoder.encode(thumbnailUrl));
        facebookLikeButton.loadUrl(url);


        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        Bitmap bitmapOrg = bitmap;

        int width = bitmapOrg.getWidth();
        int height = bitmapOrg.getHeight();
//        int newWidth = defaultDisplay.getWidth();
        int newHeight = defaultDisplay.getHeight();

        // calculate the scale - in this case = 0.4f
        float scaleWidth = ((float) newHeight) / (float) width;

        // createa matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postRotate(90);
        matrix.postScale(scaleWidth, scaleWidth);
        // rotate the Bitmap

        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0,
                width, height, matrix, true);

        // make a Drawable from Bitmap to allow to set the BitMap
        // to the ImageView, ImageButton or what ever
        BitmapDrawable bmd = new BitmapDrawable(resizedBitmap);


        // set the Drawable on the ImageView
        thumb.setImageDrawable(bmd);

        // center the Image
        thumb.setScaleType(ImageView.ScaleType.FIT_XY);
        player.setEmbedCode(embedCode);

    }

    private void showTwitterDialog() {


        final TwitterDialog dialog = new TwitterDialog(this, title, thumbnailUrl);
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(final DialogInterface dialogInterface) {
                player.play();
            }
        });

        dialog.show();
    }
}

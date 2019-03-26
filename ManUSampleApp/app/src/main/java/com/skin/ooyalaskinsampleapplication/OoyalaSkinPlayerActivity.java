package com.skin.ooyalaskinsampleapplication;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.ooyala.android.skin.OoyalaSkinLayout;
import com.skin.ooyalaskinsampleapplication.ooyala.CustomRecyclerView;
import com.skin.ooyalaskinsampleapplication.ooyala.MultiMediaPlayListener;
import com.skin.ooyalaskinsampleapplication.ooyala.OoyalaPlayerConfig;
import com.skin.ooyalaskinsampleapplication.ooyala.OoyalaPlayerManager;

import java.util.ArrayList;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import butterknife.BindView;
import butterknife.ButterKnife;

public class OoyalaSkinPlayerActivity extends Activity implements DefaultHardwareBackBtnHandler, MediaClickListener, DispatchEvent {

    public int mLastVideoPositionClicked = -1;

    public boolean isVideoCompelete = false;

    //Image Card View Ids
    @BindView(R.id.image_view_image_card_fullscreen_close)
    AppCompatImageView mImageViewClose;

    @BindView(R.id.recycler_view_video)
    CustomRecyclerView mCustomRecyclerView;

    PlayerAdapter mNowScreenAdapter;
    private ArrayList<PlayerSelectionOption> docList = new ArrayList<>();
    LinearLayoutManager mLayoutManager;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ooyala_player);
        ButterKnife.bind(this);
        populateList();
        OoyalaPlayerManager.getInstance().setActivity(this);
        mCustomRecyclerView.getItemAnimator().setChangeDuration(0);

        mNowScreenAdapter = new PlayerAdapter(docList, this);
        mLayoutManager =  new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mCustomRecyclerView.setLayoutManager(mLayoutManager);
       // mCustomRecyclerView.setLayoutManager(mLayoutManager);
        mCustomRecyclerView.setHasFixedSize(true);
        mCustomRecyclerView.setDrawingCacheEnabled(true);
        mCustomRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        //
        mCustomRecyclerView.setAdapter(mNowScreenAdapter);
        mCustomRecyclerView.computeVerticalScrollOffset();
        mCustomRecyclerView.computeVerticalScrollExtent();
        //
        mNowScreenAdapter.notifyDataSetChanged();
        mCustomRecyclerView.setInterface(this);

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(mCustomRecyclerView);

        mCustomRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_IDLE) {
                    View centerView = snapHelper.findSnapView(mLayoutManager);
                    int pos = mLayoutManager.getPosition(centerView);
                    //Log.e("Snapped Item Position:",""+pos);
                    if (mLastVideoPositionClicked != pos) {
                        onMediaPlayCLicked(pos);
                    }
                }

            }
        });

        //
        onMediaPlayCLicked(0);
        //
        setUpListeners();
//        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                if (!isVideoCompelete) {
//                    handleViewOnScroll();
//                }
//
//            }
//        });


    }

    public void populateList() {
        docList.add(new PlayerSelectionOption("Z1NTQ1YTE6WEd5oxNzLwjrVrU0Bmfsxj", "U0c2gyOlJzI4nyJViWvgbZaKc2JV", "http://ooyala.com"));
//        docList.add(new PlayerSelectionOption("5oZWI1aDE6W_SWTjibqEecApPd8Kqm",   "U0c2gyOlJzI4nyJViWvgbZaKc2JV", "http://ooyala.com"));
        docList.add(new PlayerSelectionOption("U1ZWI1aDE6cWOa8S7jV6zD4GxWw_ds9S", "U0c2gyOlJzI4nyJViWvgbZaKc2JV", "http://ooyala.com"));
        docList.add(new PlayerSelectionOption("VtMjg1aDE6HRaWQrZbqm_AmufWY1EG1y", "U0c2gyOlJzI4nyJViWvgbZaKc2JV", "http://ooyala.com"));
        docList.add(new PlayerSelectionOption("NnNDg1aDE6DD3N0EzKUdrz6oY8B6rOmN", "U0c2gyOlJzI4nyJViWvgbZaKc2JV", "http://ooyala.com"));
        docList.add(new PlayerSelectionOption("s2Zzg1aDE6ivNrxJicgFpNH2pYUR9ONF", "U0c2gyOlJzI4nyJViWvgbZaKc2JV", "http://ooyala.com"));
        docList.add(new PlayerSelectionOption("Z1NTQ1YTE6WEd5oxNzLwjrVrU0Bmfsxj", "U0c2gyOlJzI4nyJViWvgbZaKc2JV", "http://ooyala.com"));
        docList.add(new PlayerSelectionOption("Z1NTQ1YTE6WEd5oxNzLwjrVrU0Bmfsxj", "U0c2gyOlJzI4nyJViWvgbZaKc2JV", "http://ooyala.com"));
        docList.add(new PlayerSelectionOption("Z1NTQ1YTE6WEd5oxNzLwjrVrU0Bmfsxj", "U0c2gyOlJzI4nyJViWvgbZaKc2JV", "http://ooyala.com"));
        docList.add(new PlayerSelectionOption("Z1NTQ1YTE6WEd5oxNzLwjrVrU0Bmfsxj", "U0c2gyOlJzI4nyJViWvgbZaKc2JV", "http://ooyala.com"));
        docList.add(new PlayerSelectionOption("Z1NTQ1YTE6WEd5oxNzLwjrVrU0Bmfsxj", "U0c2gyOlJzI4nyJViWvgbZaKc2JV", "http://ooyala.com"));
    }


    private void setUpListeners() {

        mImageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onStopVideo();
                finish();
            }

        });
    }

    /**
     * Start DefaultHardwareBackBtnHandler.
     **/
    @Override
    public void invokeDefaultOnBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    public void onMediaPlayCLicked(final int position) {
        if (mLastVideoPositionClicked != -1) {
            OoyalaPlayerManager.getInstance().pause();
            docList.get(mLastVideoPositionClicked).getState().startAutoPlaying = false;
        }

        docList.get(position).getState().startAutoPlaying = true;
        mLastVideoPositionClicked = position;

        mNowScreenAdapter.notifyItemChanged(mLastVideoPositionClicked);
        isVideoCompelete = false;
    }

    @Override
    public void onMediaPlay(OoyalaSkinLayout ooyalaSkinLayout, Object object, MultiMediaPlayListener multiMediaPlayListener, String embedcode) {
        OoyalaPlayerManager ooyalaPlayerManager = OoyalaPlayerManager.getInstance();
        PlayerSelectionOption video = (PlayerSelectionOption) object;
        ooyalaPlayerManager.init(this, new OoyalaPlayerConfig(video.getEmbedCode(), video.getPcode(), video.getDomain(), multiMediaPlayListener), ooyalaSkinLayout, object);
        if (video.getEmbedCode() != null) {
            Log.i("SkinPlayer", "onMediaPlay embedcode: "+embedcode+" playHead: "+video.getPlayedHeadTime());
            Log.i("SkinPlayer", "onMediaPlay v embedcode: "+video.getEmbedCode());

            ooyalaPlayerManager.play(video.getPlayedHeadTime(), embedcode);
        }

    }

    @Override
    public void onMediaComplete(int Position) {

        if ((Position) < (docList.size() - 1)) {
            isVideoCompelete = true;
            mLayoutManager.setSmoothScrollbarEnabled(true);
            mLayoutManager.scrollToPositionWithOffset(Position, 10);
            onMediaPlayCLicked(Position);
        }
    }


    @Override
    public void handleView() {
    }

}
package com.ooyala.fullscreensampleapp;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.ooyala.android.skin.OoyalaSkinLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

public class PlayerActivity extends AppCompatActivity implements MediaPlayerListener {

    private static final String TAG = "PLAYER-5406"; //PlayerActivity.class.getName();

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private PlayerAdapter playerAdapter;
    private PagerSnapHelper snapHelper;
    private ScrollListener scrollListener;
    private List<Data> dataList;

    private class ScrollListener extends RecyclerView.OnScrollListener {
        private int snapPosition = RecyclerView.NO_POSITION;

        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                snapPosition = getCurrentPosition();
                initItem(snapPosition);
                play(snapPosition);
            }
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
                return;
            }
            MediaPlayer player = MediaPlayer.getInstance();
            if (player.isPlaying()) {
                pause(snapPosition);
            }
        }

        void setSnapPosition(int position) {
            snapPosition = position;
        }

        int getCurrentPosition() {
            View centerView = snapHelper.findSnapView(layoutManager);
            return centerView != null ? layoutManager.getPosition(centerView) : RecyclerView.NO_POSITION;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players);

        MediaPlayer.getInstance().setActivity(this);

        populateData();

        scrollListener = new ScrollListener();
        scrollListener.setSnapPosition(0);

        FrameLayout expandedLayout = findViewById(R.id.empty_view);
        playerAdapter = new PlayerAdapter(dataList , expandedLayout);
        playerAdapter.setAutoPlayIndex(0);


        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(scrollListener);
        recyclerView.setHasFixedSize(true);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        recyclerView.getItemAnimator().setChangeDuration(0);
        recyclerView.setAdapter(playerAdapter);

        snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaPlayer.getInstance().onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MediaPlayer.getInstance().onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        MediaPlayer.getInstance().onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaPlayer.getInstance().onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MediaPlayer.getInstance().onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MediaPlayer.getInstance().onBackPressed();
    }

    private void populateData() {
        final Data data = new Data("h4aHB1ZDqV7hbmLEv4xSOx3FdUUuephx", "c0cTkxOqALQviQIGAHWY5hP0q9gU", "http://www.ooyala.com/");
        dataList = new ArrayList<>();
        dataList.add(new Data(data));
        dataList.add(new Data(data));
        dataList.add(new Data(data));
        dataList.add(new Data(data));
        dataList.add(new Data(data));
    }

    private void initItem(int snapPosition) {
			PlayerHolder holder = (PlayerHolder) recyclerView.findViewHolderForAdapterPosition(snapPosition);
			if (holder != null) {
				Data data = playerAdapter.getDataByPosition(snapPosition);
				holder.init(data);
			}
		}

    private void play(int snapPosition) {
        PlayerHolder holder = (PlayerHolder) recyclerView.findViewHolderForAdapterPosition(snapPosition);
        if (holder != null) {
            Data data = playerAdapter.getDataByPosition(snapPosition);
            if (data.isWasPaused()) {
                data.setWasPaused(false);
                holder.play(data);
            }
        }
    }

    private void pause(int snapPosition) {
        PlayerHolder holder = (PlayerHolder) recyclerView.findViewHolderForAdapterPosition(snapPosition);
        if (holder != null) {
            Data data = playerAdapter.getDataByPosition(snapPosition);
            data.setWasPaused(true);
            holder.pause(data);
        }
    }

    @Override
    public void onMediaPlay(OoyalaSkinLayout ooyalaSkinLayout, Data data) {

    }
}

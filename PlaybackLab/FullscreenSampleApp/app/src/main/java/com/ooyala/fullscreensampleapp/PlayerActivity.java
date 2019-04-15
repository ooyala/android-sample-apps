package com.ooyala.fullscreensampleapp;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER;

public class PlayerActivity extends AppCompatActivity {

    private static final String TAG = PlayerActivity.class.getName();

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.title_text_view)
    TextView textView;

    private RecyclerView.LayoutManager layoutManager;
    private PlayerAdapter playerAdapter;
    private PagerSnapHelper snapHelper;
    private ScrollListener scrollListener;
    private List<Data> dataList;

    private class ScrollListener extends RecyclerView.OnScrollListener {
        private static final int PLAY_DELAY = 500;

        private int snapPosition = RecyclerView.NO_POSITION;
        private int state = RecyclerView.SCROLL_STATE_IDLE;
        private Handler handler = new Handler();
        private Runnable runnable = () -> {
            if (state == RecyclerView.SCROLL_STATE_IDLE) {
                int currentPosition = getCurrentPosition();
                if (snapPosition != currentPosition) {
                    snapPosition = currentPosition;
                    initItem(snapPosition);
                }
                play(snapPosition);
            }
        };

        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            state = newState;

            //As the user scrolls, the video autoplays when the player is fully in view AND
            // the scrolling pauses for 500 ms
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                handler.postDelayed(runnable, PLAY_DELAY);
            }
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
                return;
            }

            updateCurrentDataPlayheadTime(snapPosition);

            MediaPlayer player = MediaPlayer.getInstance();
            if (player.isPlaying()) {
                pause(snapPosition);
            }
        }

        void destroy() {
            handler.removeCallbacks(runnable);
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
        ButterKnife.bind(this);

        populateData();

        scrollListener = new ScrollListener();
        scrollListener.setSnapPosition(0);

        playerAdapter = new PlayerAdapter(dataList);
        playerAdapter.setAutoPlayIndex(0);

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(scrollListener);
        recyclerView.setHasFixedSize(true);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        recyclerView.getItemAnimator().setChangeDuration(0);
        recyclerView.setAdapter(playerAdapter);

        MediaPlayer.getInstance().setRecyclerView(recyclerView);
        MediaPlayer.getInstance().setActivity(this);

        snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (scrollListener != null) {
            scrollListener.destroy();
        }
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        MediaPlayer player = MediaPlayer.getInstance();
        player.setFullscreenMode(newConfig.orientation == SCREEN_ORIENTATION_USER);
        textView.setVisibility(newConfig.orientation == SCREEN_ORIENTATION_USER ? View.GONE : View.VISIBLE);
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
				holder.init();
			}
		}

    private void play(int snapPosition) {
        PlayerHolder holder = (PlayerHolder) recyclerView.findViewHolderForAdapterPosition(snapPosition);
        if (holder != null) {
            holder.play();
        }
    }

    private void pause(int snapPosition) {
        PlayerHolder holder = (PlayerHolder) recyclerView.findViewHolderForAdapterPosition(snapPosition);
        if (holder != null) {
            holder.pause();
        }
    }

    private void updateCurrentDataPlayheadTime(int snapPosition) {
        PlayerHolder holder = (PlayerHolder) recyclerView.findViewHolderForAdapterPosition(snapPosition);
        if (holder != null) {
            holder.updatePlayheadTime();
        }
    }
}

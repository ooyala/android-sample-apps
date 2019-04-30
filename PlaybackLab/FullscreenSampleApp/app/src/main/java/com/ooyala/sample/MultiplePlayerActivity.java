package com.ooyala.sample;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import java.util.List;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER;

public class MultiplePlayerActivity extends AppCompatActivity {

    private static final String TAG = MultiplePlayerActivity.class.getName();

    public static String getName() {
        return "RecyclerView with multiple players";
    }

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.title_text_view)
    TextView textView;

    private RecyclerView.LayoutManager layoutManager;
    private MultiplePlayerAdapter playerAdapter;
    private PagerSnapHelper snapHelper;
    private ScrollListener scrollListener;

    private class ScrollListener extends RecyclerView.OnScrollListener {
        private int snapPosition = RecyclerView.NO_POSITION;
        private Handler handler = new Handler();
        private Runnable playRunnable = () -> {
            int currentSnapPosition = getCurrentPosition();
            if (currentSnapPosition == snapPosition) {
                play(snapPosition);
            }
        };

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
                return;
            }

            int currentSnapPosition = getCurrentPosition();
            if (currentSnapPosition != snapPosition) {
                if (snapPosition != RecyclerView.NO_POSITION) {
                    updateCurrentData(snapPosition);
                    pause(snapPosition);
                }

                //As the user scrolls, the video autoplays when the player is fully in view AND
                // the scrolling pauses for 500 ms
                snapPosition = currentSnapPosition;
                handler.postDelayed(playRunnable, Constants.PLAY_DELAY);
            }
        }

        void destroy() {
            handler.removeCallbacks(playRunnable);
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

        List<Data> dataList = Constants.populateData();

        scrollListener = new ScrollListener();
        scrollListener.setSnapPosition(0);

        playerAdapter = new MultiplePlayerAdapter(dataList, this, recyclerView);
        playerAdapter.setAutoPlayIndex(0);

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(scrollListener);
        recyclerView.setHasFixedSize(true);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        recyclerView.setAdapter(playerAdapter);

        snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (scrollListener != null) {
            scrollListener.destroy();
        }

        if (playerAdapter != null) {
            updatePlayerList(MediaPlayer::onDestroy);
            playerAdapter.destroy();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        updatePlayerList(MediaPlayer::onStart);
    }

    @Override
    protected void onStop() {
        super.onStop();
        updatePlayerList(MediaPlayer::onStop);
    }

    @Override
    protected void onPause() {
        super.onPause();
        updatePlayerList(MediaPlayer::onPause);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePlayerList(MediaPlayer::onResume);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        updatePlayerList(MediaPlayer::onBackPressed);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        boolean isFullScreen = newConfig.orientation == SCREEN_ORIENTATION_USER;
        textView.setVisibility(isFullScreen ? View.GONE : View.VISIBLE);
        playerAdapter.setFullscreenMode(isFullScreen);
    }

    private void updatePlayerList(Consumer<MediaPlayer> method) {
        for (MediaPlayer player : playerAdapter.getPlayers()) {
            method.accept(player);
        }
    }

    private void play(int snapPosition) {
        MultiplePlayerHolder holder = (MultiplePlayerHolder) recyclerView.findViewHolderForAdapterPosition(snapPosition);
        if (holder != null) {
            holder.play();
        }
    }

    private void pause(int snapPosition) {
        MultiplePlayerHolder holder = (MultiplePlayerHolder) recyclerView.findViewHolderForAdapterPosition(snapPosition);
        if (holder != null && holder.getPlayer() != null && holder.getPlayer().isPauseNeeded()) {
            holder.pause();
        }
    }

    private void updateCurrentData(int snapPosition) {
        MultiplePlayerHolder holder = (MultiplePlayerHolder) recyclerView.findViewHolderForAdapterPosition(snapPosition);
        if (holder != null) {
            holder.updateData();
        }
    }
}

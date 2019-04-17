package com.ooyala.sample;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ooyala.android.util.DebugMode;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

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
    private List<Data> dataList = new ArrayList<>();

    private class ScrollListener extends RecyclerView.OnScrollListener {
        private int snapPosition = RecyclerView.NO_POSITION;

        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                // play the current item
                snapPosition = getCurrentPosition();
                play(snapPosition);
            }
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
                return;
            }

            updateCurrentDataPlayheadTime(snapPosition);
            pause(snapPosition);
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

        playerAdapter = new MultiplePlayerAdapter(dataList, this, recyclerView);
        playerAdapter.setAutoPlayIndex(0);

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        updatePlayerList(MediaPlayer::onDestroy);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onStart() {
        super.onStart();
        updatePlayerList(MediaPlayer::onStart);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onStop() {
        super.onStop();
        updatePlayerList(MediaPlayer::onStop);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onPause() {
        super.onPause();
        updatePlayerList(MediaPlayer::onPause);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onResume() {
        super.onResume();
        updatePlayerList(MediaPlayer::onResume);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        updatePlayerList(MediaPlayer::onBackPressed);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        MediaPlayer player = MediaPlayer.getInstance();
        player.setFullscreenMode(newConfig.orientation == SCREEN_ORIENTATION_USER);
        textView.setVisibility(newConfig.orientation == SCREEN_ORIENTATION_USER ? View.GONE : View.VISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updatePlayerList(Consumer<MediaPlayer> method) {
        for (MediaPlayer player : playerAdapter.getPlayers()) {
            method.accept(player);
        }
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

    private void play(int snapPosition) {
        MultiplePlayerHolder holder = (MultiplePlayerHolder) recyclerView.findViewHolderForAdapterPosition(snapPosition);
        if (holder != null) {
            holder.play();
            DebugMode.logD(TAG, "play snapPosition: " + snapPosition);
        }
    }

    private void pause(int snapPosition) {
        MultiplePlayerHolder holder = (MultiplePlayerHolder) recyclerView.findViewHolderForAdapterPosition(snapPosition);
        if (holder != null && holder.player != null && holder.player.isPlaying()) {
            holder.pause();
            DebugMode.logD(TAG, "pause snapPosition: " + snapPosition);
        }
    }

    private void updateCurrentDataPlayheadTime(int snapPosition) {
        MultiplePlayerHolder holder = (MultiplePlayerHolder) recyclerView.findViewHolderForAdapterPosition(snapPosition);
        if (holder != null) {
            holder.updatePlayheadTime();
        }
    }
}

package com.ooyala.fullscreensampleapp;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.ooyala.android.util.DebugMode;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

public class PlayersActivity extends AppCompatActivity {

    private static final String TAG = PlayersActivity.class.getName();

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerAdapter recyclerAdapter;
    private LinearSnapHelper snapHelper;
    private int snapPosition = RecyclerView.NO_POSITION;
    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                View centerView = snapHelper.findSnapView(layoutManager);
                if (centerView != null) {
                    snapPosition = layoutManager.getPosition(centerView);
                    playItem();
                }
                DebugMode.logD(TAG, "position " + snapPosition);
            } else {
                pause();
                DebugMode.logD(TAG, "scrolling " + snapPosition);
            }
        }
    };

    private List<String> embedCodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players);

        initializeData();

        FrameLayout expandedLayout = findViewById(R.id.empty_view);
        recyclerAdapter = new RecyclerAdapter(embedCodes, expandedLayout, getApplication());

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
            LinearLayoutManager.HORIZONTAL);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.addOnScrollListener(onScrollListener);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setHasFixedSize(true);

        snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
    }

    private void initializeData() {
        embedCodes = new ArrayList<>();
        embedCodes.add("h4aHB1ZDqV7hbmLEv4xSOx3FdUUuephx");
        embedCodes.add("h4aHB1ZDqV7hbmLEv4xSOx3FdUUuephx");
        embedCodes.add("h4aHB1ZDqV7hbmLEv4xSOx3FdUUuephx");
        embedCodes.add("h4aHB1ZDqV7hbmLEv4xSOx3FdUUuephx");
        embedCodes.add("h4aHB1ZDqV7hbmLEv4xSOx3FdUUuephx");
    }

    private void playItem() {
//			recyclerAdapter.
    }

    private void pause() {

    }
}

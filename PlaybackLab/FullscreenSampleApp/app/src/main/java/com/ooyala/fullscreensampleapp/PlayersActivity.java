package com.ooyala.fullscreensampleapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

public class PlayersActivity extends AppCompatActivity {

    private RecyclerAdapter recyclerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players);

        FrameLayout expandedLayout = (FrameLayout) findViewById(R.id.empty_view);
        CustomRecyclerView recyclerView = (CustomRecyclerView) findViewById(R.id.recycler_view);

        List<String> embedCodes = new ArrayList<>();
        embedCodes.add("h4aHB1ZDqV7hbmLEv4xSOx3FdUUuephx");
        embedCodes.add("h4aHB1ZDqV7hbmLEv4xSOx3FdUUuephx");
        embedCodes.add("h4aHB1ZDqV7hbmLEv4xSOx3FdUUuephx");
        embedCodes.add("h4aHB1ZDqV7hbmLEv4xSOx3FdUUuephx");
        embedCodes.add("h4aHB1ZDqV7hbmLEv4xSOx3FdUUuephx");
        recyclerAdapter = new RecyclerAdapter(embedCodes, expandedLayout, getApplication());

        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setHasFixedSize(true);
    }
}

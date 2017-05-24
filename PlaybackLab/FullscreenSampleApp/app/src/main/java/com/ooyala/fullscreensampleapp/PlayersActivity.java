package com.ooyala.fullscreensampleapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;

import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;

import java.util.ArrayList;
import java.util.List;

public class PlayersActivity extends AppCompatActivity {

    private RecyclerAdapter recyclerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        List<String> embedCodes = new ArrayList<>();
        embedCodes.add("JiOTdrdzqAujYa5qvnOxszbrTEuU5HMt");
        embedCodes.add("h4aHB1ZDqV7hbmLEv4xSOx3FdUUuephx");
        embedCodes.add("42cnNsMjE62UDH0JlCssXEPhxlhj1YBN");
        embedCodes.add("E5NWlqMzE6nxrKShm0gR4DzpM49Wl0l9");
        embedCodes.add("JiOTdrdzqAujYa5qvnOxszbrTEuU5HMt");
        recyclerAdapter = new RecyclerAdapter(embedCodes, getApplication());
        recyclerView.setAdapter(recyclerAdapter);
    }
}

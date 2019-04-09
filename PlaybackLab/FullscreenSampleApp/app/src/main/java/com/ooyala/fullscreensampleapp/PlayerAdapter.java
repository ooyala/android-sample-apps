package com.ooyala.fullscreensampleapp;

import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerHolder> {
    private static final String TAG = "PLAYER-5406"; //PlayerAdapter.class.getSimpleName();

    private List<Data> dataList;
    private int autoPlayIndex = 0;

    PlayerAdapter(List<Data> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public PlayerHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new PlayerHolder(viewGroup, false);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerHolder holder, int position) {
        Data data = dataList.get(position);
        if (position == autoPlayIndex) {
            // Play the media on start
            data.setWasPaused(false);
            holder.init(data);
            holder.play(data);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public Data getDataByPosition(int position) {
        return dataList.get(position);
    }

    public void setAutoPlayIndex(int index) {
        autoPlayIndex = index;
    }
}

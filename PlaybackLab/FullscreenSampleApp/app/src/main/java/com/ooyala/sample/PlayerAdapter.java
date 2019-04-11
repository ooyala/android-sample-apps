package com.ooyala.sample;

import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerHolder> {
    private static final String TAG = PlayerAdapter.class.getSimpleName();

    private List<Data> dataList;
    private int autoPlayIndex = 0;

    public PlayerAdapter(List<Data> dataList) {
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
        holder.setData(data);

        if (position == autoPlayIndex) {
            // Play the media on start
            holder.init();
            holder.play();
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setAutoPlayIndex(int index) {
        autoPlayIndex = index;
    }
}

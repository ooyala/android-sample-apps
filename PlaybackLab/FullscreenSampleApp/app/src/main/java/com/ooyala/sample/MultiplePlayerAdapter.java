package com.ooyala.sample;

import android.app.Activity;
import android.view.ViewGroup;

import com.ooyala.android.util.DebugMode;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MultiplePlayerAdapter extends RecyclerView.Adapter<MultiplePlayerHolder> {
	private static final String TAG = MultiplePlayerAdapter.class.getSimpleName();

	private List<Data> dataList;
	private List<MediaPlayer> players = new ArrayList<>();
	private int autoPlayIndex = 0;
	private int holderIndex = 0;

	private Activity activity;
	private RecyclerView recyclerView;

	public MultiplePlayerAdapter(List<Data> dataList, Activity activity, RecyclerView recyclerView) {
		this.dataList = dataList;
		this.activity = activity;
		this.recyclerView = recyclerView;
	}

	@NonNull
	@Override
	public MultiplePlayerHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
		MultiplePlayerHolder holder = new MultiplePlayerHolder(viewGroup, false);

		// The tag is used to demonstrate that the itemView is reused in the recyclerView
		holder.itemView.setTag(holderIndex++);

		// Initialize the player and after that add the player to the array of existing players
		holder.createPlayer(activity, recyclerView);
		players.add(holder.getPlayer());

		return holder;
	}

	@Override
	public void onBindViewHolder(@NonNull MultiplePlayerHolder holder, int position) {
		Data data = dataList.get(position);
		holder.setData(data);
		holder.init();

		DebugMode.logD(TAG, "onBindViewHolder: position = " + position + " " + holder.itemView.getTag());

		if (position == autoPlayIndex) {
			// Play the media on start
			holder.play();
		}
	}

	@Override
	public int getItemCount() {
		return dataList.size();
	}

	public List<MediaPlayer> getPlayers() {
		return players;
	}

	public void destroy() {
		activity = null;
		recyclerView = null;
		players.clear();
	}

	public void setAutoPlayIndex(int index) {
		autoPlayIndex = index;
	}
}

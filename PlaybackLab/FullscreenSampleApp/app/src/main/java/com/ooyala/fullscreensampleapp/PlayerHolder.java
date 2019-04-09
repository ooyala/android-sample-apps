package com.ooyala.fullscreensampleapp;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ooyala.android.skin.OoyalaSkinLayout;
import com.ooyala.android.util.DebugMode;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayerHolder extends RecyclerView.ViewHolder {
	private static final String TAG = PlayerHolder.class.getSimpleName();

	private OoyalaSkinLayout playerLayout;

	@BindView(R.id.parent_layout)
	FrameLayout parentLayout;

	PlayerHolder(ViewGroup viewGroup, boolean attachToRoot) {
		super(LayoutInflater.from(viewGroup.getContext())
				.inflate(R.layout.recyclerview_item_row, viewGroup, attachToRoot));
		ButterKnife.bind(this, itemView);
	}

	public void init(Data data) {
		MediaPlayer player = MediaPlayer.getInstance();
		initPlayerLayout();
		player.init(playerLayout, data);
		player.seekTo(data.getPlayedHeadTime());
	}

	public void play(Data data) {
		MediaPlayer player = MediaPlayer.getInstance();
		player.play(data.getPlayedHeadTime());
	}

	public void pause() {
		MediaPlayer player = MediaPlayer.getInstance();
		player.pause();
	}

	private void initPlayerLayout() {
		playerLayout = MediaPlayer.getInstance().getPlayerLayout();
		if (playerLayout != null) {
			DebugMode.logD(TAG, "Player layout was added to the parent");
			parentLayout.addView(playerLayout, 1);
		}
	}
}

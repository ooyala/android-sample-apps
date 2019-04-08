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
	private static final String TAG = "PLAYER-5406"; //PlayerHolder.class.getSimpleName();

	private OoyalaSkinLayout playerLayout;

	@BindView(R.id.parent_layout)
	FrameLayout parentLayout;

	PlayerHolder(ViewGroup viewGroup, boolean attachToRoot) {
		super(LayoutInflater.from(viewGroup.getContext())
				.inflate(R.layout.recyclerview_item_row, viewGroup, attachToRoot));
		ButterKnife.bind(this, itemView);

//            playerLayout = MediaPlayer.getInstance().getPlayerLayout();
//            playerLayout = itemView.findViewById(R.id.ooyala_skin_layout);
//            playerLayout.setOnSystemUiVisibilityChangeListener(visibility -> {
//                boolean isFullScreenMode = playerLayout.isFullscreen();
//                if (isFullScreenMode) {
//                    recyclerFullScreenHelper.expandPlayerLayout(playerLayout);
//                } else {
//                    recyclerFullScreenHelper.collapsePlayerLayout();
//                }
//            });
	}

	public void init(Data data) {
		MediaPlayer player = MediaPlayer.getInstance();
		initPlayerLayout();
		player.init(playerLayout, data);
	}

	public void play(Data data) {
		MediaPlayer player = MediaPlayer.getInstance();
		player.play(data.getPlayedHeadTime());
	}

	public void pause(Data data) {
		MediaPlayer player = MediaPlayer.getInstance();
		data.setPlayedHeadTime(player.getPlayheadTime());
		player.pause();
	}

	private void initPlayerLayout() {
		playerLayout = MediaPlayer.getInstance().getPlayerLayout();
		if (playerLayout != null) {
			DebugMode.logD("PLAYER-5406", "initPlayerLayout(): addView");
			parentLayout.addView(playerLayout, 1);
		}
	}
}

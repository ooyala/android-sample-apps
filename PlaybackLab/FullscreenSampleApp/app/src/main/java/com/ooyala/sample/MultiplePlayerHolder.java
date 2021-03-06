package com.ooyala.sample;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.skin.OoyalaSkinLayout;

public class MultiplePlayerHolder extends RecyclerView.ViewHolder {
	private static final String TAG = MultiplePlayerHolder.class.getSimpleName();

	@BindView(R.id.parent_layout)
	FrameLayout parentLayout;

	private MediaPlayer player;
	private OoyalaSkinLayout playerLayout;
	private Data data;

	MultiplePlayerHolder(ViewGroup viewGroup, boolean attachToRoot) {
		super(LayoutInflater.from(viewGroup.getContext())
				.inflate(R.layout.recyclerview_item_row, viewGroup, attachToRoot));
		ButterKnife.bind(this, itemView);
	}

	public void setData(Data data) {
		this.data = data;
	}

	public void createPlayer(Activity activity, RecyclerView recyclerView) {
		player = new MediaPlayer();
		player.setActivity(activity);
		player.setRecyclerView(recyclerView);
		playerLayout = player.getPlayerLayout();
		parentLayout.addView(playerLayout, 1);
	}

	public void init() {
		player.init(playerLayout, data);
		player.seekTo(data.getPlayedHeadTime());
		player.setFullscreenMode(MultiplePlayerAdapter.isFullscreenMode);
	}

	public void play() {
		if (data.isAutoPaused()) {
			data.setAutoPaused(false);
			player.play(data.getPlayedHeadTime());
		}
	}

	public void pause() {
		data.setAutoPaused(true);
		player.pause();
	}

    public void updateData() {
		data.setPlayedHeadTime(player.getPlayheadTime());

        if (player.getState() == OoyalaPlayer.State.COMPLETED) {
            data.setAutoPaused(true);
        }
	}

	public MediaPlayer getPlayer() {
		return player;
	}
}

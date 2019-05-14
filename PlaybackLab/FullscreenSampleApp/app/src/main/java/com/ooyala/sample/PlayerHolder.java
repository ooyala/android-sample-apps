package com.ooyala.sample;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.skin.OoyalaSkinLayout;
import com.ooyala.android.util.DebugMode;

public class PlayerHolder extends RecyclerView.ViewHolder {
	private static final String TAG = PlayerHolder.class.getSimpleName();

	private OoyalaSkinLayout playerLayout;
	private Data data;

	@BindView(R.id.parent_layout)
	FrameLayout parentLayout;

	PlayerHolder(ViewGroup viewGroup, boolean attachToRoot) {
		super(LayoutInflater.from(viewGroup.getContext())
				.inflate(R.layout.recyclerview_item_row, viewGroup, attachToRoot));
		ButterKnife.bind(this, itemView);
	}

	public void setData(Data data) {
		this.data = data;
	}

	public void init() {
		MediaPlayer player = MediaPlayer.getInstance();
		initPlayerLayout();
		player.init(playerLayout, data);
		player.seekTo(data.getPlayedHeadTime());
	}

	public void play() {
		if (data.isAutoPaused()) {
			data.setAutoPaused(false);

			MediaPlayer player = MediaPlayer.getInstance();
			player.play(data.getPlayedHeadTime());
		}
	}

	public void pause() {
		data.setAutoPaused(true);

		MediaPlayer player = MediaPlayer.getInstance();
		player.pause();
	}

    public void updateData() {
		MediaPlayer player = MediaPlayer.getInstance();
		data.setPlayedHeadTime(player.getPlayheadTime());

        if (player.getState() == OoyalaPlayer.State.COMPLETED) {
            data.setAutoPaused(true);
        }
	}

	private void initPlayerLayout() {
		playerLayout = MediaPlayer.getInstance().getPlayerLayout();
		if (playerLayout != null) {
			DebugMode.logD(TAG, "Player layout was added to the parent");
			parentLayout.addView(playerLayout, 1);
		}
	}
}

package com.ooyala.fullscreensampleapp;

import android.app.Application;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.skin.OoyalaSkinLayout;
import com.ooyala.android.skin.OoyalaSkinLayoutController;
import com.ooyala.android.skin.configuration.SkinOptions;
import com.ooyala.android.skin.util.RecyclerViewFullScreenManager;

import java.util.List;

/**
 * TODO: Add brief for RecyclerAdapter.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.PlayerHolder> {

    private Application app;
    private List<String> embedCodes;
    private RecyclerViewFullScreenManager recyclerFullScreenHelper;

    public RecyclerAdapter(List<String> embedCodes, FrameLayout expandedLayout, Application app) {
        this.embedCodes= embedCodes;
        this.app = app;

        recyclerFullScreenHelper = new RecyclerViewFullScreenManager(expandedLayout);
    }

    @Override
    public PlayerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_row, parent, false);
        return new PlayerHolder(view);
    }

    @Override
    public void onBindViewHolder(PlayerHolder holder, int position) {
        String embedCode = embedCodes.get(position);
        holder.bindPlayer(embedCode, app);
    }

    @Override
    public int getItemCount() {
        return embedCodes.size();
    }

    public class PlayerHolder extends RecyclerView.ViewHolder {
        private OoyalaSkinLayout playerLayout;
        private OoyalaSkinLayoutController playerController;

        public PlayerHolder(View itemView) {
            super(itemView);

            playerLayout = (OoyalaSkinLayout) itemView.findViewById(R.id.ooyalaSkinLayout);
            playerLayout.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    boolean isFullScreenMode = playerLayout.isFullscreen();
                    if (isFullScreenMode) {
                        recyclerFullScreenHelper.expandPlayerLayout(playerLayout);
                    } else {
                        recyclerFullScreenHelper.collapsePlayerLayout();
                    }
                }
            });
        }

        public void bindPlayer(String embedCode, Application app) {

            OoyalaPlayer player = new OoyalaPlayer("c0cTkxOqALQviQIGAHWY5hP0q9gU", new PlayerDomain("http://www.ooyala.com/"));

            SkinOptions options = new SkinOptions.Builder().build();
            playerController = new OoyalaSkinLayoutController(app, playerLayout, player, options);
            player.setEmbedCode(embedCode);
        }
    }
}

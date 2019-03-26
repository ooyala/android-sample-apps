package com.skin.ooyalaskinsampleapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.ooyala.android.skin.OoyalaSkinLayout;
import com.skin.ooyalaskinsampleapplication.ooyala.MultiMediaPlayListener;
import com.skin.ooyalaskinsampleapplication.ooyala.OoyalaPlayerManager;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TemplateVideoModal extends RecyclerView.ViewHolder implements MultiMediaPlayListener, View.OnClickListener {


    @BindView(R.id.framelayout_player_parent)
    FrameLayout frameLayoutPlayerParent;
    //@Bind(R.id.ooyala_player_skin)
    OoyalaSkinLayout ooyalaSkinLayout;
    private String mEmbedCode;

    @BindView(R.id.layout_parent_rl)
    FrameLayout mLinearLayoutParent;

    @Nullable
    @BindView(R.id.image_view_background)
    ImageView mImageViewBackground;

    private MediaClickListener mMediaClickListener;
    private int mPosition;
    private PlayerSelectionOption mDoc;

    public TemplateVideoModal(ViewGroup viewGroup, MediaClickListener mediaClickListener, boolean attachToRoot) {

        super(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.dialog_fullscreen_video, viewGroup, attachToRoot));
        ButterKnife.bind(this, itemView);
        this.mMediaClickListener = mediaClickListener;
    }

    private void getOoyalaPlayerSkinLayout() {
        ooyalaSkinLayout = OoyalaPlayerManager.getInstance().getModelOoyalaSkinLayout();
        if (ooyalaSkinLayout != null) {
            frameLayoutPlayerParent.addView(ooyalaSkinLayout, 1);
        }
    }

    public void updateData(int position, PlayerSelectionOption doc) {
        try {

            this.mPosition = position;
            this.mDoc = doc;
            updateVideoEvents();
        } catch (Exception ex) {

        }

    }

    private void updateVideoEvents() {

        mEmbedCode = mDoc.getEmbedCode();

        if (mDoc.getState().startAutoPlaying && !mDoc.getState().videoCompleted) {
            //
            getOoyalaPlayerSkinLayout();
            if (mMediaClickListener != null) {
                itemView.post(new Runnable() {
                    @Override
                    public void run() {
                        mMediaClickListener.onMediaPlay(ooyalaSkinLayout, mDoc, TemplateVideoModal.this, mEmbedCode);
                    }
                });
            }

        } else {
        }

    }

    @Override
    public void startedPlaying() {
        mDoc.getState().isPaused = false;
        OoyalaPlayerManager.getInstance().mutePlayer(false);
        mImageViewBackground.setVisibility(View.GONE);
    }

    @Override
    public void pausedPlaying() {

        mDoc.getState().isPaused = true;
    }

    @Override
    public void stoppedPlaying() {
    }

    @Override
    public void completedPlaying() {
        mMediaClickListener.onMediaComplete(mPosition + 1);
    }

    @Override
    public void headTimeChanged(long playHeadTime, long totalDuration) {

    }

    @Override
    public void completedMilestone(VideoMilestone videoMilestone) {

    }

    @Override
    public void seekStarted() {
        OoyalaPlayerManager.getInstance().mutePlayer(false);
    }

    @Override
    public void seekCompleted() {
        OoyalaPlayerManager.getInstance().mutePlayer(false);
        mImageViewBackground.setVisibility(View.GONE);
    }

    @Override
    public void adStarted() {
        OoyalaPlayerManager.getInstance().mutePlayer(false);
        mImageViewBackground.setVisibility(View.GONE);
    }

    @Override
    public void adCompleted() {
        mImageViewBackground.setVisibility(View.VISIBLE);
    }

    @Override
    public void adSkipped() {

    }

    @Override
    public void errorOccured(String message) {
        mImageViewBackground.setVisibility(View.VISIBLE);
    }


    @Override
    public void onClick(View v) {


        if (!mDoc.getState().videoCompleted) {
            //
            getOoyalaPlayerSkinLayout();
            if (mMediaClickListener != null) {
                itemView.post(new Runnable() {
                    @Override
                    public void run() {

                        mMediaClickListener.onMediaPlayCLicked(mPosition);
                        mMediaClickListener.onMediaPlay(ooyalaSkinLayout, mDoc, TemplateVideoModal.this, mEmbedCode);
                    }


                });
            }

        } else {
        }

    }
}
package com.ooyala.fullscreensampleapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Application;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.skin.OoyalaSkinLayout;
import com.ooyala.android.skin.OoyalaSkinLayoutController;
import com.ooyala.android.skin.configuration.SkinOptions;

import java.util.List;

/**
 * TODO: Add brief for RecyclerAdapter.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.PlayerHolder> {

    private Application app;
    private List<String> embedCodes;
    private FrameLayout expandedLayout;
    private Animator currentAnimator;
    private FrameLayout currentParentLayout;
    private OoyalaSkinLayout currentPlayerLayout;

    public RecyclerAdapter(List<String> embedCodes, FrameLayout expandedLayout, Application app) {
        this.embedCodes= embedCodes;
        this.expandedLayout = expandedLayout;
        this.app = app;
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
        private static final int ANIMATION_DURATION = 300;
        private TextView textView;
        private OoyalaSkinLayout playerLayout;
        private OoyalaSkinLayoutController playerController;

        public PlayerHolder(View itemView) {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.textView);
            playerLayout = (OoyalaSkinLayout) itemView.findViewById(R.id.ooyalaSkinLayout);
            playerLayout.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    boolean isFullScreenMode = playerLayout.isFullscreen();
                    if (isFullScreenMode) {
                        expandPlayerLayout();
                    } else {
                        collapsePlayerLayout();
                    }
                }
            });
        }

        public void bindPlayer(String embedCode, Application app) {
            textView.setText("Sample text");

            OoyalaPlayer player = new OoyalaPlayer("c0cTkxOqALQviQIGAHWY5hP0q9gU", new PlayerDomain("http://www.ooyala.com/"));

            SkinOptions options = new SkinOptions.Builder().build();
            playerController = new OoyalaSkinLayoutController(app, playerLayout, player, options);
            player.setEmbedCode(embedCode);
        }

        private void collapsePlayerLayout() {
            if (expandedLayout.getVisibility() != View.VISIBLE || currentAnimator != null) {
                return;
            }

            final Point globalOffset = new Point();
            final Rect startBounds = new Rect();
            final Rect finalBounds = new Rect();

            setFinalBounds(finalBounds, globalOffset);

            expandedLayout.getGlobalVisibleRect(startBounds);
            expandedLayout.getGlobalVisibleRect(finalBounds, globalOffset);
            startBounds.offset(-globalOffset.x, -globalOffset.y);
            final float startScaleFinal = getStartScale(startBounds, finalBounds);

            AnimatorSet set = new AnimatorSet();
            set
                    .play(ObjectAnimator.ofFloat(expandedLayout, View.X, startBounds.left))
                    .with(ObjectAnimator.ofFloat(expandedLayout, View.Y, startBounds.top))
                    .with(ObjectAnimator
                            .ofFloat(expandedLayout, View.SCALE_X, startScaleFinal))
                    .with(ObjectAnimator
                            .ofFloat(expandedLayout, View.SCALE_Y, startScaleFinal));
            set.setDuration(ANIMATION_DURATION);
            set.setInterpolator(new DecelerateInterpolator());
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    cancelAnimation();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    cancelAnimation();
                }
            });
            set.start();
            currentAnimator = set;

        }

        private void expandPlayerLayout() {
            if (expandedLayout.getVisibility() != View.INVISIBLE) {
                return;
            }

            if (currentAnimator != null) {
                currentAnimator.cancel();
            }

            currentPlayerLayout = playerLayout;
            currentParentLayout = (FrameLayout) playerLayout.getParent();
            currentParentLayout.removeView(playerLayout);
            expandedLayout.addView(playerLayout);

            final Point globalOffset = new Point();
            final Rect startBounds = new Rect();
            final Rect finalBounds = new Rect();

            setFinalBounds(finalBounds, globalOffset);

            expandedLayout.getGlobalVisibleRect(startBounds);
            expandedLayout.getGlobalVisibleRect(finalBounds, globalOffset);
            startBounds.offset(-globalOffset.x, -globalOffset.y);

            final float startScale = getStartScale(startBounds, finalBounds);
            setStartBounds(startBounds, finalBounds, startScale);

            expandedLayout.setVisibility(View.VISIBLE);
            expandedLayout.setPivotX(0f);
            expandedLayout.setPivotY(0f);

            AnimatorSet set = new AnimatorSet();
            set
                    .play(ObjectAnimator.ofFloat(expandedLayout, View.X, startBounds.left,
                            finalBounds.left))
                    .with(ObjectAnimator.ofFloat(expandedLayout, View.Y, startBounds.top,
                            finalBounds.top))
                    .with(ObjectAnimator.ofFloat(expandedLayout, View.SCALE_X, startScale, 1f))
                    .with(ObjectAnimator.ofFloat(expandedLayout, View.SCALE_Y, startScale, 1f));
            set.setDuration(ANIMATION_DURATION);
            set.setInterpolator(new DecelerateInterpolator());
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    currentAnimator = null;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    currentAnimator = null;
                }
            });
            set.start();
            currentAnimator = set;
        }

        private void setStartBounds(final Rect startBounds, final Rect finalBounds, final float startScale) {
            if (isExtendedHorizontally(startBounds, finalBounds)) {
                // Extend start bounds horizontally
                float startWidth = startScale * finalBounds.width();
                float deltaWidth = (startWidth - startBounds.width()) / 2;
                startBounds.left -= deltaWidth;
                startBounds.right += deltaWidth;
            } else {
                // Extend start bounds vertically
                float startHeight = startScale * finalBounds.height();
                float deltaHeight = (startHeight - startBounds.height()) / 2;
                startBounds.top -= deltaHeight;
                startBounds.bottom += deltaHeight;
            }
        }

        private void setFinalBounds(final Rect finalBounds, final Point globalOffset) {
            expandedLayout.getGlobalVisibleRect(finalBounds, globalOffset);
            finalBounds.offset(-globalOffset.x, -globalOffset.y);
        }

        private float getStartScale(final Rect startBounds, final Rect finalBounds) {
            float startScale;
            if (isExtendedHorizontally(startBounds, finalBounds)) {
                startScale = (float) startBounds.height() / finalBounds.height();
            } else {
                startScale = (float) startBounds.width() / finalBounds.width();
            }
            return startScale;
        }

        private boolean isExtendedHorizontally(final Rect startBounds, final Rect finalBounds) {
            float finalAspectRatio = (float) finalBounds.width() / finalBounds.height();
            float startAspectRatio = (float) startBounds.width() / startBounds.height();

            return finalAspectRatio > startAspectRatio;
        }

        private void cancelAnimation() {
            FrameLayout parent = (FrameLayout) currentPlayerLayout.getParent();
            if (currentParentLayout != null && parent == expandedLayout) {
                expandedLayout.removeAllViews();
                expandedLayout.setVisibility(View.INVISIBLE);

                currentParentLayout.addView(currentPlayerLayout);

                currentPlayerLayout.setVisibility(View.VISIBLE);
                currentPlayerLayout.setAlpha(1f);
                currentAnimator = null;
            }
        }
    }
}

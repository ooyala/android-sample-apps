package com.ooyala.fullscreensampleapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.ooyala.android.skin.OoyalaSkinLayout;

/**
 * FullScreenHelper is a class developed to support a full screen mode with RecyclerView
 */

public class FullScreenHelper {
    private static final int ANIMATION_DURATION = 300;

    private FrameLayout expandedLayout;
    private FrameLayout currentParentLayout;
    private Animator currentAnimator;
    private OoyalaSkinLayout currentPlayerLayout;

    public FullScreenHelper(FrameLayout expandedLayout) {
        this.expandedLayout = expandedLayout;
    }

    public void collapsePlayerLayout() {
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

    public void expandPlayerLayout(OoyalaSkinLayout playerLayout) {
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

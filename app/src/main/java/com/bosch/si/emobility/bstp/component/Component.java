package com.bosch.si.emobility.bstp.component;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;

import com.bosch.si.emobility.bstp.activity.MapsActivity;

/**
 * Created by sgp0458 on 16/12/15.
 */
public abstract class Component implements IComponent {

    private static final long DURATION = 100;
    protected MapsActivity activity;

    protected ViewGroup layout;

    public Component() {

    }

    @Override
    public void setActivity(MapsActivity activity) {
        this.activity = activity;
    }

    @Override
    public void toggleView() {
        if (isShown()) {
            setEnabled(false, false);
        } else {
            setEnabled(true, false);
        }
    }

    protected ViewPropertyAnimator slideUp(final boolean enabled) {
        return layout.animate().translationY(enabled ? 0 : -layout.getHeight())
                .setDuration(DURATION)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        layout.setVisibility(enabled ? View.GONE : View.VISIBLE);
                        layout.setAlpha(enabled ? 0.0f : 1.0f);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        layout.setVisibility(enabled ? View.VISIBLE : View.GONE);
                        layout.setAlpha(enabled ? 1.0f : 0.0f);
                    }
                });
    }

    protected ViewPropertyAnimator slideDown(final boolean enabled) {
        return layout.animate().translationY(enabled ? 0 : layout.getHeight())
                .setDuration(DURATION)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        layout.setVisibility(enabled ? View.GONE : View.VISIBLE);
                        layout.setAlpha(enabled ? 0.0f : 1.0f);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        layout.setVisibility(enabled ? View.VISIBLE : View.GONE);
                        layout.setAlpha(enabled ? 1.0f : 0.0f);
                    }
                });
    }

    protected ViewPropertyAnimator slideLeft(final boolean enabled) {
        return layout.animate().translationX(enabled ? 0 : layout.getWidth())
                .setDuration(DURATION)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        layout.setVisibility(enabled ? View.GONE : View.VISIBLE);
                        layout.setAlpha(enabled ? 0.0f : 1.0f);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        layout.setVisibility(enabled ? View.VISIBLE : View.GONE);
                        layout.setAlpha(enabled ? 1.0f : 0.0f);
                    }
                });
    }

    protected ViewPropertyAnimator slideRight(final boolean enabled) {
        return layout.animate().translationX(enabled ? 0 : -layout.getWidth())
                .setDuration(DURATION)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        layout.setVisibility(enabled ? View.GONE : View.VISIBLE);
                        layout.setAlpha(enabled ? 0.0f : 1.0f);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        layout.setVisibility(enabled ? View.VISIBLE : View.GONE);
                        layout.setAlpha(enabled ? 1.0f : 0.0f);
                    }
                });
    }

    @Override
    public void setEnabled(boolean enabled, boolean noAnimation) {
        layout.setEnabled(enabled);
        if (!noAnimation && isSlideUp()) {
            slideUp(enabled);
        } else if (!noAnimation && isSlidDown()) {
            slideDown(enabled);
        } else if (!noAnimation && isSlideLeft()) {
            slideLeft(enabled);
        } else if (!noAnimation && isSlideRight()) {
            slideRight(enabled);
        } else {
            layout.setVisibility(enabled ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public boolean isShown() {
        return layout.getVisibility() == View.VISIBLE;
    }


    protected boolean isSlideUp() {
        return false;
    }

    protected boolean isSlidDown() {
        return false;
    }

    protected boolean isSlideLeft() {
        return false;
    }

    protected boolean isSlideRight() {
        return false;
    }
}

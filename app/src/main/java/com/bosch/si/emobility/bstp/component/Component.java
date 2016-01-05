package com.bosch.si.emobility.bstp.component;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.bosch.si.emobility.bstp.R;
import com.bosch.si.emobility.bstp.activity.Activity;
import com.bosch.si.emobility.bstp.activity.MapsActivity;

/**
 * Created by sgp0458 on 16/12/15.
 */
public abstract class Component implements IComponent {

    private static final long DURATION = 100;
    protected Activity activity;

    @Override
    public Activity getActivity() {
        return activity;
    }

    @Override
    public Resources getResources() {
        return getActivity().getResources();
    }

    @Override
    public View findViewById(int id) {
        return getActivity().findViewById(id);
    }

    protected ViewGroup layout;

    public Component(Activity activity) {
        setActivity(activity);
    }

    @Override
    public void setActivity(Activity activity) {
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

    protected void slideUp(final boolean enabled) {
        layout.animate().translationY(enabled ? 0 : -layout.getHeight())
                .setDuration(DURATION)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        layout.setVisibility(enabled ? View.VISIBLE : View.INVISIBLE);
                        layout.setAlpha(enabled ? 1.0f : 0.0f);
                    }
                });
    }

    protected void slideDown(final boolean enabled) {
        layout.animate().translationY(enabled ? 0 : layout.getHeight())
                .setDuration(DURATION)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        layout.setVisibility(enabled ? View.VISIBLE : View.INVISIBLE);
                        layout.setAlpha(enabled ? 1.0f : 0.0f);
                    }
                });
    }

    protected void slideLeft(final boolean enabled) {
        layout.animate().translationX(enabled ? 0 : layout.getWidth())
                .setDuration(DURATION)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        layout.setVisibility(enabled ? View.VISIBLE : View.INVISIBLE);
                        layout.setAlpha(enabled ? 1.0f : 0.0f);
                    }
                });
    }

    protected void slideRight(final boolean enabled) {
        layout.animate().translationX(enabled ? 0 : -layout.getWidth())
                .setDuration(DURATION)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        layout.setVisibility(enabled ? View.VISIBLE : View.INVISIBLE);
                        layout.setAlpha(enabled ? 1.0f : 0.0f);
                    }
                });
    }

    protected void fade(final boolean enabled) {
        Animation fadeAnimation = null;
        if (enabled)
            fadeAnimation = AnimationUtils.loadAnimation(this.activity, R.anim.fade_in);
        else
            fadeAnimation = AnimationUtils.loadAnimation(this.activity, R.anim.fade_out);
        fadeAnimation.setDuration(DURATION);
        fadeAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                layout.setVisibility(enabled ? View.VISIBLE : View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        layout.startAnimation(fadeAnimation);
    }

    @Override
    public void setEnabled(boolean enabled, boolean noAnimation) {
        layout.setEnabled(enabled);
        layout.setVisibility(enabled ? View.VISIBLE : View.INVISIBLE);
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

    protected boolean isFade() {
        return false;
    }

}

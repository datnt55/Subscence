package subscene.datnt.com.subscene.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.Interpolator;

public class AnimUtils {

    public static final int DEFAULT_DURATION = -1;
    public static final int NO_DELAY = 0;
    public static final Interpolator EASE_IN = PathInterpolatorCompat.create(0.0f, 0.0f, 0.2f, 1.0f);
    public static final Interpolator EASE_OUT = PathInterpolatorCompat.create(0.4f, 0.0f, 1.0f, 1.0f);
    public static final Interpolator EASE_OUT_EASE_IN = PathInterpolatorCompat.create(0.4f, 0, 0.2f, 1);

    public static class AnimationCallback {
        public void onAnimationEnd() {
            // Do nothing
        }

        public void onAnimationCancel() {
            // Do nothing
        }
    }

    public static void crossFadeViews(View fadeIn, View fadeOut, int duration) {
        fadeIn(fadeIn, duration);
        fadeOut(fadeOut, duration);
    }

    public static void fadeOut(View fadeOut, int duration) {
        fadeOut(fadeOut, duration, null);
    }

    public static void fadeOut(final View fadeOut, int durationMs, final AnimationCallback callback) {
        fadeOut.setAlpha(1);
        final ViewPropertyAnimator animator = fadeOut.animate();
        animator.cancel();
        animator.alpha(0).withLayer().setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                fadeOut.setVisibility(View.GONE);
                if (callback != null) {
                    callback.onAnimationEnd();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                fadeOut.setVisibility(View.GONE);
                fadeOut.setAlpha(0);
                if (callback != null) {
                    callback.onAnimationCancel();
                }
            }
        });
        if (durationMs != DEFAULT_DURATION) {
            animator.setDuration(durationMs);
        }
        animator.start();
    }

    public static void fadeIn(View fadeIn, int durationMs) {
        fadeIn(fadeIn, durationMs, NO_DELAY, null);
    }

    public static void fadeIn(final View fadeIn, int durationMs, int delay, final AnimationCallback callback) {
        fadeIn.setAlpha(0);
        final ViewPropertyAnimator animator = fadeIn.animate();
        animator.cancel();
        animator.setStartDelay(delay);
        animator.alpha(1).withLayer().setListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                fadeIn.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                fadeIn.setAlpha(1);
                if (callback != null) {
                    callback.onAnimationCancel();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (callback != null) {
                    callback.onAnimationEnd();
                }
            }
        });
        if (durationMs != DEFAULT_DURATION) {
            animator.setDuration(durationMs);
        }
        animator.start();
    }
}

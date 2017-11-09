package com.wasder.wasder;

import android.animation.Animator;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;

import co.wasder.wasder.R;

/**
 * Created by Ahmed AlAskalany on 11/9/2017.
 * Navigator
 */

public class NavFragmentUtils {

    public static boolean handleNavigationDrawer(DrawerLayout mDrawerLayout, @NonNull MenuItem
            item) {
        // Handle navigation view item clicks here.
        final int id = item.getItemId();

        if (id == R.id.nav_profile) {
            // Handle the camera action
        } else if (id == R.id.nav_friends) {

        } else if (id == R.id.nav_followers) {

        } else if (id == R.id.nav_achievements) {

        } else if (id == R.id.nav_settings_account) {

        } else if (id == R.id.nav_settings_notifications) {

        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void AnimateAppBarColor(final View view, final Animator.AnimatorListener listener) {
        final int cx = view.getWidth() / 2;
        final int cy = view.getHeight() / 2;
        final float finalRadius = Math.max(view.getWidth(), view.getHeight());
        final Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.setDuration(1000);
        view.setVisibility(View.VISIBLE);
        anim.addListener(listener);
        anim.start();
    }
}

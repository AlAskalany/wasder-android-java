package co.wasder.wasder.ui.fragment.navigation;

import android.animation.Animator;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;

import co.wasder.wasder.R;
import co.wasder.wasder.ui.base.BaseTabFragment;

/** Created by Ahmed AlAskalany on 11/13/2017. Navigator */
abstract class BaseNavigationFragment extends Fragment
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String ARG_SECTION_NUMBER = "param1";

    @SuppressWarnings("unused")
    public View appBarLayout;

    @NonNull
    @SuppressWarnings("unused")
    public Animator.AnimatorListener mAnimationListener =
            new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(final Animator animation) {}

                @Override
                public void onAnimationEnd(final Animator animation) {
                    appBarLayout.setBackgroundColor(Color.RED);
                }

                @Override
                public void onAnimationCancel(final Animator animation) {}

                @Override
                public void onAnimationRepeat(final Animator animation) {}
            };

    @SuppressWarnings("unused")
    public DrawerLayout mDrawerLayout;

    public static void showSearchBar(@NonNull FragmentActivity activity) {
        final View view = activity.findViewById(R.id.searchView);
        view.setVisibility(View.VISIBLE);
        final TabLayout tabLayout = activity.findViewById(R.id.tabLayout);
        tabLayout.removeAllTabs();
        // tabLayout.setVisibility(View.GONE);
    }

    @NonNull
    @SuppressWarnings("unused")
    public static Runnable createRunnable(
            @NonNull final View appbar, final Animator.AnimatorListener animatorListener) {
        return new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                appbar.setVisibility(View.INVISIBLE);
                appbar.setBackgroundColor(Color.RED);
                AnimateAppBarColor(appbar, animatorListener);
            }
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void AnimateAppBarColor(
            @NonNull final View view, final Animator.AnimatorListener listener) {
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

    public abstract void addTab(BaseTabFragment tab);

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        // inflater.inflate(R.menu.menu_main, menu);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        // Handle navigation view item clicks here.
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
}

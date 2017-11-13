package co.wasder.wasder.fragment.navigation;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.ArrayList;
import java.util.Collection;

import co.wasder.wasder.R;
import co.wasder.wasder.base.BaseTabFragment;
import co.wasder.wasder.listener.OnFragmentInteractionListener;

/**
 * Created by Ahmed AlAskalany on 11/13/2017.
 * Navigator
 */

abstract class BaseNavigationFragment extends Fragment implements NavigationView
        .OnNavigationItemSelectedListener {


    public static final String ARG_SECTION_NUMBER = "param1";
    public final Collection<BaseTabFragment> fragments = new ArrayList<>();
    @Nullable
    public String TAG;
    public ViewPager viewPager;
    @SuppressWarnings("unused")
    public View appBarLayout;
    // TODO: Rename and change types of parameters
    public int mSectionNumber;
    @Nullable
    public OnFragmentInteractionListener mListener;
    @NonNull
    @SuppressWarnings("unused")
    public Animator.AnimatorListener mAnimationListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(final Animator animation) {

        }

        @Override
        public void onAnimationEnd(final Animator animation) {
            appBarLayout.setBackgroundColor(Color.RED);
        }

        @Override
        public void onAnimationCancel(final Animator animation) {

        }

        @Override
        public void onAnimationRepeat(final Animator animation) {

        }
    };
    @SuppressWarnings("unused")
    public DrawerLayout mDrawerLayout;

    public static void showSearchBar(@NonNull FragmentActivity activity) {
        final View view = activity.findViewById(R.id.searchView);
        view.setVisibility(View.VISIBLE);
        final TabLayout tabLayout = activity.findViewById(R.id.tabLayout);
        tabLayout.removeAllTabs();
        //tabLayout.setVisibility(View.GONE);
    }

    @NonNull
    @SuppressWarnings("unused")
    public static Runnable createRunnable(@NonNull final View appbar, final Animator
            .AnimatorListener animatorListener) {
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
    public static void AnimateAppBarColor(@NonNull final View view, final Animator
            .AnimatorListener listener) {
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
        //inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public void setUserVisibleHint(final boolean isVisibleToUser) {
        if (isVisibleToUser) {
            Log.d(TAG, "setUserVisibleHint: " + this.mSectionNumber);
            final FragmentActivity activity = getActivity();
            if (activity != null) {
                if (mSectionNumber == 0) {
                    showSearchBar(activity);
                } else {
                    hideSearchBar(activity);
                }
            }
        }

    }

    private void hideSearchBar(@NonNull FragmentActivity activity) {
        final TabLayout tabLayout = activity.findViewById(R.id.tabLayout);
        if (tabLayout.getVisibility() != View.VISIBLE) {
            tabLayout.setVisibility(View.VISIBLE);
        }
        final View view = activity.findViewById(R.id.searchView);
        view.setVisibility(View.GONE);
        tabLayout.setupWithViewPager(this.viewPager);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        final TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter(getChildFragmentManager());
        for (final BaseTabFragment tab : fragments) {
            tabsPagerAdapter.addFragment(tab);
        }

        viewPager = view.findViewById(R.id.fragment_navigation_viewPager);
        viewPager.setAdapter(tabsPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int
                    positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                Log.d(TAG, "onPageSelected: Tab" + position);
            }

            @Override
            public void onPageScrollStateChanged(final int state) {

            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    @SuppressWarnings("unused")
    public void onButtonPressed(final Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteractionListener(uri);
        }
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        Log.d(TAG, "Navigation Fragment onAttach: " + mSectionNumber);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement " +
                    "OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "Navigation Fragment onDetach: " + mSectionNumber);
        mListener = null;
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
        Log.d(TAG, "Navigation Fragment onDestroyView: " + mSectionNumber);
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

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        Log.d(TAG, "Navigation Fragment onCreateView: " + mSectionNumber);
        return inflater.inflate(R.layout.fragment_navigation, container, false);
    }
}
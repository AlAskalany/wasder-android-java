package co.wasder.ui.navigation.live;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
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

import butterknife.OnClick;
import co.wasder.ui.navigation.NavigationFragment;
import co.wasder.ui.navigation.home.FeedNavigationFragment;
import co.wasder.ui.navigation.live.tabs.discovery.DiscoveryTabFragment;
import co.wasder.ui.navigation.live.tabs.favorites.FavoritesTabFragment;
import co.wasder.ui.navigation.live.tabs.following.FollowingTabFragment;
import co.wasder.ui.navigation.tab.BaseTabFragment;
import co.wasder.ui.navigation.tab.OnFragmentInteractionListener;
import co.wasder.wasder.R;
import co.wasder.wasder.ui.WasderActivity;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LiveNavigationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@Keep
public class LiveNavigationFragment extends Fragment implements NavigationFragment,
        NavigationView.OnNavigationItemSelectedListener {

    public static final String ARG_TAG = "tag";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
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

    public LiveNavigationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * * @param sectionNumber Section number.
     *
     * @return A new instance of fragment MessagesNavigationFragment.
     */
    // TODO: Rename and change types and number of parameters
    @NonNull
    public static NavigationFragment newInstance(final int sectionNumber) {
        final LiveNavigationFragment fragment = new LiveNavigationFragment();
        final Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
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

    @NonNull
    public LiveNavigationFragment addTab(final BaseTabFragment tab) {
        fragments.add(tab);
        return this;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mSectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            TAG = getArguments().getString(ARG_TAG);


            final FollowingTabFragment twitchLive = FollowingTabFragment.newInstance(0,
                    "Following");
            final DiscoveryTabFragment twitchStreams = DiscoveryTabFragment.newInstance(1,
                    "Discovery");
            final FavoritesTabFragment esports = FavoritesTabFragment.newInstance(2, "Favorites");
            this.addTab(twitchLive).addTab(twitchStreams).addTab(esports);


        }
        Log.d(TAG, "Navigation Fragment onCreate: " + mSectionNumber);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final
    Bundle savedInstanceState) {
        Log.d(TAG, "Navigation Fragment onCreateView: " + mSectionNumber);
        return inflater.inflate(R.layout.fragment_navigation, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        final WasderActivity.TabsPagerAdapter tabsPagerAdapter = new WasderActivity
                .TabsPagerAdapter(getChildFragmentManager());
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

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        //inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public void setUserVisibleHint(final boolean isVisibleToUser) {
        if (isVisibleToUser) {
            Log.d(TAG, "setUserVisibleHint: " + this.mSectionNumber);
            final WasderActivity activity = (WasderActivity) getActivity();
            if (activity != null) {
                if (mSectionNumber == 0) {
                    FeedNavigationFragment.showSearchBar(activity);
                    //tabLayout.setVisibility(View.GONE);
                } else {
                    final TabLayout tabLayout = activity.findViewById(R.id.tabLayout);
                    if (tabLayout.getVisibility() != View.VISIBLE) {
                        tabLayout.setVisibility(View.VISIBLE);
                    }
                    final View view = activity.findViewById(R.id.searchView);
                    view.setVisibility(View.GONE);
                    tabLayout.setupWithViewPager(this.viewPager);
                }
            }
        }

    }

    @SuppressWarnings("MethodMayBeStatic")
    @OnClick(R.id.fab)
    public void submit(@NonNull final View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show();
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

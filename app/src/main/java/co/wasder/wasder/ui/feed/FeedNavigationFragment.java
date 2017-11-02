package co.wasder.wasder.ui.feed;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
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
import java.util.List;

import butterknife.OnClick;
import co.wasder.wasder.R;
import co.wasder.wasder.WasderActivity;
import co.wasder.wasder.ui.NavigationFragment;
import co.wasder.wasder.ui.OnFragmentInteractionListener;
import co.wasder.wasder.ui.TabFragment;
import co.wasder.wasder.ui.TabsPagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FeedNavigationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@Keep
public class FeedNavigationFragment extends Fragment implements NavigationFragment,
        NavigationView.OnNavigationItemSelectedListener {

    private static final String ARG_TAG = "tag";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SECTION_NUMBER = "param1";
    private static final String ARG_SECTION_TYPE = "param2";
    private final List<TabFragment> fragments = new ArrayList<>();
    private String TAG;
    private ViewPager viewPager;
    @SuppressWarnings("unused")
    private View appBarLayout;
    // TODO: Rename and change types of parameters
    private int mSectionNumber;
    private OnFragmentInteractionListener mListener;
    @SuppressWarnings("unused")
    private Animator.AnimatorListener mAnimationListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            appBarLayout.setBackgroundColor(Color.RED);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };
    @SuppressWarnings("unused")
    private DrawerLayout mDrawerLayout;

    public FeedNavigationFragment() {
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
    public static FeedNavigationFragment newInstance(int sectionNumber) {
        FeedNavigationFragment fragment = new FeedNavigationFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    private FeedNavigationFragment addTab(TabFragment tab) {
        fragments.add(tab);
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mSectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            int mSectionType = getArguments().getInt(ARG_SECTION_TYPE);
            TAG = getArguments().getString(ARG_TAG);


            FeedTabFragment feedTab = FeedTabFragment.newInstance(0);
            this.addTab(feedTab);


        }
        Log.d(TAG, "Navigation Fragment onCreate: " + mSectionNumber);
    }

    @SuppressWarnings("unused")
    public Runnable createRunnable(final View appbar, final Animator.AnimatorListener
            animatorListener) {
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        Log.d(TAG, "Navigation Fragment onCreateView: " + mSectionNumber);
        return inflater.inflate(R.layout.fragment_navigation, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter(getChildFragmentManager());
        for (TabFragment tab : fragments) {
            tabsPagerAdapter.addFragment(tab);
        }

        viewPager = view.findViewById(R.id.fragment_navigation_viewPager);
        viewPager.setAdapter(tabsPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int
                    positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected: Tab" + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            Log.d(TAG, "setUserVisibleHint: " + this.mSectionNumber);
            WasderActivity activity = (WasderActivity) getActivity();
            if (activity != null) {
                if (mSectionNumber == 0) {
                    View view = activity.findViewById(R.id.searchView);
                    view.setVisibility(View.VISIBLE);
                    TabLayout tabLayout = activity.findViewById(R.id.tabLayout);
                    tabLayout.removeAllTabs();
                    //tabLayout.setVisibility(View.GONE);
                } else {
                    TabLayout tabLayout = activity.findViewById(R.id.tabLayout);
                    if (tabLayout.getVisibility() != View.VISIBLE) {
                        tabLayout.setVisibility(View.VISIBLE);
                    }
                    View view = activity.findViewById(R.id.searchView);
                    view.setVisibility(View.GONE);
                    tabLayout.setupWithViewPager(this.viewPager);
                }
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void AnimateAppBarColor(View view, Animator.AnimatorListener listener) {
        int cx = view.getWidth() / 2;
        int cy = view.getHeight() / 2;
        float finalRadius = Math.max(view.getWidth(), view.getHeight());
        Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.setDuration(1000);
        view.setVisibility(View.VISIBLE);
        anim.addListener(listener);
        anim.start();
    }

    @OnClick(R.id.fab)
    public void submit(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show();
    }

    // TODO: Rename method, update argument and hook method into UI event
    @SuppressWarnings("unused")
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteractionListener(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

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

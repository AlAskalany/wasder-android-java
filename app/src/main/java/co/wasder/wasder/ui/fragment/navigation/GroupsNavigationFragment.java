package co.wasder.wasder.ui.fragment.navigation;

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
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.wasder.wasder.NavFragmentUtils;

import java.util.ArrayList;
import java.util.Collection;

import butterknife.OnClick;
import co.wasder.wasder.R;
import co.wasder.wasder.ui.activity.WasderActivity;
import co.wasder.wasder.ui.fragment.OnFragmentInteractionListener;
import co.wasder.wasder.ui.fragment.tab.TabFragment;
import co.wasder.wasder.ui.fragment.tab.groups.AllTabFragment;
import co.wasder.wasder.ui.fragment.tab.groups.OwnedTabFragment;
import co.wasder.wasder.ui.pageradapter.TabsPagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GroupsNavigationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@Keep
public class GroupsNavigationFragment extends Fragment implements NavigationFragment,
        NavigationView.OnNavigationItemSelectedListener {

    public static final String ARG_TAG = "tag";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_SECTION_NUMBER = "param1";
    public static final String ARG_SECTION_TYPE = "param2";
    public final Collection<TabFragment> fragments = new ArrayList<>();
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

    public GroupsNavigationFragment() {
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
        final GroupsNavigationFragment fragment = new GroupsNavigationFragment();
        final Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    public GroupsNavigationFragment addTab(final TabFragment tab) {
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


            final AllTabFragment all = AllTabFragment.newInstance(0);
            final OwnedTabFragment owned = OwnedTabFragment.newInstance(1);
            this.addTab(all).addTab(owned);


        }
        Log.d(TAG, "Navigation Fragment onCreate: " + mSectionNumber);
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

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle
            savedInstanceState) {
        Log.d(TAG, "Navigation Fragment onCreateView: " + mSectionNumber);
        return inflater.inflate(R.layout.fragment_navigation, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        final TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter(getChildFragmentManager());
        for (final TabFragment tab : fragments) {
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void AnimateAppBarColor(final View view, final Animator.AnimatorListener listener) {
        NavFragmentUtils.AnimateAppBarColor(view, listener);
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
        return NavFragmentUtils.handleNavigationDrawer(mDrawerLayout, item);
    }
}

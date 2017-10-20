package com.wasder.wasder.ui;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wasder.wasder.R;
import com.wasder.wasder.TabbedActivity;
import com.wasder.wasder.Util.RestaurantUtil;
import com.wasder.wasder.model.Restaurant;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NavigationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NavigationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NavigationFragment extends Fragment implements NavigationView
        .OnNavigationItemSelectedListener {

    private static final String ARG_TAG = "tag";
    private static final int HOME = 0;
    private static final int LIVE = 1;
    private static final int GROUPS = 2;
    private static final int MESSAGES = 3;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SECTION_NUMBER = "param1";
    private static final String ARGY_SECTION_TYPE = "param2";
    public String TAG;
    public ViewPager viewPager;
    public View appBarLayout;
    // TODO: Rename and change types of parameters
    private int mSectionNumber;
    private int mSectionType;
    private OnFragmentInteractionListener mListener;
    private String asd;
    private List<TabFragment> fragments = new ArrayList<>();
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
    private DrawerLayout mDrawerLayout;

    public NavigationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * * @param sectionNumber Section number.
     *
     * @param sectionType Section type.
     * @return A new instance of fragment NavigationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NavigationFragment newInstance(int sectionNumber, SectionType sectionType) {
        NavigationFragment fragment = new NavigationFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putInt(ARGY_SECTION_TYPE, sectionType.getValue());
        fragment.TAG = sectionType.getTitle();
        args.putString(ARG_TAG, sectionType.name());
        fragment.setArguments(args);
        return fragment;
    }

    public NavigationFragment addTab(TabFragment tab) {
        fragments.add(tab);
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mSectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            mSectionType = getArguments().getInt(ARGY_SECTION_TYPE);
            TAG = getArguments().getString(ARG_TAG);

            switch (mSectionType) {
                case HOME:
                    TabFragment feedTab = TabFragment.newInstance(0, TabFragment.TabType.FEED);
                    this.addTab(feedTab);
                    break;
                case LIVE:
                    TabFragment twitchLive = TabFragment.newInstance(0, TabFragment.TabType
                            .TWITCHLIVE);
                    TabFragment twitchStreams = TabFragment.newInstance(1, TabFragment.TabType
                            .TWITCHSTREAMS);
                    TabFragment esports = TabFragment.newInstance(2, TabFragment.TabType.ESPORTS);
                    this.addTab(twitchLive).addTab(twitchStreams).addTab(esports);
                    break;
                case GROUPS:
                    TabFragment all = TabFragment.newInstance(0, TabFragment.TabType.ALL);
                    TabFragment owned = TabFragment.newInstance(1, TabFragment.TabType.OWNED);
                    this.addTab(all).addTab(owned);
                    break;
                case MESSAGES:
                    TabFragment mentions = TabFragment.newInstance(0, TabFragment.TabType.MENTIONS);
                    TabFragment pm = TabFragment.newInstance(1, TabFragment.TabType.PM);
                    this.addTab(mentions).addTab(pm);
                    break;
                default:
                    break;
            }

        }
        Log.d(TAG, "Navigation Fragment onCreate: " + mSectionNumber);
    }

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
        View view = inflater.inflate(R.layout.fragment_navigation, container, false);
        return view;
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
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity != null) {
                TabLayout tabLayout = activity.findViewById(R.id.tabLayout);
                tabLayout.setupWithViewPager(this.viewPager);
            }
        } else {
        }
    }

    @Override
    public void onStart() {

        super.onStart();
        Log.d(TAG, "Navigation Fragment onStart: " + this.mSectionNumber);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "Navigation Fragment onPause: " + mSectionNumber);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Navigation Fragment onResume: " + mSectionNumber);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "Navigation Fragment onStop: " + mSectionNumber);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "Navigation Fragment onActivityCreated: " + mSectionNumber);

    }

    private void onAddItemsClicked() {
        // Get a reference to the events collection
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        CollectionReference events = mFirestore.collection("feed");

        for (int i = 0; i < 10; i++) {
            // Get a random events POJO
            Restaurant event = RestaurantUtil.getRandom(getContext());

            // Add a new document to the events collection
            events.add(event);
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
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            // Handle the camera action
            startActivity(new Intent(getActivity(), TabbedActivity.class));
        } else if (id == R.id.nav_friends) {

        } else if (id == R.id.nav_followers) {

        } else if (id == R.id.nav_achievements) {

        } else if (id == R.id.nav_settings_account) {

        } else if (id == R.id.nav_settings_notifications) {

        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public enum SectionType {
        HOME(0, "Home"), LIVE(1, "Live"), GROUPS(2, "Groups"), MESSAGES(3, "Messages");

        private int value;
        private String title;

        SectionType(int value, String title) {
            this.value = value;
            this.title = title;
        }

        public int getValue() {
            return value;
        }

        public String getTitle() {
            return title;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Created by Ahmed AlAskalany on 10/14/2017.
     * Wasder AB
     */
    public static class TabsPagerAdapter extends FragmentPagerAdapter {

        private List<TabFragment> fragments = new ArrayList<>();
        private List<String> titles = new ArrayList<>();

        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public TabFragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

        public void addFragment(TabFragment fragment) {
            fragments.add(fragment);
            titles.add(fragment.getTitle());
        }
    }
}

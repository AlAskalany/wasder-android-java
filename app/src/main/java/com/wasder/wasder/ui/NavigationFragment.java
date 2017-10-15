package com.wasder.wasder.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.wasder.wasder.R;

import java.util.ArrayList;
import java.util.List;

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

    private static final int HOME = 0;
    private static final int LIVE = 1;
    private static final int GROUPS = 2;
    private static final int MESSAGES = 3;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SECTION_NUMBER = "param1";
    private static final String ARGY_SECTION_TYPE = "param2";
    // TODO: Rename and change types of parameters
    private int mSectionNumber;
    private int mSectionType;
    private OnFragmentInteractionListener mListener;
    private String asd;
    private List<TabFragment> fragments = new ArrayList<>();
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
        if (getArguments() != null) {
            mSectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            mSectionType = getArguments().getInt(ARGY_SECTION_TYPE);


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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation, container, false);

        TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter(getChildFragmentManager());
        for (TabFragment tab : fragments) {
            tabsPagerAdapter.addFragment(tab);
        }
        ViewPager viewPager = view.findViewById(R.id.fragment_navigation_viewPager);
        viewPager.setAdapter(tabsPagerAdapter);
        TabLayout tabLayout = getActivity().findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        // Inflate the layout for this fragment
        return view;
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
        mListener = null;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    public enum SectionType {
        HOME(0), LIVE(1), GROUPS(2), MESSAGES(3);

        private int value;

        SectionType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
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

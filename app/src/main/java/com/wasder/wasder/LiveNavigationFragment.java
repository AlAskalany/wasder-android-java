package com.wasder.wasder;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ahmed AlAskalany on 10/14/2017.
 * Wasder AB
 */

public class LiveNavigationFragment extends NavigationFragment {

    private List<TabFragment> mTabFragments = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FeedTabFragment feedTabFragment = FeedTabFragment.newInstance();
        //AllTabFragment groupsTabFragment = AllTabFragment.newInstance();
        //mTabFragments.add(feedTabFragment);
        //mTabFragments.add(groupsTabFragment);

        mTabFragments.add(new TabFragment());
        mTabFragments.add(new TabFragment());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blank, container, false);

        LiveTabsPagerAdapter liveTabsPagerAdapter = new LiveTabsPagerAdapter
                (getChildFragmentManager());
        for (TabFragment tab : mTabFragments) {
            liveTabsPagerAdapter.addFragment(tab, tab.getClass().getSimpleName());
        }
        ViewPager viewPager = view.findViewById(R.id.viewPager);
        viewPager.setAdapter(liveTabsPagerAdapter);
        TabLayout tabLayout = getActivity().findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        // Inflate the layout for this fragment
        return view;
    }

    class LiveTabsPagerAdapter extends TabsPagerAdapter {

        public LiveTabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }
    }
}

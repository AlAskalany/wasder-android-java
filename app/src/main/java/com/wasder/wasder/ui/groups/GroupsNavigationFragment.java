package com.wasder.wasder.ui.groups;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wasder.wasder.R;
import com.wasder.wasder.ui.NavigationFragment;
import com.wasder.wasder.ui.TabFragment;
import com.wasder.wasder.ui.TabsPagerAdapter;
import com.wasder.wasder.ui.home.FeedTabFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ahmed AlAskalany on 10/14/2017.
 * Wasder AB
 */

public class GroupsNavigationFragment extends NavigationFragment {

    private List<TabFragment> mTabFragments = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FeedTabFragment feedTabFragment = FeedTabFragment.newInstance();
        //AllTabFragment groupsTabFragment = AllTabFragment.newInstance();
        //mTabFragments.add(feedTabFragment);
        //mTabFragments.add(groupsTabFragment);

        mTabFragments.add(new FeedTabFragment());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation_groups, container, false);

        HomeTabsPagerAdapter homeTabsPagerAdapter = new HomeTabsPagerAdapter
                (getChildFragmentManager());
        for (TabFragment tab : mTabFragments) {
            homeTabsPagerAdapter.addFragment(tab, tab.getClass().getSimpleName());
        }
        ViewPager viewPager = view.findViewById(R.id.groupsViewPager);
        viewPager.setAdapter(homeTabsPagerAdapter);
        TabLayout tabLayout = getActivity().findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        // Inflate the layout for this fragment
        return view;
    }

    class HomeTabsPagerAdapter extends TabsPagerAdapter {

        public HomeTabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }
    }
}

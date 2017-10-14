package com.wasder.wasder.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ahmed AlAskalany on 10/14/2017.
 * Wasder AB
 */
public class TabsPagerAdapter extends FragmentStatePagerAdapter {

    private final Map<Integer, Pair<String, Fragment>> mFragmentsMap = new HashMap<>();

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentsMap.get(position).second;
    }

    @Override
    public int getCount() {
        return mFragmentsMap.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentsMap.get(position).first;
    }

    public void addFragment(Fragment fragment, String title) {
        int position = mFragmentsMap.size();
        mFragmentsMap.put(position, new Pair<String, Fragment>(title, fragment));
    }
}

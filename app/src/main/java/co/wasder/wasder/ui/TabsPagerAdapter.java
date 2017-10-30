package co.wasder.wasder.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ahmed AlAskalany on 10/14/2017.
 * Wasder AB
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {

    private final List<TabFragment> fragments = new ArrayList<>();
    private final List<String> titles = new ArrayList<>();

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return (Fragment) fragments.get(position);
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

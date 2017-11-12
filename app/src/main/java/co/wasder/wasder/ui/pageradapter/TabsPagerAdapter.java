package co.wasder.wasder.ui.pageradapter;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import co.wasder.wasder.ui.TabFragment;

/**
 * Created by Ahmed AlAskalany on 10/14/2017.
 * Wasder AB
 */
@Keep
public class TabsPagerAdapter extends FragmentPagerAdapter {

    public final List<TabFragment> fragments = new ArrayList<>();
    public final List<String> titles = new ArrayList<>();

    public TabsPagerAdapter(final FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(final int position) {
        return (Fragment) fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(final int position) {
        return titles.get(position);
    }

    public void addFragment(@NonNull final TabFragment fragment) {
        fragments.add(fragment);
        titles.add(fragment.getTitle());
    }
}

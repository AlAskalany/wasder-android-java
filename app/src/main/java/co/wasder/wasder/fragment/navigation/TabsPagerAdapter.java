package co.wasder.wasder.fragment.navigation;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import co.wasder.wasder.base.BaseTabFragment;

/**
 * Created by Ahmed AlAskalany on 10/14/2017.
 * Wasder AB
 */
@Keep
class TabsPagerAdapter extends FragmentPagerAdapter {

    private final List<BaseTabFragment> fragments = new ArrayList<>();
    private final List<String> titles = new ArrayList<>();

    TabsPagerAdapter(final FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(final int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(final int position) {
        return titles.get(position);
    }

    void addFragment(@NonNull final BaseTabFragment fragment) {
        fragments.add(fragment);
        titles.add(fragment.getTitle());
    }
}

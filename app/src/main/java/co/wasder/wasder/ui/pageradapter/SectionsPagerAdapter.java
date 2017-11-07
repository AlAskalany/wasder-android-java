package co.wasder.wasder.ui.pageradapter;

import android.support.annotation.Keep;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import co.wasder.wasder.ui.activity.WasderActivity;
import co.wasder.wasder.ui.fragment.navigation.FeedNavigationFragment;
import co.wasder.wasder.ui.fragment.navigation.GroupsNavigationFragment;
import co.wasder.wasder.ui.fragment.navigation.LiveNavigationFragment;
import co.wasder.wasder.ui.fragment.navigation.MessagesNavigationFragment;
import co.wasder.wasder.ui.fragment.navigation.NavigationFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
@Keep
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(@SuppressWarnings("unused") WasderActivity activity,
                                FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        NavigationFragment fragment;
        switch (position) {
            case 0:
                fragment = FeedNavigationFragment.newInstance(0);
                break;
            case 1:
                fragment = LiveNavigationFragment.newInstance(1);
                break;
            case 2:
                fragment = GroupsNavigationFragment.newInstance(2);
                break;
            case 3:
                fragment = MessagesNavigationFragment.newInstance(3);
                break;
            default:
                fragment = null;
                break;
        }
        return (Fragment) fragment;
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 4;
    }
}

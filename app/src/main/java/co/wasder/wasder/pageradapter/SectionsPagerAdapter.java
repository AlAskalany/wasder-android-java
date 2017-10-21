package co.wasder.wasder.pageradapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import co.wasder.wasder.WasderActivity;
import co.wasder.wasder.ui.NavigationFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private WasderActivity activity;

    public SectionsPagerAdapter(WasderActivity activity, FragmentManager fm) {
        super(fm);
        this.activity = activity;
    }

    @Override
    public NavigationFragment getItem(int position) {
        NavigationFragment fragment;
        switch (position) {
            case 0:
                fragment = NavigationFragment.newInstance(0, NavigationFragment.SectionType.HOME);
                break;
            case 1:
                fragment = NavigationFragment.newInstance(1, NavigationFragment.SectionType.LIVE);
                break;
            case 2:
                fragment = NavigationFragment.newInstance(2, NavigationFragment.SectionType.GROUPS);
                break;
            case 3:
                fragment = NavigationFragment.newInstance(3, NavigationFragment.SectionType
                        .MESSAGES);
                break;
            default:
                fragment = NavigationFragment.newInstance(0, NavigationFragment.SectionType.HOME);
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 4;
    }
}

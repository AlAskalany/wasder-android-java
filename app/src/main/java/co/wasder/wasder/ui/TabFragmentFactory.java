package co.wasder.wasder.ui;

import android.os.Bundle;

import co.wasder.wasder.ui.discovery.DiscoveryTabFragment;
import co.wasder.wasder.ui.favorites.FavoritesTabFragment;
import co.wasder.wasder.ui.feed.FeedTabFragment;
import co.wasder.wasder.ui.following.FollowingTabFragment;
import co.wasder.wasder.ui.groups.AllTabFragment;
import co.wasder.wasder.ui.groups.OwnedTabFragment;
import co.wasder.wasder.ui.messaging.mentions.MentionsTabFragment;
import co.wasder.wasder.ui.messaging.messages.PmTabFragment;

/** Created by Ahmed AlAskalany on 1/14/2018. Navigator */
public class TabFragmentFactory {

    public static FeedTabFragment getFeedTabFragment(int sectionNumber, String title) {
        FeedTabFragment fragment = new FeedTabFragment();
        final Bundle args = new Bundle();
        args.putInt(FeedTabFragment.ARG_SECTION_NUMBER, sectionNumber);
        fragment.mTitle = title;
        fragment.setArguments(args);
        return fragment;
    }

    public static DiscoveryTabFragment getDiscoveryTabFragment(int sectionNumber, String title) {
        DiscoveryTabFragment fragment = new DiscoveryTabFragment();
        final Bundle args = new Bundle();
        args.putInt(DiscoveryTabFragment.ARG_SECTION_NUMBER, sectionNumber);
        fragment.mTitle = title;
        fragment.setArguments(args);
        return fragment;
    }

    public static FavoritesTabFragment getFavoritesTabFragment(int sectionNumber, String title) {
        FavoritesTabFragment fragment = new FavoritesTabFragment();
        final Bundle args = new Bundle();
        args.putInt(FavoritesTabFragment.ARG_SECTION_NUMBER, sectionNumber);
        fragment.mTitle = title;
        fragment.setArguments(args);
        return fragment;
    }

    public static FollowingTabFragment getFollowingTabFragment(int sectionNumber, String title) {
        FollowingTabFragment fragment = new FollowingTabFragment();
        final Bundle args = new Bundle();
        args.putInt(FollowingTabFragment.ARG_SECTION_NUMBER, sectionNumber);
        fragment.mTitle = title;
        fragment.setArguments(args);
        return fragment;
    }

    public static AllTabFragment getAllTabFragment(int sectionNumber, String title) {
        AllTabFragment fragment = new AllTabFragment();
        final Bundle args = new Bundle();
        args.putInt(AllTabFragment.ARG_SECTION_NUMBER, sectionNumber);
        fragment.mTitle = title;
        fragment.setArguments(args);
        return fragment;
    }

    public static OwnedTabFragment getOwnGroupsTabFragment(int sectionNumber, String title) {
        OwnedTabFragment fragment = new OwnedTabFragment();
        final Bundle args = new Bundle();
        args.putInt(OwnedTabFragment.ARG_SECTION_NUMBER, sectionNumber);
        fragment.mTitle = title;
        fragment.setArguments(args);
        return fragment;
    }

    public static MentionsTabFragment getMentionsTabFragment(int sectionNumber, String title) {
        MentionsTabFragment fragment = new MentionsTabFragment();
        final Bundle args = new Bundle();
        args.putInt(MentionsTabFragment.ARG_SECTION_NUMBER, sectionNumber);
        fragment.mTitle = title;
        fragment.setArguments(args);
        return fragment;
    }

    public static PmTabFragment getPmTabFragment(int sectionNumber, String title) {
        PmTabFragment fragment = new PmTabFragment();
        final Bundle args = new Bundle();
        args.putInt(PmTabFragment.ARG_SECTION_NUMBER, sectionNumber);
        fragment.mTitle = title;
        fragment.setArguments(args);
        return fragment;
    }
}

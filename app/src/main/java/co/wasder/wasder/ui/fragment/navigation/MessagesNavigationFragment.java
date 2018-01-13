package co.wasder.wasder.ui.fragment.navigation;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;

import butterknife.OnClick;
import co.wasder.wasder.R;
import co.wasder.wasder.databinding.FragmentNavigationBinding;
import co.wasder.wasder.ui.base.BaseTabFragment;
import co.wasder.wasder.ui.fragment.tab.MentionsTabFragment;
import co.wasder.wasder.ui.fragment.tab.PmTabFragment;
import co.wasder.wasder.ui.listener.OnFragmentInteractionListener;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface to handle interaction events. Use the {@link
 * MessagesNavigationFragment#newInstance} factory method to create an instance of this fragment.
 */
@Keep
public class MessagesNavigationFragment extends BaseNavigationFragment {

    public final Collection<BaseTabFragment> fragments = new ArrayList<>();
    @Nullable public String TAG;
    public ViewPager viewPager;
    public int mSectionNumber;
    @Nullable public OnFragmentInteractionListener mListener;
    private FragmentNavigationBinding binding;

    public MessagesNavigationFragment() {
        // Required empty public constructor
    }

    @NonNull
    public static Fragment newInstance(final int sectionNumber) {
        final MessagesNavigationFragment fragment = new MessagesNavigationFragment();
        final Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressWarnings("MethodMayBeStatic")
    @OnClick(R.id.fab)
    public void submit(@NonNull final View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show();
    }

    @Override
    public void addTab(final BaseTabFragment tab) {
        fragments.add(tab);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mSectionNumber = getArguments().getInt(BaseNavigationFragment.ARG_SECTION_NUMBER);

            final MentionsTabFragment mentionsTabFragment =
                    MentionsTabFragment.newInstance(0, "Mentions");
            final PmTabFragment pmTabFragment = PmTabFragment.newInstance(0, "PM");
            addTab(mentionsTabFragment);
            addTab(pmTabFragment);
        }
        Log.d(TAG, "Navigation Fragment onCreate: " + mSectionNumber);
    }

    @Override
    public View onCreateView(
            @NonNull final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState) {
        Log.d(TAG, "Navigation Fragment onCreateView: " + mSectionNumber);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_navigation, container, false);
        return binding.getRoot();
    }

    @Override
    public void setUserVisibleHint(final boolean isVisibleToUser) {
        if (isVisibleToUser) {
            Log.d(TAG, "setUserVisibleHint: " + this.mSectionNumber);
            final FragmentActivity activity = getActivity();
            if (activity != null) {
                if (mSectionNumber == 0) {
                    showSearchBar(activity);
                } else {
                    hideSearchBar(activity);
                }
            }
        }
    }

    private void hideSearchBar(@NonNull FragmentActivity activity) {
        final TabLayout tabLayout = activity.findViewById(R.id.tabLayout);
        if (tabLayout.getVisibility() != View.VISIBLE) {
            tabLayout.setVisibility(View.VISIBLE);
        }
        final View view = activity.findViewById(R.id.searchView);
        view.setVisibility(View.GONE);
        tabLayout.setupWithViewPager(this.viewPager);
    }

    @Override
    public void onViewCreated(@NonNull final View view, final Bundle savedInstanceState) {
        final TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter(getChildFragmentManager());
        for (final BaseTabFragment tab : fragments) {
            tabsPagerAdapter.addFragment(tab);
        }

        viewPager = view.findViewById(R.id.fragment_navigation_viewPager);
        viewPager.setAdapter(tabsPagerAdapter);
        viewPager.addOnPageChangeListener(
                new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(
                            final int position,
                            final float positionOffset,
                            final int positionOffsetPixels) {}

                    @Override
                    public void onPageSelected(final int position) {
                        Log.d(TAG, "onPageSelected: Tab" + position);
                    }

                    @Override
                    public void onPageScrollStateChanged(final int state) {}
                });
    }

    @SuppressWarnings("unused")
    public void onButtonPressed(final Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteractionListener(uri);
        }
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        Log.d(TAG, "Navigation Fragment onAttach: " + mSectionNumber);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(
                    context.toString() + " must implement " + "OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "Navigation Fragment onDetach: " + mSectionNumber);
        mListener = null;
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
        Log.d(TAG, "Navigation Fragment onDestroyView: " + mSectionNumber);
    }
}

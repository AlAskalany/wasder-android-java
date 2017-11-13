package co.wasder.wasder.fragment.navigation;

import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import butterknife.OnClick;
import co.wasder.wasder.R;
import co.wasder.wasder.base.BaseTabFragment;
import co.wasder.wasder.fragment.tab.FeedTabFragment;
import co.wasder.wasder.listener.OnFragmentInteractionListener;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FeedNavigationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@Keep
public class FeedNavigationFragment extends BaseNavigationFragment {

    public FeedNavigationFragment() {
        // Required empty public constructor
    }

    @NonNull
    public static Fragment newInstance(final int sectionNumber) {
        final FeedNavigationFragment fragment = new FeedNavigationFragment();
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


            final FeedTabFragment feedTab = FeedTabFragment.newInstance(0, "Feed");
            this.addTab(feedTab);


        }
        Log.d(TAG, "Navigation Fragment onCreate: " + mSectionNumber);
    }
}

package co.wasder.wasder.ui.feed;

import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.FirebaseFirestore;

import butterknife.ButterKnife;
import co.wasder.wasder.R;
import co.wasder.wasder.ui.navigation.BaseTabFragment;

/** Created by Ahmed AlAskalany on 10/30/2017. Navigator */
@Keep
public class FeedTabFragment extends BaseTabFragment {

    public static String ARG_SECTION_NUMBER = "section-number";

    static {
        FirebaseFirestore.setLoggingEnabled(true);
    }

    public String TAG;

    public static FeedTabFragment newInstance(int sectionNumber, String title) {
        FeedTabFragment fragment = new FeedTabFragment();
        final Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.mTitle = title;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(
            @NonNull final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_tab, container, false);
        ButterKnife.bind(this, view);
        // TODO create FeedTabFragmentViewModel
        assert mRecyclerView != null;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAddPostDialog = new AddPostDialogFragment();
        setupSearchAndFilters();
        return view;
    }
}

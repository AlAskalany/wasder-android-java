package co.wasder.wasder.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.FirebaseFirestore;

import butterknife.ButterKnife;
import co.wasder.ui.base.BaseTabFragmentViewModel;
import co.wasder.ui.viewmodel.FollowingTabFragmentViewModel;
import co.wasder.wasder.R;
import co.wasder.wasder.base.BaseTabFragment;
import co.wasder.wasder.dialogfragment.AddFirestoreItemDialogFragment;
import co.wasder.wasder.util.Dialogs;

/**
 * Created by Ahmed AlAskalany on 10/30/2017.
 * Navigator
 */
@Keep
public class FollowingTabFragment extends BaseTabFragment {

    public static String ARG_SECTION_NUMBER = "section-number";

    static {
        FirebaseFirestore.setLoggingEnabled(true);
    }

    public String TAG;
    public String USERS;
    public AddFirestoreItemDialogFragment mAddPostDialog;
    public BaseTabFragmentViewModel mViewModel;
    private long LIMIT;

    public static FollowingTabFragment newInstance(int sectionNumber, String title) {
        FollowingTabFragment fragment = new FollowingTabFragment();
        final Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.mTitle = title;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_tab, container, false);
        ButterKnife.bind(this, view);
        mViewModel = ViewModelProviders.of(this).get(FollowingTabFragmentViewModel.class);
        assert mRecyclerView != null;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAddPostDialog = Dialogs.AddPostDialogFragment();
        setupSearchAndFilters();
        return view;
    }
}

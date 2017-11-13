package co.wasder.wasder.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.common.ChangeEventType;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import butterknife.ButterKnife;
import co.wasder.data.base.BaseModel;
import co.wasder.ui.base.BaseTabFragmentViewModel;
import co.wasder.ui.viewmodel.DiscoveryTabFragmentViewModel;
import co.wasder.wasder.R;
import co.wasder.wasder.base.BaseTabFragment;
import co.wasder.wasder.dialogfragment.AddFirestoreItemDialogFragment;
import co.wasder.wasder.listener.OnFirestoreItemSelectedListener;
import co.wasder.wasder.util.Dialogs;

/**
 * Created by Ahmed AlAskalany on 10/30/2017.
 * Navigator
 */
@Keep
public class DiscoveryTabFragment extends BaseTabFragment {

    public static String ARG_SECTION_NUMBER = "section-number";

    static {
        FirebaseFirestore.setLoggingEnabled(true);
    }

    public String TAG;
    public String USERS;
    public AddFirestoreItemDialogFragment mAddPostDialog;
    public BaseTabFragmentViewModel mViewModel;
    public String mTitle;
    private long LIMIT;
    @NonNull
    private OnFirestoreItemSelectedListener onFirestoreItemSelectedListener = new
            OnFirestoreItemSelectedListener() {
        @Override
        public void onFirestoreItemSelected(BaseModel event, View itemView) {

        }

        @Override
        public void onChildChanged(ChangeEventType type, DocumentSnapshot snapshot, int newIndex,
                                   int oldIndex) {

        }

        @Override
        public void onDataChanged() {

        }

        @Override
        public void onError(FirebaseFirestoreException e) {

        }
    };

    public static DiscoveryTabFragment newInstance(int sectionNumber, String title) {
        DiscoveryTabFragment fragment = new DiscoveryTabFragment();
        final Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.mTitle = title;
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressWarnings("unused")
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        setUserVisibleHint(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_tab, container, false);
        ButterKnife.bind(this, view);
        mViewModel = ViewModelProviders.of(this).get(DiscoveryTabFragmentViewModel.class);
        assert mRecyclerView != null;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAddPostDialog = Dialogs.AddPostDialogFragment();
        setupSearchAndFilters();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            attachRecyclerViewAdapter();
        }
        FirebaseAuth.getInstance().addAuthStateListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    @Nullable
    public FirebaseFirestore getFirestore() {
        return null;
    }

    @Override
    public void setFirestore(final FirebaseFirestore instance) {

    }

    @Override
    @Nullable
    public Query getQuery() {
        return null;
    }

    @Override
    public void setQuery(final Query timestamp) {

    }

    @Override
    @Nullable
    public String getTitle() {
        return mTitle;
    }
}

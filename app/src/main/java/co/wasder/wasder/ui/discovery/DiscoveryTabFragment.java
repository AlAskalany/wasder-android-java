package co.wasder.wasder.ui.discovery;

import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.common.ChangeEventType;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.wasder.wasder.R;
import co.wasder.wasder.data.BaseModel;
import co.wasder.wasder.data.FeedModel;
import co.wasder.wasder.ui.OnFirestoreItemSelectedListener;
import co.wasder.wasder.ui.feed.AddPostDialogFragment;
import co.wasder.wasder.ui.feed.FeedRecyclerAdapter;
import co.wasder.wasder.ui.feed.FeedViewHolder;
import co.wasder.wasder.ui.navigation.BaseTabFragment;

/** Created by Ahmed AlAskalany on 10/30/2017. Navigator */
@Keep
public class DiscoveryTabFragment extends BaseTabFragment {

    public static String ARG_SECTION_NUMBER = "section-number";

    static {
        FirebaseFirestore.setLoggingEnabled(true);
    }

    public String TAG;
    public AddPostDialogFragment mAddPostDialog;

    @BindView(R.id.recyclerView)
    public RecyclerView mRecyclerView;

    @NonNull
    protected OnFirestoreItemSelectedListener onFirestoreItemSelectedListener =
            new OnFirestoreItemSelectedListener() {
                @Override
                public void onFirestoreItemSelected(BaseModel event, View itemView) {}

                @Override
                public void onChildChanged(
                        ChangeEventType type,
                        DocumentSnapshot snapshot,
                        int newIndex,
                        int oldIndex) {}

                @Override
                public void onDataChanged() {}

                @Override
                public void onError(FirebaseFirestoreException e) {}
            };

    public static DiscoveryTabFragment newInstance(int sectionNumber, String title) {
        DiscoveryTabFragment fragment = new DiscoveryTabFragment();
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
        // TODO create DiscoveryFragmentViewModel
        assert mRecyclerView != null;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAddPostDialog = new AddPostDialogFragment();
        setupSearchAndFilters();
        return view;
    }

    protected void attachRecyclerViewAdapter() {
        final RecyclerView.Adapter<FeedViewHolder> adapter =
                FeedRecyclerAdapter.getAdapter(
                        this, onFirestoreItemSelectedListener, mQuery, FeedModel.class);

        // Scroll to bottom on new messages
        adapter.registerAdapterDataObserver(
                new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onItemRangeInserted(final int positionStart, final int itemCount) {
                        assert mRecyclerView != null;
                        mRecyclerView.smoothScrollToPosition(0);
                    }
                });
        assert mRecyclerView != null;
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
        super.onAuthStateChanged(firebaseAuth);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            attachRecyclerViewAdapter();
        }
        FirebaseAuth.getInstance().addAuthStateListener(this);
    }
}

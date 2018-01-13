package co.wasder.wasder.ui.fragment.tab;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.wasder.wasder.R;
import co.wasder.wasder.data.base.BaseModel;
import co.wasder.wasder.data.model.FeedModel;
import co.wasder.wasder.ui.base.BaseTabFragment;
import co.wasder.wasder.ui.base.BaseTabFragmentViewModel;
import co.wasder.wasder.ui.dialogfragment.addFirestoreItemDialogFragment;
import co.wasder.wasder.ui.listener.OnFirestoreItemSelectedListener;
import co.wasder.wasder.ui.recycleradapter.FeedRecyclerAdapter;
import co.wasder.wasder.ui.util.Dialogs;
import co.wasder.wasder.ui.viewholder.FeedViewHolder;
import co.wasder.wasder.ui.viewmodel.FollowingTabFragmentViewModel;

/** Created by Ahmed AlAskalany on 10/30/2017. Navigator */
@Keep
public class FollowingTabFragment extends BaseTabFragment {

    public static String ARG_SECTION_NUMBER = "section-number";

    static {
        FirebaseFirestore.setLoggingEnabled(true);
    }

    public String TAG;
    public String USERS;
    public addFirestoreItemDialogFragment mAddPostDialog;
    public BaseTabFragmentViewModel mViewModel;

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
    public View onCreateView(
            @NonNull final LayoutInflater inflater,
            final ViewGroup container,
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

    protected void attachRecyclerViewAdapter() {
        final FirestoreRecyclerOptions<FeedModel> options =
                new FirestoreRecyclerOptions.Builder<FeedModel>()
                        .setQuery(mQuery, FeedModel.class)
                        .build();
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
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            attachRecyclerViewAdapter();
        } else {
            Toast.makeText(getContext(), R.string.signing_in, Toast.LENGTH_SHORT).show();
            firebaseAuth
                    .signInAnonymously()
                    .addOnCompleteListener(
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {}
                            });
        }
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

package co.wasder.ui.navigation.home.tabs.feed;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.wasder.data.filter.FirestoreItemFilters;
import co.wasder.data.model.AbstractFirestoreItem;
import co.wasder.data.model.FeedModel;
import co.wasder.data.model.User;
import co.wasder.ui.navigation.live.tabs.following.FollowingRecyclerAdapter;
import co.wasder.ui.navigation.tab.BaseTabFragment;
import co.wasder.ui.navigation.tab.BaseTabFragmentViewModel;
import co.wasder.ui.navigation.tab.OnFragmentInteractionListener;
import co.wasder.wasder.R;
import co.wasder.wasder.ui.adapter.OnFirestoreItemSelected;
import co.wasder.wasder.ui.dialog.AddFirestoreItemDialogFragment;
import co.wasder.wasder.ui.dialog.Dialogs;

/**
 * Created by Ahmed AlAskalany on 10/30/2017.
 * Navigator
 */
@Keep
public class FeedTabFragment extends BaseTabFragment {

    private static final CollectionReference postsCollection = FirebaseFirestore.getInstance()
            .collection("posts");
    private static final Query mQuery = postsCollection.orderBy("timestamp", Query.Direction
            .DESCENDING)
            .limit(50);
    public static String ARG_SECTION_NUMBER = "section-number";

    static {
        FirebaseFirestore.setLoggingEnabled(true);
    }

    public String TAG;
    public String USERS;
    @BindView(R.id.recyclerView)
    public RecyclerView mRecyclerView;
    public AddFirestoreItemDialogFragment mAddPostDialog;
    public BaseTabFragmentViewModel mViewModel;
    @Nullable
    public OnFragmentInteractionListener mListener;
    public String mTitle;
    private long LIMIT;
    @NonNull
    private OnFirestoreItemSelected onFirestoreItemSelected = new OnFirestoreItemSelected() {
        @Override
        public void onFirestoreItemSelected(AbstractFirestoreItem event, View itemView) {

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

    public static FeedTabFragment newInstance(int sectionNumber, String title) {
        FeedTabFragment fragment = new FeedTabFragment();
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
        mViewModel = ViewModelProviders.of(this).get(FeedTabFragmentViewModel.class);
        assert mRecyclerView != null;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAddPostDialog = Dialogs.AddPostDialogFragment();
        setupSearchAndFilters();
        return view;
    }

    @Override
    protected void setupSearchAndFilters() {
        final SearchView mSearchView = getActivity().findViewById(R.id.searchView);
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
        });
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String search) {
                final CollectionReference usersReference = FirebaseFirestore.getInstance()
                        .collection("users");
                final Query query = usersReference.whereEqualTo("displayName", search);
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            final QuerySnapshot queryResult = task.getResult();
                            final List<DocumentSnapshot> usersDocuments = queryResult
                                    .getDocuments();
                            if (queryResult.size() > 0) {
                                final User firstUser = usersDocuments.get(0).toObject(User.class);
                                final String userId = firstUser.getUid();
                                final FirestoreItemFilters firestoreItemFilters = new
                                        FirestoreItemFilters();
                                firestoreItemFilters.setUid(userId);
                            }
                        }
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String search) {
                return false;
            }
        });
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
    protected void attachRecyclerViewAdapter() {
        final FirestoreRecyclerOptions<FeedModel> options = new FirestoreRecyclerOptions
                .Builder<FeedModel>()
                .setQuery(mQuery, FeedModel.class)
                .build();
        final RecyclerView.Adapter<FeedViewHolder> adapter = FollowingRecyclerAdapter.getAdapter
                (this, onFirestoreItemSelected, mQuery, FeedModel.class);

        // Scroll to bottom on new messages
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
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
    public void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement " +
                    "OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            attachRecyclerViewAdapter();
        } else {
            Toast.makeText(getContext(), R.string.signing_in, Toast.LENGTH_SHORT).show();
            firebaseAuth.signInAnonymously()
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                        }
                    });
        }
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

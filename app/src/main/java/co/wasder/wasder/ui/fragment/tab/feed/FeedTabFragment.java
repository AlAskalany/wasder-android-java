package co.wasder.wasder.ui.fragment.tab.feed;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.wasder.wasder.R;
import co.wasder.wasder.Util.FirebaseUtil;
import co.wasder.wasder.Utils;
import co.wasder.wasder.data.filter.FirestoreItemFilters;
import co.wasder.wasder.data.model.AbstractFirestoreItem;
import co.wasder.wasder.data.model.FeedModel;
import co.wasder.wasder.data.model.User;
import co.wasder.wasder.ui.SignInResultNotifier;
import co.wasder.wasder.ui.dialog.AddFirestoreItemDialogFragment;
import co.wasder.wasder.ui.dialog.Dialogs;
import co.wasder.wasder.ui.fragment.OnFragmentInteractionListener;
import co.wasder.wasder.ui.fragment.tab.TabFragment;
import co.wasder.wasder.ui.fragment.tab.TabFragmentViewModel;
import co.wasder.wasder.ui.fragment.tab.adapter.FirestoreItemsAdapter;

/**
 * Created by Ahmed AlAskalany on 10/30/2017.
 * Navigator
 */
@Keep
public class FeedTabFragment extends Fragment implements TabFragment, LifecycleOwner,
        FirebaseAuth.AuthStateListener {

    public static final long LIMIT = FirebaseUtil.LIMIT;
    public static final String TAG = "TabFragment";
    public static final String ARG_SECTION_NUMBER = Utils.ARG_SECTION_NUMBER;
    public static final String USERS = "users";
    private static final CollectionReference postsCollection = FirebaseUtil
            .getUsersCollectionReference(Utils.POSTS);
    private static final Query mQuery = postsCollection.orderBy(Utils.TIMESTAMP, Query.Direction
            .DESCENDING)
            .limit(LIMIT);

    static {
        FirebaseFirestore.setLoggingEnabled(true);
    }

    @Nullable
    @BindView(R.id.recyclerView)
    public RecyclerView mRecyclerView;
    public AddFirestoreItemDialogFragment mAddPostDialog;
    public TabFragmentViewModel mViewModel;
    @Nullable
    public OnFragmentInteractionListener mListener;

    public String mTitle;
    @NonNull
    private FirestoreItemsAdapter.OnFirestoreItemSelected onFirestoreItemSelected = new
            FirestoreItemsAdapter.OnFirestoreItemSelected() {


        @Override
        public void onFirestoreItemSelected(final AbstractFirestoreItem event, final View
                itemView) {

        }
    };

    @NonNull
    public static FeedTabFragment newInstance() {
        final FeedTabFragment fragment = new FeedTabFragment();
        fragment.mTitle = "Feed";
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
        mViewModel = ViewModelProviders.of(this).get(TabFragmentViewModel.class);
        assert mRecyclerView != null;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAddPostDialog = Dialogs.AddPostDialogFragment();
        setupSearchAndFilters();
        return view;
    }

    private void setupSearchAndFilters() {
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
                final CollectionReference usersReference = FirebaseUtil
                        .getUsersCollectionReference(USERS);
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
        if (FirebaseUtil.getCurrentUser() != null) {
            attachRecyclerViewAdapter();
        }
        FirebaseUtil.getAuth().addAuthStateListener(this);
    }

    private void attachRecyclerViewAdapter() {
        final FirestoreRecyclerOptions<FeedModel> options = new FirestoreRecyclerOptions
                .Builder<FeedModel>()
                .setQuery(mQuery, FeedModel.class)
                .build();
        final RecyclerView.Adapter<FeedViewHolder> adapter = newAdapter();

        // Scroll to bottom on new messages
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(final int positionStart, final int itemCount) {
                assert mRecyclerView != null;
                mRecyclerView.smoothScrollToPosition(adapter.getItemCount());
            }
        });
        assert mRecyclerView != null;
        mRecyclerView.setAdapter(adapter);
    }


    @Override
    public void onStop() {
        super.onStop();
        FirebaseUtil.getAuth().removeAuthStateListener(this);
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

    @NonNull
    protected RecyclerView.Adapter<FeedViewHolder> newAdapter() {
        final FirestoreRecyclerOptions<FeedModel> options =
                getFirestoreItemFirestoreRecyclerOptions();

        return new FirestoreRecyclerAdapter<FeedModel, FeedViewHolder>(options) {
            @NonNull
            @Override
            public FeedViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int
                    viewType) {
                return new FeedViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_firestore_item, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull final FeedViewHolder holder, final int
                    position, @NonNull final FeedModel model) {
                holder.bind(model, onFirestoreItemSelected);
            }

            @Override
            public void onDataChanged() {
            }
        };
    }

    private FirestoreRecyclerOptions<FeedModel> getFirestoreItemFirestoreRecyclerOptions() {
        return new FirestoreRecyclerOptions.Builder<FeedModel>().setQuery(mQuery, FeedModel.class)
                .setLifecycleOwner(this)
                .build();
    }

    @Override
    public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
        if (FirebaseUtil.getCurrentUser() != null) {
            attachRecyclerViewAdapter();
        } else {
            Toast.makeText(getContext(), R.string.signing_in, Toast.LENGTH_SHORT).show();
            firebaseAuth.signInAnonymously()
                    .addOnCompleteListener(new SignInResultNotifier(getContext()));
        }
    }

    @Nullable
    @Override
    public FirebaseFirestore getFirestore() {
        return null;
    }

    @Override
    public void setFirestore(final FirebaseFirestore instance) {

    }

    @Nullable
    @Override
    public Query getQuery() {
        return null;
    }

    @Override
    public void setQuery(final Query timestamp) {

    }

    @Nullable
    @Override
    public String getTitle() {
        return null;
    }

}

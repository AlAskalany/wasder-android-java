package co.wasder.wasder.base;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
import co.wasder.data.base.BaseModel;
import co.wasder.data.filter.FirestoreItemFilters;
import co.wasder.data.model.FeedModel;
import co.wasder.data.model.User;
import co.wasder.wasder.R;
import co.wasder.wasder.listener.OnFirestoreItemSelectedListener;
import co.wasder.wasder.listener.OnFragmentInteractionListener;
import co.wasder.wasder.recycleradapter.FollowingRecyclerAdapter;
import co.wasder.wasder.viewholder.FeedViewHolder;

/**
 * Created by Ahmed AlAskalany on 11/12/2017.
 * Navigator
 */
@Keep
public abstract class BaseTabFragment extends Fragment implements LifecycleOwner, FirebaseAuth
        .AuthStateListener {

    protected static final CollectionReference postsCollection = FirebaseFirestore.getInstance()
            .collection("posts");
    protected static final Query mQuery = postsCollection.orderBy("timestamp", Query.Direction
            .DESCENDING)
            .limit(50);
    @Nullable
    public OnFragmentInteractionListener mListener;
    @BindView(R.id.recyclerView)
    public RecyclerView mRecyclerView;
    @NonNull
    protected OnFirestoreItemSelectedListener onFirestoreItemSelectedListener = new
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

    @Override
    public abstract View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                      Bundle savedInstanceState);

    protected void setupSearchAndFilters() {
        FragmentActivity activity = getFragmentActivity();
        final SearchView mSearchView = activity.findViewById(R.id.searchView);
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

    @NonNull
    protected FragmentActivity getFragmentActivity() {
        FragmentActivity activity = getActivity();
        assert activity != null;
        return activity;
    }

    protected void attachRecyclerViewAdapter() {
        final FirestoreRecyclerOptions<FeedModel> options = new FirestoreRecyclerOptions
                .Builder<FeedModel>()
                .setQuery(mQuery, FeedModel.class)
                .build();
        final RecyclerView.Adapter<FeedViewHolder> adapter = FollowingRecyclerAdapter.getAdapter
                (this, onFirestoreItemSelectedListener, mQuery, FeedModel.class);

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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Nullable
    public abstract FirebaseFirestore getFirestore();

    public abstract void setFirestore(FirebaseFirestore instance);

    @Nullable
    public abstract Query getQuery();

    public abstract void setQuery(Query timestamp);

    @Nullable
    public abstract String getTitle();

}
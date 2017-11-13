package co.wasder.wasder.base;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import co.wasder.data.filter.FirestoreItemFilters;
import co.wasder.data.model.User;
import co.wasder.wasder.R;
import co.wasder.wasder.listener.OnFragmentInteractionListener;

/**
 * Created by Ahmed AlAskalany on 11/12/2017.
 * Navigator
 */
@Keep
public abstract class BaseTabFragment extends Fragment implements LifecycleOwner, FirebaseAuth
        .AuthStateListener {

    @Nullable
    public OnFragmentInteractionListener mListener;

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

    protected abstract void attachRecyclerViewAdapter();

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
    public abstract void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth);

    @Nullable
    public abstract FirebaseFirestore getFirestore();

    public abstract void setFirestore(FirebaseFirestore instance);

    @Nullable
    public abstract Query getQuery();

    public abstract void setQuery(Query timestamp);

    @Nullable
    public abstract String getTitle();

}
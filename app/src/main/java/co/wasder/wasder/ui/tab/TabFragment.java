package co.wasder.wasder.ui.tab;

import android.arch.lifecycle.LifecycleOwner;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * Created by Ahmed AlAskalany on 11/12/2017.
 * Navigator
 */
@Keep
public abstract class TabFragment extends Fragment implements LifecycleOwner, FirebaseAuth
        .AuthStateListener {

    @Override
    public abstract View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                      Bundle savedInstanceState);

    protected abstract void setupSearchAndFilters();

    protected abstract void attachRecyclerViewAdapter();

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
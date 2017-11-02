package co.wasder.wasder.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.Keep;

import co.wasder.wasder.filter.FirestoreItemFilters;

/**
 * Created by Ahmed AlAskalany on 10/11/2017.
 * Wasder AB
 */
@Keep
public class MainActivityViewModel extends ViewModel {

    private boolean mIsSigningIn;
    private FirestoreItemFilters mFirestoreItemFilters;

    public MainActivityViewModel() {
        mIsSigningIn = false;
        mFirestoreItemFilters = FirestoreItemFilters.getDefault();
    }

    public boolean getIsSigningIn() {
        return mIsSigningIn;
    }

    public void setIsSigningIn(boolean mIsSigningIn) {
        this.mIsSigningIn = mIsSigningIn;
    }

    public FirestoreItemFilters getFilters() {
        return mFirestoreItemFilters;
    }

    public void setFilters(FirestoreItemFilters mFirestoreItemFilters) {
        this.mFirestoreItemFilters = mFirestoreItemFilters;
    }
}

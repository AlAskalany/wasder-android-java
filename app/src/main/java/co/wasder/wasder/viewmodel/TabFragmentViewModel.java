package co.wasder.wasder.viewmodel;

import android.arch.lifecycle.ViewModel;

import co.wasder.wasder.filter.FirestoreItemFilters;

/**
 * Created by Ahmed AlAskalany on 10/14/2017.
 * Wasder AB
 */

public class TabFragmentViewModel extends ViewModel {

    private FirestoreItemFilters mFirestoreItemFilters;

    public TabFragmentViewModel() {
        mFirestoreItemFilters = FirestoreItemFilters.getDefault();
    }

    public FirestoreItemFilters getFilters() {
        return mFirestoreItemFilters;
    }

    public void setFilters(FirestoreItemFilters mFirestoreItemFilters) {
        this.mFirestoreItemFilters = mFirestoreItemFilters;
    }
}

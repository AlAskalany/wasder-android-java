package co.wasder.wasder.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.Keep;

import co.wasder.wasder.filter.FirestoreItemFilters;

/**
 * Created by Ahmed AlAskalany on 10/31/2017.
 * Navigator
 */
@Keep
public class ProfileActivityViewModel extends ViewModel {

    private FirestoreItemFilters mFirestoreItemFilters;

    public FirestoreItemFilters getFilters() {
        return mFirestoreItemFilters;
    }

    public void setFilters(FirestoreItemFilters mFirestoreItemFilters) {
        this.mFirestoreItemFilters = mFirestoreItemFilters;
    }
}

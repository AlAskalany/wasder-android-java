package co.wasder.wasder.ui.fragment.tab;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.Keep;

import co.wasder.wasder.data.filter.FirestoreItemFilters;

/**
 * Created by Ahmed AlAskalany on 10/14/2017.
 * Wasder AB
 */
@Keep
public class TabFragmentViewModel extends ViewModel {

    public FirestoreItemFilters mFirestoreItemFilters;

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

package co.wasder.wasder.ui.navigation.tab;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.Keep;

import co.wasder.wasder.data.filter.FirestoreItemFilters;

/**
 * Created by Ahmed AlAskalany on 10/14/2017.
 * Wasder AB
 */
@Keep
public abstract class BaseTabFragmentViewModel extends ViewModel {

    public FirestoreItemFilters mFirestoreItemFilters;

    public abstract FirestoreItemFilters getFilters();

    public abstract void setFilters(final FirestoreItemFilters mFirestoreItemFilters);
}

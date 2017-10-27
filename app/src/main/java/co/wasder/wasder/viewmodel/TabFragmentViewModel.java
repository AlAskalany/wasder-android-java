package co.wasder.wasder.viewmodel;

import android.arch.lifecycle.ViewModel;

import co.wasder.wasder.filter.RestaurantsFilters;

/**
 * Created by Ahmed AlAskalany on 10/14/2017.
 * Wasder AB
 */

public class TabFragmentViewModel extends ViewModel {

    private RestaurantsFilters mRestaurantsFilters;

    public TabFragmentViewModel() {
        mRestaurantsFilters = RestaurantsFilters.getDefault();
    }

    public RestaurantsFilters getFilters() {
        return mRestaurantsFilters;
    }

    public void setFilters(RestaurantsFilters mRestaurantsFilters) {
        this.mRestaurantsFilters = mRestaurantsFilters;
    }
}

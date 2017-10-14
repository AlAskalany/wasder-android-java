package com.wasder.wasder.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.wasder.wasder.filter.RestaurantsFilters;

/**
 * Created by Ahmed AlAskalany on 10/14/2017.
 * Wasder AB
 */

public class FeedFragmentViewModel extends ViewModel {

    private RestaurantsFilters mRestaurantsFilters;

    public FeedFragmentViewModel() {
        mRestaurantsFilters = RestaurantsFilters.getDefault();
    }

    public RestaurantsFilters getFilters() {
        return mRestaurantsFilters;
    }

    public void setFilters(RestaurantsFilters mRestaurantsFilters) {
        this.mRestaurantsFilters = mRestaurantsFilters;
    }
}

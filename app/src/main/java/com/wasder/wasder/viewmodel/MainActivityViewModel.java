package com.wasder.wasder.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.wasder.wasder.RestaurantsFilters;

/**
 * Created by Ahmed AlAskalany on 10/11/2017.
 * Wasder AB
 */

public class MainActivityViewModel extends ViewModel {

    private boolean mIsSigningIn;
    private RestaurantsFilters mRestaurantsFilters;

    public MainActivityViewModel() {
        mIsSigningIn = false;
        mRestaurantsFilters = RestaurantsFilters.getDefault();
    }

    public boolean getIsSigningIn() {
        return mIsSigningIn;
    }

    public void setIsSigningIn(boolean mIsSigningIn) {
        this.mIsSigningIn = mIsSigningIn;
    }

    public RestaurantsFilters getFilters() {
        return mRestaurantsFilters;
    }

    public void setFilters(RestaurantsFilters mRestaurantsFilters) {
        this.mRestaurantsFilters = mRestaurantsFilters;
    }
}

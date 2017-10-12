package com.wasder.wasder.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.wasder.wasder.EventsFilters;
import com.wasder.wasder.RestaurantsFilters;

/**
 * Created by Ahmed AlAskalany on 10/11/2017.
 * Wasder AB
 */

public class EventsActivityViewModel extends ViewModel {

    private boolean mIsSigningIn;
    private EventsFilters mEventsFilters;

    public EventsActivityViewModel() {
        mIsSigningIn = false;
        mEventsFilters = EventsFilters.getDefault();
    }

    public boolean getIsSigningIn() {
        return mIsSigningIn;
    }

    public void setIsSigningIn(boolean mIsSigningIn) {
        this.mIsSigningIn = mIsSigningIn;
    }

    public EventsFilters getFilters() {
        return mEventsFilters;
    }

    public void setFilters(EventsFilters mEventsFilters) {
        this.mEventsFilters = mEventsFilters;
    }
}

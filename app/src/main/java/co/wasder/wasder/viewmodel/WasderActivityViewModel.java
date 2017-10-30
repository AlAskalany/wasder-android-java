package co.wasder.wasder.viewmodel;

import android.arch.lifecycle.ViewModel;

/**
 * Created by Ahmed AlAskalany on 10/13/2017.
 * Wasder AB
 */

public class WasderActivityViewModel extends ViewModel {

    private boolean mIsSigningIn;

    public WasderActivityViewModel() {
        mIsSigningIn = false;
    }

    public boolean getIsSigningIn() {
        return mIsSigningIn;
    }

    public void setIsSigningIn(boolean mIsSigningIn) {
        this.mIsSigningIn = mIsSigningIn;
    }
}

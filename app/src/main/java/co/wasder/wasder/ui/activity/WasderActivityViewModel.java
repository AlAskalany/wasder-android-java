package co.wasder.wasder.ui.activity;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.Keep;

/**
 * Created by Ahmed AlAskalany on 10/13/2017.
 * Wasder AB
 */
@Keep
public class WasderActivityViewModel extends ViewModel {

    public boolean mIsSigningIn;

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

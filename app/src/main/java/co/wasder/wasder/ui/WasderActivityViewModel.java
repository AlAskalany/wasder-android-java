package co.wasder.wasder.ui;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import co.wasder.wasder.thirdparty.amplitude.AmplitudeComponent;
import co.wasder.wasder.ui.authentication.AuthenticationComponent;
import co.wasder.wasder.thirdparty.fabric.CrashlyticsComponent;
import co.wasder.wasder.thirdparty.hockeyapp.HockeyAppComponent;
import co.wasder.wasder.ui.presence.PresenceComponent;

/** Created by Ahmed AlAskalany on 10/13/2017. Wasder AB */
@Keep
public class WasderActivityViewModel extends ViewModel implements FirebaseAuth.AuthStateListener {
    final AmplitudeComponent amplitudeComponent = new AmplitudeComponent();
    final CrashlyticsComponent crashlyticsComponent = new CrashlyticsComponent();
    final HockeyAppComponent hockeyAppComponent = new HockeyAppComponent();
    final PresenceComponent presenceComponent = new PresenceComponent();
    final AuthenticationComponent authenticationComponent = new AuthenticationComponent();
    private boolean mIsSigningIn;

    public WasderActivityViewModel() {
        mIsSigningIn = false;
    }

    public boolean getIsSigningIn() {
        return mIsSigningIn;
    }

    public void setIsSigningIn(final boolean mIsSigningIn) {
        this.mIsSigningIn = mIsSigningIn;
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {}

    FirebaseUser getUser() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        return firebaseAuth.getCurrentUser();
    }
}

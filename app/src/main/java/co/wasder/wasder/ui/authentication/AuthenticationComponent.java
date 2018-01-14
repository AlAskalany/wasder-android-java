package co.wasder.wasder.ui.authentication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;

import java.util.Arrays;
import java.util.List;

import co.wasder.wasder.BuildConfig;
import co.wasder.wasder.R;
import co.wasder.wasder.ui.WasderActivity;

public class AuthenticationComponent {
    private static final int RC_SIGN_IN = 9001;

    public AuthenticationComponent() {}

    public void signIn(AppCompatActivity activity) {
        // Sign in with FirebaseUI
        List<AuthUI.IdpConfig> idpConfigs =
                Arrays.asList(
                        new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                        new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build());
        final Intent intent =
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.GreenTheme)
                        .setAvailableProviders(idpConfigs)
                        .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                        .build();

        activity.startActivityForResult(intent, RC_SIGN_IN);
    }

    public void signOut(WasderActivity wasderActivity) {
        AuthUI.getInstance().signOut(wasderActivity);
    }
}

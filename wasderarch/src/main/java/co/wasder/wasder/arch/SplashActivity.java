package co.wasder.wasder.arch;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SplashActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener,
        FirebaseAuth.IdTokenListener {

    private static final int RC_SIGN_IN = 100;
    private static final String TAG = "SplashActivity";
    @BindView(R.id.root)
    View rootView;
    @BindView(R.id.logoImageView)
    ImageView logoImageView;
    private SplashActivityModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        model = ViewModelProviders.of(this).get(SplashActivityModel.class);
        model.getUser().observe(this, user -> {
            if (user != null) {
                if (!user.isAnonymous()) {
                    startActivity(MainActivity.createIntent(SplashActivity.this, null));
                } else {
                    startActivity(MainActivity.createIntent(SplashActivity.this, null));
                }
            }
        });
    }

    @OnClick(R.id.signInButton)
    public void signIn(View view) {
        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setIsSmartLockEnabled(false)
                .setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI
                        .EMAIL_PROVIDER)
                        .build(), new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                .build(), RC_SIGN_IN);
    }

    @OnClick(R.id.skipButton)
    public void skip(View view) {
        if (model.isSignedIn().getValue() == Boolean.TRUE) {
            startActivity(MainActivity.createIntent(SplashActivity.this, null));
        } else {
            model.startSignInAnonymous();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when
        // starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
                startActivity(MainActivity.createIntent(this, response));
                finish();
                return;
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    showSnackbar(R.string.sign_in_cancelled);
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackbar(R.string.no_internet_connection);
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackbar(R.string.unknown_error);
                    return;
                }
            }
            showSnackbar(R.string.unknown_sign_in_response);
        }
    }

    @MainThread
    private void showSnackbar(@StringRes int errorMessageRes) {
        Snackbar.make(rootView, errorMessageRes, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            Log.d(TAG, "onAuthStateChanged: " + user.getUid());
        }
    }

    @Override
    public void onIdTokenChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            Log.d(TAG, "onIdTokenChanged: " + user.getIdToken(true));
        }
    }
}
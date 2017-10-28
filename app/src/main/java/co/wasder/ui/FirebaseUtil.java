package co.wasder.ui;

import android.content.Intent;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

import co.wasder.viewmodel.WasderActivityViewModel;
import co.wasder.wasder.BuildConfig;
import co.wasder.wasder.R;
import co.wasder.wasder.WasderActivity;

/**
 * Created by Ahmed AlAskalany on 10/21/2017.
 * Navigator
 */

public class FirebaseUtil {

    public static void startSignIn(WasderActivity activity, WasderActivityViewModel mViewModel, @SuppressWarnings("SameParameterValue") int rcSignIn) {
        // Sign in with FirebaseUI
        Intent intent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.GreenTheme)
                .setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI
                        .EMAIL_PROVIDER)
                        .build(), new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                .build();

        activity.startActivityForResult(intent, rcSignIn);
        mViewModel.setIsSigningIn(true);
    }

    public static boolean shouldStartSignIn(@SuppressWarnings("unused") WasderActivity activity,
                                            WasderActivityViewModel
            viewModel) {
        return (!viewModel.getIsSigningIn() && FirebaseAuth.getInstance().getCurrentUser() == null);
    }
}

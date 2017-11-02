package co.wasder.wasder.ui;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Keep;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

import co.wasder.wasder.BuildConfig;
import co.wasder.wasder.R;
import co.wasder.wasder.WasderActivity;
import co.wasder.wasder.jobservices.FirestoreQueryJobService;
import co.wasder.wasder.viewmodel.WasderActivityViewModel;

/**
 * Created by Ahmed AlAskalany on 10/21/2017.
 * Navigator
 */
@Keep
public class FirebaseUtil {

    public static void startSignIn(WasderActivity activity, WasderActivityViewModel mViewModel,
                                   @SuppressWarnings("SameParameterValue") int rcSignIn) {
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
                                            WasderActivityViewModel viewModel) {
        return (!viewModel.getIsSigningIn() && FirebaseAuth.getInstance().getCurrentUser() == null);
    }

    private static Job createJob(FirebaseJobDispatcher dispatcher) {
        return dispatcher.newJobBuilder()
                // persist the task across boots
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                // Call this service when the criteria are met.
                .setService(FirestoreQueryJobService.class)
                // unique id of the task
                .setTag("OneTimeJob")
                // We are mentioning that the job is not periodic.
                .setRecurring(false)
                // Run between 30 - 60 seconds from now.
                .setTrigger(Trigger.executionWindow(0, 60))
                // don't overwrite an existing job with the same tag
                .setReplaceCurrent(false)
                // retry with exponential backoff
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                //Run this job only when the network is available.
                .setConstraints(Constraint.ON_ANY_NETWORK).build();
    }

    private static void scheduleJob(Context context) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        Job job = createJob(dispatcher);
        dispatcher.schedule(job);
    }

    public static void initFirestore(TabFragment tabFragment, String mCollectionReferenceString,
                                     long limit) {
        tabFragment.setFirestore(FirebaseFirestore.getInstance());
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        tabFragment.getFirestore().setFirestoreSettings(settings);

        // Get the 50 highest rated posts
        tabFragment.setQuery(tabFragment.getFirestore()
                .collection(mCollectionReferenceString)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(limit));
    }
}

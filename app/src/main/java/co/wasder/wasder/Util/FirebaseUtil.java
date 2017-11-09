package co.wasder.wasder.Util;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.wasder.wasder.Utils;

import java.util.Arrays;

import co.wasder.wasder.BuildConfig;
import co.wasder.wasder.R;
import co.wasder.wasder.network.service.jobservices.FirestoreQueryJobService;
import co.wasder.wasder.ui.activity.WasderActivity;
import co.wasder.wasder.ui.activity.WasderActivityViewModel;
import co.wasder.wasder.ui.fragment.tab.TabFragment;

/**
 * Created by Ahmed AlAskalany on 10/21/2017.
 * Navigator
 */
@Keep
public class FirebaseUtil {

    public static final long LIMIT = 50;

    public static void startSignIn(@NonNull final WasderActivity activity, @NonNull final WasderActivityViewModel mViewModel,
                                   @SuppressWarnings("SameParameterValue") final int rcSignIn) {
        // Sign in with FirebaseUI
        final Intent intent = AuthUI.getInstance()
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

    public static boolean shouldStartSignIn(@SuppressWarnings("unused") final WasderActivity activity,
                                            @NonNull final WasderActivityViewModel viewModel) {
        return (!viewModel.getIsSigningIn() && getCurrentUser() == null);
    }

    public static Job createJob(@NonNull final FirebaseJobDispatcher dispatcher) {
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

    public static void scheduleJob(final Context context) {
        final FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        final Job job = createJob(dispatcher);
        dispatcher.schedule(job);
    }

    public static void initFirestore(@NonNull final TabFragment tabFragment, @NonNull final String mCollectionReferenceString,
                                     final long limit) {
        tabFragment.setFirestore(getFirestore());
        final FirebaseFirestoreSettings settings = getFirebaseFirestoreSettings();
        tabFragment.getFirestore().setFirestoreSettings(settings);

        // Get the 50 highest rated posts
        tabFragment.setQuery(tabFragment.getFirestore()
                .collection(mCollectionReferenceString)
                .orderBy(Utils.TIMESTAMP, Query.Direction.DESCENDING)
                .limit(limit));
    }

    @NonNull
    public static CollectionReference getUsersCollectionReference(@NonNull String users) {
        return getFirestore().collection(users);
    }

    public static FirebaseAuth getAuth() {
        return FirebaseAuth.getInstance();
    }

    @NonNull
    public static FirebaseFirestoreSettings getFirebaseFirestoreSettings() {
        return new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .build();
    }

    @NonNull
    public static FirebaseFirestore getFirestore() {
        return FirebaseFirestore.getInstance();
    }

    @Nullable
    public static FirebaseUser getCurrentUser() {
        return getAuth().getCurrentUser();
    }
}

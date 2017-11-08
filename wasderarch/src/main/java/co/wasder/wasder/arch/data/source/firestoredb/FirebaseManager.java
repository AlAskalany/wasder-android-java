package co.wasder.wasder.arch.data.source.firestoredb;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.firebase.ui.auth.User;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.provider.FirebaseInitProvider;

import java.io.IOException;

import co.wasder.wasder.arch.MainActivity;

/**
 * Created by Ahmed AlAskalany on 11/8/2017.
 * Navigator
 */

public class FirebaseManager {

    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth.IdTokenListener idTokenListener;

    public FirebaseManager() {
        FirebaseApp app = FirebaseApp.getInstance();
        app.getApplicationContext();
        app.getName();
        app.getOptions();
        app.getToken(true);
        try {
            app.getUid();
        } catch (FirebaseApiNotAvailableException e) {
            e.printStackTrace();
        }
        app.setAutomaticResourceManagementEnabled(true);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.getCurrentUser();
        auth.signInAnonymously();
        auth.signOut();
        auth.removeIdTokenListener(idTokenListener);
        auth.addAuthStateListener(authStateListener);
        auth.confirmPasswordReset("A", "B");
        auth.createUserWithEmailAndPassword("email", "password");
        auth.fetchProvidersForEmail("email");
        auth.getUid();
        auth.getLanguageCode();
        auth.sendPasswordResetEmail("email");
        auth.useAppLanguage();
        auth.signInWithCustomToken("Ad");
        AuthCredential asd = null;
        auth.signInWithCredential(asd);
        auth.verifyPasswordResetCode("asd");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("A");
        database.getReference();
        database.setPersistenceCacheSizeBytes(100);
        database.getApp();
        database.getReferenceFromUrl("URL");
        database.goOffline();
        database.goOnline();
        database.purgeOutstandingWrites();
        database.setLogLevel(Logger.Level.DEBUG);

        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(app.getApplicationContext());
        analytics.logEvent("asd", new Bundle());
        analytics.getAppInstanceId();
        analytics.resetAnalyticsData();
        analytics.setAnalyticsCollectionEnabled(true);
        analytics.setCurrentScreen(new Activity(), "Asd", "asd");
        analytics.setMinimumSessionDuration(123);
        analytics.setSessionTimeoutDuration(123);
        analytics.setUserId("asd");
        analytics.setUserProperty("Asd", "ASd");

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("123");
        FirebaseFirestoreSettings sdfsf = null;
        firestore.setFirestoreSettings(sdfsf);
        firestore.batch();
        firestore.document("doc");
        firestore.getApp();
        firestore.getFirestoreSettings();
        Transaction.Function<?> asdasdasd = new Transaction.Function<Object>() {
            @Nullable
            @Override
            public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                return null;
            }
        };
        firestore.runTransaction(asdasdasd);
        FirebaseFirestoreSettings.Builder builder = new FirebaseFirestoreSettings.Builder();

        FirebaseInstanceId instanceId = FirebaseInstanceId.getInstance();
        instanceId.getToken();
        try {
            instanceId.getToken("ASd", "ASd");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            instanceId.deleteInstanceId();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            instanceId.deleteToken("ASd", "Asd");
        } catch (IOException e) {
            e.printStackTrace();
        }
        instanceId.getCreationTime();
        instanceId.getId();

        FirebaseInstanceIdReceiver instanceIdReceiver = new FirebaseInstanceIdReceiver();

        FirebaseInstanceIdService instanceIdService = new FirebaseInstanceIdService();
        instanceIdService.onTokenRefresh();

        FirebaseOptions options = FirebaseOptions.fromResource(app.getApplicationContext());
        options.getApiKey();
        options.getApplicationId();
        options.getDatabaseUrl();
        options.getGcmSenderId();
        options.getProjectId();
        options.getStorageBucket();

        FirebaseInitProvider initProvider = new FirebaseInitProvider();

        User.Builder userBuilder = new User.Builder("A", "B");
    }

    public FirebaseManager(MainActivity activity) {

    }
}

/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package co.wasder.wasder;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.appsee.Appsee;
import com.crashlytics.android.Crashlytics;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.Collections;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.wasder.wasder.Util.PostUtil;
import co.wasder.wasder.adapter.Adapters;
import co.wasder.wasder.adapter.PostAdapter;
import co.wasder.wasder.dialog.AddPostDialogFragment;
import co.wasder.wasder.dialog.Dialogs;
import co.wasder.wasder.dialog.PostsFilterDialogFragment;
import co.wasder.wasder.filter.PostsFilters;
import co.wasder.wasder.model.Post;
import co.wasder.wasder.viewmodel.MainActivityViewModel;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity implements PostsFilterDialogFragment
        .FilterListener, FirebaseAuth.AuthStateListener, LifecycleOwner {

    public static final int WELCOME_MESSAGE_EXPIRATION = 3600;
    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 9001;
    private static final int LIMIT = 50;
    private static final int REQUEST_INVITE = 0;
    // Remote Config keys
    private static final String LOADING_PHRASE_CONFIG_KEY = "loading_phrase";
    private static final String WELCOME_MESSAGE_KEY = "welcome_message";
    private static final String WELCOME_MESSAGE_CAPS_KEY = "welcome_message_caps";
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.text_current_search)
    TextView mCurrentSearchView;

    @BindView(R.id.text_current_sort_by)
    TextView mCurrentSortByView;

    @BindView(R.id.recycler_restaurants)
    RecyclerView mRestaurantsRecycler;

    @BindView(R.id.welcome_text_view)
    TextView mWelcomeTextView;

    @BindString(R.string.invitation_message)
    String mInviteMessage;

    @BindString(R.string.invitation_deep_link)
    String mInviteDeepLink;

    @BindString(R.string.invitation_custom_image)
    String mInviteCustomImage;

    @BindString(R.string.invitation_cta)
    String mInviteCta;

    private FirebaseFirestore mFirestore;
    private Query mQuery;

    private PostsFilterDialogFragment mFilterDialog;
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private AddPostDialogFragment mAddRestaurantDialog;
    private FirebaseRemoteConfig mRemoteConfig;

    private MainActivityViewModel mViewModel;

    private void logUserCrashlytics() {
        // TODO: Use the current user's information
        // You can call any combination of these three methods
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String uId = user.getUid();
        String userEmail = user.getEmail();
        String userName = user.getDisplayName();
        Crashlytics.setUserIdentifier(uId);
        Crashlytics.setUserEmail(userEmail);
        Crashlytics.setUserName(userName);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        // View model
        mViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);
        // Initialize Firestore and the main RecyclerView
        initFirestore();
        initRecyclerView();

        // Filter Dialog
        mFilterDialog = Dialogs.PostsFilterDialogFragment();
        mAddRestaurantDialog = Dialogs.AddPostDialogFragment();
    }

    private void onInviteClicked() {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string
                .invitation_title)).setMessage(mInviteMessage).setDeepLink(Uri.parse
                (mInviteDeepLink)).setCustomImage(Uri.parse(mInviteCustomImage))
                .setCallToActionText(mInviteCta).build();
        startActivityForResult(intent, REQUEST_INVITE);
    }

    private void fetchWelcomeMessage() {
        mWelcomeTextView.setText(mRemoteConfig.getString(LOADING_PHRASE_CONFIG_KEY));

        long cacheExpiration = WELCOME_MESSAGE_EXPIRATION; // 1 hour in
        // seconds.
        // If your app is using developer mode, cacheExpiration is set to 0, so each fetch will
        // retrieve values from the service.
        if (mRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        // [START fetch_config_with_callback]
        // cacheExpirationSeconds is set to cacheExpiration here, indicating the next fetch request
        // will use fetch data from the Remote Config service, rather than cached parameter values,
        // if cached parameter values are more than cacheExpiration seconds old.
        // See Best Practices in the README for more information.
        mRemoteConfig.fetch(cacheExpiration).addOnCompleteListener(this, new
                OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Fetch Succeeded", Toast.LENGTH_SHORT).show();

                    // After config data is successfully fetched, it must be activated before
                    // newly fetched
                    // values are returned.
                    mRemoteConfig.activateFetched();
                } else {
                    Toast.makeText(MainActivity.this, "Fetch Failed", Toast.LENGTH_SHORT).show();
                }
                displayWelcomeMessage();
            }
        });
        // [END fetch_config_with_callback]
    }

    private void displayWelcomeMessage() {
        String welcomeMessage = mRemoteConfig.getString(WELCOME_MESSAGE_KEY);
        if (mRemoteConfig.getBoolean(WELCOME_MESSAGE_CAPS_KEY)) {
            mWelcomeTextView.setAllCaps(true);
        } else {
            mWelcomeTextView.setAllCaps(false);
        }
        mWelcomeTextView.setText(welcomeMessage);
    }

    private void setupRemoteConfig() {
        mRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG).build();
        mRemoteConfig.setConfigSettings(configSettings);
    }

    private void initFirestore() {
        mFirestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true).build();
        mFirestore.setFirestoreSettings(settings);

        // Get the 50 highest rated restaurants
        mQuery = mFirestore.collection("restaurants").orderBy("avgRating", Query.Direction
                .DESCENDING).limit(LIMIT);
    }

    private void initRecyclerView() {
        if (mQuery == null) {
            Log.w(TAG, "No query, not initializing RecyclerView");
        }
        PostAdapter adapter = Adapters.PostAdapter(this, mQuery);
        mRestaurantsRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRestaurantsRecycler.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Start sign in if necessary
        if (shouldStartSignIn()) {
            startSignIn();
            return;
        } else {

            Fabric.with(this, new Crashlytics());
            Appsee.start(getString(R.string.com_apsee_apikey));
            logUserCrashlytics();
            setupRemoteConfig();
            fetchWelcomeMessage();

            // Apply filters
            onFilter(mViewModel.getFilters());
        }

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void onAddItemsClicked() {
        // Get a reference to the restaurants collection
        CollectionReference restaurants = mFirestore.collection("restaurants");

        for (int i = 0; i < 10; i++) {
            // Get a random Post POJO
            Post post = PostUtil.getRandom(this);

            // Add a new document to the restaurants collection
            restaurants.add(post);
        }
    }

    @Override
    public void onFilter(PostsFilters filters) {
        // Construct query basic query
        Query query = mFirestore.collection("restaurants");

        // Category (equality filter)
        if (filters.hasCategory()) {
            query = query.whereEqualTo("category", filters.getCategory());
        }

        // City (equality filter)
        if (filters.hasCity()) {
            query = query.whereEqualTo("city", filters.getCity());
        }

        // Price (equality filter)
        if (filters.hasPrice()) {
            query = query.whereEqualTo("price", filters.getPrice());
        }

        // Sort by (orderBy with direction)
        if (filters.hasSortBy()) {
            query = query.orderBy(filters.getSortBy(), filters.getSortDirection());
        }

        // Limit items
        query = query.limit(LIMIT);

        // Update the query
        mQuery = query;

        // Set header
        mCurrentSearchView.setText(Html.fromHtml(filters.getSearchDescription(this)));
        mCurrentSortByView.setText(filters.getOrderDescription(this));

        // Save filters
        mViewModel.setFilters(filters);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_restaurants:
                onAddItemsClicked();
                break;
            case R.id.menu_sign_out:
                AuthUI.getInstance().signOut(this);
                startSignIn();
                break;
            case R.id.menu_invite:
                onInviteClicked();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            mViewModel.setIsSigningIn(false);
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode != RESULT_OK && shouldStartSignIn()) {
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
                showSnackbar(R.string.unknown_sign_in_response);
                startSignIn();
            }
        }
    }

    private void showSnackbar(int message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT);
    }


    @OnClick(R.id.filter_bar)
    public void onFilterClicked() {
        // Show the dialog containing filter options
        mFilterDialog.show(getSupportFragmentManager(), PostsFilterDialogFragment.TAG);
    }

    @OnClick(R.id.floatingActionButton)
    public void submit(View view) {
        mAddRestaurantDialog.show(getSupportFragmentManager(), AddPostDialogFragment.TAG);
        //startActivity(new Intent(this, EventsActivity.class));
    }

    @OnClick(R.id.button_clear_filter)
    public void onClearFilterClicked() {
        mFilterDialog.resetFilters();

        onFilter(PostsFilters.getDefault());
    }

    private boolean shouldStartSignIn() {
        return (!mViewModel.getIsSigningIn() && FirebaseAuth.getInstance().getCurrentUser() ==
                null);
    }

    private void startSignIn() {
        // Sign in with FirebaseUI
        Intent intent = AuthUI.getInstance().createSignInIntentBuilder().setTheme(R.style
                .GreenTheme).setAvailableProviders(Collections.singletonList(new AuthUI.IdpConfig
                .Builder(AuthUI.EMAIL_PROVIDER).build())).setIsSmartLockEnabled(!BuildConfig
                .DEBUG).build();

        startActivityForResult(intent, RC_SIGN_IN);
        mViewModel.setIsSigningIn(true);
    }

    @SuppressWarnings("unused")
    private void showTodoToast() {
        Toast.makeText(this, "TODO: Implement", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
    }
}

package co.wasder.wasder.ui.activity;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.amplitude.api.Amplitude;
import com.crashlytics.android.Crashlytics;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.FeedbackManager;
import net.hockeyapp.android.UpdateManager;
import net.hockeyapp.android.metrics.MetricsManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.wasder.wasder.R;
import co.wasder.wasder.Util.FirebaseUtil;
import co.wasder.wasder.Util.FirestoreItemUtil;
import co.wasder.wasder.data.filter.FirestoreItemFilters;
import co.wasder.wasder.data.model.User;
import co.wasder.wasder.ui.NonSwipeableViewPager;
import co.wasder.wasder.ui.activity.detail.ProfileActivity;
import co.wasder.wasder.ui.dialog.AddEventDialogFragment;
import co.wasder.wasder.ui.dialog.AddFirestoreItemDialogFragment;
import co.wasder.wasder.ui.dialog.Dialogs;
import co.wasder.wasder.ui.dialog.FirestoreItemFilterDialogFragment;
import co.wasder.wasder.ui.fragment.OnFragmentInteractionListener;
import co.wasder.wasder.ui.fragment.tab.TabFragment;
import co.wasder.wasder.ui.pageradapter.SectionsPagerAdapter;
import io.fabric.sdk.android.Fabric;

@Keep
public class WasderActivity extends AppCompatActivity implements LifecycleOwner, NavigationView
        .OnNavigationItemSelectedListener, FirebaseAuth.AuthStateListener,
        FirestoreItemFilterDialogFragment.FilterListener, OnFragmentInteractionListener {

    public static final String TAG = "WasderActivity";
    public static final int RC_SIGN_IN = 9001;
    private static final java.lang.String AMPLITUDE_API_KEY = "937ae55b73eb164890021fe9b2d4fa63";
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.drawer_layout)
    public DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view)
    public NavigationView mNavigationView;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.navigation2)
    public BottomNavigationView mBottomNavigationView;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.container)
    public NonSwipeableViewPager mViewPager;
    public final BottomNavigationView.OnNavigationItemSelectedListener
            mOnNavigationItemSelectedListener = new BottomNavigationView
            .OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();
            switch (id) {
                case R.id.navigation_home:
                    logAmplitudeEvent("Navigation", "BottomNav", "Home");
                    mViewPager.setCurrentItem(0, false);
                    return true;
                case R.id.navigation_live:
                    logAmplitudeEvent("Navigation", "BottomNav", "Live");
                    mViewPager.setCurrentItem(1, false);
                    return true;
                case R.id.navigation_groups:
                    logAmplitudeEvent("Navigation", "BottomNav", "Groups");
                    mViewPager.setCurrentItem(2, false);
                    return true;
                case R.id.navigation_messages:
                    logAmplitudeEvent("Navigation", "BottomNav", "Messages");
                    mViewPager.setCurrentItem(3, false);
                    return true;
                default:
                    return false;
            }
        }
    };
    @BindView(R.id.swipeRefreshLayout)
    public SwipeRefreshLayout mSwipeRefreshLayout;
    @SuppressWarnings("unused")
    public GoogleApiClient mGoogleApiClient;
    public SectionsPagerAdapter mSectionsPagerAdapter;
    public WasderActivityViewModel mViewModel;
    @SuppressWarnings("unused")
    public ActionBarDrawerToggle toggle;
    public FirestoreItemFilterDialogFragment mFilterDialog;
    public boolean enableCrashButton = false;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_wasder);
        ButterKnife.bind(this);

        MetricsManager.register(getApplication());
        MetricsManager.trackEvent("WasderActivity");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            Amplitude.getInstance()
                    .initialize(this, AMPLITUDE_API_KEY)
                    .enableForegroundTracking(getApplication())
                    .setUserId(userId);
            Amplitude.getInstance().trackSessionEvents(true);
            long sessionId = Amplitude.getInstance().getSessionId();
        }

        logAmplitudeEvent("App Open", "KEY", "VALUE");

        // add this wherever you want to track a custom event and attach properties or
        // measurements to it
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Property1", "Value1");
        HashMap<String, Double> measurements = new HashMap<>();
        measurements.put("Measurement1", 1.0);

        MetricsManager.trackEvent("YOUR_EVENT_NAME", properties, measurements);

        checkForUpdates();

        // View model
        mViewModel = ViewModelProviders.of(this).get(WasderActivityViewModel.class);
        //mNavigationView.setNavigationItemSelectedListener(this);
        /*ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R
                .string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();*/
        mBottomNavigationView.setOnNavigationItemSelectedListener
                (mOnNavigationItemSelectedListener);

        //appBarLayout.post(createRunnable(appBarLayout, mAnimationListener));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.addOnMenuVisibilityListener(new ActionBar.OnMenuVisibilityListener() {
                @Override
                public void onMenuVisibilityChanged(boolean isVisible) {
                    Log.d(TAG, "onMenuVisibilityChanged: " + isVisible);
                }
            });
        }
        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R
                .string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mSectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        Log.d(TAG, "onCreate: SectionAdapterCount" + mSectionsPagerAdapter.getCount());
        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams)
                mBottomNavigationView
                .getLayoutParams();

        mFilterDialog = Dialogs.PostsFilterDialogFragment();
        @SuppressWarnings("unused") final AddFirestoreItemDialogFragment mAddPostDialog = Dialogs
                .AddPostDialogFragment();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "onRefresh: ");
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        FeedbackManager.register(this);

        if (enableCrashButton) {
            Button crashButton = new Button(this);
            crashButton.setText("Crash!");
            crashButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Crashlytics.getInstance().crash(); // Force a crash
                }
            });
            addContentView(crashButton, new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                    .MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        String idToken = FirebaseInstanceId.getInstance().getToken();
        FirebaseMessaging.getInstance().subscribeToTopic("news");
    }

    private void logAmplitudeEvent(@NonNull String eventType, @NonNull String key, @NonNull
            String value) {
        JSONObject eventProperties = createEventProperties(key, value);
        Amplitude.getInstance().logEvent(eventType, eventProperties);
    }

    @NonNull
    private JSONObject createEventProperties(@NonNull String key, @NonNull String value) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(key, value);
        } catch (JSONException exception) {
            exception.getStackTrace();
        }
        return eventProperties;
    }

    public void checkForUpdates() {
        // Remove this for store builds!
        UpdateManager.register(this);
    }

    public void unregisterManagers() {
        UpdateManager.unregister();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterManagers();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterManagers();
    }

    @OnClick(R.id.fab)
    public void submit(@SuppressWarnings("unused") View view) {
        /**/
        /**/
        if (mViewPager.getCurrentItem() == 0) {
            new AddFirestoreItemDialogFragment().show(getSupportFragmentManager(),
                    AddFirestoreItemDialogFragment.TAG);
        } else if (mViewPager.getCurrentItem() == 1) {
            new AddEventDialogFragment().show(getSupportFragmentManager(),
                    AddFirestoreItemDialogFragment.TAG);
        }
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (toggle != null) {
            toggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (toggle != null) {
            toggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: " + "Task is root " + getTaskId());
    }

    @Override
    public void onStart() {
        super.onStart();
        // Start sign in if necessary
        if (FirebaseUtil.shouldStartSignIn(this, mViewModel)) {
            FirebaseUtil.startSignIn(this, mViewModel, RC_SIGN_IN);
            //noinspection UnnecessaryReturnStatement
            return;
        } else {
            if (firebaseAuth != null) {
                if (firebaseUser != null) {
                    //User newUser = new User(user, "Ahmed", "AlAskalany");
                    User newUser = new User(firebaseUser, firebaseUser.getDisplayName(), "");
                    newUser.addToFirestore();
                }
            }
        }
        Log.d(TAG, "onStart: SectionAdapterCount" + mSectionsPagerAdapter.getCount());
    }

    @Override
    public void onResume() {
        super.onResume();
        checkForCrashes();
    }

    private void setPresenceOnline(boolean online) {
        // Write a message to the database
        String userId = firebaseUser.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        DatabaseReference userRef = myRef.child(userId).child("notificationTokens");
        String token = FirebaseInstanceId.getInstance().getToken();
        Map<String, Object> data = new HashMap<>();
        data.put(token, online);
        userRef.updateChildren(data);

        Map<String, Object> onlineStatus = new HashMap<>();
        onlineStatus.put("online", true);
        myRef.child(userId).updateChildren(onlineStatus);
    }

    public void checkForCrashes() {
        CrashManager.register(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_events:
                FirestoreItemUtil.onAddItemsClicked(this);
                logAmplitudeEvent("Options Menu", "WasderActivity", "Add Events");
                return true;
            case R.id.menu_sign_out:
                logAmplitudeEvent("Options Menu", "WasderActivity", "Sign Out");
                AuthUI.getInstance().signOut(this);
                FirebaseUtil.startSignIn(this, mViewModel, RC_SIGN_IN);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            logAmplitudeEvent("Navigation", "NavDrawer", "Profile");
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("user-reference", FirebaseAuth.getInstance().getCurrentUser().getUid());
            startActivity(intent);
        } else if (id == R.id.nav_friends) {
            logAmplitudeEvent("Navigation", "NavDrawer", "Friends");
        } else if (id == R.id.nav_followers) {
            logAmplitudeEvent("Navigation", "NavDrawer", "Followers");
        } else if (id == R.id.nav_achievements) {
            logAmplitudeEvent("Navigation", "NavDrawer", "Achievements");
        } else if (id == R.id.nav_settings_account) {
            logAmplitudeEvent("Navigation", "NavDrawer", "Account");
        } else if (id == R.id.nav_settings_notifications) {
            logAmplitudeEvent("Navigation", "NavDrawer", "Notifications");
        } else if (id == R.id.nav_drawer_feedback) {
            logAmplitudeEvent("Navigation", "NavDrawer", "Feedback");
            FeedbackManager.showFeedbackActivity(WasderActivity.this);
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        logAmplitudeEvent("Authentication", "State", "Changed");
        FirebaseUser user = firebaseAuth.getCurrentUser();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*callbackManager.onActivityResult(requestCode, resultCode, data);*/
        if (requestCode == RC_SIGN_IN) {
            mViewModel.setIsSigningIn(false);
            if (resultCode != RESULT_OK && FirebaseUtil.shouldStartSignIn(this, mViewModel)) {
                FirebaseUtil.startSignIn(this, mViewModel, RC_SIGN_IN);
            }
        }
    }

    @Override
    public void onFragmentInteractionListener(Uri uri) {

    }

    @Override
    public void onFilter(FirestoreItemFilters firestoreItemFilters) {
        TabFragment fragment = (TabFragment) getSupportFragmentManager().findFragmentById(R.id
                .nestedScrollView_appbar);
        if (fragment != null) {
            //fragment.onFilter(firestoreItemFilters);
        }
    }

}

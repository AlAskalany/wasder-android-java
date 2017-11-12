package co.wasder.wasder.ui;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
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

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.FeedbackManager;
import net.hockeyapp.android.UpdateManager;
import net.hockeyapp.android.metrics.MetricsManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.wasder.wasder.BuildConfig;
import co.wasder.wasder.R;
import co.wasder.wasder.Util.FirestoreItemUtil;
import co.wasder.wasder.data.filter.FirestoreItemFilters;
import co.wasder.wasder.data.model.User;
import co.wasder.wasder.ui.detail.ProfileActivity;
import co.wasder.wasder.ui.dialog.AddEventDialogFragment;
import co.wasder.wasder.ui.dialog.AddFirestoreItemDialogFragment;
import co.wasder.wasder.ui.dialog.Dialogs;
import co.wasder.wasder.ui.dialog.FirestoreItemFilterDialogFragment;
import co.wasder.wasder.ui.navigation.FeedNavigationFragment;
import co.wasder.wasder.ui.navigation.GroupsNavigationFragment;
import co.wasder.wasder.ui.navigation.LiveNavigationFragment;
import co.wasder.wasder.ui.navigation.MessagesNavigationFragment;
import co.wasder.wasder.ui.navigation.NavigationFragment;
import co.wasder.wasder.ui.tab.OnFragmentInteractionListener;
import io.fabric.sdk.android.Fabric;

@Keep
public class WasderActivity extends AppCompatActivity implements LifecycleOwner, NavigationView
        .OnNavigationItemSelectedListener, FirebaseAuth.AuthStateListener,
        FirestoreItemFilterDialogFragment.FilterListener, OnFragmentInteractionListener {

    public static final String TAG = "WasderActivity";
    public static final int RC_SIGN_IN = 9001;
    private static final java.lang.String AMPLITUDE_API_KEY = "937ae55b73eb164890021fe9b2d4fa63";
    @Nullable
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.drawer_layout)
    public DrawerLayout mDrawerLayout;
    @Nullable
    @BindView(R.id.nav_view)
    public NavigationView mNavigationView;
    @Nullable
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.navigation2)
    public BottomNavigationView mBottomNavigationView;
    @Nullable
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.container)
    public NonSwipeableViewPager mViewPager;
    public final BottomNavigationView.OnNavigationItemSelectedListener
            mOnNavigationItemSelectedListener = new BottomNavigationView
            .OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
            final int id = item.getItemId();
            switch (id) {
                case R.id.navigation_home:
                    mViewPager.setCurrentItem(0, false);
                    return true;
                case R.id.navigation_live:
                    mViewPager.setCurrentItem(1, false);
                    return true;
                case R.id.navigation_groups:
                    mViewPager.setCurrentItem(2, false);
                    return true;
                case R.id.navigation_messages:
                    mViewPager.setCurrentItem(3, false);
                    return true;
                default:
                    return false;
            }
        }
    };
    @Nullable
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
    @Nullable
    private FirebaseUser firebaseUser;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_wasder);
        ButterKnife.bind(this);
        MetricsManager.register(getApplication());
        MetricsManager.trackEvent("WasderActivity");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            final String userId = firebaseUser.getUid();
            Amplitude.getInstance()
                    .initialize(this, AMPLITUDE_API_KEY)
                    .enableForegroundTracking(getApplication())
                    .setUserId(userId);
            Amplitude.getInstance().trackSessionEvents(true);
            final long sessionId = Amplitude.getInstance().getSessionId();
        }
        final Map<String, String> properties = new HashMap<>();
        properties.put("Property1", "Value1");
        final Map<String, Double> measurements = new HashMap<>();
        measurements.put("Measurement1", 1.0);
        MetricsManager.trackEvent("YOUR_EVENT_NAME", properties, measurements);
        checkForUpdates();
        mViewModel = ViewModelProviders.of(this).get(WasderActivityViewModel.class);
        mBottomNavigationView.setOnNavigationItemSelectedListener
                (mOnNavigationItemSelectedListener);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.addOnMenuVisibilityListener(new ActionBar.OnMenuVisibilityListener() {
                @Override
                public void onMenuVisibilityChanged(final boolean isVisible) {
                    Log.d(TAG, "onMenuVisibilityChanged: " + isVisible);
                }
            });
        }
        mDrawerLayout = findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        final NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mSectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        Log.d(TAG, "onCreate: SectionAdapterCount" + mSectionsPagerAdapter.getCount());
        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        final CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams)
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
            final Button crashButton = new Button(this);
            crashButton.setText("Crash!");
            crashButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(final View view) {
                    Crashlytics.getInstance().crash(); // Force a crash
                }
            });
            addContentView(crashButton, new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                    .MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    public void checkForUpdates() {
        // Remove this for store builds!
        UpdateManager.register(this);
    }

    public static void unregisterManagers() {
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
    public void submit(@SuppressWarnings("unused") final View view) {
        /**/
        /**/
        if (mViewPager.getCurrentItem() == 0) {
            Dialogs.AddPostDialogFragment()
                    .show(getSupportFragmentManager(), AddFirestoreItemDialogFragment.TAG);
        } else if (mViewPager.getCurrentItem() == 1) {
            new AddEventDialogFragment().show(getSupportFragmentManager(),
                    AddFirestoreItemDialogFragment.TAG);
        }
    }

    @Override
    public void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (toggle != null) {
            toggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (toggle != null) {
            toggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        if ((!mViewModel.getIsSigningIn() && FirebaseAuth.getInstance().getCurrentUser() == null)) {
            // Sign in with FirebaseUI
            final Intent intent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setTheme(R.style.GreenTheme)
                    .setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI
                            .EMAIL_PROVIDER)
                            .build(), new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                    .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                    .build();

            startActivityForResult(intent, RC_SIGN_IN);
            mViewModel.setIsSigningIn(true);
            return;
        } else {
            if (firebaseAuth != null) {
                if (firebaseUser != null) {
                    final User newUser = new User(firebaseUser, firebaseUser.getDisplayName(), "");
                    newUser.addToFirestore();
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkForCrashes();
    }

    public void checkForCrashes() {
        CrashManager.register(this);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
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
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_events:
                FirestoreItemUtil.onAddItemsClicked(this);
                return true;
            case R.id.menu_sign_out:
                AuthUI.getInstance().signOut(this);
                // Sign in with FirebaseUI
                final Intent intent = AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.GreenTheme)
                        .setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI
                                .EMAIL_PROVIDER)
                                .build(), new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                        .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                        .build();

                startActivityForResult(intent, RC_SIGN_IN);
                mViewModel.setIsSigningIn(true);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        final int id = item.getItemId();
        if (id == R.id.nav_profile) {
            final Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("user-reference", FirebaseAuth.getInstance().getCurrentUser().getUid());
            startActivity(intent);
        } else if (id == R.id.nav_friends) {
        } else if (id == R.id.nav_followers) {
        } else if (id == R.id.nav_achievements) {
        } else if (id == R.id.nav_settings_account) {
        } else if (id == R.id.nav_settings_notifications) {
        } else if (id == R.id.nav_drawer_feedback) {
            FeedbackManager.showFeedbackActivity(WasderActivity.this);
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent
            data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            mViewModel.setIsSigningIn(false);
            if (resultCode != RESULT_OK && (!mViewModel.getIsSigningIn() && FirebaseAuth
                    .getInstance()
                    .getCurrentUser() == null)) {
                // Sign in with FirebaseUI
                final Intent intent = AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.GreenTheme)
                        .setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI
                                .EMAIL_PROVIDER)
                                .build(), new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                        .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                        .build();

                startActivityForResult(intent, RC_SIGN_IN);
                mViewModel.setIsSigningIn(true);
            }
        }
    }

    @Override
    public void onFragmentInteractionListener(final Uri uri) {
    }

    @Override
    public void onFilter(final FirestoreItemFilters firestoreItemFilters) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    @Keep
    public static class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(@SuppressWarnings("unused") final WasderActivity activity,
                                    final FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(final int position) {
            final NavigationFragment fragment;
            switch (position) {
                case 0:
                    fragment = FeedNavigationFragment.newInstance(0);
                    break;
                case 1:
                    fragment = LiveNavigationFragment.newInstance(1);
                    break;
                case 2:
                    fragment = GroupsNavigationFragment.newInstance(2);
                    break;
                case 3:
                    fragment = MessagesNavigationFragment.newInstance(3);
                    break;
                default:
                    fragment = null;
                    break;
            }
            return (Fragment) fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }
    }
}

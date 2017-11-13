package co.wasder.wasder.activity;

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
import co.wasder.data.filter.FirestoreItemFilters;
import co.wasder.data.model.User;
import co.wasder.ui.viewmodel.WasderActivityViewModel;
import co.wasder.wasder.BuildConfig;
import co.wasder.wasder.R;
import co.wasder.wasder.dialogfragment.AddEventDialogFragment;
import co.wasder.wasder.dialogfragment.AddFirestoreItemDialogFragment;
import co.wasder.wasder.dialogfragment.FirestoreItemFilterDialogFragment;
import co.wasder.wasder.fragment.navigation.FeedNavigationFragment;
import co.wasder.wasder.fragment.navigation.GroupsNavigationFragment;
import co.wasder.wasder.fragment.navigation.LiveNavigationFragment;
import co.wasder.wasder.fragment.navigation.MessagesNavigationFragment;
import co.wasder.wasder.listener.OnFragmentInteractionListener;
import co.wasder.wasder.util.Dialogs;
import co.wasder.wasder.util.FirestoreItemUtil;
import co.wasder.wasder.viewpager.NonSwipeableViewPager;
import io.fabric.sdk.android.Fabric;

@Keep
public class WasderActivity extends AppCompatActivity implements LifecycleOwner, NavigationView
        .OnNavigationItemSelectedListener, FirebaseAuth.AuthStateListener,
        FirestoreItemFilterDialogFragment.FilterListener, OnFragmentInteractionListener {

    private static final String TAG = "WasderActivity";
    private static final int RC_SIGN_IN = 9001;
    private static final java.lang.String AMPLITUDE_API_KEY = "937ae55b73eb164890021fe9b2d4fa63";
    @Nullable
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @Nullable
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;
    @Nullable
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.navigation2)
    BottomNavigationView mBottomNavigationView;
    @Nullable
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.container)
    NonSwipeableViewPager mViewPager;
    private final BottomNavigationView.OnNavigationItemSelectedListener
            mOnNavigationItemSelectedListener = new BottomNavigationView
            .OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
            final int id = item.getItemId();
            assert mViewPager != null;
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
    SwipeRefreshLayout mSwipeRefreshLayout;
    @SuppressWarnings("unused")
    private GoogleApiClient mGoogleApiClient;
    private WasderActivityViewModel mViewModel;
    @SuppressWarnings("unused")
    private ActionBarDrawerToggle toggle;
    private FirebaseAuth firebaseAuth;
    @Nullable
    private FirebaseUser firebaseUser;

    private static void unregisterManagers() {
        UpdateManager.unregister();
    }

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
        }
        final Map<String, String> properties = new HashMap<>();
        properties.put("Property1", "Value1");
        final Map<String, Double> measurements = new HashMap<>();
        measurements.put("Measurement1", 1.0);
        MetricsManager.trackEvent("YOUR_EVENT_NAME", properties, measurements);
        // Remove this for store builds!
        UpdateManager.register(this);
        mViewModel = ViewModelProviders.of(this).get(WasderActivityViewModel.class);
        assert mBottomNavigationView != null;
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
        assert mDrawerLayout != null;
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        final NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(this,
                getSupportFragmentManager());
        Log.d(TAG, "onCreate: SectionAdapterCount" + mSectionsPagerAdapter.getCount());
        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        @SuppressWarnings("unused") final AddFirestoreItemDialogFragment mAddPostDialog = Dialogs
                .AddPostDialogFragment();
        assert mSwipeRefreshLayout != null;
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "onRefresh: ");
                assert mSwipeRefreshLayout != null;
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        FeedbackManager.register(this);
        boolean enableCrashButton = false;
        //noinspection ConstantConditions
        if (enableCrashButton) {
            final Button crashButton = new Button(this);
            crashButton.setText(R.string.crash);
            crashButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    Crashlytics.getInstance().crash(); // Force a crash
                }
            });
            addContentView(crashButton, new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                    .MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
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
        assert mViewPager != null;
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
        CrashManager.register(this);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        assert mDrawerLayout != null;
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
                        .setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER)
                                .build(), new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER)
                                .build()))
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
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            assert currentUser != null;
            intent.putExtra("user-reference", currentUser.getUid());
            startActivity(intent);
        } else if (id == R.id.nav_friends) {
        } else if (id == R.id.nav_followers) {
        } else if (id == R.id.nav_achievements) {
        } else if (id == R.id.nav_settings_account) {
        } else if (id == R.id.nav_settings_notifications) {
        } else if (id == R.id.nav_drawer_feedback) {
            FeedbackManager.showFeedbackActivity(this);
        }

        assert mDrawerLayout != null;
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
                        .setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER)
                                .build(), new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER)
                                .build()))
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
    private static class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(@SuppressWarnings("unused") final WasderActivity activity, final
        FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(final int position) {
            switch (position) {
                case 0:
                    return FeedNavigationFragment.newInstance(0);
                case 1:
                    return LiveNavigationFragment.newInstance(1);
                case 2:
                    return GroupsNavigationFragment.newInstance(2);
                case 3:
                    return MessagesNavigationFragment.newInstance(3);
                default:
                    return FeedNavigationFragment.newInstance(0);
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }
    }

}

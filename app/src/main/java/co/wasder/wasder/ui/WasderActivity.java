package co.wasder.wasder.ui;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.amplitude.api.Amplitude;
import com.crashlytics.android.Crashlytics;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.FeedbackManager;
import net.hockeyapp.android.UpdateManager;
import net.hockeyapp.android.metrics.MetricsManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import co.wasder.wasder.BuildConfig;
import co.wasder.wasder.R;
import co.wasder.wasder.data.FirestoreItemFilters;
import co.wasder.wasder.data.User;
import co.wasder.wasder.databinding.ActivityWasderBinding;
import io.fabric.sdk.android.Fabric;

@Keep
public class WasderActivity extends AppCompatActivity
        implements LifecycleOwner,
                NavigationView.OnNavigationItemSelectedListener,
                FirebaseAuth.AuthStateListener,
                FirestoreItemFilterDialogFragment.FilterListener,
                OnFragmentInteractionListener {

    private static final String TAG = "WasderActivity";
    private static final int RC_SIGN_IN = 9001;
    private static final java.lang.String AMPLITUDE_API_KEY = "937ae55b73eb164890021fe9b2d4fa63";
    private WasderActivityViewModel mViewModel;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private ActivityWasderBinding binding;
    private final BottomNavigationView.OnNavigationItemSelectedListener
            mOnNavigationItemSelectedListener =
                    new BottomNavigationView.OnNavigationItemSelectedListener() {

                        @Override
                        public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
                            final int id = item.getItemId();
                            switch (id) {
                                case R.id.navigation_home:
                                    binding.include.container.setCurrentItem(0, false);
                                    return true;
                                case R.id.navigation_live:
                                    binding.include.container.setCurrentItem(1, false);
                                    return true;
                                case R.id.navigation_groups:
                                    binding.include.container.setCurrentItem(2, false);
                                    return true;
                                case R.id.navigation_messages:
                                    binding.include.container.setCurrentItem(3, false);
                                    return true;
                                case R.id.navigation_add:
                                    if (binding.include.container.getCurrentItem() == 0) {
                                        Dialogs.AddPostDialogFragment()
                                                .show(
                                                        getSupportFragmentManager(),
                                                        addFirestoreItemDialogFragment.TAG);
                                    } else if (binding.include.container.getCurrentItem() == 1) {
                                        new AddEventDialogFragment()
                                                .show(
                                                        getSupportFragmentManager(),
                                                        addFirestoreItemDialogFragment.TAG);
                                    }
                                default:
                                    return false;
                            }
                        }
                    };
    private ActionBarDrawerToggle toggle;

    private static void unregisterManagers() {
        UpdateManager.unregister();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_wasder);
        binding.setActivity(this);

        Fabric.with(this, new Crashlytics());
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
        binding.include.bottomNavigationView.setOnNavigationItemSelectedListener(
                mOnNavigationItemSelectedListener);
        setSupportActionBar(binding.include.toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.addOnMenuVisibilityListener(
                    new ActionBar.OnMenuVisibilityListener() {
                        @Override
                        public void onMenuVisibilityChanged(final boolean isVisible) {
                            Log.d(TAG, "onMenuVisibilityChanged: " + isVisible);
                        }
                    });
        }
        toggle =
                new ActionBarDrawerToggle(
                        this,
                        binding.drawerLayout,
                        binding.include.toolbar,
                        R.string.navigation_drawer_open,
                        R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        binding.navView.setNavigationItemSelectedListener(this);
        SectionsPagerAdapter mSectionsPagerAdapter =
                new SectionsPagerAdapter(this, getSupportFragmentManager());
        Log.d(TAG, "onCreate: SectionAdapterCount" + mSectionsPagerAdapter.getCount());
        // Set up the ViewPager with the sections adapter.
        binding.include.container.setOffscreenPageLimit(3);
        binding.include.container.setAdapter(mSectionsPagerAdapter);
        @SuppressWarnings("unused")
        final addFirestoreItemDialogFragment mAddPostDialog = Dialogs.AddPostDialogFragment();
        binding.include.swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.d(TAG, "onRefresh: ");
                        binding.include.swipeRefreshLayout.setRefreshing(false);
                    }
                });
        FeedbackManager.register(this);
        boolean enableCrashButton = false;
        //noinspection ConstantConditions
        if (enableCrashButton) {
            final Button crashButton = new Button(this);
            crashButton.setText(R.string.crash);
            crashButton.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(final View view) {
                            Crashlytics.getInstance().crash(); // Force a crash
                        }
                    });
            addContentView(
                    crashButton,
                    new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        setOnlineStatus("true");
    }

    private void setOnlineStatus(String status) {
        if (firebaseUser != null) {
            String uid = firebaseUser.getUid();
            FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(uid)
                    .child("online")
                    .setValue(status);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterManagers();
        setOnlineStatus("false");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterManagers();
        setOnlineStatus("false");
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
            final Intent intent =
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setTheme(R.style.GreenTheme)
                            .setAvailableProviders(
                                    Arrays.asList(
                                            new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER)
                                                    .build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER)
                                                    .build()))
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
        assert binding.drawerLayout != null;
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
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
                final Intent intent =
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setTheme(R.style.GreenTheme)
                                .setAvailableProviders(
                                        Arrays.asList(
                                                new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER)
                                                        .build(),
                                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER)
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

        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {}

    @Override
    protected void onActivityResult(
            final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            mViewModel.setIsSigningIn(false);
            if (resultCode != RESULT_OK
                    && (!mViewModel.getIsSigningIn()
                            && FirebaseAuth.getInstance().getCurrentUser() == null)) {
                // Sign in with FirebaseUI
                final Intent intent =
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setTheme(R.style.GreenTheme)
                                .setAvailableProviders(
                                        Arrays.asList(
                                                new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER)
                                                        .build(),
                                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER)
                                                        .build()))
                                .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                                .build();

                startActivityForResult(intent, RC_SIGN_IN);
                mViewModel.setIsSigningIn(true);
            }
        }
    }

    @Override
    public void onFragmentInteractionListener(final Uri uri) {}

    @Override
    public void onFilter(final FirestoreItemFilters firestoreItemFilters) {}

    public void onClickAdd(View view) {
        assert binding.include.container != null;
        if (binding.include.container.getCurrentItem() == 0) {
            /*co.wasder.wasder.ui.Dialogs.AddPostDialogFragment()
            .show(getSupportFragmentManager(), addFirestoreItemDialogFragment.TAG);*/
            final BottomSheetDialog controlCenter = new BottomSheetDialog(this);
            View controlCenterView =
                    getLayoutInflater().inflate(R.layout.bottom_sheet_control_center, null);
            controlCenter.setContentView(controlCenterView);
            controlCenter.show();
        } else if (binding.include.container.getCurrentItem() == 1) {
            /*new AddEventDialogFragment().show(getSupportFragmentManager(),
            addFirestoreItemDialogFragment.TAG);*/
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the
     * sections/tabs/pages.
     */
    @Keep
    private static class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(
                @SuppressWarnings("unused") final WasderActivity activity,
                final FragmentManager fm) {
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

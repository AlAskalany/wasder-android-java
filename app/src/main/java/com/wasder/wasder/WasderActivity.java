package com.wasder.wasder;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.wasder.wasder.ui.NavigationFragment;
import com.wasder.wasder.ui.TabFragment;
import com.wasder.wasder.viewmodel.WasderActivityViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WasderActivity extends AppCompatActivity implements LifecycleOwner, NavigationView
        .OnNavigationItemSelectedListener, FirebaseAuth.AuthStateListener, NavigationFragment
        .OnFragmentInteractionListener, TabFragment.OnFragmentInteractionListener {

    @SuppressWarnings("unused")
    private static final String TAG = "WasderActivity";
    private static final int RC_SIGN_IN = 9001;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;
    @BindView(R.id.navigation2)
    BottomNavigationView mBottomNavigationView;
    @BindView(R.id.container_navigation_fragment)
    FrameLayout mNavigationFragmentContainer;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private final BottomNavigationView.OnNavigationItemSelectedListener
            mOnNavigationItemSelectedListener = new BottomNavigationView
            .OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            NavigationFragment currentFragment = (NavigationFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.container_navigation_fragment);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            int id = item.getItemId();
            switch (id) {
                case R.id.navigation_home:
                    if (currentFragment != null && currentFragment.getTag() != "Home") {
                        fragmentManager.saveFragmentInstanceState(currentFragment);
                        // Create new fragment and transaction
                        Fragment home = mSectionsPagerAdapter.getItem(0);
                        // Replace whatever is in the fragment_container view with this fragment,
                        // and add the transaction to the back stack
                        transaction.replace(R.id.container_navigation_fragment, home);
                        transaction.addToBackStack(null);
                        // Commit the transaction
                        transaction.commit();
                    }
                    return true;
                case R.id.navigation_live:
                    if (currentFragment != null && currentFragment.getTag() != "Live") {
                        fragmentManager.saveFragmentInstanceState(currentFragment);
                        // Create new fragment and transaction
                        Fragment live = mSectionsPagerAdapter.getItem(1);
                        // Replace whatever is in the fragment_container view with this fragment,
                        // and add the transaction to the back stack
                        transaction.replace(R.id.container_navigation_fragment, live);
                        transaction.addToBackStack(null);
                        // Commit the transaction
                        transaction.commit();
                    }
                    return true;
                case R.id.navigation_groups:
                    if (currentFragment != null && currentFragment.getTag() != "Live") {
                        fragmentManager.saveFragmentInstanceState(currentFragment);
                        // Create new fragment and transaction
                        Fragment groups = mSectionsPagerAdapter.getItem(1);
                        // Replace whatever is in the fragment_container view with this fragment,
                        // and add the transaction to the back stack
                        transaction.replace(R.id.container_navigation_fragment, groups);
                        transaction.addToBackStack(null);
                        // Commit the transaction
                        transaction.commit();
                    }
                    return true;
                case R.id.navigation_messages:
                    if (currentFragment != null && currentFragment.getTag() != "Live") {
                        fragmentManager.saveFragmentInstanceState(currentFragment);
                        // Create new fragment and transaction
                        Fragment messages = mSectionsPagerAdapter.getItem(1);
                        // Replace whatever is in the fragment_container view with this fragment,
                        // and add the transaction to the back stack
                        transaction.replace(R.id.container_navigation_fragment, messages);
                        transaction.addToBackStack(null);
                        // Commit the transaction
                        transaction.commit();
                    }
                    return true;
                default:
                    return false;
            }
        }
    };
    private WasderActivityViewModel mViewModel;
    private Fragment mCurrentFragment;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wasder);
        ButterKnife.bind(this);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        NavigationFragment home = mSectionsPagerAdapter.getItem(0);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction t = fm.beginTransaction();
        ViewGroup viewGroup = findViewById(R.id.container_navigation_fragment);
        t.add(R.id.container_navigation_fragment, home, "Home");
        t.commit();
        Log.d(TAG, "onCreate: SectionAdapterCount" + mSectionsPagerAdapter.getCount());


        // View model
        mViewModel = ViewModelProviders.of(this).get(WasderActivityViewModel.class);
        //mNavigationView.setNavigationItemSelectedListener(this);
        /*ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R
                .string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();*/
        mBottomNavigationView.setOnNavigationItemSelectedListener
                (mOnNavigationItemSelectedListener);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        //tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
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
    public void onStart() {
        super.onStart();
        // Start sign in if necessary
        if (shouldStartSignIn()) {
            startSignIn();
            //noinspection UnnecessaryReturnStatement
            return;
        }
        Log.d(TAG, "onStart: SectionAdapterCount" + mSectionsPagerAdapter.getCount());
    }

    private boolean shouldStartSignIn() {
        return (!mViewModel.getIsSigningIn() && FirebaseAuth.getInstance()
                .getCurrentUser() == null);
    }

    private void startSignIn() {
        // Sign in with FirebaseUI
        Intent intent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.GreenTheme)
                .setAvailableProviders(Collections.singletonList(new AuthUI.IdpConfig.Builder
                        (AuthUI.EMAIL_PROVIDER)
                        .build()))
                .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                .build();

        startActivityForResult(intent, RC_SIGN_IN);
        mViewModel.setIsSigningIn(true);
    }

    /*@Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_events:
                //onAddItemsClicked();
                return true;
            case R.id.menu_sign_out:
                AuthUI.getInstance().signOut(this);
                startSignIn();
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
            // Handle the camera action
            startActivity(new Intent(this, TabbedActivity.class));
        } else if (id == R.id.nav_friends) {

        } else if (id == R.id.nav_followers) {

        } else if (id == R.id.nav_achievements) {

        } else if (id == R.id.nav_settings_account) {

        } else if (id == R.id.nav_settings_notifications) {

        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            mViewModel.setIsSigningIn(false);
            if (resultCode != RESULT_OK && shouldStartSignIn()) {
                startSignIn();
            }
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<NavigationFragment> mNavFragments = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public NavigationFragment getItem(int position) {
            NavigationFragment fragment;
            switch (position) {
                case 0:
                    fragment = NavigationFragment.newInstance(0, NavigationFragment.SectionType
                            .HOME);
                    break;
                case 1:
                    fragment = NavigationFragment.newInstance(1, NavigationFragment.SectionType
                            .LIVE);
                    break;
                case 2:
                    fragment = NavigationFragment.newInstance(2, NavigationFragment.SectionType
                            .GROUPS);
                    break;
                case 3:
                    fragment = NavigationFragment.newInstance(3, NavigationFragment.SectionType
                            .MESSAGES);
                    break;
                default:
                    fragment = NavigationFragment.newInstance(0, NavigationFragment.SectionType
                            .HOME);
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }
    }
}

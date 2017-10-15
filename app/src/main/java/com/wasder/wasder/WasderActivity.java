package com.wasder.wasder;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wasder.wasder.Util.EventUtil;
import com.wasder.wasder.model.Event;
import com.wasder.wasder.ui.NavigationFragment;
import com.wasder.wasder.ui.TabFragment;
import com.wasder.wasder.viewmodel.WasderActivityViewModel;

import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WasderActivity extends AppCompatActivity implements LifecycleOwner, NavigationView
        .OnNavigationItemSelectedListener, FirebaseAuth.AuthStateListener, NavigationFragment
        .OnFragmentInteractionListener, TabFragment.OnFragmentInteractionListener {

    @SuppressWarnings("unused")
    private static final String TAG = "WasderActivity";
    private static final int RC_SIGN_IN = 9001;
    private final SparseArrayCompat<NavigationFragment> mNavFragments = new SparseArrayCompat<>();
    private final BottomNavigationView.OnNavigationItemSelectedListener
            mOnNavigationItemSelectedListener = new BottomNavigationView
            .OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if (getCurrentNavigationFragment() != null) {
                getSupportFragmentManager().saveFragmentInstanceState
                        (getCurrentNavigationFragment());
            }
            NavigationFragment fragment = mNavFragments.get(item.getItemId());
            if (fragment != getCurrentNavigationFragment()) {
                //mActionBar.setTitle(fragment.getFragmentTitle());
                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment)
                        .addToBackStack(null).commit();
                return true;
            }
            return false;
        }
    };

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;
    @BindView(R.id.navigation2)
    BottomNavigationView mBottomNavigationView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;
    private WasderActivityViewModel mViewModel;
    private Fragment mCurrentFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wasder);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        // View model
        mViewModel = ViewModelProviders.of(this).get(WasderActivityViewModel.class);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R
                .string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);


        NavigationFragment homeNavigationFragment = new NavigationFragment();
        NavigationFragment liveNavigationFragment = new NavigationFragment();
        NavigationFragment groupsNavigationFragment = new NavigationFragment();
        NavigationFragment messagesNavigationFragment = new NavigationFragment();
        TabFragment feedTab = TabFragment.newInstance("restaurants", "Feed");
        TabFragment twitchLiveTab = TabFragment.newInstance("restaurants", "Twitch Live");
        TabFragment twitchStreamTab = TabFragment.newInstance("restaurants", "Twitch " + "Stream");
        TabFragment esportsTab = TabFragment.newInstance("restaurants", "Esports");
        TabFragment allGroupsTab = TabFragment.newInstance("restaurants", "All");
        TabFragment ownedGroupsTab = TabFragment.newInstance("restaurants", "Owned");
        TabFragment mentionsTab = TabFragment.newInstance("restaurants", "Mentions");
        TabFragment pmTab = TabFragment.newInstance("restaurants", "PM");


        homeNavigationFragment.mTabFragments.add(0, feedTab);
        homeNavigationFragment.mTabFragments.add(1, pmTab);

        liveNavigationFragment.mTabFragments.add(0, twitchLiveTab);
        liveNavigationFragment.mTabFragments.add(1, twitchStreamTab);
        liveNavigationFragment.mTabFragments.add(2, esportsTab);

        groupsNavigationFragment.mTabFragments.add(0, allGroupsTab);
        groupsNavigationFragment.mTabFragments.add(1, ownedGroupsTab);

        messagesNavigationFragment.mTabFragments.add(0, mentionsTab);
        messagesNavigationFragment.mTabFragments.add(1, pmTab);


        // Setup BottomNavigationView with NavigationFragments
        mNavFragments.put(R.id.navigation_home, homeNavigationFragment);
        mNavFragments.put(R.id.navigation_live, liveNavigationFragment);
        mNavFragments.put(R.id.navigation_groups, groupsNavigationFragment);
        mNavFragments.put(R.id.navigation_messages, messagesNavigationFragment);

        getSupportFragmentManager().beginTransaction().add(R.id.container, mNavFragments.get(R.id
                .navigation_home), "Home").addToBackStack(null).commit();


        mBottomNavigationView.setOnNavigationItemSelectedListener
                (mOnNavigationItemSelectedListener);
    }

    private Fragment getCurrentNavigationFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.container);
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

    @OnClick(R.id.fab)
    public void submit(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction
                ("Action", null).show();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_events:
                onAddItemsClicked();
                break;
            case R.id.menu_sign_out:
                AuthUI.getInstance().signOut(this);
                startSignIn();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onAddItemsClicked() {
        // Get a reference to the events collection
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        CollectionReference events = mFirestore.collection("events");

        for (int i = 0; i < 10; i++) {
            // Get a random events POJO
            Event event = EventUtil.getRandom(this);

            // Add a new document to the events collection
            events.add(event);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            // Handle the camera action
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
}

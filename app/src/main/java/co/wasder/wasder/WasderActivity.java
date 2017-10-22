package co.wasder.wasder;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.wasder.wasder.Util.FirebaseUtil;
import co.wasder.wasder.Util.PostUtil;
import co.wasder.wasder.behavior.BottomNavigationViewBehavior;
import co.wasder.wasder.dialog.AddPostDialogFragment;
import co.wasder.wasder.dialog.Dialogs;
import co.wasder.wasder.dialog.PostsFilterDialogFragment;
import co.wasder.wasder.filter.PostsFilters;
import co.wasder.wasder.pageradapter.SectionsPagerAdapter;
import co.wasder.wasder.ui.NavigationFragment;
import co.wasder.wasder.ui.TabFragment;
import co.wasder.wasder.viewmodel.WasderActivityViewModel;


public class WasderActivity extends AppCompatActivity implements LifecycleOwner, NavigationView
        .OnNavigationItemSelectedListener, FirebaseAuth.AuthStateListener, NavigationFragment
        .OnFragmentInteractionListener, TabFragment.OnFragmentInteractionListener,
        PostsFilterDialogFragment.FilterListener {

    private static final String TAG = "WasderActivity";
    private static final int RC_SIGN_IN = 9001;
    public AddPostDialogFragment mAddPostDialog;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;
    @BindView(R.id.navigation2)
    BottomNavigationView mBottomNavigationView;
    @BindView(R.id.container)
    NonSwipeableViewPager mViewPager;
    private final BottomNavigationView.OnNavigationItemSelectedListener
            mOnNavigationItemSelectedListener = new BottomNavigationView
            .OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();
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
    private GoogleApiClient mGoogleApiClient;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private WasderActivityViewModel mViewModel;
    private ActionBarDrawerToggle toggle;
    private PostsFilterDialogFragment mFilterDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wasder);
        ButterKnife.bind(this);

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
        actionBar.addOnMenuVisibilityListener(new ActionBar.OnMenuVisibilityListener() {
            @Override
            public void onMenuVisibilityChanged(boolean isVisible) {
                Log.d(TAG, "onMenuVisibilityChanged: " + isVisible);
            }
        });
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
        layoutParams.setBehavior(new BottomNavigationViewBehavior());
        mFilterDialog = Dialogs.PostsFilterDialogFragment();
        mAddPostDialog = Dialogs.AddPostDialogFragment();
    }

    @OnClick(R.id.filter_bar)
    public void onFilterClicked() {
        // Show the dialog containing filter options
        mFilterDialog.show(getSupportFragmentManager(), PostsFilterDialogFragment.TAG);
    }

    @OnClick(R.id.fab)
    public void submit(View view) {
        new AddPostDialogFragment().show(getSupportFragmentManager(), AddPostDialogFragment.TAG);
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
        if (FirebaseUtil.shouldStartSignIn(this, mViewModel)) {
            FirebaseUtil.startSignIn(this, mViewModel, RC_SIGN_IN);
            //noinspection UnnecessaryReturnStatement
            return;
        }
        Log.d(TAG, "onStart: SectionAdapterCount" + mSectionsPagerAdapter.getCount());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_events:
                PostUtil.onAddItemsClicked(this);
                return true;
            case R.id.menu_sign_out:
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
        /*callbackManager.onActivityResult(requestCode, resultCode, data);*/
        if (requestCode == RC_SIGN_IN) {
            mViewModel.setIsSigningIn(false);
            if (resultCode != RESULT_OK && FirebaseUtil.shouldStartSignIn(this, mViewModel)) {
                FirebaseUtil.startSignIn(this, mViewModel, RC_SIGN_IN);
            }
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onFilter(PostsFilters postsFilters) {
        TabFragment fragment = (TabFragment) getSupportFragmentManager().findFragmentById(R.id
                .nestedSctollView_appbar);
        if (fragment != null) {
            fragment.onFilter(postsFilters);
        }
    }

}

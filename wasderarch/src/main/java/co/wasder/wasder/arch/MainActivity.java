package co.wasder.wasder.arch;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.wasder.wasder.arch.data.source.firestoredb.FirebaseManager;

public class MainActivity extends AppCompatActivity implements NavigationView
        .OnNavigationItemSelectedListener {

    private static final String EXTRA_IDP_RESPONSE = "extra_idp_response";

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    View headerLayout;

    private ImageView navHeaderImageView;
    private TextView navHeaderTitle;
    private TextView navHeaderSubtitle;

    private MainActivityModel model;

    public static Intent createIntent(Context context, IdpResponse idpResponse) {

        Intent startIntent = new Intent();
        if (idpResponse != null) {
            startIntent.putExtra(EXTRA_IDP_RESPONSE, idpResponse);
        }

        return startIntent.setClass(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        model = ViewModelProviders.of(this).get(MainActivityModel.class);
        model.isSignedIn().observe(this, isSignedIn -> {

        });

        model.getUser().observe(this, user -> {

        });

        setSupportActionBar(toolbar);

        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action",
                Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show());

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string
                .navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        headerLayout = navigationView.getHeaderView(0);
        navHeaderImageView = headerLayout.findViewById(R.id.navHeaderImageView);
        navHeaderTitle = headerLayout.findViewById(R.id.navHeaderTitle);
        navHeaderSubtitle = headerLayout.findViewById(R.id.navHeaderSubtitle);
        // TODO check getDisplayName before using
        navHeaderTitle.setText(model.getUser().getValue().getDisplayName());
        navHeaderSubtitle.setText(model.getUser().getValue().getEmail());

        FirebaseManager jobManager = new FirebaseManager(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        // TODO check isAnonymous before using
        boolean anonymous = model.getUser().getValue().isAnonymous() == Boolean.TRUE;
        menu.findItem(R.id.action_sign_up).setVisible(anonymous);
        menu.findItem(R.id.action_sign_out).setVisible(!anonymous);
        menu.findItem(R.id.action_settings).setVisible(!anonymous);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_sign_out) {
            signOut();
        } else if (id == R.id.action_sign_up) {
            signOut();
        }

        return super.onOptionsItemSelected(item);
    }

    @MainThread
    private void signOut() {
        AuthUI.getInstance().signOut(this).addOnCompleteListener(task -> {
            // user is now signed out
            startActivity(new Intent(MainActivity.this, SplashActivity.class));
            finish();
        });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

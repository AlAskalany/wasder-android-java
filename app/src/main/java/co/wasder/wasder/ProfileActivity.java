package co.wasder.wasder;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    CollapsingToolbarLayout collapsingToolbarLayout;
    ImageView profilePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayout);
        if (user != null) {
            String displayName = user.getDisplayName();
            if (!TextUtils.isEmpty(displayName)) {
                collapsingToolbarLayout.setTitle(displayName);
            }
            profilePhoto = findViewById(R.id.profilePhoto);
            Uri photoUri = user.getPhotoUrl();
            if (photoUri != null) {
                String photoUrl = photoUri.toString();
                if (!TextUtils.isEmpty(photoUrl)) {
                    Glide.with(this)
                            .load(photoUri)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(profilePhoto);
                }
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

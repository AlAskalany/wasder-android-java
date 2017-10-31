package co.wasder.wasder;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import co.wasder.wasder.model.User;

public class ProfileActivity extends AppCompatActivity implements EventListener<DocumentSnapshot> {

    private static final String ARG_USER_REFERENCE = "user-reference";
    private static final String TAG = "ProfileActivity";
    CollapsingToolbarLayout collapsingToolbarLayout;
    ImageView profilePhoto;
    private String mUserReference;
    private FirebaseFirestore mFirestore;
    private DocumentReference mDocumentReference;
    private ListenerRegistration mModelRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mUserReference = extras.getString(ARG_USER_REFERENCE);
        }
        if (mUserReference == null) {
            throw new IllegalArgumentException("Must pass extra " + ARG_USER_REFERENCE);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayout);
        profilePhoto = findViewById(R.id.profilePhoto);

        mFirestore = FirebaseFirestore.getInstance();
        mDocumentReference = mFirestore.collection("users").document(mUserReference);
    }

    @Override
    public void onStart() {
        super.onStart();

        mModelRegistration = mDocumentReference.addSnapshotListener(this);
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

    @Override
    public void onStop() {
        super.onStop();
        if (mModelRegistration != null) {
            mModelRegistration.remove();
            mModelRegistration = null;
        }
    }

    @Override
    public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "restaurant:onEvent", e);
            return;
        }

        onModelLoaded(documentSnapshot.toObject(User.class));
    }

    private void onModelLoaded(User user) {
        collapsingToolbarLayout.setTitle(user.getDisplayName());


        // Background image
        String uuid = user.getPhotoUrl();
        if (uuid != null) {
            Glide.with(this)
                    .load(uuid)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(profilePhoto);
        }
    }
}

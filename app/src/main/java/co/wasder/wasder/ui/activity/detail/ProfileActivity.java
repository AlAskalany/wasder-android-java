package co.wasder.wasder.ui.activity.detail;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import co.wasder.wasder.Utils;

import co.wasder.wasder.R;
import co.wasder.wasder.Util.FirebaseUtil;
import co.wasder.wasder.data.model.AbstractFirestoreItem;
import co.wasder.wasder.data.model.FirestoreItem;
import co.wasder.wasder.data.model.User;
import co.wasder.wasder.network.GlideApp;
import co.wasder.wasder.ui.fragment.tab.adapter.Adapters;
import co.wasder.wasder.ui.fragment.tab.adapter.FirestoreItemsAdapter;
import co.wasder.wasder.ui.views.ProfilePhoto;

@Keep
public class ProfileActivity extends AppCompatActivity implements EventListener<DocumentSnapshot> {

    public static final String ARG_USER_REFERENCE = "user-reference";
    public static final String TAG = "ProfileActivity";
    public CollapsingToolbarLayout collapsingToolbarLayout;
    public ProfilePhoto profilePhoto;
    @Nullable
    public String mUserReference;
    public FirebaseFirestore mFirestore;
    public DocumentReference mDocumentReference;
    @Nullable
    public ListenerRegistration mModelRegistration;
    public ProfileActivityViewModel viewModel;
    public FirestoreItemsAdapter adapter;
    public Query mQuery;
    @NonNull
    public FirestoreItemsAdapter.OnFirestoreItemSelected mItemSelectedListener = new
            FirestoreItemsAdapter.OnFirestoreItemSelected() {

        @Override
        public void onFirestoreItemSelected(final AbstractFirestoreItem event, final View itemView) {

        }
    };

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mUserReference = extras.getString(ARG_USER_REFERENCE);
        }
        assert mUserReference != null : "Must pass extra " + ARG_USER_REFERENCE;

        mQuery = FirebaseUtil.getUsersCollectionReference(Utils.POSTS)
                .whereEqualTo("uid", mUserReference)
                .orderBy(Utils.TIMESTAMP)
                .limit(FirebaseUtil.LIMIT);
        adapter = Adapters.PostAdapter(this, mQuery, mItemSelectedListener);
        final RecyclerView mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter((RecyclerView.Adapter<? extends RecyclerView.ViewHolder>) adapter);

        viewModel = ViewModelProviders.of(this).get(ProfileActivityViewModel.class);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayout);
        profilePhoto = findViewById(R.id.profilePhoto);

        mFirestore = FirebaseUtil.getFirestore();
        mDocumentReference = mFirestore.collection("users").document(mUserReference);
    }

    @Override
    public void onStart() {
        super.onStart();
        mModelRegistration = mDocumentReference.addSnapshotListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
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
    public void onEvent(@NonNull final DocumentSnapshot documentSnapshot, @Nullable final FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "restaurant:onEvent", e);
            return;
        }

        onUserModelLoaded(documentSnapshot.toObject(User.class));
        onItemModelLoaded(documentSnapshot.toObject(FirestoreItem.class));
    }

    public void onItemModelLoaded(final FirestoreItem firestoreItem) {

    }

    public void onUserModelLoaded(@NonNull final User user) {
        collapsingToolbarLayout.setTitle(user.getDisplayName());


        // Background image
        final String uuid = user.getPhotoUrl();
        if (uuid != null) {
            GlideApp.with(this)
                    .load(uuid)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(profilePhoto.getProfileImageView(user.getUid()));
        }
    }
}

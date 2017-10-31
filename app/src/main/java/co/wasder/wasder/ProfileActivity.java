package co.wasder.wasder;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
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
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import co.wasder.data.model.FirestoreItem;
import co.wasder.wasder.adapter.FirestoreItemAdapter;
import co.wasder.wasder.adapter.FirestoreItemsAdapter;
import co.wasder.wasder.model.User;
import co.wasder.wasder.viewmodel.ProfileActivityViewModel;
import co.wasder.wasder.views.FirestoreCollections;

public class ProfileActivity extends AppCompatActivity implements EventListener<DocumentSnapshot> {

    private static final String ARG_USER_REFERENCE = "user-reference";
    private static final String TAG = "ProfileActivity";
    CollapsingToolbarLayout collapsingToolbarLayout;
    ImageView profilePhoto;
    private String mUserReference;
    private FirebaseFirestore mFirestore;
    private DocumentReference mDocumentReference;
    private ListenerRegistration mModelRegistration;
    private ProfileActivityViewModel viewModel;
    private FirestoreItemsAdapter adapter;
    private Query mQuery;
    private FirestoreItemsAdapter.OnFirestoreItemSelected mItemSelectedListener = new
            FirestoreItemsAdapter.OnFirestoreItemSelected() {

        @Override
        public void onFirestoreItemSelected(DocumentSnapshot event, View itemView) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mQuery = FirebaseFirestore.getInstance().collection(FirestoreCollections.POSTS);
        mQuery.orderBy("timestamp").limit(50);
        FirestoreRecyclerOptions options = new FirestoreRecyclerOptions.Builder<FirestoreItem>()
                .setLifecycleOwner(this)
                .setQuery(mQuery, FirestoreItem.class)
                .build();
        adapter = new FirestoreItemAdapter(options, mItemSelectedListener);
        RecyclerView mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter((RecyclerView.Adapter<? extends RecyclerView.ViewHolder>) adapter);

        viewModel = ViewModelProviders.of(this).get(ProfileActivityViewModel.class);

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

        onUserModelLoaded(documentSnapshot.toObject(User.class));
        onItemModelLoaded(documentSnapshot.toObject(FirestoreItem.class));
    }

    private void onItemModelLoaded(FirestoreItem firestoreItem) {

    }

    private void onUserModelLoaded(User user) {
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

package co.wasder.wasder.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.drawable.Drawable;
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
import android.widget.ImageView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.firebase.ui.common.ChangeEventType;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.wasder.wasder.RecyclerAdapterFactory;

import butterknife.BindView;
import co.wasder.wasder.R;
import co.wasder.wasder.Util.FirebaseUtil;
import co.wasder.wasder.Utils;
import co.wasder.wasder.data.model.AbstractFirestoreItem;
import co.wasder.wasder.data.model.FeedModel;
import co.wasder.wasder.data.model.User;
import co.wasder.wasder.network.GlideApp;
import co.wasder.wasder.ui.adapter.OnFirestoreItemSelected;
import co.wasder.wasder.ui.viewholder.FeedViewHolder;

@Keep
public class ProfileActivity extends AppCompatActivity implements EventListener<DocumentSnapshot> {

    public static final String ARG_USER_REFERENCE = "user-reference";
    public static final String TAG = "ProfileActivity";
    public CollapsingToolbarLayout collapsingToolbarLayout;
    public String mUserReference;
    public FirebaseFirestore mFirestore;
    public DocumentReference mDocumentReference;
    public ListenerRegistration mModelRegistration;
    public ProfileActivityViewModel viewModel;
    public RecyclerView.Adapter<FeedViewHolder> adapter;
    public Query mQuery;
    public OnFirestoreItemSelected mItemSelectedListener = new OnFirestoreItemSelected() {
        @Override
        public void onFirestoreItemSelected(AbstractFirestoreItem event, View itemView) {

        }

        @Override
        public void onChildChanged(ChangeEventType type, DocumentSnapshot snapshot, int newIndex,
                                   int oldIndex) {

        }

        @Override
        public void onDataChanged() {

        }

        @Override
        public void onError(FirebaseFirestoreException e) {

        }
    };
    @BindView(R.id.itemProfileImageView)
    ImageView profileImageView;
    @BindView(R.id.presenceImageView)
    ImageView presenceImageView;
    private String userId;

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
        adapter = RecyclerAdapterFactory.getFeedRecyclerAdapter(this, mItemSelectedListener,
                mQuery, FeedModel.class);
        final RecyclerView mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);

        viewModel = ViewModelProviders.of(this).get(ProfileActivityViewModel.class);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayout);

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
    public void onEvent(@NonNull final DocumentSnapshot documentSnapshot, @Nullable final
    FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "restaurant:onEvent", e);
            return;
        }

        onUserModelLoaded(documentSnapshot.toObject(User.class));
        onItemModelLoaded(documentSnapshot.toObject(FeedModel.class));
    }

    public void onItemModelLoaded(final FeedModel feedModel) {
        userId = feedModel.getUid();
        String photoUrl = feedModel.getPhoto();
        downloadProfilePictureIntoView(userId, photoUrl, this);
    }

    public void onUserModelLoaded(@NonNull final User user) {
        collapsingToolbarLayout.setTitle(user.getDisplayName());


        // Background image
        final String uuid = user.getPhotoUrl();
        if (uuid != null) {

        }
    }

    private void downloadProfilePictureIntoView(String userId, String profilePhotoUrl, Context
            context) {
        if (profilePhotoUrl != null) {
            GlideApp.with(context)
                    .load(profilePhotoUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(getProfileImageView(userId));
        }
    }

    ImageView getProfileImageView(@NonNull final String uid) {
        FirebaseDatabase.getInstance()
                .getReference("users")
                .child("online")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final String myPresence = dataSnapshot.getValue().toString();
                            handlePresence(myPresence, getResources().getDrawable(R.drawable
                                    .ic_presence_status_online), getResources()
                                    .getDrawable(R.drawable.ic_presence_status_offline));
                        }
                    }

                    @Override
                    public void onCancelled(final DatabaseError databaseError) {

                    }
                });
        return profileImageView;
    }

    private void handlePresence(String myPresence, Drawable onlineDrawable, Drawable
            offlineDrawable) {
        if (myPresence.equals("true")) {
            setPresence(onlineDrawable, presenceImageView);
        } else if (myPresence.equals("false")) {
            setPresence(offlineDrawable, presenceImageView);
        }
    }

    private void setPresence(Drawable drawable, ImageView presenceImageView) {
        presenceImageView.setImageDrawable(drawable);
    }
}

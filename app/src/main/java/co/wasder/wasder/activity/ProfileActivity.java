package co.wasder.wasder.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
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
import com.firebase.ui.common.ChangeEventType;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import co.wasder.data.base.BaseModel;
import co.wasder.data.model.FeedModel;
import co.wasder.data.model.User;
import co.wasder.ui.viewmodel.ProfileActivityViewModel;
import co.wasder.wasder.GlideApp;
import co.wasder.wasder.R;
import co.wasder.wasder.databinding.ActivityProfileBinding;
import co.wasder.wasder.listener.OnFirestoreItemSelectedListener;
import co.wasder.wasder.recycleradapter.FollowingRecyclerAdapter;
import co.wasder.wasder.viewholder.FeedViewHolder;

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
    public OnFirestoreItemSelectedListener mItemSelectedListener = new
            OnFirestoreItemSelectedListener() {
        @Override
        public void onFirestoreItemSelected(BaseModel event, View itemView) {

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
    private ActivityProfileBinding binding;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mUserReference = extras.getString(ARG_USER_REFERENCE);
        }
        assert mUserReference != null : "Must pass extra " + ARG_USER_REFERENCE;

        mQuery = FirebaseFirestore.getInstance().collection("posts")
                .whereEqualTo("uid", mUserReference)
                .orderBy("timestamp")
                .limit((long) 50);
        adapter = FollowingRecyclerAdapter.getAdapter(this, mItemSelectedListener, mQuery,
                FeedModel.class);
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

        mFirestore = FirebaseFirestore.getInstance();
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
    }

    public void onUserModelLoaded(@NonNull final User user) {
        binding.setUser(user);
        String photoUrl = user.getPhotoUrl();
        if (photoUrl != null) {
            GlideApp.with(getApplicationContext())
                    .load(photoUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.profilePhotoImageView);
        }
    }
}

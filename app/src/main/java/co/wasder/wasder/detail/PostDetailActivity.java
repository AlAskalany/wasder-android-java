/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package co.wasder.wasder.detail;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.wasder.wasder.GlideApp;
import co.wasder.wasder.R;
import co.wasder.wasder.Util.PostUtil;
import co.wasder.wasder.adapter.RatingAdapter;
import co.wasder.wasder.dialog.AddRatingDialogFragment;
import co.wasder.wasder.model.Post;
import co.wasder.wasder.model.Rating;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class PostDetailActivity extends AppCompatActivity implements
        EventListener<DocumentSnapshot>, AddRatingDialogFragment.RatingListener {

    public static final String KEY_POST_ID = "key_post_id";
    private static final String TAG = "PostDetail";
    @BindView(R.id.post_image)
    ImageView mImageView;

    @BindView(R.id.post_name)
    TextView mNameView;

    @BindView(R.id.post_rating)
    MaterialRatingBar mRatingIndicator;

    @BindView(R.id.post_num_ratings)
    TextView mNumRatingsView;

    @BindView(R.id.post_city)
    TextView mCityView;

    @BindView(R.id.post_category)
    TextView mCategoryView;

    @BindView(R.id.post_price)
    TextView mPriceView;

    @BindView(R.id.view_empty_ratings)
    ViewGroup mEmptyView;

    @BindView(R.id.recycler_ratings)
    RecyclerView mRatingsRecycler;

    @BindView(R.id.fab_show_rating_dialog)
    FloatingActionButton floatingActionButton;

    private AddRatingDialogFragment mRatingDialog;

    private FirebaseFirestore mFirestore;
    private DocumentReference mPostRef;
    private ListenerRegistration mPostRegistration;

    private RatingAdapter mRatingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setAllowEnterTransitionOverlap(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(new AutoTransition());
            getWindow().setExitTransition(new Explode());
        }*/
        setContentView(R.layout.activity_post_detail);
        ButterKnife.bind(this);

        // Get post ID from extras
        String postId = getIntent().getExtras().getString(KEY_POST_ID);
        if (postId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_POST_ID);
        }

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get reference to the post
        mPostRef = mFirestore.collection("restaurants").document(postId);

        // Get ratings
        Query ratingsQuery = mPostRef.collection("ratings")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(50);

        // RecyclerView
        mRatingAdapter = new RatingAdapter(ratingsQuery) {
            @Override
            protected void onDataChanged() {
                if (getItemCount() == 0) {
                    mRatingsRecycler.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mRatingsRecycler.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }
            }
        };

        mRatingsRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRatingsRecycler.setAdapter(mRatingAdapter);

        mRatingDialog = new AddRatingDialogFragment();

    }

    @Override
    public void onStart() {
        super.onStart();

        mRatingAdapter.startListening();
        mPostRegistration = mPostRef.addSnapshotListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        mRatingAdapter.stopListening();

        if (mPostRegistration != null) {
            mPostRegistration.remove();
            mPostRegistration = null;
        }
    }

    private Task<Void> addRating(final DocumentReference postRef, final Rating rating) {
        // Create reference for new rating, for use inside the transaction
        final DocumentReference ratingRef = postRef.collection("ratings").document();

        // In a transaction, add the new rating and update the aggregate totals
        return mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {

                Post post = transaction.get(postRef).toObject(Post.class);

                // Compute new number of ratings
                int newNumRatings = post.getNumRatings() + 1;

                // Compute new average rating
                double oldRatingTotal = post.getAvgRating() * post.getNumRatings();
                double newAvgRating = (oldRatingTotal + rating.getRating()) / newNumRatings;

                // Set new post info
                post.setNumRatings(newNumRatings);
                post.setAvgRating(newAvgRating);

                // Commit to Firestore
                transaction.set(postRef, post);
                transaction.set(ratingRef, rating);

                return null;
            }
        });
    }

    /**
     * Listener for the Post document ({@link #mPostRef}).
     */
    @Override
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "restaurant:onEvent", e);
            return;
        }

        onPostLoaded(snapshot.toObject(Post.class));
    }

    private void onPostLoaded(Post post) {
        mNameView.setText(post.getName());
        mRatingIndicator.setRating((float) post.getAvgRating());
        mNumRatingsView.setText(getString(R.string.fmt_num_ratings, post.getNumRatings()));
        mCityView.setText(post.getCity());
        mCategoryView.setText(post.getCategory());
        mPriceView.setText(PostUtil.getPriceString(post));

        // Background image
        String uuid = post.getPhoto();
        if (uuid != null) {
            StorageReference mImageRef = FirebaseStorage.getInstance().getReference(uuid);
            GlideApp.with(mImageView.getContext())
                    .load(mImageRef)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(mImageView);
        }
    }

    @OnClick(R.id.post_button_back)
    public void onBackArrowClicked(View view) {
        onBackPressed();
    }

    @OnClick(R.id.fab_show_rating_dialog)
    public void onAddRatingClicked(View view) {
        mRatingDialog.show(getSupportFragmentManager(), AddRatingDialogFragment.TAG);
    }

    @Override
    public void onRating(Rating rating) {
        // In a transaction, add the new rating and update the aggregate totals
        addRating(mPostRef, rating).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Rating added");

                // Hide keyboard and scroll to top
                hideKeyboard();
                mRatingsRecycler.smoothScrollToPosition(0);
                Snackbar.make(findViewById(android.R.id.content), R.string.snackbar_rating_added, Snackbar.LENGTH_SHORT)
                        .show();
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Add rating failed", e);

                // Show failure message and hide keyboard
                hideKeyboard();
                Snackbar.make(findViewById(android.R.id.content), R.string.snackbar_rating_add_failed, Snackbar.LENGTH_SHORT)
                        .show();
            }
        });
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view
                    .getWindowToken(), 0);
        }
    }
}

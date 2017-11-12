/*
  Copyright 2017 Google Inc. All Rights Reserved.
  <p>
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  <p>
  http://www.apache.org/licenses/LICENSE-2.0
  <p>
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package co.wasder.wasder.ui;

import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import co.wasder.wasder.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.wasder.wasder.R;
import co.wasder.wasder.Util.FirebaseUtil;
import co.wasder.wasder.data.model.FeedModel;
import co.wasder.wasder.data.model.Rating;
import co.wasder.wasder.network.GlideApp;
import co.wasder.wasder.ui.dialog.AddRatingDialogFragment;
import co.wasder.wasder.ui.adapter.RatingAdapter;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

@Keep
public class FirestoreItemDetailActivity extends BaseDetailActivity {

    public static final String KEY_POST_ID = "key_post_id";
    public static final String TAG = "PostDetail";
    @Nullable
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.item_image)
    public ImageView mImageView;

    @Nullable
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.post_name)
    public TextView mNameView;

    @Nullable
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.item_rating)
    public MaterialRatingBar mRatingIndicator;

    @Nullable
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.item_num_ratings)
    public TextView mNumRatingsView;

    @Nullable
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.item_city)
    public TextView mCityView;

    @Nullable
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.item_category)
    public TextView mCategoryView;

    @Nullable
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.item_price)
    public TextView mPriceView;

    @Nullable
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.view_empty_ratings)
    public ViewGroup mEmptyView;

    @Nullable
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.recycler_ratings)
    public RecyclerView mRatingsRecycler;

    @Nullable
    @BindView(R.id.fab_show_rating_dialog)
    public FloatingActionButton floatingActionButton;

    public AddRatingDialogFragment mRatingDialog;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        ButterKnife.bind(this);

        // Get post ID from extras
        final Bundle extras = getIntent().getExtras();
        String postId = null;
        if (extras != null) {
            postId = extras.getString(KEY_POST_ID);
        }
        assert postId != null : "Must pass extra " + KEY_POST_ID;

        // Initialize Firestore
        mFirestore = FirebaseUtil.getFirestore();

        // Get reference to the post
        mDocumentRef = mFirestore.collection(Utils.RESTAURANTS).document(postId);

        // Get ratings
        ratingsQuery = mDocumentRef.collection("ratings")
                .orderBy(Utils.TIMESTAMP, Query.Direction.DESCENDING)
                .limit(FirebaseUtil.LIMIT);

        // RecyclerView
        mRatingAdapter = new RatingAdapter(ratingsQuery) {
            @Override
            public  void onDataChanged() {
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

    @NonNull
    public Task<Void> addRating(@NonNull final DocumentReference documentRef, @NonNull final Rating rating) {
        // Create reference for new rating, for use inside the transaction
        final DocumentReference ratingRef = documentRef.collection("ratings").document();

        // In a transaction, add the new rating and update the aggregate totals
        return mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(@NonNull final Transaction transaction) throws FirebaseFirestoreException {

                final FeedModel feedModel = transaction.get(documentRef)
                        .toObject(FeedModel.class);

                // Compute new number of ratings
                final int newNumRatings = feedModel.getNumRatings() + 1;

                // Compute new average rating
                final double oldRatingTotal = feedModel.getAvgRating() * feedModel
                        .getNumRatings();
                final double newAvgRating = (oldRatingTotal + rating.getRating()) / newNumRatings;

                // Set new feedModel info
                feedModel.setNumRatings(newNumRatings);
                feedModel.setAvgRating(newAvgRating);

                // Commit to Firestore
                transaction.set(documentRef, feedModel);
                transaction.set(ratingRef, rating);

                return null;
            }
        });
    }

    /**
     * Listener for the FeedModel document ({@link #mDocumentRef}).
     */
    @Override
    public void onEvent(@NonNull final DocumentSnapshot snapshot, @Nullable final FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "restaurant:onEvent", e);
            return;
        }

        onModelLoaded(snapshot.toObject(FeedModel.class));
    }

    public void onModelLoaded(@NonNull final FeedModel feedModel) {
        mNameView.setText(feedModel.getName());
        mRatingIndicator.setRating((float) feedModel.getAvgRating());
        mNumRatingsView.setText(getString(R.string.fmt_num_ratings, feedModel.getNumRatings()));

        // Background image
        final String uuid = feedModel.getPhoto();
        if (uuid != null) {
            final StorageReference mImageRef = FirebaseStorage.getInstance().getReference(uuid);
            GlideApp.with(mImageView.getContext())
                    .load(mImageRef)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(mImageView);
        }
    }

    @OnClick(R.id.post_button_back)
    public void onBackArrowClicked(@SuppressWarnings("unused") final View view) {
        onBackPressed();
    }

    @OnClick(R.id.fab_show_rating_dialog)
    public void onAddRatingClicked(@SuppressWarnings("unused") final View view) {
        mRatingDialog.show(getSupportFragmentManager(), AddRatingDialogFragment.TAG);
    }

    @Override
    public void onRating(@NonNull final Rating rating) {
        // In a transaction, add the new rating and update the aggregate totals
        addRating(mDocumentRef, rating).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(final Void aVoid) {
                Log.d(TAG, "Rating added");

                // Hide keyboard and scroll to top
                hideKeyboard();
                mRatingsRecycler.smoothScrollToPosition(0);
                Snackbar.make(findViewById(android.R.id.content), R.string.snackbar_rating_added,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull final Exception e) {
                Log.w(TAG, "Add rating failed", e);

                // Show failure message and hide keyboard
                hideKeyboard();
                Snackbar.make(findViewById(android.R.id.content), R.string
                        .snackbar_rating_add_failed, Snackbar.LENGTH_SHORT)
                        .show();
            }
        });
    }

}

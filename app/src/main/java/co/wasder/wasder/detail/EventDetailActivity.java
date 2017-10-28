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
package co.wasder.wasder.detail;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Transaction;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.wasder.wasder.R;
import co.wasder.wasder.Util.EventUtil;
import co.wasder.wasder.adapter.RatingAdapter;
import co.wasder.wasder.dialog.AddEventDialogFragment;
import co.wasder.wasder.dialog.AddRatingDialogFragment;
import co.wasder.wasder.model.Event;
import co.wasder.wasder.model.Rating;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class EventDetailActivity extends BaseDetailActivity {

    public static final String KEY_EVENT_ID = "key_event_id";
    private static final String TAG = "EventDetail";
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.event_user_user_name)
    TextView mUserName;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.event_image)
    ImageView mImageView;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.event_name)
    TextView mNameView;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.event_rating)
    MaterialRatingBar mRatingIndicator;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.event_num_ratings)
    TextView mNumRatingsView;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.event_city)
    TextView mCityView;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.event_category)
    TextView mCategoryView;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.event_price)
    TextView mPriceView;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.event_date)
    TextView mDateView;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.view_empty_ratings)
    ViewGroup mEmptyView;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.recycler_ratings)
    RecyclerView mRatingsRecycler;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.activity_event_detail_coordinator_layout)
    View mCoordinatorLayout;

    private AddEventDialogFragment mEventDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        ButterKnife.bind(this);

        // Get event ID from extras
        Bundle extras = getIntent().getExtras();
        String eventId = null;
        if (extras != null) {
            eventId = extras.getString(KEY_EVENT_ID);
        }
        if (eventId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_EVENT_ID);
        }

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get reference to the event
        mDocumentRef = mFirestore.collection("events").document(eventId);

        // Get ratings
        ratingsQuery = mDocumentRef.collection("ratings")
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

        mEventDialog = new AddEventDialogFragment();

    }

    @SuppressWarnings("WeakerAccess")
    protected Task<Void> addRating(final DocumentReference documentRef, final Rating rating) {
        // Create reference for new rating, for use inside the transaction
        final DocumentReference ratingRef = documentRef.collection("ratings").document();

        // In a transaction, add the new rating and update the aggregate totals
        return mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                Event event = transaction.get(documentRef).toObject(Event.class);

                // Compute new number of ratings
                int newNumRatings = event.getNumRatings() + 1;

                // Compute new average rating
                double oldRatingTotal = event.getAvgRating() * event.getNumRatings();
                double newAvgRating = (oldRatingTotal + rating.getRating()) / newNumRatings;

                // Set new event info
                event.setNumRatings(newNumRatings);
                event.setAvgRating(newAvgRating);

                // Commit to Firestore
                transaction.set(documentRef, event);
                transaction.set(ratingRef, rating);

                return null;
            }
        });
    }

    /**
     * Listener for the Event document ({@link #mDocumentRef}).
     */
    @Override
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "event:onEvent", e);
            return;
        }

        onModelLoaded(snapshot.toObject(Event.class));
    }

    private void onModelLoaded(Event event) {
        mUserName.setText(event.getUserName());
        mNameView.setText(event.getEventName());
        mRatingIndicator.setRating((float) event.getAvgRating());
        mNumRatingsView.setText(getString(R.string.fmt_num_ratings, event.getNumRatings()));
        mCityView.setText(event.getCity());
        mCategoryView.setText(event.getCategory());
        mPriceView.setText(EventUtil.getPriceString(event));
        mDateView.setText(event.getDate());

        // Background image
        Glide.with(mImageView.getContext()).load(event.getPhoto()).into(mImageView);
    }

    @OnClick(R.id.event_button_back)
    public void onBackArrowClicked(@SuppressWarnings("unused") View view) {
        onBackPressed();
    }

    @OnClick(R.id.fab_show_rating_dialog)
    public void onAddRatingClicked(@SuppressWarnings("unused") View view) {
        mEventDialog.show(getSupportFragmentManager(), AddRatingDialogFragment.TAG);
    }

    @Override
    public void onRating(Rating rating) {
        // In a transaction, add the new rating and update the aggregate totals
        addRating(mDocumentRef, rating).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                // Hide keyboard and scroll to top
                hideKeyboard();
                //mRatingsRecycler.smoothScrollToPosition(0);
                Snackbar.make(mCoordinatorLayout, R.string.snackbar_rating_added, Snackbar
                        .LENGTH_SHORT)
                        .show();
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                // Show failure message and hide keyboard
                hideKeyboard();
                Snackbar.make(mCoordinatorLayout, "Failed to add rating", Snackbar.LENGTH_SHORT)
                        .show();
            }
        });
    }
}

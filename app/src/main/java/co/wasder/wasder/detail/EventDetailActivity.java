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

import com.bumptech.glide.Glide;
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

public class EventDetailActivity extends AppCompatActivity implements
        EventListener<DocumentSnapshot>, AddRatingDialogFragment.RatingListener {

    public static final String KEY_EVENT_ID = "key_event_id";
    private static final String TAG = "EventDetail";
    @BindView(R.id.event_user_user_name)
    TextView mUserName;

    @BindView(R.id.event_image)
    ImageView mImageView;

    @BindView(R.id.event_name)
    TextView mNameView;

    @BindView(R.id.event_rating)
    MaterialRatingBar mRatingIndicator;

    @BindView(R.id.event_num_ratings)
    TextView mNumRatingsView;

    @BindView(R.id.event_city)
    TextView mCityView;

    @BindView(R.id.event_category)
    TextView mCategoryView;

    @BindView(R.id.event_price)
    TextView mPriceView;

    @BindView(R.id.event_date)
    TextView mDateView;

    @BindView(R.id.view_empty_ratings)
    ViewGroup mEmptyView;

    @BindView(R.id.recycler_ratings)
    RecyclerView mRatingsRecycler;

    @BindView(R.id.fab_show_rating_dialog)
    FloatingActionButton floatingActionButton;

    @BindView(R.id.activity_event_detail_coordinator_layout)
    View mCoordinatorLayout;

    private AddEventDialogFragment mEventDialog;

    private FirebaseFirestore mFirestore;
    private DocumentReference mEventRef;
    private ListenerRegistration mEventRegistration;

    private RatingAdapter mRatingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        ButterKnife.bind(this);

        // Get event ID from extras
        String eventId = getIntent().getExtras().getString(KEY_EVENT_ID);
        if (eventId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_EVENT_ID);
        }

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get reference to the event
        mEventRef = mFirestore.collection("events").document(eventId);

        // Get ratings
        Query ratingsQuery = mEventRef.collection("ratings").orderBy("timestamp", Query.Direction
                .DESCENDING).limit(50);

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

    @Override
    public void onStart() {
        super.onStart();

        mRatingAdapter.startListening();
        mEventRegistration = mEventRef.addSnapshotListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        mRatingAdapter.stopListening();

        if (mEventRegistration != null) {
            mEventRegistration.remove();
            mEventRegistration = null;
        }
    }

    private Task<Void> addRating(final DocumentReference eventRef, final Rating rating) {
        // Create reference for new rating, for use inside the transaction
        final DocumentReference ratingRef = eventRef.collection("ratings").document();

        // In a transaction, add the new rating and update the aggregate totals
        return mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {

                Event event = transaction.get(eventRef).toObject(Event.class);

                // Compute new number of ratings
                int newNumRatings = event.getNumRatings() + 1;

                // Compute new average rating
                double oldRatingTotal = event.getAvgRating() * event.getNumRatings();
                double newAvgRating = (oldRatingTotal + rating.getRating()) / newNumRatings;

                // Set new event info
                event.setNumRatings(newNumRatings);
                event.setAvgRating(newAvgRating);

                // Commit to Firestore
                transaction.set(eventRef, event);
                transaction.set(ratingRef, rating);

                return null;
            }
        });
    }

    /**
     * Listener for the Event document ({@link #mEventRef}).
     */
    @Override
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "event:onEvent", e);
            return;
        }

        onEventLoaded(snapshot.toObject(Event.class));
    }

    private void onEventLoaded(Event event) {
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
    public void onBackArrowClicked(View view) {
        onBackPressed();
    }

    @OnClick(R.id.fab_show_rating_dialog)
    public void onAddRatingClicked(View view) {
        mEventDialog.show(getSupportFragmentManager(), AddRatingDialogFragment.TAG);
    }

    @Override
    public void onRating(Rating rating) {
        // In a transaction, add the new rating and update the aggregate totals
        addRating(mEventRef, rating).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                // Hide keyboard and scroll to top
                hideKeyboard();
                mRatingsRecycler.smoothScrollToPosition(0);
                Snackbar.make(mCoordinatorLayout, R.string.snackbar_rating_added, Snackbar
                        .LENGTH_SHORT).show();
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

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}

package co.wasder.wasder.detail;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import co.wasder.wasder.adapter.RatingAdapter;
import co.wasder.wasder.dialog.AddRatingDialogFragment;
import co.wasder.wasder.model.Rating;

/**
 * Created by Ahmed AlAskalany on 10/22/2017.
 * Navigator
 */

abstract class BaseDetailActivity extends AppCompatActivity implements
        EventListener<DocumentSnapshot>, AddRatingDialogFragment.RatingListener {

    protected FirebaseFirestore mFirestore;
    protected RatingAdapter mRatingAdapter;
    protected ListenerRegistration mModelRegisteration;
    protected DocumentReference mDocumentRef;
    protected Query ratingsQuery;

    protected void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view
                    .getWindowToken(), 0);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        mRatingAdapter.startListening();
        mModelRegisteration = mDocumentRef.addSnapshotListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        mRatingAdapter.stopListening();

        if (mModelRegisteration != null) {
            mModelRegisteration.remove();
            mModelRegisteration = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected abstract Task<Void> addRating(DocumentReference eventRef, Rating rating);

    @Override
    public abstract void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e);
}

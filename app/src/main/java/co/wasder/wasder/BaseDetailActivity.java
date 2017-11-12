package co.wasder.wasder;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

/**
 * Created by Ahmed AlAskalany on 10/22/2017.
 * Navigator
 */
@Keep
public abstract class BaseDetailActivity extends AppCompatActivity implements
        EventListener<DocumentSnapshot>, AddRatingDialogFragment.RatingListener {

    public FirebaseFirestore mFirestore;
    public ProfilePostAdapter mProfilePostAdapter;
    public DocumentReference mDocumentRef;
    public Query ratingsQuery;
    @Nullable
    public ListenerRegistration mModelRegistration;

    public void hideKeyboard() {
        final View view = getCurrentFocus();
        if (view != null) {
            final InputMethodManager inputMethodManager = ((InputMethodManager) getSystemService
                    (Context.INPUT_METHOD_SERVICE));
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        mProfilePostAdapter.startListening();
        mModelRegistration = mDocumentRef.addSnapshotListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        mProfilePostAdapter.stopListening();

        if (mModelRegistration != null) {
            mModelRegistration.remove();
            mModelRegistration = null;
        }
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public abstract void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e);
}

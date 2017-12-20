package co.wasder.wasder.base;

/*
  Created by Ahmed AlAskalany on 10/11/2017.
  Wasder AB
 */

import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter for displaying the results of a Firestore {@link Query}.
 * <p>
 * Note that this class forgoes some efficiency to gain simplicity. For example, the result of
 * {@link DocumentSnapshot#toObject(Class)} is not cached so the same object may be deserialized
 * many times as the user scrolls.
 */
@Keep
public abstract class FirestoreAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView
        .Adapter<VH> implements EventListener<QuerySnapshot> {

    public static final String TAG = "Firestore Adapter";
    public final List<DocumentSnapshot> mSnapshots = new ArrayList<>();
    public Query mQuery;
    @Nullable
    public ListenerRegistration mRegistration;

    protected FirestoreAdapter(final Query query) {
        mQuery = query;
    }

    @Override
    public void onEvent(@NonNull final QuerySnapshot documentSnapshots, @Nullable final
    FirebaseFirestoreException e) {

        // Handle errors
        if (e != null) {
            Log.w(TAG, "onEvent:error", e);
            return;
        }

        // Dispatch the event
        for (final DocumentChange change : documentSnapshots.getDocumentChanges()) {
            // Snapshot of the changed document
            @SuppressWarnings("unused") final DocumentSnapshot snapshot = change.getDocument();

            switch (change.getType()) {
                case ADDED:
                    onDocumentAdded(change);
                    break;
                case MODIFIED:
                    onDocumentModified(change);
                    break;
                case REMOVED:
                    onDocumentRemoved(change);
                    break;
            }
        }

        onDataChanged();
    }

    public void onDocumentAdded(@NonNull final DocumentChange change) {
        mSnapshots.add(change.getNewIndex(), change.getDocument());
        notifyItemInserted(change.getNewIndex());
    }

    public void onDocumentModified(@NonNull final DocumentChange change) {
        if (change.getOldIndex() == change.getNewIndex()) {
            // Item changed but remained in same position
            mSnapshots.set(change.getOldIndex(), change.getDocument());
            notifyItemChanged(change.getOldIndex());
        } else {
            // Item changed and changed position
            mSnapshots.remove(change.getOldIndex());
            mSnapshots.add(change.getNewIndex(), change.getDocument());
            notifyItemMoved(change.getOldIndex(), change.getNewIndex());
        }
    }

    public void onDocumentRemoved(@NonNull final DocumentChange change) {
        mSnapshots.remove(change.getOldIndex());
        notifyItemRemoved(change.getOldIndex());
    }

    public void startListening() {
        if (mQuery != null && mRegistration == null) {
            mRegistration = mQuery.addSnapshotListener(this);
        }
    }

    public void stopListening() {
        if (mRegistration != null) {
            mRegistration.remove();
            mRegistration = null;
        }

        mSnapshots.clear();
        notifyDataSetChanged();
    }

    @SuppressWarnings("SpellCheckingInspection")
    public void setQuery(final Query query) {
        // Stop listening
        stopListening();

        // Clear existinkodig data
        mSnapshots.clear();
        notifyDataSetChanged();

        // Listen to new query
        mQuery = query;
        startListening();
    }

    @Override
    public int getItemCount() {
        return mSnapshots.size();
    }

    protected DocumentSnapshot getSnapshot(final int index) {
        return mSnapshots.get(index);
    }

    @SuppressWarnings("unused")
    public void onError(@SuppressWarnings("unused") final FirebaseFirestoreException e) {
    }

    public void onDataChanged() {
    }
}

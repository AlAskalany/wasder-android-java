package co.wasder.wasder.adapter;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import co.wasder.wasder.model.FirestoreItem;

/**
 * Created by Ahmed AlAskalany on 10/13/2017.
 * Wasder AB
 */

public class Adapters {

    public static FirestoreItemAdapter PostAdapter(@NonNull LifecycleOwner lifecycleOwner, Query
            query, FirestoreItemsAdapter.OnFirestoreItemSelected listener) {
        FirestoreRecyclerOptions options = new FirestoreRecyclerOptions.Builder<FirestoreItem>()
                .setLifecycleOwner(lifecycleOwner).setQuery(query, FirestoreItem.class)
                .build();
        return new FirestoreItemAdapter(options, listener);
    }
}

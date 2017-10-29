package co.wasder.adapter;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import co.wasder.data.model.Post;

/**
 * Created by Ahmed AlAskalany on 10/13/2017.
 * Wasder AB
 */

public class Adapters {

    public static PostAdapter PostAdapter(@NonNull LifecycleOwner lifecycleOwner, Query query,
                                          PostAdapter.OnPostSelectedListener listener) {
        FirestoreRecyclerOptions options = new FirestoreRecyclerOptions.Builder<Post>()
                .setLifecycleOwner(lifecycleOwner)
                .setQuery(query, Post.class)
                .build();
        return new PostAdapter(options, listener);
    }
}

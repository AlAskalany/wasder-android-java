package com.wasder.wasder.adapter;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.wasder.wasder.model.Restaurant;

/**
 * Created by Ahmed AlAskalany on 10/13/2017.
 * Wasder AB
 */

public class Adapters {
    public static RestaurantAdapter RestaurantAdapter(@NonNull LifecycleOwner lifecycleOwner, Query
            query) {
        FirestoreRecyclerOptions options = new FirestoreRecyclerOptions.Builder<Restaurant>()
                .setLifecycleOwner(lifecycleOwner).setQuery(query, Restaurant.class).build();
        return new RestaurantAdapter(options);
    }
}

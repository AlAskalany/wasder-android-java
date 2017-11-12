package co.wasder.wasder.ui.adapter;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import co.wasder.wasder.data.model.Event;
import co.wasder.wasder.data.model.FeedModel;

/**
 * Created by Ahmed AlAskalany on 10/13/2017.
 * Wasder AB
 */
@Keep
public class Adapters {

    public static FirestoreItemsAdapter PostAdapter(@NonNull final LifecycleOwner lifecycleOwner, final Query
            query, final FirestoreItemsAdapter.OnFirestoreItemSelected listener) {
        final FirestoreRecyclerOptions<FeedModel> options = new FirestoreRecyclerOptions.Builder<FeedModel>().setLifecycleOwner(lifecycleOwner)
                .setQuery(query, FeedModel.class)
                .build();
        return new FirestoreItemAdapter(options, listener);
    }

    public static EventsAdapter eventAdapter(@NonNull final LifecycleOwner lifecycleOwner, final Query query,
                                             final EventsAdapter.OnEventSelected listener) {
        final FirestoreRecyclerOptions<Event> options = new FirestoreRecyclerOptions.Builder<Event>()
                .setLifecycleOwner(lifecycleOwner)
                .setQuery(query, Event.class)
                .build();
        return new EventAdapter(options, listener);
    }
}

package co.wasder.wasder.ui.fragment.tab.adapter;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import co.wasder.wasder.data.model.Event;
import co.wasder.wasder.data.model.FirestoreItem;

/**
 * Created by Ahmed AlAskalany on 10/13/2017.
 * Wasder AB
 */
@Keep
public class Adapters {

    public static FirestoreItemsAdapter PostAdapter(@NonNull final LifecycleOwner lifecycleOwner, final Query
            query, final FirestoreItemsAdapter.OnFirestoreItemSelected listener) {
        final FirestoreRecyclerOptions<FirestoreItem> options = new FirestoreRecyclerOptions.Builder<FirestoreItem>().setLifecycleOwner(lifecycleOwner)
                .setQuery(query, FirestoreItem.class)
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

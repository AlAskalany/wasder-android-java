package co.wasder.wasder.ui.recycleradpater;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import co.wasder.wasder.data.model.Event;

/**
 * Created by Ahmed AlAskalany on 11/12/2017.
 * Navigator
 */

abstract class BaseRecyclerAdapter extends FirestoreRecyclerAdapter<Event, EventAdapter.EventHolder> {

    public BaseRecyclerAdapter(FirestoreRecyclerOptions<Event> options) {
        super(options);
    }

    @Override
    protected abstract void onBindViewHolder(@NonNull EventAdapter.EventHolder holder, int
            position, Event model);

    @NonNull
    @Override
    public abstract EventAdapter.EventHolder onCreateViewHolder(@NonNull ViewGroup group, int viewType);
}

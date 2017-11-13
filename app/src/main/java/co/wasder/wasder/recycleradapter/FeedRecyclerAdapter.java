package co.wasder.wasder.recycleradapter;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import co.wasder.data.model.FeedModel;
import co.wasder.wasder.R;
import co.wasder.wasder.base.BaseRecyclerAdapter;
import co.wasder.wasder.listener.OnFirestoreItemSelectedListener;
import co.wasder.wasder.viewholder.FeedViewHolder;

/**
 * Created by Ahmed AlAskalany on 11/12/2017.
 * Navigator
 */

public class FeedRecyclerAdapter extends BaseRecyclerAdapter {

    /**
     * @param owner      LifecycleOwner
     * @param listener   OnItemSelectedListener
     * @param query      Query
     * @param modelClass ModelCalss
     * @return
     */
    @NonNull
    public static RecyclerView.Adapter<FeedViewHolder> getAdapter(LifecycleOwner owner, final
    OnFirestoreItemSelectedListener listener, Query query, Class<FeedModel> modelClass) {
        final FirestoreRecyclerOptions<FeedModel> options = FeedRecyclerAdapter.getAdapterOptions
                (query, modelClass, owner);

        return new FirestoreRecyclerAdapter<FeedModel, FeedViewHolder>(options) {
            @NonNull
            @Override
            public FeedViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int
                    viewType) {
                return new FeedViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_feed, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull final FeedViewHolder holder, final int
                    position, @NonNull final FeedModel model) {
                holder.bind(model, listener);
            }

            @Override
            public void onDataChanged() {
            }
        };
    }

    private static FirestoreRecyclerOptions<FeedModel> getAdapterOptions(Query query,
                                                                         Class<FeedModel>
                                                                                 modelClass,
                                                                         LifecycleOwner
                                                                                 lifecycleOwner) {
        return new FirestoreRecyclerOptions.Builder<FeedModel>().setQuery(query, modelClass)
                .setLifecycleOwner(lifecycleOwner)
                .build();
    }
}

package co.wasder.wasder.ui.feed;

import android.arch.lifecycle.LifecycleOwner;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import co.wasder.wasder.R;
import co.wasder.wasder.data.FeedModel;
import co.wasder.wasder.databinding.ItemFeedBinding;
import co.wasder.wasder.ui.OnFirestoreItemSelectedListener;

/** Created by Ahmed AlAskalany on 11/12/2017. Navigator */
public class FeedRecyclerAdapter {

    /**
     * @param owner LifecycleOwner
     * @param listener OnItemSelectedListener
     * @param query Query
     * @param modelClass ModelClass
     * @return Feed RecyclerView Adapter
     */
    @NonNull
    public static RecyclerView.Adapter<FeedViewHolder> getAdapter(
            LifecycleOwner owner,
            final OnFirestoreItemSelectedListener listener,
            Query query,
            Class<FeedModel> modelClass) {
        final FirestoreRecyclerOptions<FeedModel> options =
                FeedRecyclerAdapter.getAdapterOptions(query, modelClass, owner);

        return new FirestoreRecyclerAdapter<FeedModel, FeedViewHolder>(options) {
            @NonNull
            @Override
            public FeedViewHolder onCreateViewHolder(
                    @NonNull final ViewGroup parent, final int viewType) {
                LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
                ItemFeedBinding itemFeedBinding =
                        DataBindingUtil.inflate(layoutInflater, R.layout.item_feed, parent, false);
                return new FeedViewHolder(itemFeedBinding);
            }

            @Override
            protected void onBindViewHolder(
                    @NonNull final FeedViewHolder holder,
                    final int position,
                    @NonNull final FeedModel model) {
                holder.bind(model, listener);
            }

            @Override
            public void onDataChanged() {}
        };
    }

    private static FirestoreRecyclerOptions<FeedModel> getAdapterOptions(
            Query query, Class<FeedModel> modelClass, LifecycleOwner lifecycleOwner) {
        return new FirestoreRecyclerOptions.Builder<FeedModel>()
                .setQuery(query, modelClass)
                .setLifecycleOwner(lifecycleOwner)
                .build();
    }
}

package co.wasder.ui.navigation.live.tabs.favorites;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import co.wasder.ui.navigation.home.tabs.feed.FeedViewHolder;
import co.wasder.ui.navigation.tab.BaseRecyclerAdapter;
import co.wasder.wasder.R;
import co.wasder.wasder.data.model.FeedModel;
import co.wasder.wasder.ui.adapter.OnFirestoreItemSelected;

/**
 * Created by Ahmed AlAskalany on 11/12/2017.
 * Navigator
 */

public class FavoriteRecyclerAdapter extends BaseRecyclerAdapter {

    /**
     * @param owner      LifecycleOwner
     * @param listener   OnItemSelectedListener
     * @param query      Query
     * @param modelClass ModelCalss
     * @return
     */
    @NonNull
    public static RecyclerView.Adapter<FeedViewHolder> getAdapter(LifecycleOwner owner, final
    OnFirestoreItemSelected listener, Query query, Class<FeedModel> modelClass) {
        final FirestoreRecyclerOptions<FeedModel> options = getAdapterOptions(query, modelClass,
                owner);

        return new FirestoreRecyclerAdapter<FeedModel, FeedViewHolder>(options) {
            @NonNull
            @Override
            public FeedViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int
                    viewType) {
                return new FeedViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_firestore_item, parent, false));
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

package com.wasder.wasder.adapter;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.wasder.wasder.R;
import com.wasder.wasder.Util.RestaurantUtil;
import com.wasder.wasder.detail.RestaurantDetailActivity;
import com.wasder.wasder.model.Restaurant;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * Created by Ahmed AlAskalany on 10/13/2017.
 * Wasder AB
 */

public class RestaurantAdapter extends FirestoreRecyclerAdapter<Restaurant, RestaurantAdapter
        .RestaurantHolder> {

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See
     * {@link FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    RestaurantAdapter(FirestoreRecyclerOptions options) {
        super(options);
    }

    @SuppressWarnings("unused")
    public static RestaurantAdapter newInstance(@NonNull LifecycleOwner lifecycleOwner, Query
            query) {
        FirestoreRecyclerOptions options = new FirestoreRecyclerOptions.Builder<Restaurant>()
                .setLifecycleOwner(lifecycleOwner).setQuery(query, Restaurant.class).build();
        return new RestaurantAdapter(options);
    }

    @SuppressWarnings("unused")
    public static RestaurantAdapter newInstance(Query query) {
        FirestoreRecyclerOptions options = new FirestoreRecyclerOptions.Builder<Restaurant>()
                .setQuery(query, Restaurant.class).build();
        return new RestaurantAdapter(options);
    }

    @Override
    protected void onBindViewHolder(RestaurantHolder holder, int position, Restaurant model) {
        holder.bind(getSnapshots().getSnapshot(position));
    }

    @Override
    public RestaurantHolder onCreateViewHolder(ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.item_restaurant,
                group, false);

        return new RestaurantAdapter.RestaurantHolder(view);
    }

    /**
     * Created by Ahmed AlAskalany on 10/13/2017.
     * Wasder AB
     */
    public static class RestaurantHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.restaurant_item_image)
        ImageView imageView;

        @BindView(R.id.restaurant_item_name)
        TextView nameView;

        @BindView(R.id.restaurant_item_rating)
        MaterialRatingBar ratingBar;

        @BindView(R.id.restaurant_item_num_ratings)
        TextView numRatingsView;

        @BindView(R.id.restaurant_item_price)
        TextView priceView;

        @BindView(R.id.restaurant_item_category)
        TextView categoryView;

        @BindView(R.id.restaurant_item_city)
        TextView cityView;

        public RestaurantHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final DocumentSnapshot snapshot) {

            final Restaurant restaurant = snapshot.toObject(Restaurant.class);
            Resources resources = itemView.getResources();

            // Load image
            Glide.with(imageView.getContext()).load(restaurant.getPhoto()).into(imageView);

            nameView.setText(restaurant.getName());
            ratingBar.setRating((float) restaurant.getAvgRating());
            cityView.setText(restaurant.getCity());
            categoryView.setText(restaurant.getCategory());
            numRatingsView.setText(resources.getString(R.string.fmt_num_ratings, restaurant
                    .getNumRatings()));
            priceView.setText(RestaurantUtil.getPriceString(restaurant));

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), RestaurantDetailActivity.class);
                    intent.putExtra("key_restaurant_id", snapshot.getId());
                    view.getContext().startActivity(intent);
                }
            });
        }

    }
}

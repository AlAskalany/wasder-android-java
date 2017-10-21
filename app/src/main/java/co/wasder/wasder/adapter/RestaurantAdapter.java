package co.wasder.wasder.adapter;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.wasder.wasder.GlideApp;
import co.wasder.wasder.R;
import co.wasder.wasder.Util.RestaurantUtil;
import co.wasder.wasder.detail.RestaurantDetailActivity;
import co.wasder.wasder.model.Restaurant;
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
        FirestoreRecyclerOptions options = new FirestoreRecyclerOptions.Builder<Restaurant>().setLifecycleOwner(lifecycleOwner)
                .setQuery(query, Restaurant.class)
                .build();
        return new RestaurantAdapter(options);
    }

    @SuppressWarnings("unused")
    public static RestaurantAdapter newInstance(Query query) {
        FirestoreRecyclerOptions options = new FirestoreRecyclerOptions.Builder<Restaurant>().setQuery(query, Restaurant.class)
                .build();
        return new RestaurantAdapter(options);
    }

    @Override
    protected void onBindViewHolder(RestaurantHolder holder, int position, Restaurant model) {
        holder.bind(getSnapshots().getSnapshot(position));
    }

    @Override
    public RestaurantHolder onCreateViewHolder(ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext())
                .inflate(R.layout.item_restaurant, group, false);

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
            String uuid = restaurant.getPhoto();
            if (!TextUtils.isEmpty(uuid)) {
                StorageReference mImageRef = FirebaseStorage.getInstance().getReference(uuid);
                GlideApp.with(imageView.getContext())
                        .load(mImageRef)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(imageView);
            }
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
                    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        view.getContext()
                                .startActivity(intent, ActivityOptions
                                        .makeSceneTransitionAnimation((AppCompatActivity) view
                                        .getContext()).toBundle());
                    } else {*/
                    //ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation
                    // (view.getContext(), android.R.anim.fade_in, android.R.anim.fade_out);
                    //ActivityOptionsCompat myOptions = ActivityOptionsCompat
                    // .makeSceneTransitionAnimation(((AppCompatActivity)view.getContext()),
                    // imageView, "restaurant_image");
                    view.getContext().startActivity(intent/*, options.toBundle()*/);
                    /*}*/
                }
            });
        }
    }
}

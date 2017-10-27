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
import co.wasder.wasder.Util.PostUtil;
import co.wasder.wasder.detail.PostDetailActivity;
import co.wasder.wasder.model.Post;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * Created by Ahmed AlAskalany on 10/13/2017.
 * Wasder AB
 */

public class PostAdapter extends FirestoreRecyclerAdapter<Post, PostAdapter.PostHolder> {

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See
     * {@link FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    PostAdapter(FirestoreRecyclerOptions options) {
        super(options);
    }

    @SuppressWarnings("unused")
    public static PostAdapter newInstance(@NonNull LifecycleOwner lifecycleOwner, Query
            query) {
        FirestoreRecyclerOptions options = new FirestoreRecyclerOptions.Builder<Post>()
                .setLifecycleOwner(lifecycleOwner)
                .setQuery(query, Post.class)
                .build();
        return new PostAdapter(options);
    }

    @SuppressWarnings("unused")
    public static PostAdapter newInstance(Query query) {
        FirestoreRecyclerOptions options = new FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post.class)
                .build();
        return new PostAdapter(options);
    }

    @Override
    protected void onBindViewHolder(PostHolder holder, int position, Post model) {
        holder.bind(getSnapshots().getSnapshot(position));
    }

    @Override
    public PostHolder onCreateViewHolder(ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext())
                .inflate(R.layout.item_post, group, false);

        return new PostHolder(view);
    }

    /**
     * Created by Ahmed AlAskalany on 10/13/2017.
     * Wasder AB
     */
    public static class PostHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.post_item_image)
        ImageView imageView;

        @BindView(R.id.post_item_name)
        TextView nameView;

        @BindView(R.id.post_item_rating)
        MaterialRatingBar ratingBar;

        @BindView(R.id.post_item_num_ratings)
        TextView numRatingsView;

        @BindView(R.id.post_item_price)
        TextView priceView;

        @BindView(R.id.post_item_category)
        TextView categoryView;

        @BindView(R.id.post_item_city)
        TextView cityView;

        public PostHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final DocumentSnapshot snapshot) {

            final Post post = snapshot.toObject(Post.class);
            Resources resources = itemView.getResources();

            // Load image
            String uuid = post.getPhoto();
            if (!TextUtils.isEmpty(uuid)) {
                StorageReference mImageRef = FirebaseStorage.getInstance().getReference(uuid);
                GlideApp.with(imageView.getContext())
                        .load(mImageRef)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(imageView);
            }
            nameView.setText(post.getName());
            ratingBar.setRating((float) post.getAvgRating());
            cityView.setText(post.getCity());
            categoryView.setText(post.getCategory());
            numRatingsView.setText(resources.getString(R.string.fmt_num_ratings, post
                    .getNumRatings()));
            priceView.setText(PostUtil.getPriceString(post));

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), PostDetailActivity.class);
                    intent.putExtra("key_post_id", snapshot.getId());
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

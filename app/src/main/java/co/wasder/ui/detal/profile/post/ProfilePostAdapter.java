package co.wasder.ui.detal.profile.post;

/*
  Created by Ahmed AlAskalany on 10/11/2017.
  Wasder AB
 */

import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.wasder.data.model.Rating;
import co.wasder.wasder.R;
import co.wasder.wasder.ui.adapter.FirestoreAdapter;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * RecyclerView adapter for a bunch of Ratings.
 */
@Keep
public class ProfilePostAdapter extends FirestoreAdapter<ProfilePostAdapter.ViewHolder> {

    public ProfilePostAdapter(final Query query) {
        super(query);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_profile_post, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.bind(getSnapshot(position).toObject(Rating.class));
    }

    @Keep
    public static class ViewHolder extends RecyclerView.ViewHolder {

        @Nullable
        @BindView(R.id.profile_post_item_name)
        public TextView nameView;

        @Nullable
        @BindView(R.id.profile_post_item_rating)
        public MaterialRatingBar ratingBar;

        @Nullable
        @BindView(R.id.profile_post_item_text)
        public TextView textView;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(@NonNull final Rating rating) {
            nameView.setText(rating.getUserName());
            ratingBar.setRating((float) rating.getRating());
            textView.setText(rating.getText());
        }
    }

}

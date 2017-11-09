package co.wasder.wasder.ui.fragment.tab.adapter;

/*
  Created by Ahmed AlAskalany on 10/11/2017.
  Wasder AB
 */

import android.support.annotation.Keep;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.wasder.wasder.R;
import co.wasder.wasder.data.model.Rating;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * RecyclerView adapter for a bunch of Ratings.
 */
@Keep
public class RatingAdapter extends FirestoreAdapter<RatingAdapter.ViewHolder> {

    public RatingAdapter(final Query query) {
        super(query);
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout
                .item_rating, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.bind(getSnapshot(position).toObject(Rating.class));
    }

    @Keep
    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rating_item_name)
        public TextView nameView;

        @BindView(R.id.rating_item_rating)
        public MaterialRatingBar ratingBar;

        @BindView(R.id.rating_item_text)
        public TextView textView;

        public ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final Rating rating) {
            nameView.setText(rating.getUserName());
            ratingBar.setRating((float) rating.getRating());
            textView.setText(rating.getText());
        }
    }

}
